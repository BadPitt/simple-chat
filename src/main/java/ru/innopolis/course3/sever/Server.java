package ru.innopolis.course3.sever;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.innopolis.course3.Const.PORT;

/**
 * @author Danil Popov
 */
public class Server implements GlobalSender {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final int port;
    private final ExecutorService service = Executors.newCachedThreadPool();
    private List<Sender> senders = new ArrayList<>();

    public static void main(String[] args) {
        new Server().startServer();
    }

    public Server() {
        this(PORT);
    }

    public Server(int port) {
        this.port = port;
    }

    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (!Thread.interrupted()) {
                Socket socket = serverSocket.accept();
                ThreadHandler handler = new ThreadHandler(socket, this);
                senders.add(handler);
                service.execute(handler);
            }
        } catch (IOException e) {
            logger.error("IO exception in Server streams creation", e);
        }
    }

    @Override
    public synchronized void sendToAll(String message) {
        for (Sender sender: senders) {
            sender.send(message);
        }
    }
}
