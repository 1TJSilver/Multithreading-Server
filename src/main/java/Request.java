import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;


public class Request {
    private List<NameValuePair> nameValueParams;
    private String method;
    private String path;
    private String body;

    public Request(String method, String path, Map<String, ConcurrentHashMap<String, Handler>> handlers) {
        this.path = path;
        this.method = method;
        nameValueParams = getQueryParams();
    }

    public Request(String method, String path, String body,
                   Map<String, ConcurrentHashMap<String, Handler>> handlers) {
        this.path = path;
        this.method = method;
        this.body = body;
        nameValueParams = getQueryParams();
    }

    public List<NameValuePair> getPostParams() {
        return URLEncodedUtils.parse(body, StandardCharsets.UTF_8);
    }

    public List<String> getPostParam(String name) {
        List<String> result = new ArrayList<>();
        for (NameValuePair pair : nameValueParams) {
            if (pair.getName().equals(name)) {
                result.add(pair.getValue());
            }
        }
        return result;
    }

    public List<NameValuePair> getQueryParams() {
        return URLEncodedUtils.parse(path, StandardCharsets.UTF_8);
    }

    public List<String> getQueryParam(String name) {
        List<String> result = new ArrayList<>();
        for (NameValuePair pair : nameValueParams) {
            if (pair.getName().equals(name)) {
                result.add(pair.getValue());
            }
        }
        return result;
    }

    public String getPath() {
        return path;
    }

    public String getBody() {
        return body;
    }

    public String getMethod() {
        return method;
    }
}


