package controller;

import view.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Controller {
    //반환값이 redirect: 면 리다이렉트, 포어드면 그냥 경로 값
    View execute(HttpServletRequest req, HttpServletResponse resp) throws Exception;
}
