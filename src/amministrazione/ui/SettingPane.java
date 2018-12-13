package amministrazione.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import amministrazione.controller.Controller;
import amministrazione.model.Setting;
import amministrazione.persistence.SettingWriter;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class SettingPane {
	private Stage window;
	private Controller controller;
	private Parent parent;
	
	public SettingPane(Stage window, Controller controller) {
		this.window = window;
		this.controller = controller;
		parent = insertSettingPane();
	}
	
	public Parent getParent() {
		return parent;
	}

	private Parent insertSettingPane() {
		GridPane grid = new GridPane();
		grid.setVgap(20);
		grid.setHgap(30);
		grid.setPadding(new Insets(20, 20, 20, 20));
		{
			List<LocalTime> localTimes = new ArrayList<>();
			for (int i = 0; i < 24; i++)
				localTimes.add(LocalTime.of(i, 0));
			
			List<Integer> scaglionamenti = new ArrayList<>();
			scaglionamenti.addAll(Arrays.asList(15, 30, 60));

			ComboBox<LocalTime> inizioOraLavorativa;
			ComboBox<LocalTime> fineOraLavorativa;
			ComboBox<Integer> comboBoxScaglionamento;
			ComboBox<String> comboBoxPalette;
			{
				Label labelOre = new Label("Inizio Orario Lavorativo");
				inizioOraLavorativa = new ComboBox<>(FXCollections.observableArrayList(localTimes));
				inizioOraLavorativa.setValue(controller.getInizioLavoro());
				grid.add(labelOre, 0, 0);
				grid.add(inizioOraLavorativa, 1, 0);
			}
			{
				Label labelOre = new Label("Fine Orario Lavorativo");
				fineOraLavorativa = new ComboBox<>(FXCollections.observableArrayList(localTimes));
				fineOraLavorativa.setValue(controller.getFineLavoro());
				grid.add(labelOre, 0, 1);
				grid.add(fineOraLavorativa, 1, 1);
			}
			{
				Label labelScaglionamento = new Label("Scaglionamento");
				comboBoxScaglionamento = new ComboBox<>(FXCollections.observableArrayList(scaglionamenti));
				comboBoxScaglionamento.setValue(controller.getScaglionamentoOra());
				grid.add(labelScaglionamento, 0, 2);
				grid.add(comboBoxScaglionamento, 1, 2);
			}
			{
				String[] s = new String[] {"Default", "Dark"};
				Label labelPalette = new Label("Palette");
				
				comboBoxPalette = new ComboBox<>(FXCollections.observableArrayList(Arrays.asList(s)));
				comboBoxPalette.setValue(controller.getSetting().getColorPalette());
				grid.addRow(3, labelPalette, comboBoxPalette);
			}
			{
				TextField fileAppuntamenti = new TextField(controller.getFileAppuntamento().getName());
								
				Button button = new Button("Conferma");
				GridPane.setHalignment(button, HPos.CENTER);
				button.setOnAction(event -> {
					try {
						new SettingWriter().writer(new FileWriter("setting.txt"), new Setting(inizioOraLavorativa.getValue(), fineOraLavorativa.getValue(), comboBoxScaglionamento.getValue(), new File(fileAppuntamenti.getText()), comboBoxPalette.getValue(), controller.getAlert()));
						controller.setFileAppuntamenti(new File(fileAppuntamenti.getText()));
						controller.saveAppuntamenti();
						
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Attenzione");
						alert.setHeaderText("Necessario Riavvio");
						alert.setContentText("Sono appena state modificate alcune impostazioni.\nRiavviare il programma");
						alert.showAndWait().ifPresent(rs -> {
						    if (rs == ButtonType.OK) {
						        window.close();
						    }
						});
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				grid.addRow(4, fileAppuntamenti, button);
			}
		GridPane.setHalignment(inizioOraLavorativa, HPos.CENTER);
		GridPane.setHalignment(fineOraLavorativa, HPos.CENTER);
		GridPane.setHalignment(comboBoxScaglionamento, HPos.CENTER);
		}
		return grid;
	}
}
