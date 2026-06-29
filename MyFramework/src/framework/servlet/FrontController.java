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
    private HashMap<Urlkey, Mapping> mappingUrl = new HashMap<>();
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

    public void addRoute(String url, String httpMethod, Mapping mapping) throws Exception {
        Urlkey key = new Urlkey(url, httpMethod);

        // Si la clé (URL + Méthode) existe déjà dans la Map
        if (mappingUrls.containsKey(key)) {
            throw new Exception("L'URL '" + url + "' avec la méthode HTTP '" + httpMethod + "' est déjà associée à un contrôleur !");
        }

        mappingUrl.put(key, mapping);
    }


    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Récupération de l'URL et de la méthode HTTP de la requête
        String urlInterceptee = request.getRequestURI().substring(request.getContextPath().length());
        String httpMethod = request.getMethod(); // Renvoie "GET", "POST", etc.

        // 2. Création de la clé de recherche
        Urlkey lookupKey = new Urlkey(urlInterceptee, httpMethod);

        // 3. Recherche du Mapping associé
        Mapping mapping = mappingUrls.get(lookupKey);

        if (mapping == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Aucun contrôleur trouvé pour " + lookupKey);
            return;
        }

        try {
            // 4. instanciation et invocation dynamique (Reflect)
            Class<?> clazz = Class.forName(mapping.getClassname());
            Object controllerInstance = clazz.getDeclaredConstructor().newInstance();
            
            // Récupération de la méthode par son nom
            java.lang.reflect.Method methodToExecute = clazz.getDeclaredMethod(mapping.getMethodname());
            
            // Exécution !
            methodToExecute.invoke(controllerInstance);

            // Affichage de confirmation demandé par le Sprint 3.2
            System.out.println("[SUCCESS] Méthode exécutée par réflexion : " + mapping.getMethodname() + " dans " + mapping.getClassname());

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de l'exécution : " + e.getMessage());
        }
    }

    
}