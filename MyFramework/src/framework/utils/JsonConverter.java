package utils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public class JsonConverter {

    public static String toJson(Object obj){
        if(obj == null) return "null";
        if(obj instanceof String) return "\""+obj.toString().replace("\"", "\\\"") + "\"";
        if(obj instanceof Number || obj instanceof Boolean) return obj.toString();
        
        if(obj instanceof Collection){
            StringBuilder stb = new StringBuilder("["); // on définit les accolades en json
            Collection<?> list = (Collection<?>) obj; // on caste l'objet dans le cas où il s'agit d'une liste
            int i =0;
            for(Object item : list){
                if(i++> 0) stb.append(",");
                stb.append(toJson(item));// appelle en récursion pour effectuer la convertion en json
                /**
                 * si l'objet de type liste contient un nombre ou un boolean la fonction toJson(obj) utilisera le 3ième if
                 */
            }
            return stb.append("]").toString();
        }

        if(obj instanceof Map){
            StringBuilder sb = new StringBuilder("{");
            Map<?,?> map = (Map<?,?>) obj; // on caste l'objet de type map
            /**
             * en apparté , ici il montre Map<?,?> indique que nous ne savons pas d'avance ce sera un 
             * Map<String,Integer> 
             * Map<String,String> 
             * Map<Object,Classe> 
             */ 
            int i = 0;
            for(Map.Entry<?,?> entrer : map.entrySet()){
                /**
                 * traduction la fonction Entry implique une clée (le premier ?) et une definition (le second ?) 
                 * entry.getKey() -> par exemple "employeListe"
                 * entry.getValue() -> par exemple la List<UserModel>
                 */
                if(i++ > 0) sb.append(",");
                sb.append("\"").append(entrer.getKey()).append("\":");
                sb.append(toJson(entrer.getValue())); // appel récursif de la fonction 
            }
            return sb.append("}").toString();
        }
        
        try {
            StringBuilder sb = new StringBuilder("{");
            Field[] fields = obj.getClass().getDeclaredFields();
            int i = 0;
            for (Field field : fields) {
                if (field.getName().startsWith("$")) continue; // ignorer les proxy
                    field.setAccessible(true); // brise l'encapsulation
                if (i++ > 0) sb.append(",");
                    sb.append("\"").append(field.getName()).append("\":").append(toJson(field.get(obj))); // appel récursif sur la valeur du champs
            }
            return sb.append("}").toString();
        } catch (Exception e) {
            return "{\"error\":\"Sérialisation impossible\"}";
        }
    }

}
