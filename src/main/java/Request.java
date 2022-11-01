import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Request {
    private String method;
    private String path;
    private String body;
    public Request(String method, String path, Map<String, ConcurrentHashMap<String, Handler>> handlers){
        this.path = path;
        this.method = method;
    }
    public Request(String method, String path, String body,
                   Map<String, ConcurrentHashMap<String, Handler>> handlers){
        this.path = path;
        this.method = method;
        this.body = body;
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

