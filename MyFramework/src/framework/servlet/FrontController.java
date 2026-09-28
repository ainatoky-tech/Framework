package servlet;

import model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;

import annotation.APIRest;

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
            
            // INJECTION PAR RÉFLEXION (Aucun import org.springframework)
            Object springContext = getServletContext().getAttribute("springContext");
            if (springContext != null) {
                try {
                    Object beanFactory = springContext.getClass().getMethod("getAutowireCapableBeanFactory").invoke(springContext);
                    beanFactory.getClass().getMethod("autowireBean", Object.class).invoke(beanFactory, controllerInstance);
                    System.out.println("[DEBUG FRONTCONTROLLER] -> Injection réussie pour : " + controllerInstance.getClass().getName());
                } catch (Exception ex) {
                    System.err.println("[DEBUG FRONTCONTROLLER ERROR] -> Échec de autowireBean : " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                System.err.println("[DEBUG FRONTCONTROLLER ERROR] -> springContext est NULL dans ServletContext !");
            }
            
            java.lang.reflect.Method methodToExecute = clazz.getDeclaredMethod(mapping.getMethodName());
            Object result = methodToExecute.invoke(controllerInstance);

            if (methodToExecute.isAnnotationPresent(APIRest.class)) {
                
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();
                
                if (result == null) {
                    out.print("{}");
                } 
                // A. Si la méthode renvoie déjà une String JSON faite main
                else if (result instanceof String) {
                    out.print((String) result);
                } 
                // B. Si la méthode renvoie un ModelView, on sérialise sa Map "data"
                else if (result instanceof ModelView) {
                    ModelView mv = (ModelView) result;
                    out.print(convertObjectToJson(mv.getData()));
                } 
                // C. Si la méthode renvoie un objet brut (Employe, List, etc.), le framework le convertit !
                else {
                    out.print(convertObjectToJson(result));
                }
                
                out.flush();
                return; // Court-circuit complet des JSP
            }
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

    private String convertObjectToJson(Object obj){
        if(obj == null) return "null";
        if(obj instanceof String){
            return "\"" + obj.toString().replace("\"", "\\\"")+ "\"";
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        if (obj instanceof Collection) {
            StringBuilder sb = new  StringBuilder("[");
            Collection<?> liste = (Collection<?>) obj;//on caste la liste en collection qu'on string-ifiera
            int i=0;
            for (Object item : liste) {
                if (i > 0) sb.append(",");
                sb.append(convertObjectToJson(item));
                i++;
            }
            sb.append("]");
            return sb.toString();
        }
        // 4. Gestion des Maps (comme la Map de données du ModelView)
        if (obj instanceof java.util.Map) {
            StringBuilder sb = new StringBuilder("{");
            java.util.Map<?, ?> map = (java.util.Map<?, ?>) obj;
            int i = 0;
            for (java.util.Map.Entry<?, ?> entry : map.entrySet()) {
                if (i > 0) sb.append(",");
                sb.append("\"").append(entry.getKey()).append("\":");
                sb.append(convertObjectToJson(entry.getValue()));
                i++;
            }
            sb.append("}");
            return sb.toString();
        }

        // 5. Gestion des objets personnalisés (POJO comme Employe, Departement, etc.) via la Réflexion
        try {
            StringBuilder sb = new StringBuilder("{");
            java.lang.reflect.Field[] fields = obj.getClass().getDeclaredFields();
            int i = 0;
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true); // Permet de lire les attributs privés (private)
                
                // On ignore les champs introduits par la couverture de code ou les proxys (ex: jacoco)
                if (field.getName().startsWith("$")) continue; 

                if (i > 0) sb.append(",");
                
                sb.append("\"").append(field.getName()).append("\":");
                Object value = field.get(obj);
                sb.append(convertObjectToJson(value));
                i++;
            }
            sb.append("}");
            return sb.toString();
        } catch (Exception e) {
            return "{\"error\":\"Sérialisation impossible de l'objet: " + e.getMessage() + "\"}";
        }
    }
}