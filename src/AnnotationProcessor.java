import annotation.Init;
import annotation.Service;
import service.LazyService;
import service.SimpleService;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AnnotationProcessor {
    private static Map<String, Object> serviceMap = new HashMap<>();
    private static Map<Boolean, Object> initedServiceMap = new HashMap<>();

    public static void main(String[] args) {
        inspectService(SimpleService.class);
        inspectService(LazyService.class);
        inspectService(String.class);
        inspectService(SimpleService.class);
        inspectService(LazyService.class);

        System.out.println();
        serviceMap.forEach((k, v) -> System.out.println(k + " " + v.getClass().getSimpleName()));
    }

    static void getServiceByName(String className){

    }

    static void loadService(String className) {
        try {
            Class<?> cls = Class.forName(className);
            if (cls.isAnnotationPresent(Service.class)) {
                Object obj = cls.newInstance();
                Service serviceAnnot = cls.getAnnotation(Service.class);
                serviceMap.put(serviceAnnot.name(), obj);
                initedServiceMap.put(true, obj);

                if(!serviceAnnot.lazyLoad()){
                    Class[] paramTypes = new Class[]{String.class};
                    Class objClass = obj.getClass();
                    String methodName = "";
                    Method method = null;
                    if (objClass.getSimpleName().equals("SimpleService")) {
                        methodName = "initService";
                        method = objClass.getDeclaredMethod(methodName, paramTypes);
                        method.setAccessible(true);
                    } else if (objClass.getSimpleName().equals("LazyService")) {
                        methodName = "lazyInit";
                        method = objClass.getMethod(methodName, paramTypes);
                    }

                    Init initAnnot = method.getAnnotation(Init.class);
                    Object[] args = new Object[]{serviceAnnot.name()};
                    try {
                        int counter = (Integer) method.invoke(obj, args);
                        System.out.println("The counter of created instance of class " + objClass.getSimpleName() + " equals " + counter);
                    } catch (InvocationTargetException ite) {
                        if (initAnnot.suppressException()) {
                            System.err.println(ite.getMessage());
                        } else {
                            throw new RuntimeException();
                        }
                    }
                }

            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException cnfe) {
            System.err.println(cnfe.getMessage());
        }
    }

    static void inspectService(Class<?> service) {
        if (service.isAnnotationPresent(Service.class)) {
            Service serviceAnnot = service.getAnnotation(Service.class);
            System.out.println(serviceAnnot.name());
            System.out.println("Result of lazy load is " + (serviceAnnot.lazyLoad() ? "true" : "false"));
            loadService(service.getName());
            System.out.println();
            Method[] methods = service.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Init.class)) {
                    System.out.println("Yep, method \"" + method.getName() + "\" has annotation Init");
                    Init initAnnot = method.getAnnotation(Init.class);
                } else {
                    System.out.println("Annotation init doesn't found in method " + method.getName());
                }
            }
        } else {
            System.out.println("Service doesn't found. Class name: " + service.getSimpleName());
        }
    }
}
