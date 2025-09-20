package http;

import lombok.extern.slf4j.Slf4j;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpRequest {
    private String url;
    private String requestPath;
    private String method;
    private String queryString;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public HttpRequest (BufferedReader b) throws IOException {
        String line =  b.readLine();
        String[] firstTokens = line.split(" ");
        String url = firstTokens[1];
        method = firstTokens[0];

        log.debug("URL: " + url);

        this.url = url;

        int index = url.indexOf("?");

        if(index == -1) {
            log.debug("? 없음");
        } else{
            this.requestPath = url.substring(0, index);

            queryString = url.substring(index + 1);
        }

        //요청 헤더 읽기
        while ((line = b.readLine()) != null && !line.isEmpty()) {
            String [] tokens = line.split(":");

            headers.put(tokens[0].trim(), tokens[1].trim());
        }

        //body 읽기
        if(headers.containsKey("Content-Length")){
            int length = Integer.parseInt(headers.get("Content-Length"));
            body = IOUtils.readData(b, length);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
