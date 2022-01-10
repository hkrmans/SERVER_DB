package com.example.server_db_admin;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Connection extends Thread {
    int portNumber = 1339;


    public void run() {
        while (true) {
            try {
                getFile("backlog", "1");
                getFile("connectionList", "2");
                getFile("connectionsOverView", "3");
                getFile("errorOverView", "4");
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void getFile(String fileName, String number) {
        try {

            Socket client = new Socket(InetAddress.getByName("85.197.159.150"), portNumber);
            OutputStream clientOut = client.getOutputStream();
            PrintWriter pw = new PrintWriter(clientOut, true);
            pw.println(number);

            File file = new File(fileName + ".txt");
            FileOutputStream output = new FileOutputStream(file);
            BufferedOutputStream b_output = new BufferedOutputStream(output);
            BufferedInputStream input = new BufferedInputStream(client.getInputStream());
            int len;
            while ((len = input.read()) != -1) {
                b_output.write(len);
            }
            b_output.flush();
            input.close();
            b_output.close();
            client.close();
        } catch (Exception error) {
        }

    }
}
