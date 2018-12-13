package amministrazione.ui;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import amministrazione.model.Ripetition;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RipetitionPane {
	private Stage window;
	private Parent parent;
	private LocalDate dateInizio;
	private LocalDate dateFine;
	private Set<LocalDate> dateAppuntamenti;
	private MultipleDatePicker multipleDatePicker;

	public RipetitionPane(Stage window, DatePicker datePicker, Set<LocalDate> dates) {
		this.window = window;
		dateInizio = datePicker.getValue();
		dateFine = datePicker.getValue();
		dateAppuntamenti = dates;
		parent = initRipetitionPane();
	}

	public RipetitionPane(Stage window, DatePicker datePicker) {
		this(window, datePicker, new HashSet<>());
	}
	
	public Parent getParent() {
		return parent;
	}
	
	public Set<LocalDate> getDateAppuntamentiMultipli(){
		return dateAppuntamenti;
	}
	
	private Parent initRipetitionPane() {
		VBox panel = new VBox(20);
		{
			panel.setPadding(new Insets(20, 20, 20, 20));
			GridPane grid;
			DatePicker dataInizio;
			DatePicker dataFine;
			ComboBox<Ripetition> ripetizione;
			CheckBox checkboxSelection1;
			CheckBox checkboxSelection2;
			VBox dateBox = new VBox(10);
			{
				grid = new GridPane();
				grid.setVgap(10);
				grid.setHgap(40);
				
				dataInizio = new DatePicker(dateInizio);
				dataFine = new DatePicker(dateFine);
				dataInizio.setShowWeekNumbers(false);
				dataFine.setShowWeekNumbers(false);
				
				grid.addRow(0, dataInizio, dataFine);
			
				Separator sep1 = new Separator();
				
				Label labelRipetizione = new Label("Ripetizione: ");
				ripetizione = new ComboBox<>(FXCollections.observableArrayList(Ripetition.values()));
				ripetizione.setValue(Ripetition.Settimanalmente);
				
				GridPane.setColumnSpan(sep1, 3);
				checkboxSelection1 = new CheckBox();
				grid.addRow(2, labelRipetizione, ripetizione, checkboxSelection1);
				grid.addRow(3, sep1);
				
				dateBox.getChildren().addAll(grid);
			}
			checkboxSelection2 = new CheckBox();
			multipleDatePicker = new MultipleDatePicker(checkboxSelection2, dateAppuntamenti);
			checkboxSelection2.setOnAction(event -> {
				dataInizio.setDisable(true);
				dataFine.setDisable(true);
		        ripetizione.setDisable(true);
		        checkboxSelection1.setSelected(false);
		        multipleDatePicker.getButtonsLeft().setDisable(false);
			});
			checkboxSelection1.setOnAction(event -> {
				dataInizio.setDisable(false);
				dataFine.setDisable(false);
		        ripetizione.setDisable(false);
		        checkboxSelection2.setSelected(false);
		        multipleDatePicker.getButtonsLeft().setDisable(true);
			});
			HBox buttonsBox = new HBox(20);
			{
				Button buttonConferma = new Button("Conferma");
				buttonsBox.getChildren().add(buttonConferma);
				buttonConferma.setOnAction(event -> {
					if (checkboxSelection1.isSelected() || checkboxSelection2.isSelected()) {
						if (checkboxSelection1.isSelected()) {
							if (ripetizione.getValue().equals(Ripetition.Giornalmente)) {
								LocalDate i = dataInizio.getValue();
								LocalDate f = dataFine.getValue();
								for (; !i.isAfter(f); i = i.plusDays(1))
									dateAppuntamenti.add(i);
							}
							if (ripetizione.getValue().equals(Ripetition.Settimanalmente)) {
								LocalDate i = dataInizio.getValue();
								LocalDate f = dataFine.getValue();
								for (; !i.isAfter(f); i = i.plusWeeks(1)) {
									dateAppuntamenti.add(i);
								}
							}
							if (ripetizione.getValue().equals(Ripetition.Mensilmente)) {
								LocalDate i = dataInizio.getValue();
								LocalDate f = dataFine.getValue();
								for (; !i.isAfter(f); i = i.plusMonths(1)) {
									dateAppuntamenti.add(i);
								}
							}
						}
						if (checkboxSelection2.isSelected()) {
							dateAppuntamenti = multipleDatePicker.getDate();
						}
						
						window.close();
					}
					else {
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Conferma");
						alert.setHeaderText("Conferma chiusura senza aver inserito un periodo?");
						alert.setContentText("Chiudere?");

						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.OK){
						    window.close();
						} 
					}
				});
			}
			panel.getChildren().addAll(dateBox, multipleDatePicker.getParent(), buttonsBox);
		}
		return panel;
	}

}
