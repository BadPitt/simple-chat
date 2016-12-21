package ru.innopolis.course3.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import static ru.innopolis.course3.Const.BYE;
import static ru.innopolis.course3.Const.HOST;
import static ru.innopolis.course3.Const.PORT;

/**
 * @author Danil Popov
 */
public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private final String host;
    private final int port;

    public static void main(String[] args) {
        new Client(HOST, PORT).startClient();
    }

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void startClient() {
        try (Socket socket = new Socket(host, port);
             InputStream is = socket.getInputStream();
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             Scanner inScanner = new Scanner(System.in)) {

            ReaderThread t = new ReaderThread(is, System.out);
            t.setDaemon(true);
            t.start();
            boolean done = false;
            while (!done && inScanner.hasNext()) {
                String str = inScanner.nextLine();
                writer.println(str);
                if (BYE.equalsIgnoreCase(str)) {
                    done = true;
                }
            }

        } catch (UnknownHostException e) {
            logger.error("Unknown host exception in Client socket creation", e);
        } catch (IOException e) {
            logger.error("IO exception in Client streams creation", e);
        }
    }

    private static class ReaderThread extends Thread {

        private final Scanner scanner;
        private final PrintWriter writer;

        public ReaderThread(InputStream inputStream, OutputStream outputStream) {
            this.scanner = new Scanner(inputStream);
            this.writer = new PrintWriter(outputStream, true);
        }

        @Override
        public void run() {
            while (scanner.hasNext()) {
                String str = scanner.nextLine();
                writer.println(str);
            }
        }
    }
}
