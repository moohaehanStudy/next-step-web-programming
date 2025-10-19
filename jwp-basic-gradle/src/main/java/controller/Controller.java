package controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface Controller {
    //반환값이 redirect: 면 리다이렉트, 포어드면 그냥 경로 값
    String execute(HttpServletRequest req, HttpServletResponse resp) throws Exception;
}
