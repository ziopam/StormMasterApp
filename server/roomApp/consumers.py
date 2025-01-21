import json
from channels.generic.websocket import AsyncWebsocketConsumer
from asgiref.sync import sync_to_async
from django.contrib.auth.models import AnonymousUser

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
        await self.accept()

    async def disconnect(self, close_code):
        await self.channel_layer.group_discard(
            self.room_group_name,
            self.channel_name
        )

    async def receive(self, text_data):
        text_data_json = json.loads(text_data)
        message = text_data_json['message']

        print(f"Received message: {message}")
        await self.channel_layer.group_send(
            self.room_group_name,
            {
                'type': 'chat_message',
                'message': message
            }
        )

    async def chat_message(self, event):
        message = event['message']

        # Отправка сообщения на WebSocket
        await self.send(text_data=json.dumps({
            'message': message
        }))
