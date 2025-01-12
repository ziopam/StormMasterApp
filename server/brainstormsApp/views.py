from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework.views import APIView

from brainstormsApp.models import Brainstorm
from brainstormsApp.permissions import IsOwnerOrAdmin
from brainstormsApp.serializers import BrainstormSerializer


class GetUserBrainstormsView(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request, *args, **kwargs):
        user = request.user
        brainstorms = user.participated_brainstorms.all()
        serializer = BrainstormSerializer(brainstorms, many=True, context={'request': request})
        return Response(serializer.data)

class DeleteUserBrainstormView(APIView):
    permission_classes = [IsOwnerOrAdmin]

    def post(self, request, *args, **kwargs):
        brainstorm_id = kwargs.get('pk')
        try:
            brainstorm = Brainstorm.objects.get(id=brainstorm_id)
        except Brainstorm.DoesNotExist:
            return Response({'detail': 'Отсутствует мозговой штурм с данным id'}, status=404)

        self.check_object_permissions(request, brainstorm)

        if brainstorm.isInProgress:
            return Response({'detail': 'Мозговой штурм находится в процессе'}, status=400)

        brainstorm.delete()
        return Response({'detail': 'ok'})