from django.urls import path
from djoser.views import UserViewSet, TokenCreateView, TokenDestroyView

from customUser.views import CustomTokenCreateView

app_name = 'auth'

urlpatterns = [
    path('users/', UserViewSet.as_view({'post': 'create'}), name='user_create'),
    path('users/set_password/', UserViewSet.as_view({'post': 'set_password'}), name='set_password'),
    path('token/login/', CustomTokenCreateView.as_view(), name='token_create'),
    path('token/logout/', TokenDestroyView.as_view(), name='token_destroy'),
]