package krzaczek_rsa_password_manager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Program extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {        
        Parent root = FXMLLoader.load(getClass().getResource("Layout1.fxml"));
        Scene scene = new Scene(root);

        stage.setTitle("Krzaczek RSA Password Manager v1.4");
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResource("icon.png").toExternalForm()));
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
