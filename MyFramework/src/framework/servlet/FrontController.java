package src.framework.servlet;

import src.framework.annotation.*;
import src.framework.model.*;
import src.framework.utils.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletContext;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class FrontController extends HttpServlet {
    // Une seule Map globale unifiée avec Urlkey pour le Sprint 3
    private final HashMap<Urlkey, Mapping> mappingUrls = new HashMap<>();
    private int totalControllersFound = 0;

    
    // @Override
    /*public void init() throws ServletException {
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
    }*/

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. RÉCUPÉRATION DU CONTEXTE (Le "sac à dos" partagé par le Listener)
        ServletContext context = getServletContext();
        HashMap<Urlkey, Mapping> mappingUrls = (HashMap<Urlkey, Mapping>) context.getAttribute("mappingUrls");
        Integer totalControllersFound = (Integer) context.getAttribute("totalControllersFound");

        // 2. Préparation de la recherche
        String urlInterceptee = request.getRequestURI().substring(request.getContextPath().length());
        String httpMethod = request.getMethod(); 
        Urlkey lookupKey = new Urlkey(urlInterceptee, httpMethod);

        // 3. TABLEAU DE BORD (Si accès à la racine)[cite: 5]
        if (urlInterceptee.equals("/") || urlInterceptee.isEmpty()) {
            response.setContentType("text/plain;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("==================================================");
            out.println("          TABLEAU DE BORD DU FRAMEWORK            ");
            out.println("==================================================");
            out.println("Nombre total de classes @Controller : " + (totalControllersFound != null ? totalControllersFound : 0));
            out.println("\n--- ROUTES CARTOGRAPHIÉES ---");
            if (mappingUrls == null || mappingUrls.isEmpty()) {
                out.println("Aucune route détectée.");
            } else {
                mappingUrls.forEach((key, map) -> {
                    out.println("Méthode HTTP: " + key.getMethod() + " | URL: " + key.getUrl() + " --> " + map.getClassname() + " --> " + map.getMethodname() + "()");
                });
            }
            return;
        }

        // 4. RECHERCHE DU MAPPING[cite: 5]
        if (mappingUrls == null || !mappingUrls.containsKey(lookupKey)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Route introuvable : " + urlInterceptee);
            return;
        }

        Mapping mapping = mappingUrls.get(lookupKey);

        // 5. EXÉCUTION PAR RÉFLEXION[cite: 5, 12]
        try {
            Class<?> clazz = Class.forName(mapping.getClassname());
            Object controllerInstance = clazz.getDeclaredConstructor().newInstance();
            java.lang.reflect.Method methodToExecute = clazz.getDeclaredMethod(mapping.getMethodname());
            
            // Appel de la méthode
            Object result = methodToExecute.invoke(controllerInstance);

            // 6. GESTION DU MODELVIEW (Sprint 5)[cite: 12]
            if (result instanceof ModelView) {
                ModelView mv = (ModelView) result;
                
                // Transfert des données dans la requête[cite: 12]
                mv.getData().forEach((key, value) -> {
                    request.setAttribute(key, value);
                });

                // Redirection vers la JSP[cite: 12]
                String path = "/WEB-INF/jsp/" + mv.getUrl() + ".jsp";
                request.getRequestDispatcher(path).forward(request, response);
            } else {
                response.getWriter().println("La méthode a retourné un type non géré : " + result);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur d'exécution : " + e.getMessage());
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