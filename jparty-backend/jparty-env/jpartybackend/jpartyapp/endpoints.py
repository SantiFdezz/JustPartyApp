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

def check_link_format(link):
    if link is not None and ('http://' not in link and 'https://' not in link) and (
            '.com' not in link and '.es' not in link and '.org' not in link):
        return False
    return True

##TESTEADO
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
        birthdate = datetime.strptime(client_birthdate, '%d-%m-%Y')
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
def events(request):
    if request.method == 'GET':
        try:
            #AUTENTICAMOS AL USUARIO
            user_session = authenticate_user(request)
        except PermissionDenied:
            return JsonResponse({'error': 'Unauthorized'}, status=401)
        
        sort_province = request.GET.get('province', None)  # Aquí recogemos el parámetro de consulta 'province' del Home Screen (para filtrar por provincia)
        order_by = request.GET.get('order_by', None)  # Aquí recogemos el parámetro de consulta 'order_by' del Own Events
        mine = request.GET.get('mine', None)  # Aquí recogemos el parámetro de consulta 'mine' del Own Events
        # Verificamos si los parámetros son válidos
        if (order_by is not None and order_by not in ['price', 'date'] ) or (mine is not None and mine != 'true'):
            return JsonResponse({'error': 'Invalid parameter'}, status=400)

        if (order_by is not None and mine is not None) or (mine is not None and (order_by is not None or sort_province is not None)):
            return JsonResponse({'error': 'Invalid parameter'}, status=400)
        #Comprobamos si el usuario hace la peticion desde HomeScreen o desde OwnEvents
        
        if mine is not None and mine.lower() == 'true':   
            # OWN EVENTS
            try:
                events = Events.objects.filter(manager=user_session.user,date__gte=timezone.now())
            except Events.DoesNotExist:
                return JsonResponse({'error': 'Events not found'}, status=404)
        else:
            # Recibimos las preferencias del usuario // HOME SCREEN
            try:
                user_genres = UserPreferences.objects.filter(user=user_session.user).values_list('music_genre__name', flat=True)
            except UserPreferences.DoesNotExist:
                user_genres = None

            # Recibimos el QuerySet de eventos
            try:
                if sort_province is not None:
                    if user_genres is not None:
                        # Filtramos por géneros de música y provincia elegida (Y fecha actual)
                        events = Events.objects.filter(music_genre__name__in=user_genres, province=sort_province, date__gte=timezone.now()) 
                    else:
                        # Filtramos por provincia elegida (Y fecha actual)
                        events = Events.objects.filter(province=sort_province, date__gte=timezone.now())
                else:
                    if user_genres is not None:
                        # Filtramos por géneros de música y provincia del usuario (Y fecha actual)
                        events = Events.objects.filter(music_genre__name__in=user_genres, province=user_session.user.province, date__gte=timezone.now())
                    else:
                        # Filtramos por provincia del usuario (Y fecha actual)
                        events = Events.objects.filter(province=user_session.user.province, date__gte=timezone.now())
            except Events.DoesNotExist:
                return JsonResponse({'error': 'Events not found'}, status=404)
            # Ordenamos los eventos recibidos
            if order_by is not None and 'date' in order_by:
                events = events.order_by('date') # Ordenamos por fecha ascendente
            elif order_by is not None and 'price' in order_by:
                events = events.order_by('price') # Ordenamos por precio ascendente
        #CREAMOS UN JSON CON LOS EVENTOS Y ATRIB.(ASISTENTES, LIKES, ETC)
        json_response = []
        for event in events:
            #if  (sort_by is not None and'secretkey' in sort_by and event.secretkey is not None) or (sort_by is not None 'secretkey' not in sort_by):
            assistants = UserAssist.objects.filter(event=event).count()
            try:
                userLiked = UserLikes.objects.get(user=user_session.user, event=event)
                userLiked = True
            except UserLikes.DoesNotExist:
                userLiked = False
            try:
                userAssist = UserAssist.objects.get(user=user_session.user, event=event)
                userAssist = True
            except UserAssist.DoesNotExist:
                userAssist = False            
            music_genre = MusicGenre.objects.get(id=event.music_genre.id)
            json_response.append({
                "id": event.id,
                "title": event.title,
                "province": event.province,
                "music_genre": music_genre.name,
                "secretkey": event.secretkey,
                "link": event.link,
                "date": event.date.strftime('%d-%m-%Y'),
                "image": event.image,
                "description": event.description,
                "userLiked": userLiked,
                "userAssist": userAssist,
                "assistants": assistants
            })
        return JsonResponse(json_response, safe=False, status=200)
    elif request.method == 'POST': #TESTEADO
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
            secretkey = data('secretkey', None) 
            link = data['link']
            if check_link_format(link) is False:
                return JsonResponse({"response": "not_ok"}, status=400)
            date = data['date']
            time = data['time']
            if len(time.split(':')) == 2:
                time += ':00'
            date = datetime.strptime(f'{date} {time}', '%Y-%m-%d %H:%M:%S')###CORREGIR
            date = timezone.make_aware(date)
            
            image = data['image']
            if check_image_format(image) is False:
                return JsonResponse({"response": "not_ok"}, status=400)
            description = data['description']
        except KeyError:
            return JsonResponse({"response": "not_ok"}, status=400)
        event = Events(manager=user_session.user, title=title,street=street, province=province, music_genre=music_genre, price=price,secretkey=secretkey, link=link, date=date.isoformat(),image=image, description=description, )
        event.save()
        return JsonResponse({'message': 'Event created'}, status=201)
    else:
        return JsonResponse({'message': 'Method not allowed'}, status=405)
