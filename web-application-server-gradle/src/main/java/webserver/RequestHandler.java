package webserver;

import db.DataBase;
import http.HttpRequest;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
            DataOutputStream dos = new DataOutputStream(out);

            String url = request.getUrl();
            String requestPath = request.getRequestPath();

            if(request.getMethod().equals("GET")) {
                if(requestPath.equals("/user/create")) {

                    User user = createUser(request.getParam("userId"), request.getParam("password"), request.getParam("name"), request.getParam("email"));

                    response302Header(dos, "/index.html");

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

                        response200Header(dos, sb.length(), url);
                        responseBody(dos, sb.toString().getBytes());
                    }else if(!headerToken.containsKey("logined") || headerToken.get("logined").equals("failed")) {
                        response302HeaderWithCookie(dos, "/user/login.html", "logined=failed");
                    }
                }
            } else if(request.getMethod().equals("POST")) {
                if (requestPath.equals("/user/create")) {
                    User user = createUser(request.getParam("userId"), request.getParam("password"), request.getParam("name"), request.getParam("email"));

                    log.debug("User : {} with Method : {}", user, request.getMethod());

                    response302Header(dos, "/index.html");

                    return;
                } else if(requestPath.equals("/user/login")){
                    String body =  request.getBody();

                    Map<String, String> bodyToken = HttpRequestUtils.parseQueryString(body);

                    User user = DataBase.findUserById(bodyToken.get("userId"));

                    if(user == null){
                        log.warn("User not found : {}", bodyToken.get("userId"));
                        response302HeaderWithCookie(dos, "/user/login_failed.html", "logined=failed");
                        return;
                    }else {
                        if (user.comparePassword(bodyToken.get("password"))) {
                            log.debug("Login Success : {}", user.getUserId());
                            response302HeaderWithCookie(dos, "/index.html", "logined=true");
                            return;
                        } else {
                            log.debug("Login Failed : {}", user.getUserId());
                            response302HeaderWithCookie(dos, "/user/login_failed.html", "logined=failed");
                            return;
                        }
                    }
                }
            } else{
                log.debug("아직 지원하지 않는 방식입니다.");
            }

            log.info("Header: {}", request.getHeaders());

            byte[] bytes = Files.readAllBytes(new File("./webapp" + url).toPath());

            response200Header(dos, bytes.length, url);
            responseBody(dos, bytes);

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

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String url) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: "+ getContentType(url) + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getContentType(String url) {
        String type = "text/html;charset=UTF-8";

        if(url.endsWith(".css"))
            type = "text/css";

        return type;
    }

    private void response302Header(DataOutputStream dos, String location) {
        try{
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String location, String cookie) {
        try{
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "; Path=/\r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}