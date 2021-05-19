package org.roukou.dev.challenges.may;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SingleThreadedBlockingServer {

  private static final int SERVER_PORT = 8080;

  public static void main(String[] args) throws IOException {

    ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

    while (true) {
      Socket socket = serverSocket.accept();

      InputStream inputStream = socket.getInputStream();
      OutputStream outputStream = socket.getOutputStream();

      int inputData;

      while ((inputData = inputStream.read()) != -1) {
        outputStream.write(responseHandler(inputData));
      }

      outputStream.close();
      inputStream.close();
      socket.close();
    }
  }

  private static int responseHandler(int inputData) {

    if (!Character.isLetter(inputData)) {
      return inputData;
    } else {
      return inputData ^ ' ';
    }
  }
}
