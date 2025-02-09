from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework.views import APIView

from brainstormsApp.brainstorm_schema import get_brainstorms_schema, delete_brainstorm_schema
from brainstormsApp.models import Brainstorm
from brainstormsApp.permissions import IsOwnerOrAdmin
from brainstormsApp.serializers import BrainstormSerializer


class GetUserBrainstormsView(APIView):
    """
    This class is used to get all brainstorms that the user has participated in. The request must be authorized.
    """

    permission_classes = [IsAuthenticated]

    # Add information about the endpoint to the documentation
    @get_brainstorms_schema
    def get(self, request, *args, **kwargs):
        """
        Get all brainstorms that the user has participated in. Token required.
        """

        user = request.user
        brainstorms = user.participated_brainstorms.all()
        serializer = BrainstormSerializer(brainstorms, many=True, context={'request': request})
        return Response(serializer.data)

class DeleteUserBrainstormView(APIView):
    """
    This class is used to delete a brainstorm by its ID. The request must be authorized and be done by the creator of
    brainstorm or admin.
    """

    permission_classes = [IsOwnerOrAdmin]

    # Add information about the endpoint to the documentation
    @delete_brainstorm_schema
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

        # Delete the brainstorm
        brainstorm.delete()
        return Response({'detail': 'ok'})