package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;

import annotation.APIRest;
import annotation.Auth;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Mapfile;
import model.Mapping;
import model.ModelView;
import model.Urlkey;

import utils.ArgumentResolver;
import utils.AnnotationScanner;
import utils.JsonConverter;

/**
 * @author moi 
 * voici en bas le mode d'emploie simplifier pour comprendre
 * il y a plus d'explication que de code mais il fonctionne 
 */
@MultipartConfig (
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 10 * 1024 * 1024,
    maxRequestSize = 50 * 1024 * 1024
)
public class FrontController extends HttpServlet {

    /**
     * afficherPanel : ne sert que pour l'affichage
     * @param response
     * @throws IOException
     */
    private void afficherPanel(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("==========================================");
        out.println("            TABLEAU DE BORD               ");
        out.println("==========================================");
        HashMap<Urlkey,Mapping>  correspondanceUrlMethodInController = (HashMap<Urlkey,Mapping>) getServletContext().getAttribute("correspondanceUrlMethodInController");
        if(correspondanceUrlMethodInController != null){
            correspondanceUrlMethodInController.forEach( (method,controller) -> 
            out.print(" méthode : [ "+ method.getMethod() + " ] " + method.getUrl() + " -> " 
            + controller.getClassname() + " . " + controller.getMethodName() + "() ")
        );

        }
    }


    

