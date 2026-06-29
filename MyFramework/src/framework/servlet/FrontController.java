package src.framework.servlet;

import src.framework.annotation.*;
import src.framework.model.*;
import src.framework.utils.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FrontController extends HttpServlet {
    private final HashMap<String, Mapping> mappingUrls = new HashMap<>();
    private int totalControllersFound = 0;

    @Override
    public void init() throws ServletException {
        String packageToScan = this.getServletConfig().getInitParameter("packageToScan");
        if (packageToScan == null || packageToScan.isEmpty()) {
            System.out.println("[Framework ERROR] Le paramètre 'packageToScan' est manquant dans web.xml.");
            return;
        }

        try {
            System.out.println("================================");
            System.out.println("      DÉMARRAGE DU SCANNER      ");
            System.out.println("================================");
            
            this.totalControllersFound = AnnotationScanner.scanComponents(packageToScan, mappingUrls);
            
        } catch (Exception e) {
            throw new ServletException("Échec du scan des composants", e);
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String urlRecherchee = uri.substring(contextPath.length());

        // --- DASHBOARD (Racine ou URL vide) ---
        if (urlRecherchee.equals("/") || urlRecherchee.isEmpty()) {
            out.println("==================================================");
            out.println("          TABLEAU DE BORD DU FRAMEWORK            ");
            out.println("==================================================");
            out.println("URL actuelle interceptée : " + urlRecherchee);
            out.println("Nombre total de classes @Controller : " + totalControllersFound);
            out.println("\n--- ROUTES CARTOGRAPHIÉES ---");
            if (mappingUrls.isEmpty()) {
                out.println("Aucune route détectée.");
            } else {
                mappingUrls.forEach((url, map) -> {
                    out.println("URL: " + url + " --> " + map.getClassname() + " --> méthode " + map.getMethodname() + "()");
                });
            }
            return;
        }

        // --- RECHERCHE DE L'URL (Aiguillage) ---
        Mapping mapping = mappingUrls.get(urlRecherchee);

        if (mapping != null) {
            out.println("URL interceptée : " + urlRecherchee);
            out.println("Contrôleur ciblé : " + mapping.getClassname());
            out.println("Méthode exécutée : " + mapping.getMethodname() + "()");
        } else {
            // --- GESTION DE L'ERREUR REVISEE (Sprint 2) ---
            out.println("==================================================");
            out.println("       ERREUR DE ROUTAGE : URL INCONNUE           ");
            out.println("==================================================");
            out.println("URL interceptée introuvable : " + urlRecherchee);
            out.println("Nombre total de classes @Controller dans l'application : " + totalControllersFound);
            out.println("\n--- CARTOGRAPHIE COMPLÈTE DU PROJET ---");

            // Pour éviter les doublons de traitement, on récupère la liste unique des classes Contrôleurs existantes
            Set<String> uniqueControllers = new HashSet<>();
            mappingUrls.values().forEach(map -> uniqueControllers.add(map.getClassname()));

            if (uniqueControllers.isEmpty()) {
                out.println("Aucun contrôleur n'a été enregistré dans le système.");
            } else {
                // On parcourt chaque classe @Controller existante dans l'application
                for (String className : uniqueControllers) {
                    try {
                        Class<?> clazz = Class.forName(className);
                        out.println("\n[CLASSE CONTROLLER] : " + clazz.getName());
                        
                        // 1. Lister toutes les méthodes présentes à l'intérieur de la classe
                        out.println("  -> Toutes les méthodes présentes dans cette classe :");
                        Method[] methods = clazz.getDeclaredMethods();
                        if (methods.length == 0) {
                            out.println("     (Aucune méthode définie)");
                        } else {
                            for (Method m : methods) {
                                out.println("     * " + m.getName() + "()");
                            }
                        }

                        // 2. Isoler et lister uniquement les méthodes qui ont l'annotation @UrlMapping et leur chemin
                        out.println("  -> Méthodes annotées et configurées :");
                        boolean hasMapping = false;
                        for (Method m : methods) {
                            if (m.isAnnotationPresent(UrlMapping.class)) {
                                hasMapping = true;
                                UrlMapping ann = m.getAnnotation(UrlMapping.class);
                                out.println("     * [URL: " + ann.value() + "] ──> " + m.getName() + "()");
                            }
                        }
                        if (!hasMapping) {
                            out.println("     (Aucune méthode annotée @UrlMapping dans ce contrôleur)");
                        }

                    } catch (ClassNotFoundException e) {
                        out.println("  [Erreur lors du chargement de la classe : " + className + "]");
                    }
                }
            }

            out.println("\n==================================================");
            
            // On lève l'exception demandée par le Sprint 2
            throw new ServletException("Erreur de routage : Aucun contrôleur ou méthode n'est associé à l'URL : " + urlRecherchee);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}