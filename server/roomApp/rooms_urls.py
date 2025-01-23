from django.urls import path

from roomApp.views import CreateRoomView, JoinRoomView, LeaveRoomView

app_name = 'rooms'

# The URL patterns for the room app
urlpatterns = [
    path('create/', CreateRoomView.as_view(), name='create_room'),
    path('join/', JoinRoomView.as_view(), name='join_room'),
    path('leave/', LeaveRoomView.as_view(), name='leave_room'),
]