    /**
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * 
     */
    protected void processRequest (HttpServletRequest request , HttpServletResponse response ) throws ServletException,IOException{
        String URLintercepter = request.getRequestURI().substring(request.getContextPath().length());
        if(URLintercepter.equals("/") || URLintercepter.isEmpty() ){
            afficherPanel(response);
            return ;
        }

        HashMap<Urlkey,Mapping> correspondanceUrlMethodInController = (HashMap<Urlkey,Mapping>) getServletContext().getAttribute("correspondanceUrlMethodInController");
        Urlkey cheminUrlavecMethode = new Urlkey(URLintercepter, request.getMethod().toUpperCase());
        /**
         * on prend le chemin url et la méthode get ou post qu'on agrandira avec toUpperCase() post => POST ect.. ;
         */
        Mapping mapping = (correspondanceUrlMethodInController != null) ? correspondanceUrlMethodInController.get(cheminUrlavecMethode) : null;
        if (mapping == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Route introuvable : " + URLintercepter);
            return;
        }
            
        try {
            Class<?> classe = Class.forName(mapping.getClassname());
            Object controlleur = classe.getDeclaredConstructor().newInstance();
            DecouplageParSpring(controlleur);

            Method methode = classe.getDeclaredMethod(mapping.getMethodName(), mapping.getParameterTypes());
            if (!verifierAuthentification(methode, request, response)) return;

            /**
             * préparation en cas de méthode java avec plusieur argument qu'importe leur type
             */
            Object[] arguments = new Object[mapping.getParameterTypes().length];
            for (int i = 0; i < arguments.length; i++) {
                arguments[i] = ArgumentResolver.resolve(mapping.getParameterTypes()[i],mapping.getParameterName()[i],request);
            }

            /**
             * execution de la méthode dans le controlleur côté dev
             */
            Object objet = methode.invoke(controlleur, arguments);
            ExecutionMethodInController(methode,objet,request,response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur : "+e.getMessage());
        }
    }   


/**
     * 
     * @param controlleur
     * utilité de DecouplageParSpring => fonctionnement des @Truc comme @Autowired dans les Service,ect..
     * principe : appliquer au par exemple @UrlMapping créer dans annotation 
     * 
     * public class Empcontroller
     * 
     *       Autowired 
     *       private UserService userService;
     *       
     *       UrlMapping(value = "/emp/list", method = "GET")
     *       public ModelView list() {
     *           // Ton code métier
     *       }
     * 
     * 
     * le AnnotationScanner trouve private Userservice userService et public ModelView list() avec son code métier 
     * 
     * EmpController dépend de UseService qui dépend de UserRepository qui doit se connecté avec Database 
     * même principe que pour la méthode qui aura besoin des méthodes d'autre classe java 
     * 
     * résultat tu dois construire le constructeur de controlleur  comme ceci sans Autowired 
     *          public EmpController(){
     *              this.userService = new UserService(new UserRepository( new Database));
     *          }
     *  bonne chance pour écrire un truc pareil s'il y a des millions d'autre dépendance comme jsais pas mais tu dois faire des milliers de new dans une ()
     *  alors imagine écrire la méthode DecouplageParSpring à chaque méthode avec le UrlMapping
     */
    private void DecouplageParSpring(Object controlleur){
        Object springContexte = getServletContext().getAttribute("springContexte");
        if (springContexte != null) {
            try {
                Object factory = springContexte.getClass().getMethod("getAutowireCapableBeanFactory").invoke(springContexte);
                factory.getClass().getMethod("autowireBean", Object.class).invoke(factory, controlleur);
            } catch (Exception ignored) { }
        }
    } 


    private void ExecutionMethodInController(
        Method method ,
        Object objet , 
        HttpServletRequest request , 
        HttpServletResponse response) throws Exception{

        /**
         * s'il y a une méthode qui utilise APIRest (converti le en json)
         */
        if (method.isAnnotationPresent(APIRest.class)) {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            if(objet instanceof ModelView)  out.print(JsonConverter.toJson( ((ModelView) objet).getData() ) );
            else out.print(JsonConverter.toJson(objet));
            return ;
        }

        /**
         * si une méthode utilise l'upload de fichier
         */
        if(objet instanceof Mapfile){
            /**
             * force le caste en Mapfile
             * construit le nom du fichier
             * et lis les bytes du fichier
             */
            Mapfile mapfile = (Mapfile) objet;
            String name = mapfile.getFilename() + (mapfile.getExtension().isEmpty() ? "" : "."+mapfile.getExtension());
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
            response.getOutputStream().write(mapfile.getBytes());
            return ;
        }

        /**
         * s'il s'agit d'une simple redirection
         */
        if(objet instanceof ModelView){
            ModelView mv = (ModelView) objet;
            mv.getData().forEach(request::setAttribute);

            if(mv.getUrl().startsWith("redirect:")){
                response.sendRedirect(request.getContextPath() + mv.getUrl().substring(9));
                return;
            }

            String path = "/WEB-INF/jsp/" + mv.getUrl().replace(".jsp","") + ".jsp";
            request.getRequestDispatcher(path).forward(request, response);
        }
    }   


    private boolean verifierAuthentification(java.lang.reflect.Method method, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!method.isAnnotationPresent(Auth.class)) return true;

        Auth auth = method.getAnnotation(Auth.class);
        String sessionKey = (String) getServletContext().getAttribute("authSessionKey");
        String roleAttr = (String) getServletContext().getAttribute("authRoleAttribute");

        HttpSession session = request.getSession(false);
        Object user = (session != null) ? session.getAttribute(sessionKey) : null;

        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentification requise.");
            return false;
        }
        if (!auth.role().isEmpty()) {
            String role = extractRole(user, roleAttr);
            if (!auth.role().equalsIgnoreCase(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Rôle insuffisant (requis : " + auth.role() + ").");
                return false;
            }
        }
        return true;
    }

    private String extractRole(Object user, String roleAttr) {
        if (user instanceof String) return (String) user;
        try {
            String getterName = "get" + Character.toUpperCase(roleAttr.charAt(0)) + roleAttr.substring(1);
            return String.valueOf(user.getClass().getMethod(getterName).invoke(user));
        } catch (Exception e) { 
            return null; 
        }
    }

    /**
     * appel des méthode httpservletrequest
     */
    @Override 
    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        processRequest(request, response);
    }

    @Override 
    protected  void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{
        processRequest(request, response);
    }


}