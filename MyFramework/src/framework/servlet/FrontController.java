package servlet;

import model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class FrontController extends HttpServlet {

    // PLUS D'INIT ICI ! Le démarrage est géré par le Listener.
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Récupération des données partagées
        HashMap<Urlkey, Mapping> mappingUrls = (HashMap<Urlkey, Mapping>) getServletContext().getAttribute("mappingUrls");
        int totalControllersFound = (int) getServletContext().getAttribute("totalControllersFound");

        String urlInterceptee = request.getRequestURI().substring(request.getContextPath().length());
        
        // Protection contre l'accès direct aux JSP
        if (urlInterceptee.startsWith("/WEB-INF/jsp/")) {
            getServletContext().getNamedDispatcher("jsp").forward(request, response);
            return; 
        }   

        String httpMethod = request.getMethod().toUpperCase();
        Urlkey lookupKey = new Urlkey(urlInterceptee, httpMethod);

        // 2. Dashboard pour la racine
        if (urlInterceptee.equals("/") || urlInterceptee.isEmpty()) {
            response.setContentType("text/plain;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("==================================================");
            out.println("          TABLEAU DE BORD DU FRAMEWORK            ");
            out.println("==================================================");
            out.println("Nombre total de classes @Controller : " + totalControllersFound);
            out.println("\n--- ROUTES CARTOGRAPHIÉES AU DÉMARRAGE ---");
            if (mappingUrls == null || mappingUrls.isEmpty()) {
                out.println("Aucune route détectée.");
            } else {
                mappingUrls.forEach((key, map) -> {
                    out.println("[" + key.getMethod() + "] " + key.getUrl() + " --> " + map.getClassname() + " --> " + map.getMethodName() + "()");
                });
            }
            return;
        }

        // 3. Recherche du mapping
        Mapping mapping = (mappingUrls != null) ? mappingUrls.get(lookupKey) : null;
        if (mapping == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Route introuvable.");
            return;
        }
        
        try {
            // 4. Exécution par réflexion
            Class<?> clazz = Class.forName(mapping.getClassname());
            Object controllerInstance = clazz.getDeclaredConstructor().newInstance();
            java.lang.reflect.Method methodToExecute = clazz.getDeclaredMethod(mapping.getMethodName());
            Object result = methodToExecute.invoke(controllerInstance);

            // 5. Traitement du ModelView
            // 5. Traitement du ModelView
            if (result instanceof ModelView) {
                ModelView mv = (ModelView) result;
                
                // Injection des données dans la requête
                mv.getData().forEach(request::setAttribute);

                if (mv.getUrl() == null || mv.getUrl().trim().isEmpty()) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Vue non définie.");
                    return;
                }

                String viewName = mv.getUrl().trim();
                if (viewName.endsWith(".jsp")) {
                    viewName = viewName.substring(0, viewName.length() - 4);
                }
                
                String fullPath = "/WEB-INF/jsp/" + viewName + ".jsp";
                var dispatcher = request.getRequestDispatcher(fullPath);

                if (dispatcher != null) {
                    System.out.println("[DEBUG] Forward vers : " + fullPath);
                    dispatcher.forward(request, response);
                    return; 
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Vue introuvable : " + fullPath);
                    return;
                }
            } else {
                response.getWriter().println("[Framework Error] La méthode n'a pas renvoyé un ModelView.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur : " + e.getMessage());
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