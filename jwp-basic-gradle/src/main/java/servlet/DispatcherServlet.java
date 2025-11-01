package servlet;

import controller.Controller;
import controller.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.ModelAndView;
import view.View;

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
        log.debug("요청URI는: {}", req.getRequestURI());

        Controller controller = findController(req.getRequestURI());
        log.debug("Found controller: {}", controller);

        if(controller == null){
            log.warn("No controller found for URI: {}", req.getRequestURI());
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            ModelAndView mav = controller.execute(req, res);
            View view = mav.getView();
            view.render(mav.getModel(), req, res);

        } catch(Throwable e){
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage());
        }
    }
}
