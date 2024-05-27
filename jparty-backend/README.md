# Requerimientos

 - python 3.12.2
 - pip 
 - pyenv

# Como iniciar el servidor

 - Ir a la carpeta raíz del proyecto
 
 - Creamos el pyenv para instalar las dependencias. 

    - pyenv virtualenv 3.12.2 jparty-venv

    - pyenv activate jparty-venv

    - pip install -r requirements.txt

 - Una vez instaladas las dependencias en la carpeta raíz, entramos en jpart-backend y ejecutamos:

    - python manage.py runserver