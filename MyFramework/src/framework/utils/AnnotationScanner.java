package src.framework.utils;

import src.framework.model.*;
import src.framework.annotation.*;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;

public class AnnotationScanner {
    
    // Changement du type de retour : void -> int
    public static int scanComponents(String packageToScan, HashMap<String, Mapping> mappingUrls) throws Exception {
        int controllerCount = 0; // Compteur pour le Sprint 1 / Exigences
        
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

                        // Sprint 1 : Vérification du @Controller
                        if (clazz.isAnnotationPresent(Controller.class)) {
                            controllerCount++; // Un contrôleur de plus trouvé !
                            System.out.println("[CLASSE DÉTECTÉE] -> " + clazz.getName());

                            // Sprint 2 : Extraction des méthodes avec @UrlMapping
                            for (Method method : clazz.getDeclaredMethods()) {
                                if (method.isAnnotationPresent(UrlMapping.class)) {
                                    UrlMapping mapping = method.getAnnotation(UrlMapping.class);
                                    String url = mapping.value();

                                    // Stockage dans la table de hachage
                                    mappingUrls.put(url, new Mapping(clazz.getName(), method.getName()));
                                    System.out.println("  └── Enregistré : " + url + " ──> " + method.getName() + "()");
                                }
                            }
                        }
                    }
                }
            }
        }
        return controllerCount; // Renvoie le nombre total trouvé
    }
}