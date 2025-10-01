package servlet;

import controller.Controller;
import controller.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "dispatcher", urlPatterns = "/", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);
    public static final String REDIRECT = "redirect:";

    private RequestMapping requestMapping;
    @Override
    public void init(){
        requestMapping = new RequestMapping();
        requestMapping.initControllers();
    }

    public Controller findController(String url){
        return requestMapping.getController(url);
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        init();
        log.debug("요청URI는: {}", req.getRequestURI());

        Controller controller = findController(req.getRequestURI());
        String value = controller.execute(req, res);

        if(value.startsWith(REDIRECT)){
            String path = value.substring(REDIRECT.length());
            res.sendRedirect(path);
        } else{
            RequestDispatcher dispatcher = req.getRequestDispatcher(value);
            dispatcher.forward(req, res);
        }
    }
}
