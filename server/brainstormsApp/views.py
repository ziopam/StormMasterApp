from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework.views import APIView

from brainstormsApp.serializers import BrainstormSerializer


# Create your views here.
class GetUserBrainstormsView(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request, *args, **kwargs):
        user = request.user
        brainstorms = user.participated_brainstorms.all()
        serializer = BrainstormSerializer(brainstorms, many=True, context={'request': request})
        return Response(serializer.data)