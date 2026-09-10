package listener;

import model.Urlkey;
import model.Mapping;
import utils.AnnotationScanner;

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
        
        /*if (packageToScan == null || packageToScan.isEmpty()) {
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
        }*/
       if (packageToScan != null && !packageToScan.trim().isEmpty()) {
            try {
                HashMap<Urlkey, Mapping> mappingUrls = new HashMap<>();
                int totalControllers = AnnotationScanner.scanComponents(packageToScan, mappingUrls);

                context.setAttribute("mappingUrls", mappingUrls);
                context.setAttribute("totalControllersFound", totalControllers);

                System.out.println("[Listener SUCCESS] " + totalControllers + " contrôleurs chargés au démarrage !");
            } catch (Exception e) {
                System.err.println("[Listener CRITICAL ERROR] Échec du scan au démarrage !");
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("[Listener WARNING] Le paramètre 'packageToScan' est manquant dans web.xml.");
        }

        // 2. Initialisation de Spring IoC par réflexion
        try {
            Class<?> xmlContextClass = Class.forName("org.springframework.context.support.ClassPathXmlApplicationContext");
            Object springContext = xmlContextClass.getConstructor(String.class).newInstance("applicationContext.xml");  

            context.setAttribute("springContext", springContext);
            System.out.println("[Framework SUCCESS] Spring IoC initialisé avec succès !");

        } catch (ClassNotFoundException e) {
            System.out.println("[Framework INFO] Spring IoC non présent dans WEB-INF/lib. Mode standalone.");
        } catch (Exception e) {
            System.err.println("[Framework WARNING] Échec du chargement de applicationContext.xml :");
            // e.printStackTrace() est crucial ici pour voir l'erreur XML exacte dans catalina.out
            e.printStackTrace();
        }

        System.out.println("==================================================");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[LISTENER] Application arrêtée.");
    }
}