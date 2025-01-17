from django.urls import path
from brainstormsApp.views import GetUserBrainstormsView, DeleteUserBrainstormView

app_name = 'brainstorms'

urlpatterns = [
    path('get_user_brainstorms/', GetUserBrainstormsView.as_view()),
    path('delete_user_brainstorm/<int:pk>/', DeleteUserBrainstormView.as_view()),
]