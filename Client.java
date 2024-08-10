import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                
                // Create a new thread for each client request
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
            InputStream input = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = clientSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true)
        ) {
            // Read the request from the client
            String requestLine = reader.readLine();
            System.out.println("Request: " + requestLine);

            // Send a simple HTTP response
            String httpResponse = "HTTP/1.1 200 OK\r\n" +
                                  "Content-Type: text/html\r\n" +
                                  "\r\n" +
                                  "<html><body><h1>Hello, World!</h1></body></html>";
            writer.println(httpResponse);

        } catch (IOException e) {
            System.out.println("Client handler exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Failed to close socket: " + e.getMessage());
            }
        }
    }
}
