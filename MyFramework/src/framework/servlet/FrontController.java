package src.framework.servlet;

import src.framework.model.*;
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
        
        // 1. On récupère la Map déjà scannée et stockée par le Listener au démarrage
        HashMap<Urlkey, Mapping> mappingUrls = (HashMap<Urlkey, Mapping>) getServletContext().getAttribute("mappingUrls");
        int totalControllersFound = (int) getServletContext().getAttribute("totalControllersFound");

        String urlInterceptee = request.getRequestURI().substring(request.getContextPath().length());
        String httpMethod = request.getMethod();

        Urlkey lookupKey = new Urlkey(urlInterceptee, httpMethod);

        // --- DASHBOARD SI ROOT ---
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
                    out.println("[" + key.getMethod() + "] " + key.getUrl() + " --> " + map.getClassname() + " --> " + map.getMethodname() + "()");
                });
            }
            return;
        }

        // 2. Recherche et exécution par Réflexion (invoke)
        Mapping mapping = mappingUrls.get(lookupKey);
        if (mapping == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Route introuvable.");
            return;
        }

        try {
            // 1. Récupération de l'instance (via le sac à dos ou une nouvelle instance selon ton code actuel)
            Class<?> clazz = Class.forName(mapping.getClassname());
            Object controllerInstance = clazz.getDeclaredConstructor().newInstance();
            java.lang.reflect.Method methodToExecute = clazz.getDeclaredMethod(mapping.getMethodname());
            
            // 2. INVOKE : On exécute la méthode et on récupère l'objet ModelView renvoyé
            Object result = methodToExecute.invoke(controllerInstance);

            // 3. TRAITEMENT DU MODELVIEW (Nouveau pour le Sprint 5)
            if (result instanceof ModelView) {
                ModelView mv = (ModelView) result;

                // On transfère chaque élément de notre Map [String, Object] vers les attributs de la requête HTTP
                mv.getData().forEach((key, value) -> {
                    request.setAttribute(key, value);
                });

                // Concaténation avec le préfixe et le suffixe demandés par ton procédé
                String prefix = "/WEB-INF/jsp/";
                String suffix = ".jsp";
                String fullPath = prefix + mv.getUrl() + suffix; // Ex: /WEB-INF/jsp/liste-employes.jsp

                // Redirection finale vers la page JSP
                request.getRequestDispatcher(fullPath).forward(request, response);
            } else {
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().println("[Framework Error] La méthode du contrôleur n'a pas renvoyé un ModelView.");
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