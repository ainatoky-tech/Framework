package model;

public class Mapping {
    private final String classname; // nom de la classe du contrôleur genre EmployeController
    private final String methodName;// nom de la méthode à invoquer dans EmployerController

    private final Class<?>[] parameterTypes;
    private final String [] parameterName;

    public Mapping(String classname,String methodName,Class<?>[] parameterTypes, String[] parameterName){
        this.classname=classname;
        this.methodName= methodName;
        this.parameterTypes= parameterTypes;
        this.parameterName=parameterName;
    }

    public String getClassname(){return classname;}
    public String getMethodName(){return methodName;}

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public String[] getParameterName() {
        return parameterName;
    }
    
}
