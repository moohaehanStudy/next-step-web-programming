package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import model.User;

@Slf4j
public class UserCreateController implements Controller {
    private static final String BASE_REDIRECT_URL = "/index.html";

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        User user = new User(
                request.getParams().get("userId"),
                request.getParams().get("name"),
                request.getParams().get("password"),
                request.getParams().get("email"));
        DataBase.addUser(user);
        response.response302Header(BASE_REDIRECT_URL);
    };
}
