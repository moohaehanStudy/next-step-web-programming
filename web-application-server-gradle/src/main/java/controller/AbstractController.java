package controller;

import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

public class AbstractController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(AbstractController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {

        if(request.getMethod() == HttpMethod.GET){
            doGet(request, response);
        } else if(request.getMethod() == HttpMethod.POST){
            doPost(request, response);
        } else {
            log.error("지원하지 않는 메서드입니다.");
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {}
    protected void doGet(HttpRequest request, HttpResponse response) {}
}
