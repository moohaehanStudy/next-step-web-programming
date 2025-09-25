package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String requestLine = br.readLine();
            if (requestLine == null || requestLine.trim().isEmpty()) { return; }

            // Request URL
            String[] tokens = requestLine.split(" ");
            String httpMethod = tokens[0];
            String url = tokens[1];
            String protocol = tokens[2];
            String path;
            String params = "";
            char seperator = '?';
            int seperatorIndex = url.indexOf(seperator);
            if (seperatorIndex != -1) {
                path = url.substring(0, seperatorIndex);
                params = url.substring(seperatorIndex + 1);
            } else {
                path = url;
            }

            // Header
            int contentLength = 0;
            while (requestLine != null && !requestLine.trim().isEmpty()) {
                log.info("Header: {}", requestLine);
                if (requestLine.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(requestLine.split(":")[1].trim());
                }
                requestLine = br.readLine();
            }

            if (httpMethod.equals("GET")) {
                if (!params.isEmpty()) {
                    Map<String, String> queryParams = HttpRequestUtils.parseQueryString(params);

                    String userId = URLDecoder.decode(queryParams.get("userId"), StandardCharsets.UTF_8);
                    String password = URLDecoder.decode(queryParams.get("password"), StandardCharsets.UTF_8);
                    String name = URLDecoder.decode(queryParams.get("name"), StandardCharsets.UTF_8);
                    String email = URLDecoder.decode(queryParams.get("email"), StandardCharsets.UTF_8);

                    User user = new User(userId, password, name, email);
                }
                DataOutputStream dos = new DataOutputStream(out);
                byte[] body = Files.readAllBytes(Paths.get("./webapp" + url));

                response200Header(dos, body.length);
                responseBody(dos, body);
            }

            if (httpMethod.equals("POST")) {

                if (contentLength > 0) {
                    String bodyContent = IOUtils.readData(br, contentLength);
                    log.info("body: {}", bodyContent);

                    Map<String, String> queryParams = HttpRequestUtils.parseQueryString(bodyContent);

                    String userId = URLDecoder.decode(queryParams.get("userId"), StandardCharsets.UTF_8);
                    String password = URLDecoder.decode(queryParams.get("password"), StandardCharsets.UTF_8);
                    String name = URLDecoder.decode(queryParams.get("name"), StandardCharsets.UTF_8);
                    String email = URLDecoder.decode(queryParams.get("email"), StandardCharsets.UTF_8);

                    User user = new User(userId, password, name, email);
                    log.info("User details - ID: {}, Name: {}, Email: {}", userId, name, email);

                    try {
                        // TODO
                        //  1. 데이터 검증
                        //  2. DB 저장

                        DataOutputStream dos = new DataOutputStream(out);
                        response302Header(dos, "/index.html");
                        log.info("success your sign up");
                    } catch (Exception e) {
                        log.error("User registration failed", e);
                    }
                }
            }

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

    private void response302Header(DataOutputStream dos, String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + redirectUrl + "\r\n");
            dos.writeBytes("Content-Length: 0\r\n");
            dos.writeBytes("\r\n");
        } catch (Exception e) {
            log.error("Failed to send 302 redirect: " + e.getMessage());
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