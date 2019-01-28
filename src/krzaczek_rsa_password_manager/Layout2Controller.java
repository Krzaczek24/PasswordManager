package krzaczek_rsa_password_manager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Layout2Controller implements Initializable
{
    private ArrayList <Record> data;
    private String priFile;
    private String datFile;

    @FXML private AnchorPane secondAnchorPane;
    @FXML private TextField emailTextfield;
    @FXML private TextField loginTextfield;
    @FXML private TextField nicknameTextfield;
    @FXML private TextField passwordTextfield;
    @FXML private TextField resourceTextfield;
    @FXML private TextField searchTextfield;
    @FXML private ListView<Record> resourcesList;

    @FXML private void addRecordButtonAction()
    {
        String newData[] = {
            resourceTextfield.getText(),
            emailTextfield.getText(),
            nicknameTextfield.getText(),
            loginTextfield.getText(),
            passwordTextfield.getText(),
        };
        
        if(newData[0] == null) newData[0] = "NONE";
        for(int i=1; i<newData.length; i++) if(newData[i] == null) newData[i] = "none";    
        
        for(Record rec : data)
        {
            if(rec.isIdentical(newData))
            {
                Record record = new Record();
                data.add(record);
                resourcesList.setItems(FXCollections.observableArrayList(data));
                resourcesList.refresh();
                resourcesList.getSelectionModel().select(record);   
                return;
            }
        }
        
        Record record = new Record(newData);
        data.add(record);
        resourcesList.setItems(FXCollections.observableArrayList(data));
        resourcesList.refresh();
        resourcesList.getSelectionModel().select(record);        
    }
    
    @FXML private void removeRecordButtonAction()
    {
        if(!resourcesList.getSelectionModel().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("What you want to do?");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure that you want to remove selected record?");
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(this.getClass().getResource("question.png").toString()));

            ButtonType buttonTypeYes = new ButtonType("Yes, remove", ButtonBar.ButtonData.YES);
            ButtonType buttonTypeNo = new ButtonType("No, cancel", ButtonBar.ButtonData.NO);

            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            ButtonType clickedOne = alert.showAndWait().get();

            if (clickedOne == buttonTypeYes)
            {
                Record record = resourcesList.getSelectionModel().getSelectedItem();
                data.remove(record);
                resourcesList.getItems().remove(record);
                resourcesList.refresh();

                FileManager.saveData(data, datFile, priFile);
                
                if (resourcesList.getSelectionModel().getSelectedItem() == null)
                {
                    emailTextfield.setText(null);
                    loginTextfield.setText(null);
                    nicknameTextfield.setText(null);
                    passwordTextfield.setText(null);
                    resourceTextfield.setText(null);
                }
            }
        }
    }
    
    @FXML private void newKeyButtonAction()
    {
        ArrayList <Integer> choices = new ArrayList<>();
        choices.add(64);
        choices.add(128);
        choices.add(256);
        choices.add(512);
        choices.add(1024);
        choices.add(2048);
        
        int length = FileManager.loadKeys(datFile, priFile).getRSAprivateKey()[0].bitLength();
        while(length % 64 != 0) length++;
        
        ChoiceDialog <Integer> dialog = new ChoiceDialog<>(length, choices);
        dialog.setTitle("Choose RSA key length");
        dialog.setHeaderText(null);
        dialog.setContentText("Choose RSA key length [x bits]:");
        Stage alertStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(this.getClass().getResource("question.png").toString()));
        
        Optional <Integer> choice = dialog.showAndWait();
        if(choice.isPresent())
        {
            FileManager.generateKeys(datFile, priFile, choice.get());
            FileManager.saveData(data, datFile, priFile);
            showInfoAlert(null, "The keys have been successfully changed, data has been re-encrypted using " + choice.get() + " bits key.");
        }
    }
    
    @FXML private void saveChangesButtonAction()
    {
        if(!resourcesList.getSelectionModel().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("What you want to do?");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure that you want to save changes?");
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(this.getClass().getResource("question.png").toString()));

            ButtonType buttonTypeYes = new ButtonType("Yes, save", ButtonBar.ButtonData.YES);
            ButtonType buttonTypeNo = new ButtonType("No, cancel", ButtonBar.ButtonData.NO);

            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            ButtonType clickedOne = alert.showAndWait().get();

            if (clickedOne == buttonTypeYes)
            {
                Record record = resourcesList.getSelectionModel().getSelectedItem();
                record.setEmail(emailTextfield.getText().length() <= 0 ? "none" : emailTextfield.getText());
                record.setNickname(loginTextfield.getText().length() <= 0 ? "none" : loginTextfield.getText());
                record.setName(nicknameTextfield.getText().length() <= 0 ? "none" : nicknameTextfield.getText());
                record.setPassword(passwordTextfield.getText().length() <= 0 ? "none" : passwordTextfield.getText());
                record.setResource(resourceTextfield.getText().length() <= 0 ? "NONE" : resourceTextfield.getText().toUpperCase());
                data.sort(new RecordComparator());
                resourcesList.setItems(FXCollections.observableArrayList(data));
                resourcesList.refresh();
                
                FileManager.saveData(data, datFile, priFile);
                showInfoAlert(null, "Changes have been saved.");
            }
        }
        else showInfoAlert(null, "There is nothing to save.");
    }
    
    @FXML private void helpButtonAction()
    {
        showInfoAlert("RSA Password Manager v1.4\nby Tomasz 'Krzaczek' Drewek",
            "Hello user, thank you for using this application."
            + "\n\nOn the left side is a list of your stored passwords, you can use a filter above, which helps in searching."
            + "\n\nOn the right side you can read detailed data about each stored record."
            + "\n\nAdditionaly there are buttons:"
            + "\n+ Add new entry - adds new record to remember, fill the fields before you click to add prepared record or just add empty record and then add information"
            + "\n+ Remove entry - removes choosen stored password"
            + "\n+ Make new key - changes private key and RSA key length"
            + "\n+ Save changes - saves any changes to the file"
            + "\n+ Help - I hope it helps a little bit :)"
            + "\n+ Exit - leaves program or loads other file"
            + "\n\nCreated on 13th July 2017");
    }
    
    @FXML private void exitButtonAction()
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("What you want to do?");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure that you want to leave?\nMaybe you want to try with other file?");
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(this.getClass().getResource("question.png").toString()));

        ButtonType buttonTypeAgain = new ButtonType("Decrypt other file", ButtonBar.ButtonData.NO);
        ButtonType buttonTypeExit = new ButtonType("Leave application", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(buttonTypeAgain, buttonTypeExit, buttonTypeCancel);
        
        ButtonType clickedOne = alert.showAndWait().get();
        
        if (clickedOne == buttonTypeAgain)
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("Layout1.fxml"));
            try { loader.load(); }
            catch (IOException ex) { System.err.println("LOADER ERROR"); }
            
            Layout1Controller layout1 = loader.getController();
            layout1.passData(datFile, priFile);

            Parent root = loader.getRoot();
            Stage stage = (Stage)secondAnchorPane.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        }
        else if (clickedOne == buttonTypeExit) Platform.exit();
    }
    
    protected void passData(String datPath, String priPath)
    {
        data = FileManager.loadData(datFile = datPath, priFile = priPath);
        resourcesList.setItems(FXCollections.observableArrayList(data));
    }
    
    private void showInfoAlert(String header, String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(message);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().setAll(new Image(this.getClass().getResource("info.png").toString()));
        alert.showAndWait();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        resourcesList.getSelectionModel().selectedItemProperty().addListener
        ((ObservableValue<? extends Record> observable, Record oldValue, Record newValue) ->
        {
            if(newValue != null)
            {
                emailTextfield.setText(newValue.getEmail().equals("none") ? "" : newValue.getEmail());
                loginTextfield.setText(newValue.getNickname().equals("none") ? "" : newValue.getNickname());
                nicknameTextfield.setText(newValue.getName().equals("none") ? "" : newValue.getName());
                passwordTextfield.setText(newValue.getPassword().equals("none") ? "" : newValue.getPassword());
                resourceTextfield.setText(newValue.getResource().equals("NONE") ? "" : newValue.getResource());
            }
        });
        
        searchTextfield.textProperty().addListener
        ((observable, oldValue, newValue) ->
        {
            resourcesList.setItems(FXCollections.observableArrayList(data.stream().filter(rec -> rec.getResource().contains(newValue.toUpperCase())).collect(Collectors.toList())));
            resourcesList.refresh();
            if(resourcesList.getSelectionModel().getSelectedItem() == null)
            {
                emailTextfield.setText(null);
                loginTextfield.setText(null);
                nicknameTextfield.setText(null);
                passwordTextfield.setText(null);
                resourceTextfield.setText(null);
            }
        });
    }    
}
