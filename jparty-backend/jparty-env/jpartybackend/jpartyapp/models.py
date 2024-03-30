from django.db import models

# Create your models here.
class User(models.Model):
    username = models.CharField(max_length=10)
    email = models.EmailField(unique=True, max_length=75)
    province = models.CharField(max_length=50)
    password = models.CharField(max_length=255)
    birthdate = models.DateField()
    manager = models.BooleanField(default=False)
    def to_json(self):
        return {
            "username": self.username,
            "email": self.email,
            "province": self.province,
            "password": self.password,
            "birthdate": self.birthdate,
            "manager": self.manager
        }

class MusicGenre(models.Model):
    name = models.CharField(max_length=20)
    image = models.CharField(max_length=100)

class UserPreferences(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    music_genre = models.ForeignKey(MusicGenre, on_delete=models.CASCADE)

class Events(models.Model):
    manager = models.ForeignKey(User, on_delete=models.CASCADE)
    title = models.CharField(max_length=15)
    street = models.CharField(max_length=25)
    province = models.CharField(max_length=50)
    music_genre = models.ForeignKey(MusicGenre, on_delete=models.CASCADE)
    price = models.DecimalField(max_digits=6, decimal_places=2)
    secretkey = models.CharField(max_length=7, default=None, blank=True, null=True)
    link = models.URLField(max_length=200, null=True, blank=True)
    date = models.DateTimeField()
    image = models.CharField(max_length=255)  
    description = models.TextField(max_length=150)




class UserSession(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    token = models.CharField(unique=True, max_length=45)

class UserLikes(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    event = models.ForeignKey(Events, on_delete=models.CASCADE)

class UserAssist(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    event = models.ForeignKey(Events, on_delete=models.CASCADE)