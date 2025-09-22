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
    private static final String LOGIN_URL = "/user/login.html";
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
                byte[] body = Files.readAllBytes(new File("./webapp"+requestUrl).toPath());
                log.debug("Sending POST request to webserver: {}", new File("./webapp"+requestUrl).toPath());
                response302Header(dos, body.length);
                responseBody(dos, body);
            }
            else if(requestUrl.equals("/user/login")){
                // 여기서도 post 요청이기에 해당하는 값들이 넘어오는걸 파싱해서 사용해아함
                String httpBody = IOUtils.readData(reader, contentLength);
                Map<String, String> paramsMap = HttpRequestUtils.parseQueryString(httpBody);

                String userId = paramsMap.get("userId");
                String password = paramsMap.get("password");

                // DB에 있는 User 객체와 같은지 찾아야함
                User user = DataBase.findUserById(userId);
                if(user == null){
                    requestUrl = LOGIN_FAILED_URL;
                    DataOutputStream dos = new DataOutputStream(out);
                    byte[] body = Files.readAllBytes(new File("./webapp"+requestUrl).toPath());
                    response302LoginedFailedHeader(dos, body.length);
                    responseBody(dos, body);
                }
                else{
                    if(!user.getPassword().equals(password)) {
                        requestUrl = LOGIN_FAILED_URL;
                        DataOutputStream dos = new DataOutputStream(out);
                        byte[] body = Files.readAllBytes(new File("./webapp"+requestUrl).toPath());
                        response302LoginedFailedHeader(dos, body.length);
                        responseBody(dos, body);
                    }
                    requestUrl = BASE_REDIRECT_URL;
                    DataOutputStream dos = new DataOutputStream(out);
                    byte[] body = Files.readAllBytes(new File("./webapp"+requestUrl).toPath());
                    response302LoginedHeader(dos, body.length);
                    responseBody(dos, body);
                }
            }
            else if(requestUrl.equals("/user/list")){
                // 1. 쿠키 값을 이용해 해당 유저가 로그인 상태인지 판단.
                // true 라면 list.html로 false라면 로그인 페이지로 이동시킨다.
                // 리스트 항목을 보여주는 것은 StringBuilder 클래스를 활용한다.
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
                byte[] body = sb.toString().getBytes(StandardCharsets.UTF_8);
                response200Header(dos, body.length);
                responseBody(dos, body);

            }
            else {
                // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
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

    private void response302Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302LoginedFailedHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("Set-Cookie: logined=false\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302LoginedHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");
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

