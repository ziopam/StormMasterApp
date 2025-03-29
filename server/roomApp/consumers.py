import json

from asgiref.sync import sync_to_async
from channels.generic.websocket import AsyncWebsocketConsumer

from .consumers_event_handler import EventHandlers
from .consumers_received_handlers import ReceivedHandlers
from .models import Room, Message
from .round_robin_handlers import get_users_current_ideas


class RoomConsumer(AsyncWebsocketConsumer, EventHandlers, ReceivedHandlers):
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

    async def send_sync_data(self):
        """
        This function is called when the user requests the sync data
        """
        room = await self.get_room()

        if room is None:
            return

        # Send data according to the room state
        if room.isChatStarted:
            # Check if users are in the Round Robin activity

            try:
                robin_data = await sync_to_async(lambda: room.round_robin_data)()
            except Room.round_robin_data.RelatedObjectDoesNotExist:
                robin_data = None

            room_type = await sync_to_async(lambda: room.room_type.name)()

            if room_type == 'Round Robin' and robin_data:
                room_data = {
                    'type': 'sync_data',
                    'isChatStarted': True,
                    'details': room.details,
                    'move_to': "round_robin",
                }

                # Check if the user has already sent their idea
                if await robin_data.users_finished_idea.acontains(self.scope['user']):
                    room_data['was_idea_sent'] = True
                else:
                    room_data['was_idea_sent'] = False
                    room_data['users_ideas'] = await get_users_current_ideas(room, robin_data)
            else:
                messages = await sync_to_async(lambda : Message.objects.filter(room=room).
                                               order_by('timestamp').
                                               values('id', 'sender__username', 'text', 'idea__idea_number', 'idea__votes'))()

                room_data = {
                    'type': 'sync_data',
                    'isChatStarted': True,
                    'details': room.details,
                    'move_to': "chat",
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

