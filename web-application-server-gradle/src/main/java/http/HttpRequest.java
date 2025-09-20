package http;

import lombok.extern.slf4j.Slf4j;
import util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpRequest {
    private String url;
    private String requestPath;
    private String method;
    private Map<String, String> params = new HashMap<>();

    public HttpRequest (BufferedReader b) throws IOException {
        String firstLine =  b.readLine();
        String[] firstTokens = firstLine.split(" ");
        String url = firstTokens[1];
        method = firstTokens[0];

        log.debug("URL: " + url);

        this.url = url;

        int index = url.indexOf("?");

        if(index == -1) {
            log.debug("? 없음");
        } else{
            this.requestPath = url.substring(0, index);

            String queryString = url.substring(index + 1);

            params = HttpRequestUtils.parseQueryString(queryString);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getMethod() {
        return method;
    }
}
