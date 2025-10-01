package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;
import http.RequestLine;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = br.readLine();
            if (line == null || line.trim().isEmpty()) { return; }

            RequestLine requestLine = new RequestLine(line);

            String url = requestLine.getUrl();
            String httpMethod = requestLine.getHttpMethod();
            String path = requestLine.getPath();
            String queryString =requestLine.getqueryString();

            // Header
            Map<String, String> headers = new HashMap<>();
            while (!line.trim().isEmpty()) {
                log.info("header: {}", line);
                line = br.readLine();
                String[] headerTokens = line.split(":\\s*");
                if (headerTokens.length == 2) {
                    headers.put(headerTokens[0], headerTokens[1]);
                }
            }

            if (url.startsWith("/user/create")) {
                String requestBody = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
                Map<String, String> params = HttpRequestUtils.parseQueryString(requestBody);

                String userId = URLDecoder.decode(params.get("userId"), StandardCharsets.UTF_8);
                String password = URLDecoder.decode(params.get("password"), StandardCharsets.UTF_8);
                String name = URLDecoder.decode(params.get("name"), StandardCharsets.UTF_8);
                String email = URLDecoder.decode(params.get("email"), StandardCharsets.UTF_8);

                User user = new User(userId, password, name, email);
                path = "/index.html";
            } else {
                DataOutputStream dos = new DataOutputStream(out);
                byte[] body = Files.readAllBytes(Paths.get("./webapp" + path));
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = Files.readAllBytes(Paths.get("./webapp" + path));
            response302Header(dos, "/index.html");
            responseBody(dos, body);

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