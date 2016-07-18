package com.example.juhee.project4;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by juhee on 2016. 7. 16..
 */
public class TCPServer implements Runnable{

    public static final int ServerPort = 12345;
    public static final String ServerIP = " 52.78.66.95";

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(ServerPort);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
