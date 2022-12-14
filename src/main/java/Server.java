import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private ServerSocket server;
    Map<String, ConcurrentHashMap<String, Handler>> handlers;
    private final int port;
    private final ExecutorService poolExecutor;

    public Server(int port) {
        poolExecutor = Executors.newFixedThreadPool(64);
        this.port = port;
        handlers = new ConcurrentHashMap<>();
    }

    public void addHandler(String method, String filename, Handler handler) {
        if (!handlers.containsKey(method)) {
            handlers.put(method, new ConcurrentHashMap<>());
        }
        handlers.get(method).put(filename, handler);
        File file = new File("./public/" + filename);
        try {
            if (file.createNewFile()) {
                System.out.println("Add new Handler: " + filename);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void start() {
        try {
            server = new ServerSocket(port);
            while (true) {
                accept();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public Map<String, ConcurrentHashMap<String, Handler>> getHandlers() {
        return handlers;
    }

    public void accept() {
        poolExecutor.execute(() -> {
            try (
                    final var socket = server.accept();
                    final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    final var out = new BufferedOutputStream(socket.getOutputStream());
            ) {
                // must be in form GET /path HTTP/1.1
                final var requestLine = in.readLine();
                final var parts = requestLine.split(" ");

                if (parts.length != 3) {
                    return;
                }

                final var path = parts[1];

                Request request = new Request(parts[0], path, handlers);
                send(request, out);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean send(Request request, BufferedOutputStream out) {
        try {
            if (handlers.containsKey(request.getMethod()) ||
                    handlers.get(request.getMethod()).containsKey(request.getPath())
            || handlers.get(request.getMethod()).get(request.getPath()) == null) {
                String errorMsg = "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n";
                System.out.println(errorMsg);
                out.write((errorMsg).getBytes());
                out.flush();
                return false;
            } else {
                Handler handler = handlers.get(request.getMethod()).get(request.getPath());
                handler.handle(request, out);
                out.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return true;
    }
}
