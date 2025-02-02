import djoser.views
from djoser.views import TokenCreateView, TokenDestroyView
from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema

# I create custom views ONLY to write docs for them. The logic of these views is up to djoser.

class CustomTokenCreateView(TokenCreateView):
    """
    This class is used to create a custom token creation view. It extends the TokenCreateView class of Djoser and adds
    documentation to the endpoint.
    """

    # Add information about the endpoint to the documentation
    @swagger_auto_schema(
        tags=["auth"],
        operation_id="create_token_login",
        operation_description="Use this endpoint to get or create token for user authorisation.",
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            required=['username', 'password'],
            properties={
                'username': openapi.Schema(type=openapi.TYPE_STRING),
                'password': openapi.Schema(type=openapi.TYPE_STRING),
            },
        ),
        responses={
            200: openapi.Response(description="OK", examples={
                "application/json": {
                    "auth_token": "YOUR_TOKEN_HERE"}
            }),
            400: openapi.Response(description="Wrong username/password combination", examples={
                "application/json": {
                    "non_field_errors": [
                        "Невозможно войти с предоставленными учетными данными."
                    ]
                }
            }),
        },
        security=[]
    )

    # Process the POST request (using the djoser class method)
    def post(self, request, *args, **kwargs):
        return super().post(request, *args, **kwargs)


class CustomTokenDeleteView(TokenDestroyView):
    """
    This class is used to create a custom token deletion view. It extends the TokenDestroyView class of Djoser and adds
    documentation to the endpoint.
    """

    # Add information about the endpoint to the documentation
    @swagger_auto_schema(
        tags=["auth"],
        operation_id="delete_token_logout",
        operation_description="Use this endpoint to delete token for user authorisation. The request must be authorized.",
        responses={
            204: openapi.Response(description="OK"),
            401: openapi.Response(description="Unauthorized", examples={
                "application/json": {
                    "detail": "Учетные данные не были предоставлены."
                }
            }),
        })

    # Process the DELETE request (using the djoser class method)
    def post(self, request):
        return super().post(request)

class CustomUserView(djoser.views.UserViewSet):
    """
    This class is used to create a custom user view. It extends the UserViewSet class of Djoser and adds documentation to
    the endpoints.
    """

    # Add information about the endpoint to the documentation
    @swagger_auto_schema(
        tags=['auth'],
        operation_id="create_user",
        operation_description="Use this endpoint to create a new user. The request must be authorized.",
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            required=['username', 'password'],
            properties={
                'username': openapi.Schema(
                    type=openapi.TYPE_STRING,
                    description="A unique username. Must be 4-12 characters long, containing only letters, digits or underscores.",
                    min_length=4,
                    max_length=12,
                    pattern=r'^[a-zA-Z0-9_]+$',
                ),
                'password': openapi.Schema(
                    type=openapi.TYPE_STRING,
                    description="A password. Must be 8-20 characters long, containing only letters, digits and special characters.",
                    min_length=8,
                    max_length=20,
                    pattern=r'^[a-zA-Z0-9!@#$%^&*()_\-+=;:,.?/\\|`~\[\]{}]+$',
                ),
            },
        ),
        responses={
            201: openapi.Response(description="OK", examples={
                "application/json": {
                    "token": "YOUR_TOKEN_HERE"
                }}),
            400: openapi.Response(description="Failed to create user with provided data", examples={
                'application/json': {
                    'username': [
                        'Пользователь с таким именем уже существует.'
                    ],
                }
            }),
        },
        security=[]
    )
    def create(self, request, *args, **kwargs):
        return super().create(request, *args, **kwargs)

    @swagger_auto_schema(
        tags=['auth'],
        operation_id="set_user_password",
        operation_description="Use this endpoint to set a new password for the user. The request must be authorized.",
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            required=['current_password', 'new_password'],
            properties={
                'current_password': openapi.Schema(
                    type=openapi.TYPE_STRING,
                    description="The current password of the user.",
                ),
                'new_password': openapi.Schema(
                    type=openapi.TYPE_STRING,
                    description=(
                            "The new password. Must be 8-20 characters long, containing only letters, digits and special characters."
                    ),
                    min_length=8,
                    max_length=20,
                    pattern=r'^[a-zA-Z0-9!@#$%^&*()_\-+=;:,.?/\\|`~\[\]{}]+$',
                ),
            },
        ),
        responses={
            200: openapi.Response(description="OK"),
            400: openapi.Response(
                description="Invalid input data",
                examples={
                    "application/json": {
                        "current_password": [
                            "Неправильный пароль."
                        ],
                    }
                },
            ),
            401: openapi.Response(
                description="Unauthorized",
                examples={
                    "application/json": {
                        "detail": "Учетные данные не были предоставлены."
                    }
                },
            ),
        },
    )
    def set_password(self, request, *args, **kwargs):
        return super().set_password(request, *args, **kwargs)
