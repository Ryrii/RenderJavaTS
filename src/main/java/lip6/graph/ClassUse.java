package lip6.graph;

import java.awt.*;
import java.util.List;

public class ClassUse {
    private String className;
    private String parentName;
    private String packageName;
    private List<String> imports;

    public ClassUse(String className, String parentName, String packageName, List<String> imports) {
        this.className = className;
        this.parentName = parentName;
        this.packageName = packageName;
        this.imports = imports;
    }
    public String getClassName() {
        return className;
    }
    public String getParentName() {
        return parentName;
    }
    public boolean isUsed(String classQName) {
//        System.out.println("parentName : "+parentName);
//        System.out.println("classQName : "+classQName);
//        System.out.println("className : "+className);
//        System.out.println("imports : "+imports);
        if(classQName.startsWith(packageName+'.') && classQName.endsWith('.'+className)){
                return true;
        }
        for(String importName: imports){
            if (importName.endsWith("*")) {
                String importPackage = importName.substring(0, importName.length() - 1);
//               System.out.println("importPackage : "+importPackage);
               if(classQName.startsWith(importPackage)&& classQName.endsWith('.'+className)){
                   return true;
               }

            }

            if(classQName.equals(importName) && classQName.endsWith('.'+className)){
                return true;
            }
        }
        return false;
    }
    @Override
    public String toString() {
        return "\n" + "ClassUse{" +
                "className='" + className + '\'' +
                ", parentName='" + parentName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", imports=" + imports +
                '}';
    }
}
