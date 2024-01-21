package com.example.socialnetworkapp.controlers;

import com.example.socialnetworkapp.domain.User;
import com.example.socialnetworkapp.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class LogIn {
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private Text emailErrorText;

    @FXML
    private Text passwordErrorText;

    @FXML
    protected void onLogInButtonCLick(ActionEvent event) throws IOException {
        User user = UserService.getInstance().getUserByEmail(email.getText());
        System.out.println(user);

        if (user == null) {
            emailErrorText.setVisible(true);
            passwordErrorText.setVisible(false);
        } else if (!UserService.getInstance().checkPassword(email.getText(), password.getText())) {
            passwordErrorText.setVisible(true);
            emailErrorText.setVisible(false);
        } else {
            emailErrorText.setVisible(false);
            passwordErrorText.setVisible(false);

            FXMLLoader stageLoader = new FXMLLoader();
            stageLoader.setLocation(getClass().getResource("/com/example/socialnetworkapp/Application.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            AnchorPane appLayout = stageLoader.load();
            Scene scene = new Scene(appLayout);
            stage.setScene(scene);

            Application appController = stageLoader.getController();
            appController.initApp(user);
            stage.show();

        }
    }

    @FXML
    public void onSignInClick(ActionEvent event) throws IOException {
        FXMLLoader stageLoader = new FXMLLoader();
        stageLoader.setLocation(getClass().getResource("/com/example/socialnetworkapp/SignIn.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        AnchorPane singUpLayout = stageLoader.load();
        Scene scene = new Scene(singUpLayout);

        stage.setScene(scene);
        SignIn signInController = stageLoader.getController();
        stage.show();
    }

    public void onTextChanged(KeyEvent evt) {
        emailErrorText.setVisible(false);
        passwordErrorText.setVisible(false);
    }

    public void onPasswordChanged(KeyEvent evt) {
        passwordErrorText.setVisible(false);
    }
}
