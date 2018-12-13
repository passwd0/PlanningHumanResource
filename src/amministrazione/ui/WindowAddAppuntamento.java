package amministrazione.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import amministrazione.controller.Controller;
import amministrazione.utils.Utils;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WindowAddAppuntamento {
	private Stage window;
	private Controller controller;
	private Parent parent;
	private LocalDateTime datetimeInizio;
	
	public WindowAddAppuntamento(Stage window, Controller controller, LocalDateTime datetimeInizio) {
		this.window = window;
		this.controller = controller;
		this.datetimeInizio = datetimeInizio;
		
		this.parent = initPane();
	}
	
	public Parent getParent() {
		return parent;
	}
	
	private Parent initPane() {
		GridPane panel = new GridPane();
		panel.setVgap(20);
		panel.setHgap(10);
		{
			Label labelData = new Label("Data");
			Utils utils = new Utils(controller);
			DatePicker dataInizio = new DatePicker(datetimeInizio.toLocalDate());
			dataInizio.setShowWeekNumbers(false);
			Label trattino1 = new Label(" - ");
			DatePicker dataFine = new DatePicker(datetimeInizio.toLocalDate());
			dataFine.setShowWeekNumbers(false);
			Label labelOra = new Label("Ora");
			ComboBox<LocalTime> oraInizio = new ComboBox<>(FXCollections.observableArrayList(utils.listaOreLavorative()));
			Label trattino2 = new Label(" - ");
			ComboBox<LocalTime> oraFine = new ComboBox<>(FXCollections.observableArrayList(utils.listaOreLavorative()));
			oraInizio.setValue(datetimeInizio.toLocalTime());
			oraFine.setValue(datetimeInizio.toLocalTime());
			panel.addRow(0, labelData, dataInizio, trattino1, dataFine);
			panel.addRow(1, labelOra, oraInizio, trattino2, oraFine);
		}
		return panel;
	}
	
}
