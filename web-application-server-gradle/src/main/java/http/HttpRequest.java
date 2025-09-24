package http;

import lombok.Getter;
import util.HttpRequestUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Getter
public class HttpRequest {

    private BufferedReader reader;

    private String request;
    private String httpMethod;
    private String url;
    private String requestUrl;

    private int contentLength;

    private boolean logined;

    private Map<String, String> header = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();

    public HttpRequest(InputStream in) {
        try {
            reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

            request = reader.readLine();
            if(request == null) return ;
            String[] path = request.split(" ");
            httpMethod = path[0];
            url = path[1];

            int index = url.indexOf("?");
            requestUrl = (index == -1)? url : url.substring(0, index);

            contentLength = 0;
            logined = false;

            while((request = reader.readLine()) != null && !request.isEmpty()){
                String[] parse = request.split(":");
                header.put(parse[0], parse[1].trim());

                if(header.containsKey("Content-Length")){
                    contentLength = Integer.parseInt(header.get("Content-Length"));
                }

                if(header.containsKey("Cookie")){
                    cookies = HttpRequestUtils.parseCookies(header.get("Cookie"));
                    String cookieLogined = cookies.get("logined");
                    if(cookieLogined == null) logined = false;
                    else logined = Boolean.parseBoolean(cookieLogined);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
