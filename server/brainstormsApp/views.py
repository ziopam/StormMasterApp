from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework.views import APIView

from brainstormsApp.models import Brainstorm
from brainstormsApp.permissions import IsOwnerOrAdmin
from brainstormsApp.serializers import BrainstormSerializer


class GetUserBrainstormsView(APIView):
    permission_classes = [IsAuthenticated]

    # Add information about the endpoint to the documentation
    @swagger_auto_schema(
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
    def get(self, request, *args, **kwargs):
        """
        Get all brainstorms that the user has participated in. Token required.
        """
        user = request.user
        brainstorms = user.participated_brainstorms.all()
        serializer = BrainstormSerializer(brainstorms, many=True, context={'request': request})
        return Response(serializer.data)

class DeleteUserBrainstormView(APIView):
    permission_classes = [IsOwnerOrAdmin]

    # Add information about the endpoint to the documentation
    @swagger_auto_schema(
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
            400: openapi.Response(description="Brainstorm is in progress", examples={
                                  "application/json": {
                                      'detail' : 'Мозговой штурм находится в процессе'
                                  }
            }),
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
    def delete(self, request, *args, **kwargs):
        """
        Delete a brainstorm by its ID. Token required. User must be the creator of the brainstorm or admin.
        """

        # Get the brainstorm by its ID
        brainstorm_id = kwargs.get('pk')
        try:
            brainstorm = Brainstorm.objects.get(id=brainstorm_id)
        except Brainstorm.DoesNotExist:
            return Response({'detail': 'Отсутствует мозговой штурм с данным id'}, status=404)

        # Check if user is the creator of the brainstorm or admin
        self.check_object_permissions(request, brainstorm)

        # Check if brainstorm is in progress to avoid deletion
        if brainstorm.isInProgress:
            return Response({'detail': 'Мозговой штурм находится в процессе'}, status=400)

        # Delete the brainstorm
        brainstorm.delete()
        return Response({'detail': 'ok'})