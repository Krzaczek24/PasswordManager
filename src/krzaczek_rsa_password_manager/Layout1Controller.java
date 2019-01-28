package krzaczek_rsa_password_manager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Layout1Controller implements Initializable
{
    @FXML private AnchorPane firstAnchorPane;
    @FXML private TextField privateTextfield;
    @FXML private TextField dataTextfield;
    
    @FXML private void privateButtonAction()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select private key file");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Private key file", "*.prikey"));
        Stage stage = (Stage)firstAnchorPane.getScene().getWindow();
        File file;
        if((file = fileChooser.showOpenDialog(stage)) != null)
            privateTextfield.setText(file.getPath());
    }
    
    @FXML private void dataButtonAction()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select data file");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Data file", "*.data"));
        Stage stage = (Stage)firstAnchorPane.getScene().getWindow();
        File file;
        if((file = fileChooser.showOpenDialog(stage)) != null)
            dataTextfield.setText(file.getPath());
    }
    
    @FXML private void loadButtonAction() throws Exception
    {
        if(privateTextfield.getText().isEmpty()
        || dataTextfield.getText().isEmpty())
        {
            if(privateTextfield.getText().isEmpty()) privateButtonAction();
            if(dataTextfield.getText().isEmpty()) dataButtonAction();
            
            if(privateTextfield.getText().isEmpty()
            || dataTextfield.getText().isEmpty())
                showWarningAlert("You must specify a location for both files!");
            else if(new File(dataTextfield.getText()).exists() 
                 && new File(privateTextfield.getText()).exists())
                goFurther();
            else
                showWarningAlert("At least one of selected file does not exists!");
        }
        else
        {
            if(new File(dataTextfield.getText()).exists() 
            && new File(privateTextfield.getText()).exists())
                goFurther();
            else
                showWarningAlert("At least one of selected file does not exists!");
        }
    }
    
    @FXML private void generateButtonAction()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select location for new files");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Multiple files", "*.*"));
        Stage stage = (Stage)firstAnchorPane.getScene().getWindow();
        File file;
        if((file = fileChooser.showSaveDialog(stage)) != null)
        {
            String path = file.getPath();
            String newPriExt = ".prikey";
            String newDatExt = ".data";
            FileManager.generateKeys(path + newDatExt, path + newPriExt, 256);
            FileManager.saveData(new ArrayList<>(), path + newDatExt, path + newPriExt);

            privateTextfield.setText(path + newPriExt);
            dataTextfield.setText(path + newDatExt);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("New files successfully created in: '" + path.substring(0, path.lastIndexOf("\\")+1) + "'.\nDefault RSA key length is 256 bits.");
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(this.getClass().getResource("info.png").toString()));
            alert.showAndWait();
        }
    }
    
    @FXML private void helpButtonAction()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("RSA Password Manager v1.4\nby Tomasz 'Krzaczek' Drewek");
        alert.setContentText("Hello user, thank you for using this application."
                + "\n\nIf you use it for the first time, you should use button called 'Generate new files', which will create new data file for your passwords and keys for them, after that, path fields will automatically fill up."
                + "\n\nIf you have those files, you can select them separately, or just click button 'Load data', then will appear open dialogs for every not filled path field."
                + "\n\nCreated on 13th July 2017");
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(this.getClass().getResource("info.png").toString()));
        alert.showAndWait();
    }
    
    @FXML private void exitButtonAction()
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("What you want to do?");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure that you want to leave?");
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(this.getClass().getResource("question.png").toString()));
        
        ButtonType buttonTypeYes = new ButtonType("Yes, leave", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("No, stay", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        
        if (alert.showAndWait().get() == buttonTypeYes) Platform.exit();
    }

    protected void passData(String datPath, String priPath)
    {
        dataTextfield.setText(datPath); 
        privateTextfield.setText(priPath);
    }
    
    private void goFurther()
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Layout2.fxml"));
        try { loader.load(); }
        catch (IOException ex) { System.err.println("LOADER ERROR"); }
                
        Layout2Controller layout2 = loader.getController();
        layout2.passData(dataTextfield.getText(), privateTextfield.getText());

        Parent root = loader.getRoot();
        Stage stage = (Stage)firstAnchorPane.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }
    
    private void showWarningAlert(String message)
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResource("info.png").toString()));
        alert.showAndWait();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { }
}
