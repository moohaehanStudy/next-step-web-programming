package controller;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private static final Map<String, Controller> controllers = new HashMap<>();

    static{
        controllers.put("/user/create", new UserCreateController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/list", new UserListController());
    }

    public static Controller getController(String requestUrl){
        return controllers.get(requestUrl);
    }
}
