package com.houarizegai.slidesremoteserver.controllers;

import com.houarizegai.slidesremoteserver.App;
import com.houarizegai.slidesremoteserver.engine.QRCodeEngine;
import com.houarizegai.slidesremoteserver.engine.SocketServer;
import com.houarizegai.slidesremoteserver.utils.NetworkUtils;
import com.houarizegai.slidesremoteserver.utils.RegexChecker;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private JFXComboBox<String> comboNetworkName;

    @FXML
    private Label lblIpAddress;

    @FXML
    private ImageView imgQRCode;

    @FXML
    private Label lblStatus;

    private SocketServer socketServer;

    // Available network addresses in my PC
    private static Map<String, String> networkAddresses;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboNetworkName.valueProperty().addListener(e -> {
            updateIpLbl();
            changeQRCodeImg();
        });
        onRefresh();
    }

    @FXML
    private void onRefresh() {
        comboNetworkName.getItems().clear();
        networkAddresses = NetworkUtils.getMyIPv4Addresses();

        if(!networkAddresses.isEmpty()) {

            for(String key: networkAddresses.keySet()) {
                comboNetworkName.getItems().add(key);
            }
            changeQRCodeImg();
        }
    }

    @FXML
    private void onStart() {
        socketServer = new SocketServer();
        if(RegexChecker.isIP(lblIpAddress.getText())) {
            socketServer.start();
            lblStatus.setText("Connected");
        } else {
            lblStatus.setText("Disconnected, please press the refresh button!");
        }
    }

    @FXML
    private void onStop() {
        socketServer.stop();
        lblStatus.setText("Disconnected");
    }

    @FXML
    private void onClose() {
        Platform.exit();
    }

    @FXML
    private void onHide() {
        App.stage.setIconified(true);
    }

    private void changeQRCodeImg() {
        String myIP = lblIpAddress.getText();
        if(RegexChecker.isIP(myIP)) {
            Image generatedQRCode = QRCodeEngine.encode(myIP, 250, 250);
            if(generatedQRCode != null) {
                imgQRCode.setImage(generatedQRCode);
                lblStatus.setText("Ready to start");
            } else {
                lblStatus.setText("Disconnected! - Generation QRCode problem");
            }
        }

    }

    private void updateIpLbl() {
        String selectedNetworkName = networkAddresses.get(comboNetworkName.getSelectionModel().getSelectedItem());
        lblIpAddress.setText(selectedNetworkName);
    }

}
