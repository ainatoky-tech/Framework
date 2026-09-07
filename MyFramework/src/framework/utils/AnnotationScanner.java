package utils;

import model.*;
import annotation.*;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;

public class AnnotationScanner {
    
    public static int scanComponents(String packageToScan, HashMap<Urlkey, Mapping> mappingUrls) throws Exception {
        int controllerCount = 0; 
        
        String path = packageToScan.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);

        if (resource == null) {
            resource = AnnotationScanner.class.getClassLoader().getResource(path);
        }
        if (resource == null) {
            resource = AnnotationScanner.class.getResource("/" + path);
        }

        if (resource == null) {
            System.out.println("[Framework] Impossible de trouver le dossier correspondant à : " + packageToScan);
            return 0;
        }

        File directory = new File(resource.toURI());
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".class")) {
                        String className = packageToScan + "." + file.getName().substring(0, file.getName().length() - 6);
                        Class<?> clazz = Class.forName(className);

                        if (clazz.isAnnotationPresent(Controller.class)) {
                            controllerCount++; 
                            System.out.println("[CLASSE DÉTECTÉE] -> " + clazz.getName());

                            for (Method method : clazz.getDeclaredMethods()) {
                                if (method.isAnnotationPresent(UrlMapping.class)) {
                                    UrlMapping mapping = method.getAnnotation(UrlMapping.class);
                                    String url = mapping.value();
                                    
                                    // REFIXE DYNAMIQUE DU SPRINT 3 : On extrait la vraie méthode HTTP configurée !
                                    String httpMethod = mapping.method().toUpperCase(); 

                                    // Création de la clé unique URL + Méthode HTTP
                                    Urlkey key = new Urlkey(url, httpMethod);

                                    // Vérification d'unicité réelle
                                    if (mappingUrls.containsKey(key)) {
                                        throw new Exception("[DUPLICATE ROUTE ERROR] L'URL '" + url + "' avec la méthode " + httpMethod + " est déjà déclarée !");
                                    }

                                    // Stockage sécurisé
                                    mappingUrls.put(key, new Mapping(clazz.getName(), method.getName()));
                                    System.out.println("  └── Enregistré : [" + httpMethod + "] " + url + " ──> " + method.getName() + "()");
                                }
                            }
                        }
                    }
                }
            }
        }
        return controllerCount; 
    }
}