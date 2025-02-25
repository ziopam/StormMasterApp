from asgiref.sync import async_to_sync
from channels.layers import get_channel_layer
from django.core.exceptions import ValidationError
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from rest_framework.views import APIView

from roomApp.room_schemas import delete_room_schema, leave_room_schema, join_room_schema, \
    create_room_schema, start_brainstorm_schema
from brainstormsApp.permissions import IsOwnerOrAdmin
from roomApp.models import Room


class CreateRoomView(APIView):
    """
    This view is used to create a new room
    """
    permission_classes = [IsAuthenticated]

    # Add information about the endpoint to the documentation
    @create_room_schema
    def post(self, request):
        """
        Create a new room
        :param request: the request object
        :return: the response object
        """
        title = request.data.get('title')
        if not title:
            return Response({'detail': 'Поле title обязательно в запросе'}, status=400)

        user = request.user
        room = Room(title=title, creator=user)

        # Check if title is well-formed
        try:
            room.full_clean()
        except ValidationError as e:
            return Response({'detail': e.message_dict.get('title')[0]}, status=400)
        room.save()

        room.participants.add(user)
        room.save()
        return Response({'room_code': room.room_code}, status=201)

class JoinRoomView(APIView):
    """
    This view is used to join a room
    """
    permission_classes = [IsAuthenticated]

    # Add information about the endpoint to the documentation
    @join_room_schema
    def post(self, request):
        """
        Join a room. Informs people in waiting room that new person has joined.
        :param request: the request object
        :return: the response object
        """

        # Check if room_code is in the request
        room_code = request.data.get('room_code')
        if not room_code:
            return Response({'detail': 'Поле room_code обязательно в запросе'}, status=400)

        # Check room_code
        try:
            room = Room.objects.get(room_code=room_code)
        except Room.DoesNotExist:
            return Response({'detail': 'Комната с таким кодом не найдена'}, status=404)

        # Add user to the room
        user = request.user
        if not room.participants.contains(user):
            room.participants.add(user)
            room.save()

            # If the chat is not started, inform people in waiting room that new person has joined
            if not room.isChatStarted:
                channel_layer = get_channel_layer()
                async_to_sync(channel_layer.group_send)(
                    f'room_{room_code}',
                    {
                        'type': 'user_joined',
                        'username': user.username
                    }
                )

        return Response({'isCreator': room.creator == user,
                         'participants' : ', '.join([user.username for user in room.participants.all()]),
                         'participants_amount': room.participants.count(),
                         'isChatStarted': room.isChatStarted}, status=200)


class LeaveRoomView(APIView):
    """
    This view is used to leave a room. Informs people in waiting room that person has left
    """
    permission_classes = [IsAuthenticated]

    # Add information about the endpoint to the documentation
    @leave_room_schema
    def post(self, request):
        """
        Leave a room
        :param request: the request object
        :return: the response object
        """

        # Check if room_code is in the request
        room_code = request.data.get('room_code')
        if not room_code:
            return Response({'detail': 'Поле room_code обязательно в запросе'}, status=400)

        # Check room_code
        try:
            room = Room.objects.get(room_code=room_code)
        except Room.DoesNotExist:
            return Response({'detail': 'Комната с таким кодом не найдена'}, status=404)

        # Remove user from the room
        user = request.user
        if room.participants.contains(user):
            if room.creator != user:
                room.participants.remove(user)
                room.save()

                # If the chat is not started, inform people in waiting room that person has left
                if not room.isChatStarted:
                    channel_layer = get_channel_layer()
                    async_to_sync(channel_layer.group_send)(
                        f'room_{room_code}',
                        {
                            'type': 'user_left',
                            'username': user.username
                        }
                    )

                return Response({'detail' : 'ok'}, status=200)
            else:
                return Response({'detail': 'Создатель комнаты не может покинуть комнату. Только удалить'}, status=400)
        return Response({'detail': 'Вы не состоите в этой комнате'}, status=403)

class StartBrainstormView(APIView):
    """
    This view is used to start a brainstorm in a room
    """
    permission_classes = [IsOwnerOrAdmin]

    # Add information about the endpoint to the documentation
    @start_brainstorm_schema
    def post(self, request):
        """
        Start a brainstorm in a room
        :param request: the request object
        :return: the response object
        """

        # Check if room_code is in the request
        room_code = request.data.get('room_code')
        if not room_code:
            return Response({'detail': 'Поле room_code обязательно в запросе'}, status=400)

        # Check room_code
        try:
            room = Room.objects.get(room_code=room_code)
        except Room.DoesNotExist:
            return Response({'detail': 'Комната с таким кодом не найдена'}, status=404)

        # Check if user is the creator of the room or admin
        self.check_object_permissions(request, room)

        if not room.isChatStarted:
            # Set room details
            details = request.data.get('details')
            if details and len(details) > 0 and not details.isspace():
                room.details = details

            # Start the brainstorm
            room.isChatStarted = True
            room.save()

            # Inform people in the room that the chat has started
            channel_layer = get_channel_layer()
            async_to_sync(channel_layer.group_send)(
                f'room_{room_code}',
                {
                    'type': 'chat_started',
                }
            )
            return Response({'detail': 'ok'}, status=200)

        return Response({'detail': 'Мозговой штурм уже начался'}, status=200)

class DeleteRoom(APIView):
    """
    This view is used to delete a room
    """
    permission_classes = [IsOwnerOrAdmin]

    # Add information about the endpoint to the documentation
    @delete_room_schema
    def delete(self, request, *args, **kwargs):
        """
        Delete a room. Informs people in the room that the room has been deleted.
        :param request: the request object
        :return: the response object
        """

        # Check if room_code is in the request
        room_code = kwargs.get('room_code')
        if not room_code:
            return Response({'detail': 'Поле room_code обязательно в запросе'}, status=400)

        # Check room_code
        try:
            room = Room.objects.get(room_code=room_code)
        except Room.DoesNotExist:
            return Response({'detail': 'Комната с таким кодом не найдена'}, status=404)

        # Check if user is the creator of the room or admin
        self.check_object_permissions(request, room)

        # Inform people in the room that the room has been deleted
        channel_layer = get_channel_layer()
        async_to_sync(channel_layer.group_send)(
            f'room_{room_code}',
            {
                'type': 'room_deleted',
            }
        )

        # Delete room if the user has access to it
        room.delete()
        return Response({'detail': 'ok'}, status=200)