package webserver;

import controller.Controller;
import controller.CreateUserController;
import controller.ListUserController;
import controller.LoginController;
import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private Socket connection;
    private static final Map<String, Controller> controllerMap;

    static {
        controllerMap = new HashMap<>();
        controllerMap.put("/user/create", new CreateUserController());
        controllerMap.put("/user/list", new ListUserController());
        controllerMap.put("/user/login", new LoginController());
    }

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            String requestPath = getDefaultUrl(request.getUrl());
            Controller controller = controllerMap.get(requestPath);

            if(controller != null) {
                controller.service(request, response);
            } else{
                response.forward(requestPath);
                log.debug("아직 지원하지 않는 URL입니다.{}", requestPath);
            }

        }catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultUrl(String url) {
        if (url.equals("/")) {
            return "/index.html";
        }
        return url;
    }
}