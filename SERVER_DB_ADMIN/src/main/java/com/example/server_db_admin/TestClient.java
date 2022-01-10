package com.example.server_db_admin;

public class TestClient {

    Connection handler;

    public Connection getHandler() {
        return handler;
    }

    public static void main(String[] args) {
        TestClient main = new TestClient();
        main.start();
    }

    public void start() {
        try {
            handler = new Connection();
            //handler.start();

        } catch (Exception e) {
            System.err.println("Exception caught:" + e);
        }
    }

}
