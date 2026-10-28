package utils;

import java.lang.reflect.Field;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import model.Mapfile;

public class ArgumentResolver {

    public static Object convertionStringFormulaireToTypeCorrespondant(String value, Class<?> type){
        if( type == Integer.class || type == int.class ) return Integer.parseInt(value);
        if( type == Long.class || type == long.class ) return Long.parseLong(value);
        if( type == Boolean.class || type == boolean.class ) return Boolean.parseBoolean(value);
        if( type == Double.class || type == double.class ) return Double.parseDouble(value);
        return value; // dans le cas ou c'est un objet de type collection(vector, list , ect) ou de type hashmap ou map ou etc ...
    }

    public static Object resolve(Class<?> type,String name, HttpServletRequest request) throws Exception{
        if(type == String.class) return request.getParameter(name);
        if(type == int.class || type  == Integer.class){
            String v = request.getParameter(name);
            return (v == null || v.isEmpty()) ? 0 :Integer.parseInt(v);
        } 
        if(type == Long.class || type == long.class){
            String v = request.getParameter(name);
            return (v == null || v.isEmpty()) ? 0L : Long.parseLong(v);
        }
        if(type == Double.class || type == double.class){
            String v = request.getParameter(name);
            return (v == null || v.isEmpty()) ? 0.0 : Double.parseDouble(v);
        }
        if(type == Boolean.class ||type == boolean.class) return  Boolean.parseBoolean(request.getParameter(name));

        /**
         * @autor : moi
         * pour l'upload de fichier*
         * le Mapfile (représente les types fichier)
         * étape : 
         *      prendre le fichier dans l'input via                                     Part part = request.getPart(name)
         *      tirer le nom du fichier (vérifier s'il a un nom) via la commande        part.getSubmittedFileName()
         *      voir le type du fichier genre .png via les méthodes de récupération     substring(index de position) après le nom du fichier
         *      l'ouvrir et lire la suite de bytes composant le fichier                 part.getInputStream().readAllByte();
         *      préparer le stockage des bytes lu dans la base de donnée avec           new Mapfile(nomfichier, extention, byte[])                              
         */
        if(type == Mapfile.class){
            Part part = request.getPart(name); 
            /**
             * Part (dans l'imput lui correspondant c'est <input type ='file'> avec cet écriteau "multiPART/form-data"): note le PART
             * getPart(name) équivalent du getParameter(name) sauf qu'au lieu de prendre du texte ou autre type d'input il reçoit un file
             */
            if(part == null || part.getSubmittedFileName() == null || part.getSubmittedFileName().isEmpty() ) return null;
            String filename = part.getSubmittedFileName();
            String extension = "";
            int pointAvantTitreExtension = filename.lastIndexOf(".");
            if(pointAvantTitreExtension > 0){
                extension = filename.substring(pointAvantTitreExtension + 1 );
                filename = filename.substring(0,pointAvantTitreExtension);
            }
            byte[] bitsComposant = part.getInputStream().readAllBytes();
            return new Mapfile(filename,extension,bitsComposant);
        }

        /**
         * pour le binding objet (la mise du nom de la classe directement en paramêtre d'une méthode) ex:
         * insertEmployer (Employer emp ) <- ça c'est le binding
         * 
         * méthode :
         *      **côté dev**
         *      créer une classe de type Employer qui contient les données du formulaire 
         *          du genre en utilisant les set de la classe model pour introduire les valeurs des champs
         *      avec le return new Employer(set(valeur1), set(valeur2), set(valeur3) ,...,set(valeurN))
         * 
         *      **côté framework(ici donc)**
         *      parcourir la nouvelle classe 
         *          chercher le constructeur de la classe mais  vide => remplir avec les valeurs contenue dans l'attribut du constructeur déclarer par le developpeur
         *          ==> return new Employer(set(valeur1), set(valeur2), set(valeur3) ,...,set(valeurN)) 
         *          avec     type.getDeclaredConstructor().newInstance();
         * 
         *      prendre toute les valeurs contenue dans les attributs trouvé                            request.getParameter(Attribut.getName())
         *      permettre l'acces à ces donnés pour une quelconque opération notamment modification     Attribut.setAccessible(true)
         *      lier la valeur contenue avec l'attribut de la classe modele                             set( (variable contenant le constructeur de la classe appelée (vide à remplir)) , valeur(correspondant à son type : int double ect ...) )
         */

        Object constructeurVide = type.getDeclaredConstructor().newInstance();
        for (Field AttributDansClasseEnAnalyse : type.getDeclaredFields()) {
            /**
             * on parcourt toute la classe pour trouvé ces attributs
             */
            String value = request.getParameter(AttributDansClasseEnAnalyse.getName());
            if(value != null){
                AttributDansClasseEnAnalyse.setAccessible(true);
                AttributDansClasseEnAnalyse.set(constructeurVide, convertionStringFormulaireToTypeCorrespondant(value, AttributDansClasseEnAnalyse.getType()));
            }
        }
        return constructeurVide;

    }
}
