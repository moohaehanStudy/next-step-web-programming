package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.util.Map;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String body =  request.getBody();
        Map<String, String> bodyToken = HttpRequestUtils.parseQueryString(body);
        User user = DataBase.findUserById(bodyToken.get("userId"));

        if(user == null){
            log.warn("User not found : {}", bodyToken.get("userId"));
            response.response302Header("/user/login_failed.html", "logined=failed; Path=/");
        }else {
            if (user.comparePassword(bodyToken.get("password"))) {
                log.debug("Login Success : {}", user.getUserId());
                response.response302Header("/index.html", "logined=true; Path=/");
            } else {
                log.debug("Login Failed : {}", user.getUserId());
                response.response302Header("/user/login_failed.html", "logined=failed; Path=/");
            }
        }
    }
}
