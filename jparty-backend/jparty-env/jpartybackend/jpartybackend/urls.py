from django.contrib import admin
from django.urls import path
from jpartyapp import endpoints

urlpatterns = [
    path('admin/', admin.site.urls),
    path('user/session', endpoints.sessions), 
    path('user', endpoints.user),
    path('events', endpoints.events),
    path('event/<int:id>', endpoints.event_id),
    path('user/assistevents', endpoints.userAssistEvents),
    path('user/assistevent/<int:id>', endpoints.userAssistEvent_id),
    path('user/likedevents', endpoints.userLikedEvents),
    path('user/likedevent/<int:id>', endpoints.userLikedEvent_id),
    path('userpreferences', endpoints.userPreferences),
]
