package webserver;

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

            String request = reader.readLine();
            String[] path = request.split(" ");
            String httpMethod = path[0];
            String url = path[1];

            // url 뒤에 쿼리파라미터가 존재하는지 안하는지 모릅니다.

            // 만역 일치하는 문자열이 없다면 -1, 있다면 해당 index가 반환됨
            int index = url.indexOf("?");

            String requestUrl = (index == -1)? url : url.substring(0, index);
            // 빈 문자열 주는것만으로도 메모리를 먹으니까 안좋은 선택인가?
            String params = (index == -1)? "":url.substring(index+1);

            if(requestUrl.equals("/user/create")){
                Map<String, String> paramsMap = HttpRequestUtils.parseQueryString(params);

                String userId = paramsMap.get("userId");
                String name = paramsMap.get("name");
                String password = paramsMap.get("password");
                String email = paramsMap.get("email");

                User user = new User(userId, password, name, email);
                log.debug("Create User : {}", user);
            }


            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = Files.readAllBytes(new File("./webapp"+url).toPath());
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