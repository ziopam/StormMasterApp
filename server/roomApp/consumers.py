import json
from channels.generic.websocket import AsyncWebsocketConsumer

from .models import Room, User

class RoomConsumer(AsyncWebsocketConsumer):
    """
    This consumer is used to handle WebSocket connections to the room
    """

    def __init__(self, *args, **kwargs):
        super().__init__(args, kwargs)
        self.room_group_name = None
        self.room_code = None

    async def connect(self):
        """
        This function is called when the WebSocket connection is established
        """

        self.room_code = self.scope['url_route']['kwargs']['room_code']
        self.room_group_name = f'room_{self.room_code}'
        await self.accept()

        # Check if the room exists
        try:
            room = await Room.objects.aget(room_code=self.room_code)
        except Room.DoesNotExist:
            await self.close(code=4004, reason="Room not found")
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
        text_data_json = json.loads(text_data)

        # Check if the room exists
        try:
            room = await Room.objects.aget(room_code=self.room_code)
        except Room.DoesNotExist:
            await self.close(code=4004, reason="Room not found")
            return

        # Check if chat is started
        if room.isChatStarted:
            message_type = text_data_json['type']
            if message_type == 'new_message':
                message = text_data_json['message']
                if message:
                    await self.channel_layer.group_send(
                        self.room_group_name,
                        {
                            'type': 'new_message',
                            'username': self.scope['user'].username,
                            'message': message
                        }
                    )

    async def new_message(self, event):
        """
        This function is called when a new message is received
        :param event: event object
        """

        username = event['username']
        message = event['message']
        await self.send(text_data=json.dumps({
            'type': 'new_message',
            'username': username,
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
        try:
            room = await Room.objects.aget(room_code=self.room_code)
        except Room.DoesNotExist:
            await self.close(code=4004, reason="Room not found")
            return

        # Send data according to the room state
        if room.isChatStarted:
            room_data = {
                'type': 'sync_data',
                'isChatStarted': True
            }
        else:
            room_data = {
                'type': 'sync_data',
                'participants':  ', '.join([user.username async for user in room.participants.all()]),
                'participants_amount': await room.participants.acount(),
                'isChatStarted': False
            }

        await self.send(text_data=json.dumps(room_data))
