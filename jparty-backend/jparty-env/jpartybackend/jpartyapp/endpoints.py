from django.http import JsonResponse
import secrets
import bcrypt
from django.utils import timezone
from django.core.exceptions import PermissionDenied
from django.views.decorators.csrf import csrf_exempt
from .models import User,UserSession, UserPreferences, UserLikes, UserAssist, MusicGenre, Events
import json
from datetime import datetime

# Metodo de autenticación del sessionToken de usuario
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

##TESTEADO
@csrf_exempt
def sessions(request):
    if request.method == 'POST': 
        print(request.body)
        try:
            data = json.loads(request.body)
            client_email = data['email']
            client_password = data['password']
        except KeyError:
            return JsonResponse({"response": "not_ok"}, status=400)
        try:
            user = User.objects.get(email=client_email)
        except User.DoesNotExist:
            return JsonResponse({"response": "Email no existente"}, status=404)
        if bcrypt.checkpw(client_password.encode('utf8'), user.password.encode('utf8')):
            # json_password y user.encrypted_password coinciden
            if 'SessionToken' in request.headers and request.headers['SessionToken'] is not None:
                try:
                    user_session = authenticate_user(request)
                    return JsonResponse({"response": "ok", "SessionToken": user_session.token}, status=200)
                except PermissionDenied:
                    return JsonResponse({"response": "Unauthorized"}, status=401)
            else:
                random_token = secrets.token_hex(10)
                session = UserSession(user=user, token=random_token)
                session.save()
                return JsonResponse({"response": "ok", "SessionToken": random_token}, status=201)
        else:
            # No coinciden
            return JsonResponse({"response": "Unauthorized"}, status=401)
    elif request.method == 'DELETE': 
        try:
            user_session = authenticate_user(request)
        except PermissionDenied:
            return JsonResponse({'response': 'Unauthorized'}, status=401)
        user_session.delete()
        return JsonResponse({"response": "Session Closed"}, status=200)
    else:
        return JsonResponse({"response": "Method Not Allowed"}, status=405)


# TESTEADO
@csrf_exempt
def user(request):
    # Registrar un usuario 
    if request.method == 'POST':  
        try:
            data = json.loads(request.body)
            client_username = data['username']
            client_email = data['email']
            client_province = data['province']
            client_password = data['password']
            client_birthdate = data['birthdate']
        except KeyError:
            return JsonResponse({"response": "not_ok"}, status=400)
        if not check_email_format(client_email):
            return JsonResponse({"response": "not_ok"}, status=400)

        try:
            User.objects.get(email=client_email)
            return JsonResponse({"response": "already_exist"}, status=409)
        except:
            pass
        birthdate = datetime.strptime(client_birthdate, '%d-%m-%Y')
        formatted_birthdate = birthdate.strftime('%Y-%m-%d')
        salted_and_hashed_pass = bcrypt.hashpw(client_password.encode('utf8'), bcrypt.gensalt()).decode('utf8')
        new_user = User(username=client_username, email=client_email, password=salted_and_hashed_pass, province=client_province,
                        birthdate=formatted_birthdate, manager=False)
        new_user.save()
        # Crear una sesión inicial
        random_token = secrets.token_hex(10)
        session = UserSession(user=new_user, token=random_token)
        session.save()
        return JsonResponse({"response": "ok", "SessionToken": random_token}, status=201)
    # Conseguir un usuario
    elif request.method == 'GET':
        try:
            user_session = authenticate_user(request)
            user_id = user_session.user.id
            user_data = User.objects.get(id=user_id)
            json_response = user_data.to_json()
            return JsonResponse(json_response, safe=False, status=200)
        except PermissionDenied:
            return JsonResponse({'error': 'Unauthorized'}, status=401)
    elif request.method == 'DELETE':
        try:
            user_session = authenticate_user(request)
        except PermissionDenied:
            return JsonResponse({'error': 'Unauthorized'}, status=401)
        try:
            user = User.objects.get(id=user_session.user.id)
            user.delete()
            return JsonResponse({'response': 'ok'}, status=200)
        except User.DoesNotExist:
            return JsonResponse({'response': 'not_ok'}, status=404)
    else:
        return JsonResponse({"response": "Method Not Allowed"}, status=405)


@csrf_exempt
def events(request):
    if request.method == 'POST': #TESTEADO
        try:
            #AUTENTICAMOS AL MANAGER
            user_session = authenticate_manager(request)
        except PermissionDenied:
            return JsonResponse({'error': 'Unauthorized'}, status=401)
        try:
            data = json.loads(request.body)
            title = data['title']
            street = data['street']
            province = data['province']
            music_genre = data['music_genre']
            music_genre = MusicGenre.objects.get(id=music_genre)
            price = data['price']
            secretkey = data['secretkey']
            link = data['link']
            date = data['date']
            time = data['time']
            if len(time.split(':')) == 2:
                time += ':00'
            date = datetime.strptime(f'{date} {time}', '%Y-%m-%d %H:%M:%S')###CORREGIR
            image = data['image']
            description = data['description']
        except KeyError:
            return JsonResponse({"response": "not_ok"}, status=400)
        event = Events(manager=user_session.user, title=title,street=street, province=province, music_genre=music_genre, price=price,secretkey=secretkey, link=link, date=date.isoformat(),image=image, description=description, )
        event.save()
        return JsonResponse({'message': 'Event created'}, status=201)
    else:
        return JsonResponse({'message': 'Method not allowed'}, status=405)