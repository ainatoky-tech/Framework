#!/bin/bash

APP_NAME="testframework"
SRC_DIR="src/main/java" 
WEB_DIR="src/main/webapp" 
BUILD_DIR="build"

# Harmonisation sur le même répertoire Tomcat
TOMCAT_DIR="/home/itu/Documents/apache-tomcat-10.0.16"
LIB_DIR="$TOMCAT_DIR/lib"
TOMCAT_WEBAPPS="$TOMCAT_DIR/webapps"
SERVLET_API_JAR="$LIB_DIR/servlet-api.jar"
WEBINF_LIB="$WEB_DIR/WEB-INF/lib"


# Nettoyage complet de l'ancien build pour repartir sur du propre
rm -rf $BUILD_DIR
mkdir -p $BUILD_DIR/WEB-INF/classes

# Construire le classpath pour javac (Servlet-API + tous les JAR du framework dans WEB-INF/lib)
CP="$SERVLET_API_JAR"
if [ -d "$WEBINF_LIB" ]; then
    for jar in "$WEBINF_LIB"/*.jar; do
        # Évite d'ajouter le littéral *.jar si le dossier est vide
        [ -e "$jar" ] && CP="$CP:$jar"
    done
fi

echo "[1/4] Compilation des classes Java..."
# Forcer la création du dossier classes dans le build pour que javac ne se perde pas
mkdir -p $BUILD_DIR/WEB-INF/classes

# Compiler tous les .java
find $SRC_DIR -name "*.java" > sources.txt
if [ -s sources.txt ]; then
    javac -cp "$CP" -d $BUILD_DIR/WEB-INF/classes @sources.txt
else
    echo "Aucun fichier Java trouvé dans $SRC_DIR"
fi
rm sources.txt

echo "[2/4] Préparation des fichiers Web..."
# Copier les fichiers web (web.xml, JSP, bootstrap, lib, etc.)
cp -r $WEB_DIR/* $BUILD_DIR/

echo "[3/4] Création du fichier .war..."
# On génère le .war à l'EXTÉRIEUR du dossier build pour éviter qu'il s'auto-inclue
cd $BUILD_DIR || exit
jar -cf ../$APP_NAME.war *
cd ..

echo "[4/4] Déploiement et redémarrage de Tomcat..."
# Déployer sur Tomcat
cp -f $APP_NAME.war $TOMCAT_WEBAPPS/
# Nettoyage du fichier war local temporaire
rm $APP_NAME.war

# Redémarrer Tomcat proprement
cd "$TOMCAT_DIR/bin" || exit
./shutdown.sh
# Un petit temps de pause pour laisser à Tomcat le temps de s'éteindre
sleep 2 
./startup.sh

echo "=== Déploiement terminé avec succès ! ==="