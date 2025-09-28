package http;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
public class RequestLine {

    private String url;
    private String requestUrl;
    private HttpMethod httpMethod;
    private Map<String, String> params = new HashMap<>();

    public RequestLine(String requestLine) {
        String[] path = requestLine.split(" ");
        httpMethod = HttpMethod.valueOf(path[0]);

        if (httpMethod == HttpMethod.POST) {
            url = path[1];
            return;
        }

        int index = url.indexOf("?");
        if (index == -1) {
            requestUrl = path[1];
        } else {
            requestUrl = path[1].substring(0, index);
            params = HttpRequestUtils.parseQueryString(path[1].substring(index + 1));
        }
    }
}