#TESTEADO
@csrf_exempt
def event_id(request, id):
    if request.method == 'GET':
        try:
        #AUTENTICAMOS AL USUARIO
            user_session = authenticate_user(request)
        except PermissionDenied:
            return JsonResponse({'error': 'Unauthorized'}, status=401)
        #CREAMOS UN JSON CONLA INFO DEL EVENTO Y ATRIB.(ASISTENCIA, LIKE, ETC)
        try:
            event = Events.objects.get(id=id)
        except Events.DoesNotExist:
            return JsonResponse({'error': 'Event not found'}, status=404)
        assistants = UserAssist.objects.filter(event=event).count()
        try:
            userLiked = UserLikes.objects.get(user=user_session.user, event=event)
            userLiked = True
        except UserLikes.DoesNotExist:
            userLiked = False
        try:
            userAssist = UserAssist.objects.get(user=user_session.user, event=event)
            userAssist = True
        except UserAssist.DoesNotExist:
            userAssist = False
        music_genre = MusicGenre.objects.get(id=event.music_genre.id)
        json_response = []
        json_response.append({
            "manager": event.manager.username,
            "title": event.title,
            "street": event.street,
            "province": event.province,
            "music_genre": music_genre.name,
            "price": str(event.price), 
            "secretkey": event.secretkey,
            "link": event.link,
            "date": event.date.strftime('%d-%m-%Y'),
            "time": event.date.strftime('%H:%M'),
            "image": event.image,
            "description": event.description,
            "userLiked": userLiked,
            "userAssist": userAssist,
            "assistants": assistants
            })
        return JsonResponse(json_response, safe=False, status=200)
    elif request.method == 'PUT':
        try:
        #AUTENTICAMOS AL USUARIO
            user_session = authenticate_user(request)
        except PermissionDenied:
            return JsonResponse({'error': 'Unauthorized'}, status=401)
        try:
            event = Events.objects.get(id=id)
        except Events.DoesNotExist:
            return JsonResponse({'error': 'Event not found'}, status=404)
         # Comprobamos si el usuario es el administrador del evento
        if event.manager.id != user_session.user.id:
            return JsonResponse({'error': 'Unauthorized'}, status=401)
        
        try:
            data = json.loads(request.body)
            image = data['image']
            title = data['title']
            music_genre = data['music_genre']
            music_genre = MusicGenre.objects.get(id=music_genre)
            price = data['price']
            street = data['street']
            province = data['province']
            link = data['link']
            date = data['date']
            time = data['time']
            if len(time.split(':')) == 2:
                time += ':00'
            date = datetime.strptime(f'{date} {time}', '%Y-%m-%d %H:%M:%S')
            date = timezone.make_aware(date)
            description = data['description']
            secretkey = data['secretkey']
        except KeyError:
            return JsonResponse({"response": "not_ok"}, status=400)
        event.image = image
        event.title = title
        event.music_genre = music_genre
        event.price = price
        event.street = street
        event.province = province
        event.link = link
        event.date = date
        event.description = description
        event.secretkey = secretkey
        event.save()
        return JsonResponse({'message': 'Event updated'}, status=200)
    elif request.method == 'DELETE':
        try:
        #AUTENTICAMOS AL USUARIO
            user_session = authenticate_user(request)
        except PermissionDenied:
            return JsonResponse({'error': 'Unauthorized'}, status=401)
        try:
            event = Events.objects.get(id=id)
        except Events.DoesNotExist:
            return JsonResponse({'error': 'Event not found'}, status=404)
         # Comprobamos si el usuario es el administrador del evento
        if event.manager.id != user_session.user.id:
            return JsonResponse({'error': 'Unauthorized'}, status=401)
        event.delete()
        return JsonResponse({'message': 'Event deleted'}, status=200)
    else:
        return JsonResponse({'message': 'Method not allowed'}, status=405)

