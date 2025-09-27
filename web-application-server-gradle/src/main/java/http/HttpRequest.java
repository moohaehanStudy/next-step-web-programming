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
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();
    private String body;
    private RequestLine requestLine;

    public HttpRequest (InputStream in) throws IOException {
        BufferedReader b = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String line =  b.readLine();

        requestLine = new RequestLine(line);

        //요청 헤더 읽기
        while ((line = b.readLine()) != null && !line.isEmpty()) {
            String [] tokens = line.split(":");
            headers.put(tokens[0].trim(), tokens[1].trim());
        }

        if(HttpMethod.GET == getMethod()) {
            params = requestLine.getParams();
        } else if(HttpMethod.POST == getMethod()) {
            body = IOUtils.readData(b, Integer.parseInt(headers.get("Content-Length")));
            params = HttpRequestUtils.parseQueryString(body);
        }
    }

    public String getUrl() {
        return requestLine.getUrl();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
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
