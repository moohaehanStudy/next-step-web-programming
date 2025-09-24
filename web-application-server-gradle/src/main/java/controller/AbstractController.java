package controller;

import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;

public class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {

        if(request.getMethod() == HttpMethod.GET){
            doGet(request, response);
        } else if(request.getMethod() == HttpMethod.POST){
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {}
    protected void doGet(HttpRequest request, HttpResponse response) {}
}
