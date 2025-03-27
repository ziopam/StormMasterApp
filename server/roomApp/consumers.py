import asyncio
import json

from asgiref.sync import sync_to_async
from channels.generic.websocket import AsyncWebsocketConsumer

from .consumers_event_handler import EventHandlers
from .models import Room, Message, Idea


class RoomConsumer(AsyncWebsocketConsumer, EventHandlers):
    """
    This consumer is used to handle WebSocket connections to the room
    """

    def __init__(self, *args, **kwargs):
        super().__init__(args, kwargs)
        self.room_group_name = None
        self.room_code = None

    async def get_room(self):
        """
        This function is used to get the room object. If the room does not exist, the connection is closed
        :return: Room object or None if the room does not exist
        """

        try:
            room = await Room.objects.aget(room_code=self.room_code)
            return room
        except Room.DoesNotExist:
            await self.close(code=4004, reason="Room not found")
            return None

    async def connect(self):
        """
        This function is called when the WebSocket connection is established
        """

        self.room_code = self.scope['url_route']['kwargs']['room_code']
        self.room_group_name = f'room_{self.room_code}'
        await self.accept()

        room = await self.get_room()

        if room is None:
            return

        # Check if the user is authenticated
        if self.scope['user'].id is None:
            await self.close(code=4001, reason="Unauthorized")
            return

        # Check if the user is in the room
        user_in_room = await room.participants.acontains(self.scope['user'])
        if not user_in_room:
            await self.close(code=4003, reason="User is not in the room")
            return

        # Add the user to the room group
        await self.channel_layer.group_add(
            self.room_group_name,
            self.channel_name
        )

        # Send the sync data
        await self.send_sync_data()

    async def disconnect(self, close_code):
        await self.channel_layer.group_discard(
            self.room_group_name,
            self.channel_name
        )

    async def receive(self, text_data):
        """
        This function is called when a message is received from the WebSocket
        :param text_data: message data
        """

        text_data_json = json.loads(text_data)

        room = await self.get_room()

        if room is None:
            return

        # Check if chat is started
        if room.isChatStarted:
            message_type = text_data_json['type']
            if message_type == 'new_message':
                await self.new_message_received(room, text_data_json)
            elif message_type == 'set_idea':
                await self.set_idea_received(text_data_json)
            elif message_type == 'remove_idea':
                await self.remove_idea_received(text_data_json)
            elif message_type == 'vote_for_idea':
                await self.vote_for_idea_received(text_data_json)
            elif message_type == "devote_for_idea":
                await self.devote_for_idea_received(text_data_json)
            elif message_type == "round_robin_update":
                await self.round_robin_update_received(text_data_json)

    async def new_message_received(self, room, text_data_json):
        """
        This function is called when a new message is received
        :param room: room in which the message is sent
        :param text_data_json: the message data
        :return:
        """

        message = text_data_json['message']
        if message:
            # Save the message to the database
            message_object = await Message.objects.acreate(
                room=room,
                sender=self.scope['user'],
                text=message
            )

            # Send the message to the room group
            await self.channel_layer.group_send(
                self.room_group_name,
                {
                    'type': 'new_message',
                    'id': message_object.id,
                    'username': self.scope['user'].username,
                    'message': message
                }
            )

    async def set_idea_received(self, text_data_json):
        """
        This function is called when a new idea is set
        :param text_data_json: the message and its idea data
        :return:
        """

        message_id = text_data_json['message_id']
        idea_number = text_data_json['idea_number']

        # Check if idea_number is an integer
        if not isinstance(idea_number, int) or not message_id:
            return

        if 1 <= idea_number <= 1000:
            room = await self.get_room()
            if room is None:
                return

            message = await sync_to_async(Message.objects.get)(id=message_id)
            idea, _ = await sync_to_async(Idea.objects.get_or_create)(room=room, idea_number=idea_number)
            message.idea = idea
            await message.asave()
            await sync_to_async(idea.update_votes)()
            idea_votes = idea.votes

            # Send the message to the room group
            await self.channel_layer.group_send(
                self.room_group_name,
                {
                    'type': 'set_idea',
                    'message_id': message.id,
                    'idea_number': idea_number,
                    'idea_votes': idea_votes
                }
            )

    async def remove_idea_received(self, text_data_json):
        """
        This function is called when the idea is removed
        :param text_data_json: the message data with the idea to remove
        :return:
        """

        message_id = text_data_json['message_id']
        message = await sync_to_async(Message.objects.get)(id=message_id)
        message.idea = None
        await message.asave()
        # Send the message to the room group
        await self.channel_layer.group_send(
            self.room_group_name,
            {
                'type': 'set_idea',
                'message_id': message.id,
                'idea_number': -1,
                'idea_votes': 0
            }
        )

    async def vote_for_idea_received(self, text_data_json):
        """
        This function is called when the user votes for an idea
        :param text_data_json: the idea data
        :return:
        """

        idea_number = text_data_json['idea_number']
        try:
            room = await self.get_room()
            if room is None:
                return
            idea = await sync_to_async(Idea.objects.get)(room=room, idea_number=idea_number)
        except Idea.DoesNotExist:
            return
        user = self.scope['user']

        # Check if the user has already voted for this idea
        if await idea.voters.acontains(user):
            return

        await sync_to_async(idea.voters.add)(user)
        await idea.asave()
        await sync_to_async(idea.update_votes)()

        # Send the message to the room group
        await self.channel_layer.group_send(
            self.room_group_name,
            {
                'type': 'update_votes',
                'idea_number': idea_number,
                'idea_votes': idea.votes
            })

    async def devote_for_idea_received(self, text_data_json):
        """
        This function is called when the user devotes for an idea
        :param text_data_json: the idea data
        :return:
        """

        idea_number = text_data_json['idea_number']
        try:
            room = await self.get_room()
            if room is None:
                return
            idea = await sync_to_async(Idea.objects.get)(room=room, idea_number=idea_number)
        except Idea.DoesNotExist:
            return
        user = self.scope['user']

        # Check if the user did not vote for this idea
        if not await idea.voters.acontains(user):
            return

        await sync_to_async(idea.voters.remove)(user)
        await idea.asave()
        await sync_to_async(idea.update_votes)()

        # Send the message to the room group
        await self.channel_layer.group_send(
            self.room_group_name,
            {
                'type': 'update_votes',
                'idea_number': idea_number,
                'idea_votes': idea.votes
            })

    async def send_sync_data(self):
        """
        This function is called when the user requests the sync data
        """
        room = await self.get_room()

        if room is None:
            return

        # Send data according to the room state
        if room.isChatStarted:
            messages = await sync_to_async(lambda : Message.objects.filter(room=room).
                                           order_by('timestamp').
                                           values('id', 'sender__username', 'text', 'idea__idea_number', 'idea__votes'))()

            room_data = {
                'type': 'sync_data',
                'isChatStarted': True,
                'details': room.details,
                'messages': await sync_to_async(list)(messages)
            }
        else:
            room_data = {
                'type': 'sync_data',
                'participants':  ', '.join([user.username async for user in room.participants.all()]),
                'participants_amount': await room.participants.acount(),
                'isChatStarted': False
            }

        await self.send(text_data=json.dumps(room_data))

    async def round_robin_update_received(self, text_data_json):
        """
        This function is called when the Round Robin data is updated
        :param text_data_json: the message data with the updated Round Robin data
        """

        room = await self.get_room()
        if room is None:
            return

        room_type_name = await sync_to_async(lambda: room.room_type.name)()

        if room_type_name == 'Round Robin':
            user = self.scope['user']
            new_text = text_data_json['new_text']
            user_message = await Message.objects.aget(sender=user, room=room)

            completed_rounds = await sync_to_async(lambda: room.round_robin_data.completed_rounds)()

            # Find the message to change by calculating the idea number
            message_to_change = await Message.objects.aget(
                idea=user_message.idea_id + completed_rounds,
                room=room
            )
            message_to_change.text = new_text
            await message_to_change.asave()

            # Send message to the sender that the message was successfully changed
            await self.send(text_data=json.dumps({
                'type': 'round_robin_updated'
            }))
