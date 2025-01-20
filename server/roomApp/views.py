from django.core.exceptions import ValidationError
from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from rest_framework.views import APIView

from roomApp.models import Room


class CreateRoomView(APIView):
    """
    This view is used to create a new room
    """
    permission_classes = [IsAuthenticated]

    # Add information about the endpoint to the documentation
    @swagger_auto_schema(
        operation_id="create_room",
        operation_description="Use this endpoint to create a new room. The request must be authorized.",
        tags=['rooms'],
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'title': openapi.Schema(
                    type=openapi.TYPE_STRING,
                    min_length=1,
                    max_length=25,
                    pattern=r'^[a-zA-Z-0-9а-яA-я\s]+$')
            },
            required=['title']
        ),
        responses={
            201: openapi.Response(
                description="OK",
                schema=openapi.Schema(
                    type=openapi.TYPE_OBJECT,
                    properties={
                        'room_code': openapi.Schema(type=openapi.TYPE_STRING)
                    }
                ),
                examples={
                    "application/json": {
                        "room_code": "ABCDEF"
                    }
                }
            ),
            400: openapi.Response(
                description="Error in the title field", examples={
                    "application/json": {
                        "detail": "Поле title обязательно в запросе"
                    }
                }
            ),
            401: openapi.Response(
                description="Unauthorized", examples={
                    "application/json": {
                        "detail": "Учетные данные не были предоставлены."
                    }
                }
            )
        }
    )
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
    @swagger_auto_schema(
        operation_id="join_room",
        operation_description="Use this endpoint to join a room. The request must be authorized.",
        tags=['rooms'],
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'room_code': openapi.Schema(type=openapi.TYPE_STRING)
            },
            required=['room_code']
        ),
        responses={
            200: openapi.Response(
                description="OK",
                schema=openapi.Schema(
                    type=openapi.TYPE_OBJECT,
                    properties={
                        'isCreator': openapi.Schema(type=openapi.TYPE_BOOLEAN),
                        'isChatStarted': openapi.Schema(type=openapi.TYPE_BOOLEAN)
                    }
                ),
                examples={
                    "application/json": {
                        "isCreator": True,
                        "isChatStarted": False
                    }
                }
            ),
            400: openapi.Response(
                description="Error in the room_code field", examples={
                    "application/json": {
                        "detail": "Поле room_code обязательно в запросе"
                    }
                }
            ),
            401: openapi.Response(
                description="Unauthorized", examples={
                    "application/json": {
                        "detail": "Учетные данные не были предоставлены."
                }}
            ),
            404: openapi.Response(
                description="Room not found", examples={
                    "application/json": {
                        "detail": "Комната с таким кодом не найдена"
                    }
                }
            )
        }
    )
    def post(self, request):
        """
        Join a room
        :param request: the request object
        :return: the response object
        """

        room_code = request.data.get('room_code')
        if not room_code:
            return Response({'detail': 'Поле room_code обязательно в запросе'}, status=400)

        user = request.user
        try:
            room = Room.objects.get(room_code=room_code)
        except Room.DoesNotExist:
            return Response({'detail': 'Комната с таким кодом не найдена'}, status=404)

        room.participants.add(user)
        room.save()
        return Response({'isCreator': room.creator == user,'isChatStarted': room.isChatStarted}, status=200)