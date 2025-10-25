package controller;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private Map<String, Controller> controllers = new HashMap<>();

    public void initControllers() {
        controllers.put("/", new HomeController());
        controllers.put("/user/create", new CreateUserController());
        controllers.put("/user/form", new ForwardController("/user/form.jsp"));
        controllers.put("/user/list", new ListUserController());
        controllers.put("/user/loginForm", new ForwardController("/user/login.jsp"));
        controllers.put("/user/login", new LoginUserController());
        controllers.put("/user/logout", new LogoutUserController());
        controllers.put("/user/update", new UpdateUserController());
        controllers.put("/user/updateForm", new ForwardController("/user/updateForm.jsp"));
        controllers.put("/api/qna/addAnswer", new AddAnswerController());
        controllers.put("/qna/show", new QuestionController());
    }

    public Controller getController(String url){
        return controllers.get(url);
    }
}