def userAssistEvents(request):
    if request.method == 'GET':
        try:
            user_session = authenticate_user(request)
        except PermissionDenied:
            return JsonResponse({'error': 'Unauthorized'}, status=401)
        try:
            user_assist = UserAssist.objects.filter(user=user_session.user)
        except UserAssist.DoesNotExist:
            return JsonResponse({'error': 'Assists not found'}, status=404)
        json_response = []
        for assist in user_assist:
            event = Events.objects.get(id=assist.event.id, date__gte=timezone.now())
            assistants = UserAssist.objects.filter(event=event).count()
            try:
                userLiked = UserLikes.objects.get(user=user_session.user, event=event)
                userLiked = True
            except UserLikes.DoesNotExist:
                userLiked = False 
            json_response.append({
                "title": event.title,
                "street": event.street,
                "price": str(event.price),
                "assistants": assistants,
                "date": event.date.strftime('%d-%m-%Y %H:%M'),
                "secretkey": event.secretkey,
                "userLiked": userLiked,
            })
        return JsonResponse(json_response, safe=False, status=200)     
    else:
        return JsonResponse({'message': 'Method not allowed'}, status=405)

@csrf_exempt
def userAssistEvent_id(request, id):
    if request.method == 'POST':
        try:
            user_session = authenticate_user(request)
        except PermissionDenied:
            return JsonResponse({'error': 'Unauthorized'}, status=401)
        try:
            event = Events.objects.get(id=id)
        except Events.DoesNotExist:
            return JsonResponse({'error': 'Event not found'}, status=404)
        try:
            event = UserAssist.objects.get(user=user_session.user, event=event)
        except UserAssist.DoesNotExist:
            assist = UserAssist(user=user_session.user, event=event)
            assist.save()
            return JsonResponse({'message': 'Event assisted'}, status=201)
        return JsonResponse({'message': 'Event assisted before'}, status=404)
    elif request.method == 'DELETE':
        try:
            user_session = authenticate_user(request)
        except PermissionDenied:
            return JsonResponse({'error': 'Unauthorized'}, status=401)
        try:
            event = Events.objects.get(id=id)
        except Events.DoesNotExist:
            return JsonResponse({'error': 'Event not found'}, status=404)
        assist = UserAssist.objects.get(user=user_session.user, event=event)
        assist.delete()
        return JsonResponse({'message': 'Event unassisted'}, status=200)
    else:
        return JsonResponse({'message': 'Method not allowed'}, status=405)

def userLikedEvents(request):
    if request.method == 'GET':
        try:
            user_session = authenticate_user(request)
        except PermissionDenied:
            return JsonResponse({'error': 'Unauthorized'}, status=401)
        try:
            user_liked = UserLikes.objects.filter(user=user_session.user)
        except UserLikes.DoesNotExist:
            return JsonResponse({'error': 'Likes not found'}, status=404)
        json_response = []
        for eventliked in user_liked:
            event = Events.objects.get(id=eventliked.event.id, date__gte=timezone.now())
            assistants = UserAssist.objects.filter(event=event).count()
            try:
                userLiked = UserLikes.objects.get(user=user_session.user, event=event)
                userLiked = True
            except UserLikes.DoesNotExist:
                userLiked = False
            try:
                userAssist = UserAssist.objects.get(user=user_session.user, event=event)
                userAssist = True
            except UserAssist.DoesNotExist:
                userAssist = False
            music_genre = MusicGenre.objects.get(id=event.music_genre.id)
            json_response.append({
                "title": event.title,
                "province": event.province,
                "music_genre": music_genre.name,
                "price": event.price,
                "secretkey": event.secretkey,
                "link": event.link,
                "date": event.date.strftime('%d-%m-%Y'),
                "image": event.image,
                "description": event.description,
                "userLiked": userLiked,
                "userAssist": userAssist,
                "assistants": assistants
            })
        return JsonResponse(json_response, safe=False, status=200)        
    else:
        return JsonResponse({'message': 'Method not allowed'}, status=405)

@csrf_exempt
def userLikedEvent_id(request, id):
    if request.method == 'POST':
        try:
            user_session = authenticate_user(request)
        except PermissionDenied:
            return JsonResponse({'error': 'Unauthorized'}, status=401)
        try:
            event = Events.objects.get(id=id)
        except Events.DoesNotExist:
            return JsonResponse({'error': 'Event not found'}, status=404)
        try:
            event = UserLikes.objects.get(event = event, user = user_session.user)
        except UserLikes.DoesNotExist:
            liked = UserLikes(user=user_session.user, event=event)
            liked.save()
            return JsonResponse({'message': 'Event liked'}, status=201)
        return JsonResponse({'error': 'Event liked before'}, status=404)    
    elif request.method == 'DELETE':
        try:
            user_session = authenticate_user(request)
        except PermissionDenied:
            return JsonResponse({'error': 'Unauthorized'}, status=401)
        try:
            event = Events.objects.get(id=id)
        except Events.DoesNotExist:
            return JsonResponse({'error': 'Event not found'}, status=404)
        try:
            liked = UserLikes.objects.get(user=user_session.user, event=event)
        except UserLikes.DoesNotExist:
            return JsonResponse({'error': 'Event already unliked'}, status=404)
        liked.delete()
        return JsonResponse({'message': 'Event unliked'}, status=200)
    else:
        return JsonResponse({'message': 'Method not allowed'}, status=405)

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


