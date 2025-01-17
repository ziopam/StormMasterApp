from djoser.views import TokenCreateView
from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema

# I create custom views ONLY to write docs for them. The logic of these views is up to djoser.

class CustomTokenCreateView(TokenCreateView):

    # Add information about the endpoint to the documentation
    @swagger_auto_schema(
        tags=["auth"],
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
            200: openapi.Response(description="OK"),
            400: openapi.Response(description="Wrong username/password combination"),
        }
    )
    def post(self, request, *args, **kwargs):
        return super().post(request, *args, **kwargs)
