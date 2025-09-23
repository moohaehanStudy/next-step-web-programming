package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import util.HttpRequestUtils;

import java.util.Collection;
import java.util.Map;

public class ListUserController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String cookie = request.getHeaders().get("Cookie");
        String url = request.getUrl();

        Map<String, String> headerToken = HttpRequestUtils.parseCookies(cookie);

        StringBuilder sb = new StringBuilder();

        if (headerToken.containsKey("logined") && headerToken.get("logined").equals("true")) {
            Collection<User> users = DataBase.findAll();

            sb.append("<html>");
            sb.append("<head><meta charset=\"UTF-8\"><title>Users</title></head>");
            sb.append("<body>");
            sb.append("<h2>사용자 목록</h2>");
            sb.append("<table border='1'>");
            sb.append("<tr><th>userId</th><th>name</th><th>email</th></tr>");

            for (User u : users) {
                sb.append("<tr>");
                sb.append("<td>").append(u.getUserId()).append("</td>");
                sb.append("<td>").append(u.getName()).append("</td>");
                sb.append("<td>").append(u.getEmail()).append("</td>");
                sb.append("</tr>");
            }

            sb.append("</table>");
            sb.append("</body>");
            sb.append("</html>");

            response.response200Header(sb.length(), url);
            response.responseBody(sb.toString().getBytes());
        }else if(!headerToken.containsKey("logined") || headerToken.get("logined").equals("failed")) {
            response.addHeader("Set-Cookie", "logined=failed");
            response.sendRedirect("/user/login.html");
        }
    }
}
