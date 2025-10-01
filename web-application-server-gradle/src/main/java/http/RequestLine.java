package http;

public class RequestLine {
    private final String httpMethod;
    private final String url;
    private final String protocol;
    private final String path;
    private final String queryString;


    public RequestLine(String requestLine) {
        String[] tokens = requestLine.split(" ");
        this.httpMethod = tokens[0];
        this.url = tokens[1];
        this.protocol = tokens[2];

        char seperator = '?';
        int seperatorIndex = url.indexOf(seperator);

        if (seperatorIndex != -1) {
            this.path = url.substring(0, seperatorIndex);
            this.queryString = url.substring(seperatorIndex + 1);
        } else {
            this.path = url;
            this.queryString = "";
        }
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getPath() {
        return path;
    }

    public String getqueryString() {
        return queryString;
    }
}
