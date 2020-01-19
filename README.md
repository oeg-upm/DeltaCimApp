# DeltaCimApp


# TODO:
* Mostrar mensajes de error
* Archivo CA - Buscar selector de directorio alternativo. Parece ser que con el formulario es imposible si no es para enviarlo debido a medidas de seguridad (https://stackoverflow.com/questions/56426302/how-to-implement-a-directory-chooser-in-spring-boot-using-thymeleaf).
* Show in the dashboard the users authority correctly, not in a Hashmap.


# BUGS:
* Only admins can connect to the OpenFire Server, due to the security in the HTML file.

# IMPROVEMENTS:
* Optimize the createUser / createAdmin code
* Memory. When the server is started for at least 30 minutes, it slows down.
