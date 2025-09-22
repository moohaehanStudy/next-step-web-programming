package http;

import lombok.extern.slf4j.Slf4j;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpRequest {
    private String url;
    private String requestPath;
    private String method;
    private String queryString;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();
    private String body;

    public HttpRequest (InputStream in) throws IOException {
        BufferedReader b = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        String line =  b.readLine();
        String[] firstTokens = line.split(" ");
        url = firstTokens[1];
        method = firstTokens[0];

        log.debug("URL: " + url);

        int index = url.indexOf("?");

        if(index == -1) {
            requestPath = url;

        } else{
            requestPath = url.substring(0, index);

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

        if(method.equals("GET")) {
            params = HttpRequestUtils.parseQueryString(queryString);
        } else if(method.equals("POST")) {
            params = HttpRequestUtils.parseQueryString(body);
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

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getParam(String key) {
        return params.get(key);
    }
}
