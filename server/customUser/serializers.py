from djoser.serializers import UserCreateSerializer as BaseUserCreateSerializer
from rest_framework.authtoken.models import Token

class UserCreateSerializer(BaseUserCreateSerializer):
    """
    This class is used to create a custom user serializer. It extends the BaseUserCreateSerializer class of Djoser and
    changes the fields that are returned in the response.
    """

    class Meta(BaseUserCreateSerializer.Meta):
        fields = BaseUserCreateSerializer.Meta.fields

    def to_representation(self, instance):
        # So, user don't have to send a new request to get the token. He can get it in the response after the registration.
        token, _ = Token.objects.get_or_create(user=instance)
        return {"token": token.key}
