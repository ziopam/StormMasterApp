import re

from django.core.exceptions import ValidationError
from django.db import models, IntegrityError
from customUser.models import User
import random
import string

def generate_room_code():
    """
    This function is used to generate a random room code
    :return: a random room code
    """

    room_code = ''.join(random.choices(string.ascii_uppercase, k=6))
    return room_code

def validate_title(value):
    """
    This function is used to validate the title of the room. It checks if the title is empty or not.
    :param value: the title to validate
    :exception ValidationError: if the title is empty
    """
    if not value or value.isspace():
        raise ValidationError('Заголовок не может быть пустым')
    if len(value) > 25:
        raise ValidationError('Длина заголовка не должна превышать 25 символов')
    if not re.match(r'^[a-zA-Z-0-9а-яA-я\s]+$', value):
        raise ValidationError('Заголовок может содержать только буквы, цифры и пробелы')

class Room(models.Model):
    """
    This class is used to define the Room model
    """

    title = models.CharField(max_length=25, null=False, blank=False, validators=[validate_title])
    room_code = models.CharField(max_length=6, default=generate_room_code, unique=True)
    creator = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name="created_rooms"
    )
    participants = models.ManyToManyField(
        User,
        related_name="participated_rooms"
    )
    details = models.TextField(null=False, blank=True)
    isChatStarted = models.BooleanField(default=False)

    def save(self, *args, **kwargs):
        """
        This function is used to save the Room object. Does the same as usual save, but generates a unique room code
        avoiding conflicts
        """

        while True:
            try:
                if not self.room_code: # Generate code if it is not set
                    self.room_code = generate_room_code()
                super().save(*args, **kwargs) # Try to save. Can raise IntegrityError (code conflict)
                break
            except IntegrityError:
                self.room_code = generate_room_code()  # Regenerate code and try to save again


class Idea(models.Model):
    """
    This class is used to define the Idea model
    """

    idea_number = models.IntegerField(unique=True)
    room_code = models.CharField(blank=False, null=False, max_length=6, default="_")
    votes = models.IntegerField(default=0)
    voters = models.ManyToManyField(User, blank=True, related_name='voted_ideas')

    def update_votes(self):
        """
        This function is used to update the number of votes for the idea
        """

        self.votes = self.voters.count()
        self.save()

class Message(models.Model):
    """
    This class is used to define the Message model
    """

    room = models.ForeignKey(
        Room,
        on_delete=models.CASCADE,
        related_name="messages"
    )
    sender = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name="messages"
    )
    text = models.TextField(null=False, blank=False)
    timestamp = models.DateTimeField(auto_now_add=True)
    idea = models.ForeignKey(
        Idea,
        on_delete=models.CASCADE,
        null=True,
        blank=True
    )