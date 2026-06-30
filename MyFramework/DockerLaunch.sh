#!/bin/bash

echo "===== lancement de la création du jar pour docker ====="

echo "=== étape de compilation en binaire ==="
javac -cp "/usr/local/tomcat/lib/servlet-api.jar" -d bin $(find src -name "*.java")
echo "=== étape de création du jar ==="
cd bin
jar -cf mon-framework.jar src/


