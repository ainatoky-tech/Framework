package listener;

import model.Urlkey;
import model.Mapping;
import utils.AnnotationScanner;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.HashMap;

/**
 * @author moi 
 * ContextLoaderListener
 * Code encore à expliquer 
 */

@WebListener // Cette annotation dit à Tomcat : "Ceci est un espion de démarrage !"
public class ContextLoaderListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        System.out.println("==================================================");
        System.out.println("   [LISTENER] DÉMARRAGE DU FRAMEWORK              ");
        System.out.println("==================================================");
        initialiserConfigurationAuth(context);
        initialiserRoutes(context);
        initialiserSpring(context);
        System.out.println("==================================================");
    }

    private void initialiserConfigurationAuth(ServletContext context){
        String sessionKey = context.getInitParameter("authSessionKey");
        String roleAttribut = context.getInitParameter("authRoleAttribute");
        context.setAttribute("authSessionKey", (sessionKey != null && !sessionKey.isEmpty()) ? sessionKey : "user");
        context.setAttribute("authRoleAttribute", (roleAttribut != null && !roleAttribut.isEmpty()) ? roleAttribut : "role");

        System.out.println("[AUTH] Clé session : '" + context.getAttribute("authSessionKey") + "' | Attribut rôle : '" + context.getAttribute("authRoleAttribute") + "'");
    }

    private void initialiserRoutes(ServletContext context) {
        String packageToScan = context.getInitParameter("packageToScan");
        if (packageToScan == null || packageToScan.trim().isEmpty()) {
            System.err.println("[WARNING] Le paramètre 'packageToScan' est manquant dans web.xml.");
            return;
        }

        try {
            HashMap<Urlkey, Mapping> mappingUrls = new HashMap<>();
            int total = AnnotationScanner.scanComponents(packageToScan, mappingUrls);
            context.setAttribute("mappingUrls", mappingUrls);
            context.setAttribute("totalControllersFound", total);
            System.out.println("[SCANNER] " + total + " contrôleur(s) chargé(s).");
        } catch (Exception e) {
            System.err.println("[ERREUR] Échec du scan au démarrage !");
            e.printStackTrace();
        }
    }


    private void initialiserSpring(ServletContext context) {
        try {
            Class<?> springClass = Class.forName("org.springframework.context.support.ClassPathXmlApplicationContext");
            Object springContext = springClass.getConstructor(String.class).newInstance("applicationContext.xml");
            context.setAttribute("springContext", springContext);
            System.out.println("[SPRING] Contexte IoC initialisé.");
        } catch (ClassNotFoundException e) {
            System.out.println("[SPRING] Non présent dans WEB-INF/lib. Mode standalone.");
        } catch (Exception e) {
            System.err.println("[SPRING WARNING] Échec du chargement de applicationContext.xml :");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[LISTENER] Application arrêtée.");
    }
}