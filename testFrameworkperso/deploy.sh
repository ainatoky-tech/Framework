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
# journalisation 
LOG_REPOSITORY="$(pwd)/log/ps"
mkdir -p "$LOG_REPOSITORY"
N=$(($(ls "LOG_REPOSITORY" 2>/dev/null | wc -l ) + 1))
# explication ls "LOG_REPOSITORY"  2 : ls vérifie si log_repository existe ou qu'il n'y a rien dedans s'il existe et envoie le code 
# erreur 2 dans "/dev/null" qui est une poubelle (1 message normale (les echo du code actuelle) 2 message d'erreur qui pollue le visuel du terminal)
# wc -l compte le nombre de ligne mais vu que ls liste des fichier et '|' envoie le resultat de 'ls "LOG_REPOSITORY" 2>/dev/null' qui est 1 alors wc -l ici liste les fichiers dans le repertoir log_repository au lieu de ligne dans un fichier
DATE_HEURE=$(date +"%d%m%Y_%Hh%M") # formatage de la date et heure
LOG_FILE="$LOG_REPOSITORY/log${N}_${DATE_HEURE}.log"
{
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
} 2>&1 | tee "$LOG_FILE" 
# '2>&1' fusionne les erreurs et les messages normaux
# 'tee' affiche dans le terminal ET écrit dans le fichier log
