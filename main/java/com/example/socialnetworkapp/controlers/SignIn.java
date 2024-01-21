package com.example.socialnetworkapp.controlers;

import com.example.socialnetworkapp.domain.User;
import com.example.socialnetworkapp.domain.validators.ValidationException;
import com.example.socialnetworkapp.domain.validators.ValidatorUser;
import com.example.socialnetworkapp.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.IOException;
public class SignIn{


    @FXML
    private TextField email;
    @FXML
    private TextField password;
    @FXML
    private TextField password_confirm;
    @FXML
    private TextField first_name;
    @FXML
    private TextField last_name;

    @FXML
    private Text firstnameErrorText;
    @FXML
    private Text lastnameErrorText;
    @FXML
    private Text emailErrorText;
    @FXML
    private Text passwordErrorText;

    private ValidatorUser utilizatorValidator = new ValidatorUser();

    @FXML
    protected void onCreateAccountClick(ActionEvent event) throws IOException {

        User newUser = new User(first_name.getText(), last_name.getText(), email.getText(), password.getText());

        try {
            utilizatorValidator.validate(newUser);
        } catch (ValidationException exception) {
            String err = exception.toString().split(":")[1];
            switch (err.charAt(1)) {
                case '0' -> {
                    firstnameErrorText.setText(err.substring(1));
                    firstnameErrorText.setVisible(true);
                }
                case '1' -> {
                    lastnameErrorText.setText(err.substring(1));
                    lastnameErrorText.setVisible(true);
                }
                default -> {
                    emailErrorText.setText(err);
                    System.out.println(err);
                    emailErrorText.setVisible(true);
                }
            }
            return;
        }
        if (!password.getText().equals(password_confirm.getText()))
            passwordErrorText.setVisible(true);
        else {
            UserService.getInstance().addUser(newUser);

            FXMLLoader stageLoader = new FXMLLoader();
            stageLoader.setLocation(getClass().getResource("/com/example/socialnetworkapp/Application.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            AnchorPane appLayout = stageLoader.load();
            Scene scene = new Scene(appLayout);
            stage.setScene(scene);

            Application appController = stageLoader.getController();
            appController.initApp(newUser);
            stage.show();

        }
    }

    public void BackToLogIn(ActionEvent actionEvent) throws IOException {
        FXMLLoader stageLoader = new FXMLLoader();
        stageLoader.setLocation(getClass().getResource("/com.example.socialnetworkapp/LogIn.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

        AnchorPane logInLayout = stageLoader.load();
        Scene scene = new Scene(logInLayout);
        stage.setScene(scene);
        LogIn logInController = stageLoader.getController();

        stage.show();
    }

    public void onFirstnameTextChanged() {
        firstnameErrorText.setVisible(false);
    }

    public void onLastnameTextChanged() {
        lastnameErrorText.setVisible(false);
    }

    public void onEmailTextChanged() {
        emailErrorText.setVisible(false);
    }

    public void onPasswordTextChanged() {
        passwordErrorText.setVisible(false);
    }

    public void onConfirmPasswordTextChanged() {
        passwordErrorText.setVisible(false);
    }
}
