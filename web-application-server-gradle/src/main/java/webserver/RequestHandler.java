package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

            String request = reader.readLine();
            if (request == null) return ;
            String[] path = request.split(" ");
            String httpMethod = path[0];
            String url = path[1];

            int index = url.indexOf("?");
            String requestUrl = (index == -1)? url : url.substring(0, index);


            int contentLength = 0;
            boolean logined = false;

            while((request = reader.readLine()) != null && !request.isEmpty()){
                String[] parse = request.split(":");
                Map<String, String> header = new HashMap<>();
                header.put(parse[0], parse[1].trim());

                if(header.containsKey("Content-Length")){
                    contentLength = Integer.parseInt(header.get("Content-Length"));
                }

                if(header.containsKey("Cookie")){
                    Map<String, String> cookies = HttpRequestUtils.parseCookies(header.get("Cookie"));
                    String cookieLogined = cookies.get("logined");
                    if(cookieLogined == null) logined = false;
                    else logined = Boolean.parseBoolean(cookieLogined);
                }
            }

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

                requestUrl = BASE_REDIRECT_URL;

                DataOutputStream dos = new DataOutputStream(out);
                log.debug("Sending POST request to webserver: {}", new File("./webapp"+requestUrl).toPath());
                response302Header(dos, requestUrl);
            }
            else if(requestUrl.equals("/user/login")){
                String httpBody = IOUtils.readData(reader, contentLength);
                Map<String, String> paramsMap = HttpRequestUtils.parseQueryString(httpBody);

                String userId = paramsMap.get("userId");
                String password = paramsMap.get("password");

                User user = DataBase.findUserById(userId);
                if(user == null){
                    requestUrl = LOGIN_FAILED_URL;
                    DataOutputStream dos = new DataOutputStream(out);
                    response302LoginedFailedHeader(dos, requestUrl);
                }
                else{
                    if(!user.comparePassword(password)) {
                        requestUrl = LOGIN_FAILED_URL;
                        DataOutputStream dos = new DataOutputStream(out);
                        response302LoginedFailedHeader(dos, requestUrl);
                    }
                    requestUrl = BASE_REDIRECT_URL;
                    DataOutputStream dos = new DataOutputStream(out);
                    response302LoginedHeader(dos, requestUrl);
                }
            }
            else if(requestUrl.equals("/user/list")){
                if(!logined){
                    requestUrl = BASE_REDIRECT_URL;
                    DataOutputStream dos = new DataOutputStream(out);
                    byte[] body = Files.readAllBytes(new File("./webapp"+requestUrl).toPath());
                    response200Header(dos, body.length);
                    responseBody(dos, body);
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
                DataOutputStream dos = new DataOutputStream(out);
                byte[] body = sb.toString().getBytes();
                response200Header(dos, body.length);
                responseBody(dos, body);

            }
            else if(url.endsWith(".css")){
                DataOutputStream dos = new DataOutputStream(out);
                byte[] body = Files.readAllBytes(new File("./webapp"+requestUrl).toPath());
                response200CssHandler(dos, body.length);
                responseBody(dos, body);
            }
            else {
                DataOutputStream dos = new DataOutputStream(out);
                byte[] body = Files.readAllBytes(new File("./webapp"+requestUrl).toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
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

    private void response302Header(DataOutputStream dos, String locationUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Location: " + locationUrl + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302LoginedFailedHeader(DataOutputStream dos, String locationUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Set-Cookie: logined=false\r\n");
            dos.writeBytes("Location: " + locationUrl + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302LoginedHeader(DataOutputStream dos, String locationUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");
            dos.writeBytes("Location: " + locationUrl + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200CssHandler(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css\r\n");
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

