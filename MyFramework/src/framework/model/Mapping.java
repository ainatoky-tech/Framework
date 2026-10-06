package model;

public class Mapping {
    private final String classname; // nom de la classe du contrôleur genre EmployeController
    private final String methodName;// nom de la méthode à invoquer dans EmployerController

    public Mapping(String classname,String methodName){
        this.classname=classname;
        this.methodName= methodName;
    }
    public String getClassname(){return classname;}
    public String getMethodName(){return methodName;}
}
