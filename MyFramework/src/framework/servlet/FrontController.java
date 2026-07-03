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
import java.util.HashMap;

public class FrontController extends HttpServlet {
    // Une seule Map globale unifiée avec Urlkey pour le Sprint 3
    private final HashMap<Urlkey, Mapping> mappingUrls = new HashMap<>();
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
            
            // On adapte l'appel au scanner (qui doit maintenant peupler notre Map <Urlkey, Mapping>)
            this.totalControllersFound = AnnotationScanner.scanComponents(packageToScan, mappingUrls);
            
        } catch (Exception e) {
            throw new ServletException("Échec du scan des composants", e);
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Récupération de l'URL et de la méthode HTTP de la requête
        String urlInterceptee = request.getRequestURI().substring(request.getContextPath().length());
        String httpMethod = request.getMethod(); // "GET", "POST", etc.

        // 2. Création de la clé de recherche complexe (Sprint 3)
        Urlkey lookupKey = new Urlkey(urlInterceptee, httpMethod);

        // --- TABLEAU DE BORD (Si accès à la racine) ---
        if (urlInterceptee.equals("/") || urlInterceptee.isEmpty()) {
            response.setContentType("text/plain;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("==================================================");
            out.println("          TABLEAU DE BORD DU FRAMEWORK            ");
            out.println("==================================================");
            out.println("Nombre total de classes @Controller : " + totalControllersFound);
            out.println("\n--- ROUTES CARTOGRAPHIÉES ---");
            if (mappingUrls.isEmpty()) {
                out.println("Aucune route détectée.");
            } else {
                mappingUrls.forEach((key, map) -> {
                    out.println("Méthode HTTP: " + key.getMethod() + " | URL: " + key.getUrl() + " --> " + map.getClassname() + " --> " + map.getMethodname() + "()");
                });
            }
            return;
        }

        // 3. Recherche du Mapping associé
        Mapping mapping = mappingUrls.get(lookupKey);

        if (mapping == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Aucun contrôleur trouvé pour l'URL " + urlInterceptee + " avec la méthode " + httpMethod);
            return;
        }

        try {
            // 4. Instanciation et invocation dynamique par Réflexion (Sprint 3.2)
            Class<?> clazz = Class.forName(mapping.getClassname());
            Object controllerInstance = clazz.getDeclaredConstructor().newInstance();
            
            java.lang.reflect.Method methodToExecute = clazz.getDeclaredMethod(mapping.getMethodname());
            
            // Exécution de la méthode
            methodToExecute.invoke(controllerInstance);

            // Message de succès demandé dans la console
            System.out.println("[SUCCESS] Méthode exécutée par réflexion : " + mapping.getMethodname() + " dans " + mapping.getClassname());

            // Optionnel : un petit retour visuel pour le navigateur
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().println("[Framework] Exécution réussie de la méthode : " + mapping.getMethodname() + "()");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de l'exécution : " + e.getMessage());
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