from django.contrib.auth.models import AnonymousUser
from rest_framework.authtoken.models import Token
from channels.db import database_sync_to_async
from channels.middleware import BaseMiddleware

@database_sync_to_async
def get_user(token_key):
    """
    This function is used to get the user from the token key
    :param token_key: The token key
    :return: The user object that is associated with the token key
    """
    try:
        token = Token.objects.get(key=token_key)
        return token.user
    except Token.DoesNotExist:
        return AnonymousUser()

class TokenAuthMiddleware(BaseMiddleware):
    """
    This middleware is used to authenticate the user using the token key
    """

    def __init__(self, inner):
        super().__init__(inner)

    async def __call__(self, scope, receive, send):
        headers = dict(scope['headers'])
        auth_header = headers.get(b'authorization', None)

        if auth_header:
            token_key = auth_header.decode().replace("Token ", "")
            scope['user'] = await get_user(token_key)
        else:
            scope['user'] = AnonymousUser()
        return await super().__call__(scope, receive, send)