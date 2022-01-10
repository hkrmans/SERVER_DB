package com.example.server_db_admin.Controllers;


import com.example.server_db_admin.Connection;
import com.example.server_db_admin.SceneChanger;
import com.example.server_db_admin.TestClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.stream.Collectors;

public class AdminPanel implements Initializable {

    ArrayList<String> backlog = new ArrayList<String>();
    List<String> matchesBacklog = new ArrayList<>();
    ArrayList<String> connections = new ArrayList<>();
    List<String> matchesConnections = new ArrayList<>();


    @FXML
    private ListView<String> connectionListView;

    @FXML
    private ListView<String> backlogListView;

    @FXML
    private LineChart<String, Number> ConnectionOverviewLineChart;

    @FXML
    private LineChart<String, Number> ErrorsOverviewLineChart;

    @FXML
    private DatePicker datePickerConnections;

    @FXML
    private DatePicker DatePicker;



    @FXML
    void updateValuesTab(MouseEvent event) {
        try {
            readConnectionOverviewFromFile("connectionsOverView.txt");
            readErrorsOverviewFromFile("errorOverView.txt");
            readBacklogFromFile("backlog.txt");
            readConnections("connectionList.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void viewAllConnectionsOnAction(ActionEvent event) {
        connectionListView.getItems().clear();
        connectionListView.getItems().addAll(connections);
    }


    @FXML
    void datePickerConnectionsOnAction(ActionEvent event) {
        matchesConnections = connections.stream().filter(it -> it.contains(datePickerConnections.getValue().toString())).collect(Collectors.toList());
        connectionListView.getItems().clear();
        connectionListView.getItems().addAll(matchesConnections);
    }

    @FXML
    void logOutButtonOnAction(ActionEvent event) {
        SceneChanger.changeScene(event, "FirstPage.fxml");
    }

    @FXML
    void exitButtonOnAction(ActionEvent event) {
        System.exit(1);
    }

    @FXML
    void ViewAllButtonOnAction(ActionEvent event) {
        backlogListView.getItems().removeAll(backlog);
        backlogListView.getItems().removeAll(matchesBacklog);
        backlogListView.getItems().addAll(backlog);
    }

    @FXML
    void DatePickerOnAction(ActionEvent event) throws IOException {
        matchesBacklog = backlog.stream().filter(it -> it.contains(DatePicker.getValue().toString())).collect(Collectors.toList());
        backlogListView.getItems().removeAll(backlog);
        backlogListView.getItems().addAll(matchesBacklog);
    }

    public void readConnections(String path) throws IOException {
        connectionListView.getItems().removeAll(connections);
        connections = readFromFile(path);
        connectionListView.getItems().addAll(connections);

    }

    public void readBacklogFromFile(String path) throws IOException {
        backlogListView.getItems().removeAll(backlog);
        backlog = readFromFile(path);
        backlogListView.getItems().addAll(backlog);
    }

    public void readConnectionOverviewFromFile(String path) throws IOException {
        ArrayList<String> values = readFromFile(path);
        XYChart.Series<String,Number> connectionsOverView = new XYChart.Series<String,Number>();
        ConnectionOverviewLineChart.getData().clear();
        connectionsOverView.getData().add(new XYChart.Data<String,Number>("January",Integer.parseInt(values.get(1))));
        connectionsOverView.getData().add(new XYChart.Data<String,Number>("February",Integer.parseInt(values.get(3))));
        connectionsOverView.getData().add(new XYChart.Data<String,Number>("March",Integer.parseInt(values.get(5))));
        connectionsOverView.getData().add(new XYChart.Data<String,Number>("April",Integer.parseInt(values.get(7))));
        connectionsOverView.getData().add(new XYChart.Data<String,Number>("May",Integer.parseInt(values.get(9))));
        connectionsOverView.getData().add(new XYChart.Data<String,Number>("June",Integer.parseInt(values.get(11))));
        connectionsOverView.getData().add(new XYChart.Data<String,Number>("July",Integer.parseInt(values.get(13))));
        connectionsOverView.getData().add(new XYChart.Data<String,Number>("August",Integer.parseInt(values.get(15))));
        connectionsOverView.getData().add(new XYChart.Data<String,Number>("September",Integer.parseInt(values.get(17))));
        connectionsOverView.getData().add(new XYChart.Data<String,Number>("October",Integer.parseInt(values.get(19))));
        connectionsOverView.getData().add(new XYChart.Data<String,Number>("November",Integer.parseInt(values.get(21))));
        connectionsOverView.getData().add(new XYChart.Data<String,Number>("December",Integer.parseInt(values.get(23))));
        ConnectionOverviewLineChart.getData().add(connectionsOverView);
    }

    public void readErrorsOverviewFromFile(String path) throws IOException {
        ArrayList<String> values = readFromFile(path);
        XYChart.Series<String,Number> errorOverView = new XYChart.Series<String,Number>();
        ErrorsOverviewLineChart.getData().clear();
        errorOverView.getData().add(new XYChart.Data<String,Number>("January",Integer.parseInt(values.get(1))));
        errorOverView.getData().add(new XYChart.Data<String,Number>("February",Integer.parseInt(values.get(3))));
        errorOverView.getData().add(new XYChart.Data<String,Number>("March",Integer.parseInt(values.get(5))));
        errorOverView.getData().add(new XYChart.Data<String,Number>("April",Integer.parseInt(values.get(7))));
        errorOverView.getData().add(new XYChart.Data<String,Number>("May",Integer.parseInt(values.get(9))));
        errorOverView.getData().add(new XYChart.Data<String,Number>("June",Integer.parseInt(values.get(11))));
        errorOverView.getData().add(new XYChart.Data<String,Number>("July",Integer.parseInt(values.get(13))));
        errorOverView.getData().add(new XYChart.Data<String,Number>("August",Integer.parseInt(values.get(15))));
        errorOverView.getData().add(new XYChart.Data<String,Number>("September",Integer.parseInt(values.get(17))));
        errorOverView.getData().add(new XYChart.Data<String,Number>("October",Integer.parseInt(values.get(19))));
        errorOverView.getData().add(new XYChart.Data<String,Number>("November",Integer.parseInt(values.get(21))));
        errorOverView.getData().add(new XYChart.Data<String,Number>("December",Integer.parseInt(values.get(23))));
        ErrorsOverviewLineChart.getData().add(errorOverView);
    }

    public ArrayList<String> readFromFile(String path) throws IOException {
        ArrayList<String> tempReader = new ArrayList<>();
        Scanner s = new Scanner(new File(path));
        tempReader = new ArrayList<>();
        while (s.hasNextLine()){
            tempReader.add(s.nextLine());
        }
        s.close();
        return tempReader;
    }







    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Connection connection = new Connection();
        connection.start();

        try {
            readConnectionOverviewFromFile("connectionsOverView.txt");
            readErrorsOverviewFromFile("errorOverView.txt");
            readBacklogFromFile("backlog.txt");
            readConnections("connectionList.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

