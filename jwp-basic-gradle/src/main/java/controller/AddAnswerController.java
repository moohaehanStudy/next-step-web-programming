package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.AnswerDao;
import model.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

public class AddAnswerController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(AddAnswerController.class);

    //응답 데이터 JSON으로..?!
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Answer answer = new Answer(
                req.getParameter("writer"),
                req.getParameter("contents"),
                Long.parseLong(req.getParameter("questionId"))
        );

        AnswerDao answerDao = new AnswerDao();
        answerDao.insert(answer);

        ObjectMapper objectMapper = new ObjectMapper();
        resp.setContentType("application/json;charset=UTF-8");

        PrintWriter out = resp.getWriter();
        out.println(objectMapper.writeValueAsString(answer));

        return null;
    }
}
