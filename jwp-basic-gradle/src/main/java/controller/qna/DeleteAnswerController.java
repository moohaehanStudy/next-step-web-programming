package controller.qna;

import com.fasterxml.jackson.databind.ObjectMapper;
import controller.Controller;
import dao.AnswerDao;
import model.Answer;
import model.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class DeleteAnswerController implements Controller {

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long answerId = Long.parseLong(req.getParameter("answerId"));

        AnswerDao answerDao = new AnswerDao();
        answerDao.delete(answerId);

        ObjectMapper objectMapper = new ObjectMapper();
        resp.setContentType("application/json;charset=utf-8");

        PrintWriter out = resp.getWriter();
        out.println(objectMapper.writeValueAsString(Result.ok()));

        return null;
    }
}
