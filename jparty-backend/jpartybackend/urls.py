from django.contrib import admin
from django.urls import path
from jpartyapp import event_endpoints, user_endpoints

urlpatterns = [
    path('admin/', admin.site.urls),
    path('user/session', user_endpoints.sessions), 
    path('user', user_endpoints.user), 
    path('user/preferences', user_endpoints.userPreferences),
    path('events', event_endpoints.events),
    path('event/<int:id>', event_endpoints.event_id),
    path('user/assistevents', event_endpoints.userAssistEvents),
    path('user/assistevent/<int:id>', event_endpoints.userAssistEvent_id),
    path('user/likedevents', event_endpoints.userLikedEvents),
    path('user/likedevent/<int:id>', event_endpoints.userLikedEvent_id),
]

