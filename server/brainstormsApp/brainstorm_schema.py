from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema

from roomApp.room_schemas import delete_room_schema

get_brainstorms_schema = swagger_auto_schema(
        operation_id="get_user_brainstorms",
        operation_description="Use this endpoint to get all brainstorms that the user has participated in. The request "
                              "must be authorized.",
        tags=['brainstorms'],
        responses={
            200: openapi.Response(
                description="OK",
                schema=openapi.Schema(
                    type=openapi.TYPE_ARRAY,
                    items=openapi.Schema(
                        type=openapi.TYPE_OBJECT,
                        properties={
                            "id": openapi.Schema(type=openapi.TYPE_INTEGER, description="ID of the brainstorm"),
                            "title": openapi.Schema(type=openapi.TYPE_STRING, description="Title of the brainstorm"),
                            "completionDate": openapi.Schema(type=openapi.TYPE_STRING, format="date",
                                                             description="Completion date"),
                            "details": openapi.Schema(type=openapi.TYPE_STRING,
                                                      description="Details of the brainstorm"),
                            "participants": openapi.Schema(
                                type=openapi.TYPE_STRING,
                                description="Comma-separated usernames of participants"
                            ),
                            "isCreator": openapi.Schema(type=openapi.TYPE_BOOLEAN,
                                                        description="Whether the current user is the creator"),
                        }
                    ),
                ),
            ),
            401: openapi.Response(
                description="Unauthorized", examples={
                    "application/json": {
                        "detail": "Учетные данные не были предоставлены."
                    }
                },
            ),
        }
    )

delete_brainstorm_schema = swagger_auto_schema(
        operation_description="Use this endpoint to delete a brainstorm by its ID. The request must be authorized and be "
                              "done by the creator of brainstorm or admin.",
        operation_id="delete_user_brainstorm",
        tags=['brainstorms'],
        manual_parameters=[
            openapi.Parameter(
                'id',
                openapi.IN_PATH,
                type=openapi.TYPE_INTEGER,
                required=True
            )
        ],
        responses={
            200: openapi.Response(description="OK", examples={
                    "application/json": {
                        "detail": "ok"
                    }}),
            401: openapi.Response(
                description="Unauthorized", examples={
                    "application/json": {
                        "detail": "Учетные данные не были предоставлены."
                    }
                },
            ),
            403: openapi.Response(
                description="User is not the creator of the brainstorm and not the admin", examples={
                                  "application/json": {
                                      'detail' : 'У вас недостаточно прав для выполнения данного действия'
                                  }}
            ),
            404: openapi.Response(description="Brainstorm with the given ID does not exist", examples={
                                  "application/json": {
                                      'detail' : 'Отсутствует мозговой штурм с данным id'
                                  }})
        },
    )