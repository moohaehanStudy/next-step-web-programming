package webserver;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    private static final String BASE_REDIRECT_URL = "/index.html";
    private static final String LOGIN_FAILED_URL = "/user/login_failed.html";

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
            String requestUrl = request.getRequestUrl();
            int contentLength = request.getContentLength();
            boolean logined = request.isLogined();
            BufferedReader reader = request.getReader();

            if(requestUrl.equals("/user/create")){
                String httpBody = IOUtils.readData(reader, contentLength);

                Map<String, String> paramsMap = HttpRequestUtils.parseQueryString(httpBody);

                String userId = paramsMap.get("userId");
                String name = paramsMap.get("name");
                String password = paramsMap.get("password");
                String email = paramsMap.get("email");

                User user = new User(userId, password, name, email);
                log.debug("Create User : {}", user);

                DataBase.addUser(user);

                response.response302Header(BASE_REDIRECT_URL);
            }
            else if(requestUrl.equals("/user/login")){
                String httpBody = IOUtils.readData(reader, contentLength);
                Map<String, String> paramsMap = HttpRequestUtils.parseQueryString(httpBody);

                String userId = paramsMap.get("userId");
                String password = paramsMap.get("password");

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
            else if(requestUrl.equals("/user/list")){
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
            else {
                response.forward(requestUrl);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


}

