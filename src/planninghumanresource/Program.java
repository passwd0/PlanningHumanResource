package planninghumanresource;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import planninghumanresource.controller.Controller;
import planninghumanresource.controller.DBConnect;
import planninghumanresource.model.Setting;
import planninghumanresource.persistence.SettingReader;
import planninghumanresource.ui.AmministrazionePane;

public class Program extends Application{
	private Controller controller;

	@Override
	public void start(Stage stage) throws Exception {
		DBConnect.verifyDB();
		Setting setting = new SettingReader().getSetting();
		controller = new Controller(setting);
		if (controller == null)
			return;
		controller.setHostServices(getHostServices());
		stage.getIcons().add(new Image("/icon-app.png"));
		stage.setTitle("Amministrazione");
		AmministrazionePane root = new AmministrazionePane(stage, controller);
		
		Scene scene = new Scene(root, 1500, 700);
		if (!setting.getColorPalette().equals("Default"))
			scene.getStylesheets().add("/amministrazione/" + setting.getColorPalette() + ".css");
		stage.setScene(scene);
		stage.show();
	}
			
	public static void main(String[] args) {
		launch(args);
	}
}
