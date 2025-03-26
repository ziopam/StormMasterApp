from django.urls import path, include, re_path
from rest_framework import permissions

from drf_yasg.views import get_schema_view
from drf_yasg import openapi

# Setting up the schema view for the API documentation
schema_view = get_schema_view(
   openapi.Info(
      title="StormMaster API",
      default_version='v1',
      description="API for the StormMaster project",
      terms_of_service="https://www.google.com/policies/terms/",
      contact=openapi.Contact(email="contact@snippets.local"),
      license=openapi.License(name=""),
   ),
   public=True,
   permission_classes=[permissions.AllowAny,],
)

# URL patterns for the API
urlpatterns = [
    path('api/auth/', include('customUser.auth_urls')),
    path('api/', include('brainstormsApp.brainstorm_urls')),
    path('api/rooms/', include('roomApp.rooms_urls')),
    path('doc/', schema_view.with_ui('redoc', cache_timeout=0), name='schema-redoc'),
]
