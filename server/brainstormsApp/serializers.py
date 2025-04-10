from rest_framework import serializers
from .models import Brainstorm

class BrainstormSerializer(serializers.ModelSerializer):
    """
    This class is used to serialize the Brainstorm model
    """

    class Meta:
        model = Brainstorm
        fields = ['id', 'title', 'completionDate', 'details']

    def to_representation(self, instance):
        representation = super().to_representation(instance)
        representation['participants'] = ', '.join([user.username for user in instance.participants.all()])
        representation['isCreator'] = instance.creator == self.context['request'].user # Useful for displaying data in the client app
        return representation
