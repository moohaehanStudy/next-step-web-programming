package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

import java.util.Collection;

public class UserListController implements Controller {
    private static final String BASE_REDIRECT_URL = "/index.html";

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        boolean logined = request.isLogined();

        if(!logined){
            response.response302Header(BASE_REDIRECT_URL);
        }

        Collection<User> users = DataBase.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1'>");
        for(User user : users){
            sb.append("<tr>");
            sb.append("<td>" + user.getUserId() + "</td>");
            sb.append("<td>" + user.getName() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        response.forwardBody(sb.toString());
    }
}
