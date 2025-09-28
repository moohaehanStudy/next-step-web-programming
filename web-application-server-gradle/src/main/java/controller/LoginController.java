package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class LoginController implements Controller {

    private static final String BASE_REDIRECT_URL = "/index.html";
    private static final String LOGIN_FAILED_URL = "/user/login_failed.html";

    @Override
    public void service(HttpRequest request, HttpResponse response) {

        String userId = request.getParams().get("userId");
        String password = request.getParams().get("password");

        User user = DataBase.findUserById(userId);

        if(user == null){
            response.addHeader("Set-Cookie", "logined=false");
            response.response302Header(LOGIN_FAILED_URL);
        }
        else{
            if(!user.comparePassword(password)) {
                response.addHeader("Set-Cookie", "logined=false");
                response.response302Header(LOGIN_FAILED_URL);
            }
            response.addHeader("Set-Cookie", "logined=true");
            response.response302Header(BASE_REDIRECT_URL);
        }
    }
}
