#!/bin/bash
APP_NAME="TestFramework"
SRC_DIRECTORY="src/main/java"
WEB_DIRECTORY="src/main/webapp"
BUILD_DIRECTORY="build"
# direction vers le tomcat de docker
TOMCAT_DIRECTORY="/usr/local/tomcat"
LIBRARY_DIRECTORY="$TOMCAT_DIRECTORY/lib"
TOMCAT_WEBAPPS="$TOMCAT_DIRECTORY/webapps"
TOMCAT_SERVLET_API_JAR="$LIBRARY_DIRECTORY/servlet-api.jar"
WEBINF_LIB="$WEB_DIRECTORY/WEB-INF/lib"

{
    rm -rf $BUILD_DIRECTORY
    mkdir -p $BUILD_DIRECTORY/WEB-INF/classes

    ClassPath="$TOMCAT_SERVLET_API_JAR"
    if [ -d "$WEBINF_LIB" ]; then
        for jar in "$WEBINF_LIB"/*.jar; do
            [ -e "$jar" ] && ClassPath="$ClassPath:$jar"
        done
    fi

    set -e # arrêt immédiat du script en cas d'erreur
    echo "[1/4] Compilation des classes Java..."
    mkdir -p $BUILD_DIRECTORY/WEB-INF/classes

    find $SRC_DIRECTORY -name "*.java" > sources.txt
    if [ -s sources.txt ]; then
        javac -parameters -cp "$ClassPath" -d $BUILD_DIRECTORY/WEB-INF/classes @sources.txt
    else    
        echo "Aucun fichier Java trouvé dans $SRC_DIRECTORY"
    fi
    rm sources.txt

    echo "[2/4] Préparation des fichiers Web..."
    cp -r $WEB_DIRECTORY/* $BUILD_DIRECTORY/

    if [ -f "src/main/resources/applicationContext.xml" ]; then
        cp src/main/resources/applicationContext.xml $BUILD_DIRECTORY/WEB-INF/classes/
    elif [ -f "src/main/java/applicationContext.xml" ]; then
        cp src/main/java/applicationContext.xml $BUILD_DIRECTORY/WEB-INF/classes/
    fi

    echo "[3/4] Création du fichier .war..."
    cd $BUILD_DIRECTORY || exit
    jar -cf ../$APP_NAME.war *
    cd ..


    echo "[4/4] Déploiement et redémarrage de Tomcat..."
    cp -f $APP_NAME.war $TOMCAT_WEBAPPS/
    rm $APP_NAME.war

    cd "$TOMCAT_DIRECTORY/bin" || exit
    ./shutdown.sh
    sleep 5
    ./startup.sh

    echo "=====Déploiement terminé====="
} 2>&1
