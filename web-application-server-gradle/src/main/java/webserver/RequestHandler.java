package webserver;

import db.DataBase;
import http.HttpRequest;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            HttpRequest request = new HttpRequest(reader);
            DataOutputStream dos = new DataOutputStream(out);
            Map<String, String> queryStringToken = new HashMap<>();
            Map<String, String> bodyToken = new HashMap<>();

            String url = request.getUrl();

            if(url.startsWith("/user/create") && request.getMethod().equals("GET")) {
                queryStringToken = HttpRequestUtils.parseQueryString(request.getQueryString());

                User user = User.builder()
                        .userId(queryStringToken.get("userId"))
                        .password(queryStringToken.get("password"))
                        .name(queryStringToken.get("name"))
                        .email(queryStringToken.get("email"))
                        .build();

                DataBase.addUser(user);

                response302Header(dos, "/index.html");

                log.debug("User : {} with Method : {}", user, request.getMethod());

                return;
            } else if(request.getMethod().equals("POST")) {
                if (request.getUrl().equals("/user/create")) {
                    String body =  request.getBody();

                    bodyToken = HttpRequestUtils.parseQueryString(body);

                    User user = User.builder()
                            .userId(bodyToken.get("userId"))
                            .password(bodyToken.get("password"))
                            .name(bodyToken.get("name"))
                            .email(bodyToken.get("email"))
                            .build();

                    DataBase.addUser(user);

                    log.debug("User : {} with Method : {}", user, request.getMethod());

                    response302Header(dos, "/index.html");

                    return;
                } else if(request.getUrl().equals("/user/login")){
                    String body =  request.getBody();

                    bodyToken = HttpRequestUtils.parseQueryString(body);

                    User user = DataBase.findUserById(bodyToken.get("userId"));

                    if(user == null){
                        log.warn("User not found : {}", bodyToken.get("userId"));
                        response302HeaderWithCookie(dos, "/user/login_failed.html", "logined=failed");
                        return;
                    }else {
                        if (user.getPassword().equals(bodyToken.get("password"))) {
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

            response200Header(dos, bytes.length);
            responseBody(dos, bytes);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
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
            //Path=/ 를 붙여야 브라우저가 모든 URL 요청 시 cookie값을 보냄
            //없으면 저장되는 범위가 현재 요청한 URL과 동일한 경로에만 유효할 수 있음
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