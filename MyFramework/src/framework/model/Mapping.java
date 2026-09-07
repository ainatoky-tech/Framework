package model;

public class Mapping {
    private final String classname;
    private final String methodName;

    public Mapping(String classname,String methodName){
        this.classname=classname;
        this.methodName= methodName;
    }
    public String getClassname(){return classname;}
    public String getMethodName(){return methodName;}
}
