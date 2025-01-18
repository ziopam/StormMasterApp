from django.apps import AppConfig


class CustomUserConfig(AppConfig):
    """
    This class is used to configure the Django app name
    """

    default_auto_field = 'django.db.models.BigAutoField'
    name = 'customUser'
