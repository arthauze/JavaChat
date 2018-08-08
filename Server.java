package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static class Handler extends Thread {
        private Socket socket;
        public Handler (Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                    //System.out.println("Введите имя пользователя");
                    connection.send(new Message(MessageType.NAME_REQUEST));
                    Message message = connection.receive();

                if (message.getType() != MessageType.USER_NAME) continue;
                String userName = message.getData();
                if (userName == null || userName.isEmpty()) continue;
                if (connectionMap.containsKey(userName)) continue;

                    connectionMap.put(userName, connection);
                    System.out.printf("Ваше имя %s было принято", userName);
                    connection.send(new Message(MessageType.NAME_ACCEPTED));

                    return userName;

            }
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException {
            for (String clientName : connectionMap.keySet()) {
                if (!clientName.equals(userName))
                    connection.send(new Message(MessageType.USER_ADDED, clientName));
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType()== MessageType.TEXT) {
                    String s = userName + ": " + message.getData();
                    Message message1 = new Message(MessageType.TEXT, s);
                    sendBroadcastMessage(message1);
                } else {
                    ConsoleHelper.writeMessage("Error");
                }
            }

        }

        @Override
        public void run() {
            ConsoleHelper.writeMessage("Установлено соединение с сервером %n" + socket.getRemoteSocketAddress());
            String userName = null;
            try {
                Connection connection =  new Connection(socket);
                userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                sendListOfUsers(connection, userName);
                serverMainLoop(connection, userName);
            } catch (IOException e) {
                System.out.println("Произошла ошибка при обмене данными с удаленным адресом");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Произошла ошибка при обмене данными с удаленным адресом");
                e.printStackTrace();
            }

            if (userName != null) {
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
            }

            ConsoleHelper.writeMessage("Соединение с удаленным адресом закрыто.");
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
