from django.core.exceptions import PermissionDenied
from .models import UserSession

def authenticate_user(request):
    session_token = request.headers.get('SessionToken', None)
    if session_token is None:
        raise PermissionDenied('Unauthorized')
    try:
        user_session = UserSession.objects.get(token=session_token)
    except UserSession.DoesNotExist:
        raise PermissionDenied('Unauthorized')
    return user_session

def authenticate_manager(request):
    session_token = request.headers.get('SessionToken', None)
    if session_token is None:
        raise PermissionDenied('Unauthorized')
    try:
        user_session = UserSession.objects.get(token=session_token)
        if not user_session.user.manager:
            raise PermissionDenied('Unauthorized')
    except UserSession.DoesNotExist:
        raise PermissionDenied('Unauthorized')
    return user_session

# Comprobaciones de formato
def check_email_format(email):
    if email is None or '@' not in email:
        return False
    return True

def check_password_format(password):
    if len(password) < 8:
        return False
    return True

def check_image_format(image):
    if image is not None and ('http://' not in image and 'https://' not in image) and (
            '.jpg' not in image and '.png' not in image and '.webp' not in image):
        return False
    return True

def check_link_format(link):
    if link is not None and ('http://' not in link and 'https://' not in link) and (
            '.com' not in link and '.es' not in link and '.org' not in link):
        return False
    return True