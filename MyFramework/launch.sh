#!/bin/bash
TEST_FRAMEWORK_PERSO="../testFrameworkperso"
LIB_TEST_FRAMEWORK_PERSO="src/main/webapp/WEB-INF/lib"
MON_FRAMEWORK="mon-framework.jar"


echo "=======création du .jar========="
echo "======étape de compilation======"
cd $(pwd)

if [ -e "$MON_FRAMEWORK" ]; then
    rm "$MON_FRAMEWORK"
    echo "ancient $MON_FRAMEWORK détecter et supprimer avec succes"
fi
javac -cp "/home/itu/Documents/apache-tomcat-10.0.16/lib/servlet-api.jar" -d bin $(find src -name "*.java")
echo "======étape de jar======"
cd bin 
jar -cf mon-framework.jar src/
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
