package webserver;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            String url = request.getUrl();
            String requestPath = request.getRequestPath();

            if(request.getMethod().equals("GET")) {
                if(requestPath.equals("/user/create")) {

                    User user = createUser(request.getParam("userId"), request.getParam("password"), request.getParam("name"), request.getParam("email"));

                    response.sendRedirect("/index.html");

                    log.debug("User : {} with Method : {}", user, request.getMethod());

                    return;
                } else if(requestPath.equals("/user/list")){
                    String cookie = request.getHeaders().get("Cookie");

                    Map<String, String> headerToken = HttpRequestUtils.parseCookies(cookie);

                    StringBuilder sb = new StringBuilder();

                    if(headerToken.containsKey("logined") && headerToken.get("logined").equals("true")) {
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
            } else if(request.getMethod().equals("POST")) {
                if (requestPath.equals("/user/create")) {
                    User user = createUser(request.getParam("userId"), request.getParam("password"), request.getParam("name"), request.getParam("email"));

                    log.debug("User : {} with Method : {}", user, request.getMethod());

                    response.sendRedirect("/index.html");

                    return;
                } else if(requestPath.equals("/user/login")){
                    String body =  request.getBody();

                    Map<String, String> bodyToken = HttpRequestUtils.parseQueryString(body);

                    User user = DataBase.findUserById(bodyToken.get("userId"));

                    if(user == null){
                        log.warn("User not found : {}", bodyToken.get("userId"));
                        response.addHeader("Set-Cookie", "logined=failed; Path=/");
                        response.sendRedirect("/user/login_failed.html");
                        return;
                    }else {
                        if (user.comparePassword(bodyToken.get("password"))) {
                            log.debug("Login Success : {}", user.getUserId());
                            response.addHeader("Set-Cookie", "logined=true; Path=/");
                            response.sendRedirect("/index.html");
                            return;
                        } else {
                            log.debug("Login Failed : {}", user.getUserId());
                            response.addHeader("Set-Cookie", "logined=failed; Path=/");
                            response.sendRedirect("/user/login.html");
                            return;
                        }
                    }
                }
            } else{
                log.debug("아직 지원하지 않는 방식입니다.");
            }

            log.info("Header: {}", request.getHeaders());

            response.forward(url);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
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