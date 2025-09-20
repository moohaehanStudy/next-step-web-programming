package webserver;

import http.HttpRequest;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
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

            String url = request.getUrl();

            if(url.startsWith("/user/create") && request.getMethod().equals("GET")) {
                Map<String, String> queryStringToken = HttpRequestUtils.parseQueryString(request.getQueryString());

                User user = User.builder()
                        .userId(queryStringToken.get("userId"))
                        .password(queryStringToken.get("password"))
                        .name(queryStringToken.get("name"))
                        .email(queryStringToken.get("email"))
                        .build();

                log.debug("User : {} with Method : {}", user, request.getMethod());
            } else if(url.startsWith("/user/create") && request.getMethod().equals("POST")) {
                String body =  request.getBody();

                Map<String, String> bodyToken = HttpRequestUtils.parseQueryString(body);

                User user = User.builder()
                        .userId(bodyToken.get("userId"))
                        .password(bodyToken.get("password"))
                        .name(bodyToken.get("name"))
                        .email(bodyToken.get("email"))
                        .build();

                log.debug("User : {} with Method : {}", user, request.getMethod());

            } else{
                log.debug("아직 지원하지 않는 방식입니다.");
            }

            byte[] bytes = Files.readAllBytes(new File("./webapp" + url).toPath());

            DataOutputStream dos = new DataOutputStream(out);
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}