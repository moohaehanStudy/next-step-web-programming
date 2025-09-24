package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {
    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

    private HttpMethod method;
    private String url;
    private Map<String, String> params = new HashMap<>();

    public RequestLine(String requestLine){
        log.debug("request line: {}", requestLine);

        String[] firstTokens = requestLine.split(" ");

        if(firstTokens.length != 3){
            throw new IllegalArgumentException(requestLine + "이 형식에 맞지 않습니다.");
        }

        method = HttpMethod.valueOf(firstTokens[0]);
        if(method == HttpMethod.POST){
            url = firstTokens[1];
            log.debug("url: {}", url);
            return;
        }

        int index = firstTokens[1].indexOf("?");

        if(index == -1) {
            url = firstTokens[1];

        } else{
            url = firstTokens[1].substring(0, index);
            params = HttpRequestUtils.parseQueryString(firstTokens[1].substring(index + 1));
        }

        log.debug("url: {}", url);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
