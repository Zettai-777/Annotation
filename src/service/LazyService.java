package service;

import annotation.Init;
import annotation.Service;

@Service(name="Igor")
public class LazyService {
    private static int counter;
    private boolean isServiceInit = false;

    @Init
    public int lazyInit(String name) throws Exception{
        System.out.println("The lazy service starting right now");
        isServiceInit = true;
        return ++counter;
    }
}
