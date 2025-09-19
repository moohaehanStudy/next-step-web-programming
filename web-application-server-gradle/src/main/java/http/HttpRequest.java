package http;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {
    private String url;
    private String requestPath;

    public HttpRequest (BufferedReader b) throws IOException {
        String firstLine =  b.readLine();
        String[] firstTokens = firstLine.split(" ");
        String url = firstTokens[1];

        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
