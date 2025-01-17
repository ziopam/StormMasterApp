from django.urls import path
from djoser.views import UserViewSet

from customUser.views import CustomTokenCreateView, CustomTokenDeleteView, CustomUserView

app_name = 'auth'

urlpatterns = [
    path('users/', CustomUserView.as_view({'post': 'create'}), name='user_create'),
    path('users/set_password/', CustomUserView.as_view({'post': 'set_password'}), name='set_password'),
    path('token/login/', CustomTokenCreateView.as_view(), name='token_create'),
    path('token/logout/', CustomTokenDeleteView.as_view(), name='token_destroy'),
]