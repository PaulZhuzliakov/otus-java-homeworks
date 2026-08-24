package hw07.api;

import com.sun.net.httpserver.HttpServer;
import hw07.registration.RegistrationService;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) throws IOException {
        RegistrationService service = RegistrationService.fromServiceLoader();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/clients", new ClientController(service));
        server.start();
        System.out.println("hw-07: сервер запущен, http://localhost:8080/api/clients");
    }
}
