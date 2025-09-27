package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class CreateUserController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        User user = createUser(request.getParam("userId"), request.getParam("password"), request.getParam("name"), request.getParam("email"));

        response.sendRedirect("/index.html");

        log.debug("User : {} with Method : {}", user, request.getMethod());
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        User user = createUser(request.getParam("userId"), request.getParam("password"), request.getParam("name"), request.getParam("email"));

        log.debug("User : {} with Method : {}", user, request.getMethod());

        response.sendRedirect("/index.html");
    }

    private User createUser(String userId, String password, String name, String email) {
        User user = User.builder()
                .userId(URLDecoder.decode(userId, StandardCharsets.UTF_8))
                .password(URLDecoder.decode(password, StandardCharsets.UTF_8))
                .name(URLDecoder.decode(name, StandardCharsets.UTF_8))
                .email(URLDecoder.decode(email, StandardCharsets.UTF_8))
                .build();

        DataBase.addUser(user);

        return user;
    }
}
