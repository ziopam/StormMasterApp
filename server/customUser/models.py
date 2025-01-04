import re

from django.db import models

from django.contrib.auth.models import AbstractUser
from django.core.exceptions import ValidationError
from django.core.validators import RegexValidator
from django.db import models

def validate_username(value):
    if len(value) < 4:
        raise ValidationError('Длина имени пользователя должна быть не менее 4 символов.')
    if User.objects.filter(username=value).exists():
        raise ValidationError('Пользователь с таким никнеймом уже существует.')


class User(AbstractUser):
    username = models.CharField(
        max_length=12,
        unique=True,
        null=False,
        blank=False,
        validators=[
            RegexValidator(
                regex='^[a-zA-Z0-9_]+$',
                message='Имя пользователя может состоять только из латинских букв, цифр и символа подчеркивания.',
            ),
            validate_username
        ]
    )



