# Gitignore
Ajouter des commentaire avec un #Ceci est un commentaire

### Ignorer un fichier précis
 secret.txt

### Ignorer tous les fichiers avec une extension
 *.log
 *.class

### Ignorer un dossier entier (le / final indique que c'est un dossier)
 node_modules/
 target/

### Ignorer un dossier à un chemin précis seulement
 backend/target/

### Ignorer tous les fichiers qui s'appellent ".env" peu importe où
 **/.env

### Exception : ne PAS ignorer ce fichier même s'il correspond à une règle au-dessus
 !src/important.log