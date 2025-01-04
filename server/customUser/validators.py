import re

from django.core.exceptions import ValidationError


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
            "Ваш пароль может содержать только латинские символы, арабские цифры и специальные символы: "
            "!@#$%^&*()-_+=;:,./?\\|`~[]{}."
        )