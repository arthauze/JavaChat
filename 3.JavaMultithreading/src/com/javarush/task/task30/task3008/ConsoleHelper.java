package com.javarush.task.task30.task3008;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader bufferedReader =
            new BufferedReader(new InputStreamReader(System.in));

    static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readString() {
        String stroka = null;
        while (true) {
            try {
                return stroka = bufferedReader.readLine();
            } catch (IOException e) {
                writeMessage("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            }
        }
    }

    public static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(readString());
            } catch (NumberFormatException e) {
                writeMessage("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
                readInt();
            }
        }
    }
}