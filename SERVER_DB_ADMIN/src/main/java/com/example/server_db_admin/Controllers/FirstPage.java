package com.example.server_db_admin.Controllers;

import com.example.server_db_admin.SceneChanger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;


public class FirstPage implements Initializable {

    int portNumber = 1339;

    @FXML
    private Button loginButton;
    @FXML
    TextField email;
    @FXML
    PasswordField password;


    @FXML
    void loginButtonOnAction(ActionEvent event) {
        checkLogin(event);
    }

    @FXML
    void exitButtonOnActionLogin(ActionEvent event) {
        System.exit(1);
    }

    public boolean validation(String username, String password){
        try {
            Socket client = new Socket(InetAddress.getByName("85.197.159.150"), portNumber);
            OutputStream clientOut = client.getOutputStream();
            PrintWriter pw = new PrintWriter(clientOut, true);
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            pw.println("5");
            pw.println(username);
            pw.println(password);
            String response = br.readLine();

            if(response.equals("1")){
                return true;
            }

        } catch (Exception e){

        }
        return false;
    }

    public void checkLogin(ActionEvent event){
       if(validation(email.getText(),password.getText())){
           SceneChanger.changeScene(event, "AdminPanel.fxml");
       } else if(email.getText().isEmpty() || password.getText().isEmpty()) {
           Alert alert = new Alert(Alert.AlertType.ERROR);
           alert.setContentText("Enter both fields");
           alert.showAndWait();
       } else  {
           Alert alert = new Alert(Alert.AlertType.ERROR);
           alert.setContentText("Wrong login");
           alert.showAndWait();
       }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
