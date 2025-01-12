from djoser.serializers import UserCreateSerializer as BaseUserCreateSerializer
from rest_framework.authtoken.models import Token

class UserCreateSerializer(BaseUserCreateSerializer):
    class Meta(BaseUserCreateSerializer.Meta):
        fields = BaseUserCreateSerializer.Meta.fields

    def to_representation(self, instance):
        token, _ = Token.objects.get_or_create(user=instance)
        return {"token": token.key}
