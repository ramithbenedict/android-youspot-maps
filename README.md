### YOUSPOT ###

#M2 Logiciels - UNIVERSITE MARNE LA VALLEE#

#GROUPE : MAJERDI, BENEDICT, LAZAR, ELKOUDIA, HATHAT#


## Ce README contient les informations sur le projet YOUSPOT, le découpage du travail et les fonctionnalités de l'application.


## Découpage du travail :
	LOGIN et REGISTER :
	
		ELKOUDIA OTHMANE : Back End de l'authentification de l'inscription et le développement des méthodes, et l'utilisation de l'API WIFI pour se connecter à l'application
		LAZAR Mohammed Amine : Front End de l'authentification, l'inscription et la base de données dans PHPMYADMIN, et le front end et back end de reservation (NON INTEGRE à cause du manque de temps)
		
		
	GEOLOCALISATION GOOGLE MAPS :
		
		RAMITH BENEDICT : Implémentation de l'API Google Maps et sa partie Front END
		HATHAT MOHAMMED : Collecte des données des installations sportifs et le développement du back end de la partie GEOLOCALISATION
		MAJERDI AHMED : Importation des données des installations sur le MAPS, et le développement de la partie historique des réservation (NON INTEGRE à cause du manque de temps)
		
## Partie serveur : 
			
	- On a utiliser wampServer (mysql et php) pour l'authentification et l'inscription et la vérification des données saisie par l'utilisateur de l'application.
	- Pour la configuration on met l'adresse IP du serveur dans la class java appConfig.

## Scénario de l'application :		

	1- Authentification via Réseau wifi:	
		* si vous êtes dejà inscrit : vous remplissez votre email et votre mot de passe pour se connecter .
		* sinon vous créer un nouveau compte, vous renseignez votre nom, prénom,email,mot de passe.
	
	2- Avoir la Vue de google maps avec les installations sportifs dans l'ile de france.

	3- Vous cliquez sur une installation, vous aurez les informations nécessaire.
		
## Fonctionnalité non intégré (manque de temps):

La réservation d'un créneau dans une installation sportif n'est pas intégré à cause du manque de temps, et cela permet :
	
	1- Choisir un créneau avec date et heure et la si l'installation et disponible ou non(Exemple : le 23/03/2017 à 14:00, disponible).
		*vérifier si le créneau déjà réserver ou nom (automatiquement par notre application)
	2- Une fois la réservation est faite, le créneau devient indisponible.
	
	3- La réservation se rajoute dans une table d'historique de reservation pour l'utilisateur.
	
# COPYRIGHT YOUSPOT_2017_M2_LOGICIELS
