import re

from django.core.exceptions import ValidationError


class PasswordLengthValidator:
    """
    This class is used to validate the length of the password. It checks if the length of the password is less than 20.
    """

    def validate(self, password, user=None):
        """
        This function is used to validate the password. It checks if the length of the password is less than 20.
        :param password: password to validate
        :param user: user object
        :exception ValidationError: if the length of the password is more than 20 characters.
        """

        if len(password) > 20:
            raise ValidationError(
                "Пароль не должен превышать 20 символов.",
                code='password_too_long',
            )

    def get_help_text(self):
        """
        This function is used to return the help text for the password length validation.
        :return: the help text for the password length validation.
        """

        return "Ваш пароль не должен превышать 20 символов."

class PasswordCharacterValidator:
    """
    This class is used to validate the characters of the password. It checks if the password contains only letters, digits,
    and special characters.
    """

    def validate(self, password, user=None):
        """
        This function is used to validate the characters of the password. It checks if the password contains only letters,
        digits, and special characters.
        :param password: password to validate
        :param user: user object
        :exception ValidationError: if the password contains characters other than letters, digits, and special characters.
        """

        allowed_characters = r'^[a-zA-Z0-9!@#$%^&*()_\-+=;:,.?/\\|`~\[\]{}]*$'
        if not re.match(allowed_characters, password):
            raise ValidationError(
                "Пароль может содержать только буквы, цифры и специальные символы: !@#$%^&*()_\-+=;:,.?/\\|`~\[\]{}]*$",
                code='password_invalid_characters',
            )

    def get_help_text(self):
        """
        This function is used to return the help text for the password character validation.
        :return: the help text for the password character validation.
        """

        return (
            "Ваш пароль может содержать только латинские символы, арабские цифры и специальные символы: "
            "!@#$%^&*()-_+=;:,./?\\|`~[]{}."
        )