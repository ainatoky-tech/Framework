package src.framework.listener;

import src.framework.model.Urlkey;
import src.framework.model.Mapping;
import src.framework.utils.AnnotationScanner;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.HashMap;

@WebListener // Cette annotation dit à Tomcat : "Ceci est un espion de démarrage !"
public class ContextLoaderListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("==================================================");
        System.out.println("   [LISTENER] APPLICATION TOMCAT DÉMARRÉE        ");
        System.out.println("==================================================");

        ServletContext context = sce.getServletContext();
        
        // 1. On récupère le paramètre global depuis le web.xml
        String packageToScan = context.getInitParameter("packageToScan");
        
        if (packageToScan == null || packageToScan.isEmpty()) {
            System.out.println("[Listener ERROR] Le paramètre global 'packageToScan' est manquant.");
            return;
        }

        try {
            // 2. On crée la Map des URLs qui va stocker toutes les routes
            HashMap<Urlkey, Mapping> mappingUrls = new HashMap<>();

            // 3. On lance le scanner d'annotations (comme on faisait dans init())
            int totalControllers = AnnotationScanner.scanComponents(packageToScan, mappingUrls);

            // 4. TRÈS IMPORTANT : On stocke la Map et le compteur dans le CONTEXTE global de l'application
            // C'est comme une boîte partagée où le FrontController pourra venir se servir plus tard !
            context.setAttribute("mappingUrls", mappingUrls);
            context.setAttribute("totalControllersFound", totalControllers);

            System.out.println("[Listener SUCCESS] " + totalControllers + " contrôleurs chargés au démarrage !");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.out.println("[Listener CRITICAL ERROR] Échec du scan au démarrage !");
            e.printStackTrace();
            // Si ça plante ici, l'application refuse de démarrer, ce qui est parfait pour la sécurité
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[LISTENER] Application arrêtée.");
    }
}