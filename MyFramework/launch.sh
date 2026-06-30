#!/bin/bash
TEST_FRAMEWORK_PERSO="../testFrameworkperso"
LIB_TEST_FRAMEWORK_PERSO="src/main/webapp/WEB-INF/lib"
MON_FRAMEWORK="mon-framework.jar"


# traçabilité des déploiement (vue des réussites et les echecs de déploiement)
LOG_REPOSITORY="./logs"
mkdir -p "$LOG_REPOSITORY"
N=$(($(ls "LOG_REPOSITORY" 2>/dev/null | wc -l ) + 1))
# explication ls "LOG_REPOSITORY"  2 : ls vérifie si log_repository existe ou qu'il n'y a rien dedans s'il existe et envoie le code 
# erreur 2 dans "/dev/null" qui est une poubelle (1 message normale (les echo du code actuelle) 2 message d'erreur qui pollue le visuel du terminal)
# wc -l compte le nombre de ligne mais vu que ls liste des fichier et '|' envoie le resultat de 'ls "LOG_REPOSITORY" 2>/dev/null' qui est 1 alors wc -l ici liste les fichiers dans le repertoir log_repository au lieu de ligne dans un fichier
DATE_HEURE=$(date +"%d%m%Y_%Hh%M") # formatage de la date et heure
LOG_FILE="$LOG_REPOSITORY/log${N}_${DATE_HEURE}.log"

{
    echo "=======création du .jar========="
    echo "======étape de compilation======"
    cd $(pwd) # aller ans ce repertoire pour ne pas se tromper de routes et de dossier

    if [ -e "$MON_FRAMEWORK" ]; then
        rm "$MON_FRAMEWORK"
        echo "ancient $MON_FRAMEWORK détecter et supprimer avec succes"
    fi
    javac -cp "/home/itu/Documents/apache-tomcat-10.0.16/lib/servlet-api.jar" -d bin $(find src -name "*.java")
    echo "======étape de jar======"
    cd bin 
    jar -cf mon-framework.jar .
    mv mon-framework.jar ../
    cd ..
    # capture du chemin absolue pour pouvoir copier le truc sans erreur d'existance ou erreur de route
    CHEMIN_JAR_SOURCE=$(pwd)/"$MON_FRAMEWORK"

    echo "=== remplacement du jar dans testFrameworkperso ==="
    cd "$TEST_FRAMEWORK_PERSO"
    if [ -d "src" ]; then
        echo "execution en cours ..."
        cd "$LIB_TEST_FRAMEWORK_PERSO"
        if [ -e "$MON_FRAMEWORK" ];then
            rm $MON_FRAMEWORK
            echo "Ancien JAR supprimé."
        fi
        cp "$CHEMIN_JAR_SOURCE" .
        echo "nouveau jar mis en place projet prêt a être déployer"
    fi

    echo "=== fin du processus ==="
} 2>&1 | tee "$LOG_FILE" 
# '2>&1' fusionne les erreurs et les messages normaux
# 'tee' affiche dans le terminal ET écrit dans le fichier log
