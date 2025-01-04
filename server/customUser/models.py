import re

from django.db import models

from django.contrib.auth.models import AbstractUser
from django.core.exceptions import ValidationError
from django.core.validators import RegexValidator
from django.db import models

def validate_username(value):
    if len(value) < 4:
        raise ValidationError('Username must be at least 4 characters long')


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


class PasswordLengthValidator:
    def validate(self, password, user=None):
        if len(password) > 20:
            raise ValidationError(
                "Пароль не должен превышать 20 символов.",
                code='password_too_long',
            )

    def get_help_text(self):
        return "Ваш пароль не должен превышать 20 символов."

class PasswordCharacterValidator:
    def validate(self, password, user=None):
        allowed_characters = r'^[a-zA-Z0-9!@#$%^&*()_\-+=;:,.?/\\|`~\[\]{}]*$'
        if not re.match(allowed_characters, password):
            raise ValidationError(
                "Пароль может содержать только буквы, цифры и специальные символы: !@#$%^&*()_\-+=;:,.?/\\|`~\[\]{}]*$",
                code='password_invalid_characters',
            )

    def get_help_text(self):
        return (
            "Your password may only contain letters, digits, and special characters: "
            "!@#$%^&*()-_+=;:,./?\\|`~[]{}."
        )
