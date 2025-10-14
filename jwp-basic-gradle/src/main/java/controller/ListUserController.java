package controller;

import db.DataBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.SessionUserUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ListUserController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(ListUserController.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if(SessionUserUtils.isLoggedIn(req.getSession())) {
            req.setAttribute("users", DataBase.findAll());

            return "/user/list.jsp";
        } else {
            log.error("로그인 한 회원만 목록을 볼 수 있습니다.");

            return "/user/login.jsp";
        }
    }
}
