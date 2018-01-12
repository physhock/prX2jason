import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.stream.Collectors;

public class Validator implements Server {

    @NotNull
    private final HttpServer server;
    @NotNull
    private final Gson builder;

    private static final int PORT = 7777;
    private static final int CODE_OK = 200;
    private static final String ROOT = "/";

    public Validator() throws IOException {
        this.builder = new GsonBuilder().setPrettyPrinting().create();
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.server.createContext(ROOT, http -> {
            int request_id = 0;
            InputStreamReader isr = new InputStreamReader(http.getRequestBody());
            final String jsonRequest = new BufferedReader(isr).lines().collect(Collectors.joining());
            System.out.println("request:" + jsonRequest);
            String jsonResponse;
            try {
                Object object = builder.fromJson(jsonRequest, Object.class);
                jsonResponse = builder.toJson(object);
            } catch (JsonSyntaxException e) {
                String[] errorSplittedString = e.getMessage().split(".+: | at ");
                jsonResponse = builder.toJson(
                        new JaEr(
                                e.hashCode(),
                                errorSplittedString[1],
                                "at " + errorSplittedString[2],
                                http.getRequestURI().getPath(),
                                request_id
                        ));
            } finally {
                request_id++;
            }
            System.out.println("response:" + jsonResponse);
            http.sendResponseHeaders(CODE_OK, jsonResponse.length());
            http.getResponseBody().write(jsonResponse.getBytes());
            http.close();
        });
    }

    public static void main(String[] args) throws IOException {
        Validator validator = new Validator();
        validator.start();
        Runtime.getRuntime().addShutdownHook(new Thread(validator::stop));
    }

    @Override
    public void start() {
        this.server.start();
    }

    @Override
    public void stop() {
        this.server.stop(0);
    }
}
