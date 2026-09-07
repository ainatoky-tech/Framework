#!/bin/bash

TEST_FRAMEWORK_PERSO="../testFrameworkPerso"
LIB_TEST_FRAMEWORK_PERSO="src/main/webapp/WEB-INF/lib"
MON_FRAMEWORK="mon-framework.jar"

rm -rf bin
mkdir -p bin

echo "===== lancement de la création du jar pour docker ====="
{
    echo "=== création du jar ==="
    echo "======compilation des classes java======"
    cd $(pwd)
    if [ -e "$MON_FRAMEWORK" ]; then
        rm "$MON_FRAMEWORK"
        echo "ancien $MON_FRAMEWORK détecter et supprimer avec succes"
    fi

    echo "compilation des classes java"
    javac -cp "/usr/local/tomcat/lib/servlet-api.jar" -d bin $(find src -name "*.java")


    echo "====.Jarisation===="
    cd bin
    jar -cf mon-framework.jar .
    mv mon-framework.jar ../
    cd ..

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

} 2>&1