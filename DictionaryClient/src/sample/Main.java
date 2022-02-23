/* 
 * Main Class
 * */

//This class controls clients' GUI components and operation based on TCP connection

package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main extends Application {
    @FXML
    private TextField wordText;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button queryButton;
    @FXML
    private TextArea resultText;
    @FXML
    private TextField defText;
    @FXML
    private Button connectButton;

    private static String IP ;
    private static int Port;
    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;
    private String addDef;
    private String result;

    // start server and launch GUI
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Insert your IP and Port as arguments");
            System.exit(0);
        } else {
            IP = args[0];
            try {
                Port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Insert a valid format of port a number below 9999 \n");
                System.exit(0);
            }if(Port > 0 && Port <=9999){
                launch(args);
            }else System.out.println("Insert a valid format of port a number below 9999 \n");
            System.exit(0);
        }
    }
    // initialize socket and interface
    public void initializeClient(String IP, int Port) {
        try {
            resultText.appendText("Connecting.. >> IP : "+IP + " Port : "+ Port +"\n");
            socket = new Socket(IP, Port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            if(socket.isConnected()) {
                addButton.setDisable(false);
                deleteButton.setDisable(false);
                queryButton.setDisable(false);
                resultText.appendText("Connected\n");
            }
        } catch (UnknownHostException e) {
            resultText.appendText("UnknownHost. Your IP is invalid, exit the program and try again\n");
        } catch (ConnectException e1) {
            resultText.appendText("Connection refused. Server is closed. Check port number and try it later\n");
        } catch (Exception e3) {
            resultText.appendText("Initialization failed");
        }
    }

    // function to add word based on GUI; send a request to server socket and print the response
    @FXML
    public synchronized void add(ActionEvent event) {
        try {
            String text = wordText.getText().trim();
            text = text.toLowerCase();
            String def = defText.getText().trim();
            if (!text.equals("") && !defText.getText().equals("")) {
                addDef = "A" +text +"F" + def+ "\n";
                output.write(addDef);
                output.flush();
                addDef = null;
                resultText.appendText("The processing word >> " + text + "\n");
                result = input.readLine();
                resultText.appendText("The result >> " + result +"\n");
                wordText.setText("");
                defText.setText("");
                wordText.requestFocus();
                addButton.setDisable(true);
                deleteButton.setDisable(true);
                queryButton.setDisable(true);
                connectButton.setDisable(false);
            } else {
                resultText.appendText("Insert the word and meaning to process\n");
            }
        }catch (IOException e) {
            resultText.appendText("Sending error..(delete)\n");
        }
    }

    // function to delete word based on GUI; send a request to server socket and print the response
    @FXML
    public synchronized void delete(ActionEvent event) {
        try{
            String text = wordText.getText().trim();
            text=text.toLowerCase();
            if (!text.equals("")) {
                output.write("D" + text + "\n");
                output.flush();
                resultText.appendText("The processing word >> " + text + "\n");
                result = input.readLine();
                resultText.appendText("The result >> " + result +"\n");
                wordText.setText("");
                wordText.requestFocus();
                addButton.setDisable(true);
                deleteButton.setDisable(true);
                queryButton.setDisable(true);
                connectButton.setDisable(false);
            } else {
                resultText.appendText("Insert the word to process\n");
            }
        }catch (IOException e){
            resultText.appendText("Sending error..(delete)\n");
        }
    }

    // function to query word based on GUI; send a request to server socket and print the response
    @FXML
    public synchronized void query(ActionEvent event) {
        try{
            String text = wordText.getText().trim();
            text = text.toLowerCase();
            if (!text.equals("")) {
                output.write("Q" + text + "\n");
                output.flush();
                resultText.appendText("The processing word >> " + text + "\n");
                result = input.readLine();
                resultText.appendText("The result >> " + result+"\n");
                wordText.setText("");
                wordText.requestFocus();
                addButton.setDisable(true);
                deleteButton.setDisable(true);
                queryButton.setDisable(true);
                connectButton.setDisable(false);
            } else {
                resultText.appendText("Insert the word to process\n");
            }
        }catch (IOException e){
            resultText.appendText("Sending error..(query)\n");
        }
    }

    //function to connect server based on GUI;
    @FXML
    public void connect(ActionEvent actionEvent) {
        if (connectButton.getText().equals("CONNECT DICTIONARY")) {
            try{
                initializeClient(IP, Port);
                connectButton.setDisable(true);
            }catch (Exception e) {
                resultText.appendText("Connecting error, try it later\n");
            }
        }
    }

    //start GUI window
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("clientGUI.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Dictionary Window");
        //primaryStage.setOnCloseRequest(event -> stopClient());
        primaryStage.show();
    }

}
