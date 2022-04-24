package client.gui.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;


public class LoginController {

    @FXML
    public AnchorPane newUserForm;

    public void LoginSigningButton(ActionEvent actionEvent) {
    }

    public void LoginNewUserButton(ActionEvent actionEvent) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NewUserFXML.fxml"));
        Parent parent = null;

        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(parent, 400, 250);
        Stage stage = new Stage();
        stage.setTitle("Import Data");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.showAndWait();

    }


    public void NewUserButtonCancel(ActionEvent actionEvent) {
        ((Stage) newUserForm.getScene().getWindow()).close();
    }

    public void NewUserButtonOk(ActionEvent actionEvent) {
    }
}
