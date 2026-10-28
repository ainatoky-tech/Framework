package utils;

import model.*;
import annotation.*;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.HashMap;

public class AnnotationScanner {
    
    private static int scannerDossier(
        File repertoire,
        String packageName,
        HashMap<Urlkey,Mapping> correspondanceUrlMethodinController
    ) throws Exception{
        int countFichier = 0;
        File[] files = repertoire.listFiles();
        for(File file : files){
            if(file.getName().endsWith(".class")){
                String className = packageName + "." + (file.getName().replace(".class", ""));
                Class<?> classe = Class.forName(className);
                if(classe.isAnnotationPresent(Controller.class)){
                    countFichier++;
                    analyserControlleur(classe,correspondanceUrlMethodinController);
                }
            }
        }
        return countFichier;
    }


    private static  void analyserControlleur(Class<?> clazz , HashMap<Urlkey,Mapping> correspondanceURLinMethodController) throws Exception{
        System.out.println("[Scanner] Contrôleur trouvé : " + clazz.getName());
        for(Method methode : clazz.getDeclaredMethods()){
            if(methode.isAnnotationPresent(UrlMapping.class)){
                UrlMapping mapping = methode.getAnnotation(UrlMapping.class);
                Urlkey key = new Urlkey(mapping.value(),mapping.method().toUpperCase());
                String[] parameter = new String[methode.getParameters().length];

                for(int i=0; i<parameter.length; i++){
                    parameter[i] = methode.getParameters()[i].getName();
                }

                correspondanceURLinMethodController.put(key, new Mapping(clazz.getName(), methode.getName(), methode.getParameterTypes(), parameter));
                System.out.println(" -> routes  : [ " + key.getUrl() + "] " + key.getMethod());
            }
        }
    }

    /**
     * scanner de l'url vers les fichiers controller ou affichage lui correspondant 
     * @param packageToScan
     * @param correspondanceUrlMethodInController
     * @throws Exception
     * 
     * - convertissez le . sur le package de chaque classe java en / 
     * - convertissez le en forme d'url 
     */
    public static int scanComponents(
        String packageToScan, 
        HashMap<Urlkey,Mapping> correspondanceUrlMethodInController
    ) throws Exception{
        String path = packageToScan.replace(".", "/");
        URL ressource = Thread.currentThread().getContextClassLoader().getResource(path);
        if (ressource == null) {
            System.out.println("[Scanner] Impossible de trouver le package : " + packageToScan);
            return 0;
        }
        File directory = new File(ressource.toURI());
        return scannerDossier(directory, packageToScan, correspondanceUrlMethodInController);
    }


}