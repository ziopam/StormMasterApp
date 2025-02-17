import asyncio
import json

from asgiref.sync import sync_to_async
from channels.generic.websocket import AsyncWebsocketConsumer

from .models import Room, Message, Idea


class RoomConsumer(AsyncWebsocketConsumer):
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
        text_data_json = json.loads(text_data)

        room = await self.get_room()

        # Check if chat is started
        if room.isChatStarted:
            message_type = text_data_json['type']
            if message_type == 'new_message':
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
            elif message_type == 'set_idea':
                message_id = text_data_json['message_id']
                idea_number = text_data_json['idea_number']

                # Check if idea_number is an integer
                if not isinstance(idea_number, int) or not message_id:
                    return

                if 1 <= idea_number <= 1000:
                    message = await sync_to_async(Message.objects.get)(id=message_id)
                    idea, _ = await sync_to_async(Idea.objects.get_or_create)(room_code=self.room_code, idea_number=idea_number)
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
            elif message_type == 'remove_idea':
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

    async def new_message(self, event):
        """
        This function is called when a new message is received
        :param event: event object
        """

        username = event['username']
        message = event['message']
        id = event['id']
        await self.send(text_data=json.dumps({
            'type': 'new_message',
            'username': username,
            'id': id,
            'message': message
        }))

    async def user_joined(self, event):
        """
        This function is called when a user joins the room
        :param event: event object
        """

        username = event['username']
        await self.send(text_data=json.dumps({
            'type': 'user_joined',
            'username': username
        }))

    async def user_left(self, event):
        """
        This function is called when a user leaves the room
        :param event: event object
        """

        username = event['username']
        await self.send(text_data=json.dumps({
            'type': 'user_left',
            'username': username
        }))

    async def chat_started(self, event):
        """
        This function is called when the chat is started
        :param event: event object (not used)
        """

        await self.send(text_data=json.dumps({
            'type': 'chat_started'
        }))

    async def room_deleted(self, event):
        """
        This function is called when the room is deleted
        :param event: event object (not used)
        """

        await self.close(code=4004, reason="Room deleted")

    async def send_sync_data(self):
        """
        This function is called when the user requests the sync data
        """
        room = await self.get_room()

        # Send data according to the room state
        if room.isChatStarted:
            messages = await sync_to_async(lambda : Message.objects.filter(room=room).
                                           order_by('timestamp').
                                           values('id', 'sender__username', 'text', 'idea__idea_number', 'idea__votes'))()

            room_data = {
                'type': 'sync_data',
                'isChatStarted': True,
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

    async def set_idea(self, event):
        """
        This function is called when a new idea is set
        :param event: event object
        """

        message_id = event['message_id']
        idea_number = event['idea_number']
        idea_votes = event['idea_votes']
        await self.send(text_data=json.dumps({
            'type': 'set_idea',
            'message_id': message_id,
            'idea_number': idea_number,
            'idea_votes': idea_votes
        }))
