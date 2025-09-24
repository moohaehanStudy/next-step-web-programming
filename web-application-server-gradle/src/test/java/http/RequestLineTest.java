package http;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestLineTest {
    @Test
    public void create_method(){
        RequestLine line = new RequestLine("GET /index.html HTTP/1.1");

        assertEquals("GET", line.getMethod());
        assertEquals("/index.html", line.getUrl());

        line = new RequestLine("POST /index.html HTTP/1.1");
        assertEquals("/index.html", line.getUrl());
    }

    @Test
    public void create_path_and_params(){
        RequestLine line = new RequestLine("GET /user/create?userId=soyun&password=1234 HTTP/1.1");

        assertEquals("GET", line.getMethod());
        assertEquals("/user/create", line.getUrl());
        Map<String, String> params = line.getParams();

        assertEquals(2, params.size());
    }
}
