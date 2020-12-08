package service;

import annotation.Init;
import annotation.Service;

@Service(name = "Sanches")
public class SimpleService {
    private static int counter = 0;
    private boolean isServiceInit = false;

    @Init
    private int initService(String name){
        System.out.println("The simple service starting right now");
        isServiceInit = true;
        return ++counter;
    }

    public void emptyMethod(){
        System.out.println("Hello, i'm f*cking empty inside...");
    }
}
