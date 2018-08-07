package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static class Handler extends Thread {
        private Socket socket;


        public Handler (Socket socket) {
            this.socket = socket;
        }

    }

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void sendBroadcastMessage(Message message) {

        connectionMap.forEach((k,v)->{
            try {
                v.send(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    public static void main(String[] args) throws IOException {
        ConsoleHelper consoleHelper = new ConsoleHelper();
        int port = consoleHelper.readInt();
        ServerSocket serverSocket = new ServerSocket(port);
        consoleHelper.writeMessage("Сервер запущен");
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                new Handler(socket).start();
            }
        } catch (IOException e) {
            System.out.println("Ошибка где-то здесь, братан");
        } finally {
            serverSocket.close();
        }


    }
}
