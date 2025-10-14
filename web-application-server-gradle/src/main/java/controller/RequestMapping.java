package controller;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private static Map<String, Controller> controllerMap;

    static {
        controllerMap = new HashMap<>();
        controllerMap.put("/user/create", new CreateUserController());
        controllerMap.put("/user/list", new ListUserController());
        controllerMap.put("/user/login", new LoginController());
    }


    public static Controller get(String requestPath) {
        return controllerMap.get(requestPath);
    }
}
