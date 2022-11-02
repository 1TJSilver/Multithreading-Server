import java.io.BufferedOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        final var server = new Server(9999);

        server.addHandler("GET", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {

                try {
                    final var filePath = Path.of(".", "public", request.getPath());
                    final var mimeType = Files.probeContentType(filePath);
                    final var length = Files.size(filePath);
                    responseStream.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + mimeType + "\r\n" +
                                    "Content-Length: " + length + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    responseStream.flush();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
        server.addHandler("POST", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {

                try {
                    final var filePath = Path.of(".", "public", request.getPath());
                    final var mimeType = request.getBody();
                    FileWriter writer = new FileWriter(filePath.toFile());
                    writer.write(mimeType);
                    writer.flush();
                    final var length = Files.size(filePath);
                    responseStream.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + mimeType + "\r\n" +
                                    "Content-Length: " + length + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    responseStream.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });

        server.start();
    }
}