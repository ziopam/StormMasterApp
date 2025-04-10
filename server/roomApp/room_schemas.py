from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema

create_room_schema = swagger_auto_schema(
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
                    pattern=r'^[a-zA-Z-0-9а-яA-я\s]+$'),
                'room_type': openapi.Schema(
                    type=openapi.TYPE_INTEGER,
                    description="The type of the room. 1 - Classic, 2 - Round Robin",
                    enum=[1, 2],
                    default=1)
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

join_room_schema = swagger_auto_schema(operation_id="join_room",
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
                        'participants': openapi.Schema(type=openapi.TYPE_STRING),
                        'participants_amount': openapi.Schema(type=openapi.TYPE_INTEGER),
                        'isChatStarted': openapi.Schema(type=openapi.TYPE_BOOLEAN)
                    }
                ),
                examples={
                    "application/json": {
                        "isCreator": True,
                        "participants": "user1, user2",
                        "participants_amount": 2,
                        "isChatStarted": False
                    }
                }
            ),
            400: openapi.Response(
                description="- Error in the room_code field\n - User that's not in room tries to join it during Round Robin step", examples={
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

leave_room_schema = swagger_auto_schema(
        operation_id="leave_room",
        operation_description="Use this endpoint to leave a room. The request must be authorized. The creator of room is "
                              "not allowed to leave a room. Only delete it. User can't also leave a room which is in Round Robin's progress.",
        tags=['rooms'],
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'room_code': openapi.Schema(type=openapi.TYPE_STRING)
            },
            required=['room_code']
        ),
        responses={
            200: openapi.Response(description="OK"),
            400: openapi.Response(
                description="- Error in the room_code field\n - User is the creator of room. They can't leave it, only delete it"
                            "\n - User tries to leave a room which is in Round Robin progress.",
                examples={
                    "application/json": {
                        "detail": "Поле room_code обязательно в запросе"
                    }
                }
            ),
            401: openapi.Response(
                description="Unauthorized", examples={
                    "application/json": {
                        "detail": "Учетные данные не были предоставлены."
                    }
                }
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

""" CREATOR AND ADMIN ONLY """

# Responses for the creator and admin (to avoid code duplication)
creator_responses = {
            200: openapi.Response(description="OK"),
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
                    }
                }
            ),
            403: openapi.Response(
                description="User is not the creator of the room and not the admin", examples={
                    "application/json": {
                        'detail': 'У вас недостаточно прав для выполнения данного действия'
                    }
                }
            ),
            404: openapi.Response(
                description="Room not found", examples={
                    "application/json": {
                        "detail": "Комната с таким кодом не найдена"
                    }
                }
            )
        }

start_brainstorm_schema = swagger_auto_schema(
        operation_id="start_brainstorm",
        operation_description="Use this endpoint to start a brainstorm in a room. The request must be authorized. Only the"
                              " creator of the room and admin can start a brainstorm. If the chat is already started, does not"
                              " do anything and returns 200 code response.",
        tags=['rooms'],
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'room_code': openapi.Schema(type=openapi.TYPE_STRING),
                'details': openapi.Schema(type=openapi.TYPE_STRING, description="(optional)")
            },
            required=['room_code']
        ),
        responses=creator_responses
)

delete_room_schema = swagger_auto_schema(
        operation_id="delete_room",
        operation_description="Use this endpoint to delete a room. The request must be authorized. Only the creator of "
                              "the room and admin can delete it.",
        tags=['rooms'],
        manual_parameters=[
            openapi.Parameter(
                'room_code',
                openapi.IN_PATH,
                type=openapi.TYPE_STRING,
                required=True
            )
        ],
        responses=creator_responses
    )

finish_brainstorm_schema = swagger_auto_schema(
    operation_id="finish_brainstorm",
    operation_description="Use this endpoint to finish a brainstorm in a room. The request must be authorized. Only the"
                          " creator of the room and admin can finish a brainstorm.",
    tags=['rooms'],
    request_body=openapi.Schema(
        type=openapi.TYPE_OBJECT,
        properties={
            'room_code': openapi.Schema(type=openapi.TYPE_STRING)
        },
        required=['room_code']
    ),
    responses=creator_responses
)