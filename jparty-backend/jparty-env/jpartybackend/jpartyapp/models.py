from django.db import models

# Create your models here.
class User(models.Model):
    username = models.CharField(max_length=10, null=False, blank=False)
    email = models.EmailField(unique=True, max_length=75, null=False, blank=False)
    province = models.CharField(max_length=50, null=False, blank=False)
    password = models.CharField(max_length=255, null=False, blank=False)
    birthdate = models.DateField(null=False, blank=False)
    manager = models.BooleanField(default=False)
    def to_json(self):
        return {
            "username": self.username,
            "email": self.email,
            "province": self.province,
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
    title = models.CharField(max_length=15, null=False, blank=False)
    street = models.CharField(max_length=25, null=False, blank=False)
    province = models.CharField(max_length=50, null=False, blank=False)
    music_genre = models.ForeignKey(MusicGenre, on_delete=models.CASCADE, null=False, blank=False)
    price = models.DecimalField(max_digits=6, decimal_places=2, null=False, blank=False)
    secretkey = models.CharField(max_length=7, null=True)
    link = models.URLField(max_length=200, null=True)
    date = models.DateTimeField(null=False, blank=False)
    image = models.CharField(max_length=255, null=True)  
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