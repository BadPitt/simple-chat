package ru.innopolis.course3.sever;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static ru.innopolis.course3.Const.BYE;

/**
 * @author Danil Popov
 */
public class ThreadHandler implements Runnable, Sender {

    private static final Logger logger = LoggerFactory.getLogger(ThreadHandler.class);
    private final Socket socket;
    private final GlobalSender globalSender;
    private PrintWriter writer = null;

    public ThreadHandler(Socket socket, GlobalSender globalSender) {
        this.socket = socket;
        this.globalSender = globalSender;
    }

    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             Scanner scanner = new Scanner(inputStream);
             OutputStream out = socket.getOutputStream();) {

            writer = new PrintWriter(out, true);

            boolean done = false;
            while (!done && scanner.hasNext()) {
                String str = scanner.nextLine();
                globalSender.sendToAll(str);
                if (BYE.equalsIgnoreCase(str)) {
                    done = true;
                }
            }

        } catch (IOException e) {
            logger.error("IO exception in ThreadHandler streams creation", e);
        } finally {
            writer.close();
        }
    }

    @Override
    public void send(String message) {
        if (writer != null) {
            writer.println(message);
        }
    }
}
