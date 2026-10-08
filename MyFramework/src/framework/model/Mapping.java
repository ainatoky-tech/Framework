package model;

public class Mapping {
    private final String classname; // nom de la classe du contrôleur genre EmployeController
    private final String methodName;// nom de la méthode à invoquer dans EmployerController

    private final Class<?>[] parameterClasses;
    private final String [] parameterName;

    public Mapping(String classname,String methodName,Class<?>[] parameterClasses, String[] parameterName){
        this.classname=classname;
        this.methodName= methodName;
        this.parameterClasses= parameterClasses;
        this.parameterName=parameterName;
    }

    public String getClassname(){return classname;}
    public String getMethodName(){return methodName;}

    public Class<?>[] getParameterClasses() {
        return parameterClasses;
    }

    public String[] getParameterName() {
        return parameterName;
    }
    
}
