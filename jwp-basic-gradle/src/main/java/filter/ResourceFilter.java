package filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebFilter("/*")
public class ResourceFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(ResourceFilter.class);
    private static final List<String> endUrls = new ArrayList<>();

    static{
        endUrls.add("/css");
        endUrls.add("/js");
        endUrls.add("/images");
        endUrls.add("/fonts");
        endUrls.add("/favicon.ico");
        endUrls.add("/.well-known");
    }

    private RequestDispatcher requestDispatcher;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.requestDispatcher = filterConfig.getServletContext().getNamedDispatcher("default");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String path = request.getServletPath().substring(request.getContextPath().length());

        if (isResourceUrl(path)) {
            requestDispatcher.forward(servletRequest, servletResponse);
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isResourceUrl(String path){
        for(String endUrl : endUrls){
            if(path.startsWith(endUrl)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {

    }
}
