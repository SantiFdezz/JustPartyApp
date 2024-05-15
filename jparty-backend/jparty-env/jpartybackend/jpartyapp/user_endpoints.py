from django.http import JsonResponse
import secrets
import bcrypt
from django.core.exceptions import PermissionDenied
from django.views.decorators.csrf import csrf_exempt
from .models import User,UserSession, MusicGenre, UserPreferences
import json
from datetime import datetime
from .common import authenticate_user, check_email_format, check_password_format



@csrf_exempt
def sessions(request):
    if request.method == 'POST': 
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

@csrf_exempt
def userManager(request):
    if request.method == 'PUT':
        try:
            user_session = authenticate_user(request)
        except PermissionDenied:
            return JsonResponse({'error': 'Unauthorized'}, status=401)
        try:
            user = User.objects.get(id=user_session.user.id)
        except User.DoesNotExist:
            return JsonResponse({'error': 'User not found'}, status=404)
        if user.manager == True:
            user.manager = False
        else:
            user.manager = True
        user.save()
        return JsonResponse({'response': 'ok'}, status=200)
    else:
        return JsonResponse({'message': 'Method not allowed'}, status=405)
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
        if not check_password_format(client_password):
            return JsonResponse({"response": "not_ok"}, status=400)
        try:
            User.objects.get(email=client_email)
            return JsonResponse({"response": "already_exist"}, status=409)
        except:
            pass
        salted_and_hashed_pass = bcrypt.hashpw(client_password.encode('utf8'), bcrypt.gensalt()).decode('utf8')
        new_user = User(username=client_username, email=client_email, password=salted_and_hashed_pass, province=client_province,
                        birthdate=client_birthdate, manager=False)
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
            user_data = User.objects.get(id=user_session.user.id)
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
    elif request.method == 'PUT':
        try:
            user_session = authenticate_user(request)
        except PermissionDenied:
            return JsonResponse({'error': 'Unauthorized'}, status=401)
        user = User.objects.get(id=user_session.user.id)
        try:
            data = json.loads(request.body)
            client_username = data['username']
            client_email = data['email']
            if not check_email_format(client_email):
                return JsonResponse({"response": "not_ok"}, status=400)
            client_province = data['province']
            client_birthdate = data['birthdate']
            client_password = data['password']
        except KeyError:
            return JsonResponse({"response": "not_ok"}, status=400)
        try:
            User.objects.get(email=client_email)
            if user.email != client_email:
                return JsonResponse({"response": "already_exist"}, status=409)
        except:
            pass
        if client_password is not None:
            if not check_password_format(client_password): 
                return JsonResponse({"response": "not_ok"}, status=400)
            user.password = bcrypt.hashpw(client_password.encode('utf8'), bcrypt.gensalt()).decode('utf8')
        birthdate = datetime.strptime(client_birthdate, '%Y-%m-%d')
        formatted_birthdate = birthdate.strftime('%Y-%m-%d')
        user.username = client_username
        user.email = client_email
        user.province = client_province
        user.birthdate = formatted_birthdate
        user.save()
        return JsonResponse({'message': 'User updated'}, status=200)
    else:
        return JsonResponse({"response": "Method Not Allowed"}, status=405)
    

@csrf_exempt    
def userPreferences(request):
    #TESTEADO
    if request.method == 'GET':
        try:
            #AUTENTICAMOS AL USUARIO
            user_session = authenticate_user(request)
        except PermissionDenied:
            return JsonResponse({'error': 'Unauthorized'}, status=401)
        #COGEMOS LOS GENEROS Y PASAMOS A LISTA LOS SELECCIONADOS POR ID
        musicGenre = MusicGenre.objects.all()
        preferences = UserPreferences.objects.filter(user=user_session.user).values_list('music_genre__id', flat=True)
        json_response = []
        for music in musicGenre:
            json_response.append({
                "id": music.id,
                "name": music.name,
                "image": music.image,
                "selected": music.id in preferences,
            })
            
        return JsonResponse(json_response, safe=False, status=200)
    elif request.method == 'PUT':
        try:
            #AUTENTICAMOS AL USUARIO
            user_session = authenticate_user(request)
        except PermissionDenied:
            return JsonResponse({'error': 'Unauthorized'}, status=401)
        data = json.loads(request.body)
        selected_ids = data.get('selected', [])
        UserPreferences.objects.filter(user=user_session.user).delete()
        for genre_id in selected_ids:
            genre = MusicGenre.objects.get(id=genre_id)
            UserPreferences.objects.create(user=user_session.user, music_genre=genre)
        return JsonResponse({'message': 'User preferences updated'}, status=200)
    else:
        return JsonResponse({'message': 'Method not allowed'}, status=405)
