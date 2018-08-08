package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.Connection;
import com.javarush.task.task30.task3008.ConsoleHelper;
import com.javarush.task.task30.task3008.Message;
import com.javarush.task.task30.task3008.MessageType;

import java.io.IOException;
import java.net.Socket;

public class Client {
    protected Connection connection;
    private volatile boolean clientConnected = false;

    protected String getServerAddress() throws IOException, ClassNotFoundException {
        System.out.println("Введите адрес сервера");
        return ConsoleHelper.readString();
    }


    protected int getServerPort() throws IOException, ClassNotFoundException {
        System.out.println("Введите порт");
    return ConsoleHelper.readInt();
    }

    protected String getUserName() throws IOException, ClassNotFoundException {
        System.out.println("Введите имя пользователя");
        return ConsoleHelper.readString();
    }

    protected boolean shouldSendTextFromConsole(){
        return true;
    }

    protected SocketThread getSocketThread() {
         return  new SocketThread();

    }

    protected void sendTextMessage(String text) {
        try {
            Message message = new Message(MessageType.TEXT, text);
            connection.send(message);
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Вывалилась ошибока");
            clientConnected = false;
        }
    }

    public void run(){
        SocketThread thread = getSocketThread();
        thread.setDaemon(true);


        try {
            synchronized (this) {
                thread.start();
                this.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            ConsoleHelper.writeMessage("При работе клиента возникла ошибка");
        }

        if (clientConnected) ConsoleHelper.writeMessage("Соединение установлено. Для выхода наберите команду ‘exit’.");
        else ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");

        while (clientConnected) {
            String line = "";
            if ((line = ConsoleHelper.readString()).equalsIgnoreCase("exit")) break;
            if (shouldSendTextFromConsole()) sendTextMessage(line);
        }
    }


    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }


    public class SocketThread extends Thread {

        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName) {
            ConsoleHelper.writeMessage(userName + " join this chat");
        }

        protected void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeMessage(userName + " hes left this chat");
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notify();
            }
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {

                switch (connection.receive().getType()) {

                    case NAME_REQUEST:
                        connection.send(new Message(MessageType.USER_NAME, getUserName()));
                        break;

                    case NAME_ACCEPTED:
                        notifyConnectionStatusChanged(true);
                        break;
                    default:
                        throw new IOException("Хули тебе надо блядь");
                }
                break;
            }
        }







    }
}
