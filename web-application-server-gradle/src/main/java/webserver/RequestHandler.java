package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

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

            // RequestLine
            String line = br.readLine();
            log.info("request line : {}", line);
            if (line == null) {return;}

            String[] tokens = line.split(" ");
            for (int i = 0; i < tokens.length; i++) {log.info("tokens : {}", tokens[i]);}
            String url = tokens[1];

            // Request URI
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

            if (!params.isEmpty()) {
                Map<String, String> queryParams = HttpRequestUtils.parseQueryString(params);

                String userId = URLDecoder.decode(queryParams.get("userId"), StandardCharsets.UTF_8);
                String password = URLDecoder.decode(queryParams.get("password"), StandardCharsets.UTF_8);
                String name = URLDecoder.decode(queryParams.get("name"), StandardCharsets.UTF_8);
                String email = URLDecoder.decode(queryParams.get("email"), StandardCharsets.UTF_8);

                User user = new User(userId, password, name, email);
                log.info("User details - ID: {}, Name: {}, Email: {}", userId, name, email);
            }

            // Header
            while (!"".equals(line) && line != null) {
                line = br.readLine();
                log.info("Header: {}", line);
            }

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = Files.readAllBytes(Paths.get("./webapp" + url));

            response200Header(dos, body.length);
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}