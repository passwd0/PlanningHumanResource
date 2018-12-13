package amministrazione.ui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import amministrazione.controller.Controller;
import amministrazione.model.HasState;
import amministrazione.model.Indisponibilita;
import amministrazione.model.Ripetition;
import amministrazione.utils.Utils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class StatoPane {
	private Stage window;
	private Controller controller;
	private Parent parent;
	private HasState itemSelected;
	private ObservableList<Indisponibilita> dates;
	
	public StatoPane(Stage window, Controller controller, HasState itemSelected) {
		this.window = window;
		window.setWidth(550);
		window.setHeight(600);
		this.controller = controller;
		this.itemSelected = itemSelected;
		this.dates = FXCollections.observableArrayList(itemSelected.getIndisponibilita());
		Collections.sort(dates);
		
		parent = initPane();
	}
	
	public Parent getParent() {
		return parent;
	}
	
	private Parent initPane() {
		GridPane panel = new GridPane();
		panel.setVgap(20);
		panel.setHgap(10);
		{
			panel.setPadding(new Insets(20, 20, 20, 20));
			
			DatePicker dataInizio = new DatePicker(LocalDate.now());
			DatePicker dataFine = new DatePicker(LocalDate.now());
			dataInizio.setShowWeekNumbers(false);
			dataFine.setShowWeekNumbers(false);
			Label labelRipetition = new Label("Ripetizione: ");
			ComboBox<Ripetition> ripetition = new ComboBox<>(FXCollections.observableArrayList(Ripetition.values()));
			ripetition.setValue(Ripetition.Giornalmente);
			Button insert = new Button("Inserisci");
			panel.addRow(0, dataInizio, dataFine);
			panel.addRow(1, labelRipetition, ripetition, insert);
			
			Separator sep1 = new Separator();
			GridPane.setColumnSpan(sep1, 4);
			panel.add(sep1, 0, 3);
			
			DatePicker datePicker = new DatePicker();
			TableView<Indisponibilita> tableView = new TableView<>(dates);
			panel.add(datePicker, 0, 5);
			GridPane.setColumnSpan(tableView, 3);
			Button conferma = new Button("Conferma");
			GridPane.setColumnSpan(conferma, 2);
			panel.add(tableView, 0, 6);
			panel.add(conferma, 1, 7);
			
			insert.setOnAction(event -> {
				LocalDate temp = dataInizio.getValue();
				for (; !temp.isAfter(dataFine.getValue()); temp = temp.plusDays(1)) {
					try {
						Indisponibilita i = controller.createIndisponibilita(temp, controller.getInizioLavoro(), controller.getFineLavoro());
						dates.add(i);
					} catch (Exception e) {
						// TODO: handle exception
					}
					Collections.sort(dates);
				}
			});
			
			datePicker.setConverter(new StringConverter<LocalDate>() {
	            @Override
	            public String toString(LocalDate date) {
	                return (date == null) ? "" : Utils.formatterData.format(date);
	            }

	            @Override
	            public LocalDate fromString(String string) {
	                return ((string == null) || string.isEmpty()) ? null : LocalDate.parse(string, Utils.formatterData);
	            }
	        });
	        datePicker.setOnAction(event -> {
	        	try {
	        		Indisponibilita i = controller.createIndisponibilita(datePicker.getValue(), controller.getInizioLavoro(), controller.getFineLavoro());
					dates.add(i);
	        	}catch (Exception e) {
						// TODO: handle exception
					}	        	
	        	Collections.sort(dates);
	        });
	        datePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
	            @Override
	            public DateCell call(DatePicker param) {
	                return new DateCell() {
	                    @Override
	                    public void updateItem(LocalDate item, boolean empty) {
	                        super.updateItem(item, empty);
	                        boolean alreadySelected = dates.stream().anyMatch(x -> x.getData().isEqual(item));
	                        //setDisable(alreadySelected);
	                        setStyle(alreadySelected ? "-fx-background-color: rgba(0, 205, 0, 0.5);" : "");
	                    }
	                };
	            }
	        });
	        List<LocalTime> localTimes = new Utils(controller).listaOreLavorative();
	        
	        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			tableView.prefHeightProperty().bind(window.heightProperty());
			tableView.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.DELETE)
					dates.removeAll(tableView.getSelectionModel().getSelectedItems());
			});
			
			Callback<TableColumn<Indisponibilita, LocalTime>, TableCell<Indisponibilita, LocalTime>> cellInizioFactory = new Callback<TableColumn<Indisponibilita, LocalTime>, TableCell<Indisponibilita, LocalTime>>(){
				@Override
				public TableCell<Indisponibilita, LocalTime> call(TableColumn<Indisponibilita, LocalTime> param) {
					final TableCell<Indisponibilita, LocalTime> cell = new TableCell<Indisponibilita, LocalTime>() {

	                    @Override
	                    public void updateItem(LocalTime item, boolean empty) {
	                        super.updateItem(item, empty);
	                        if (empty) {
	                            setGraphic(null);
	                            setText(null);
	                        } 
	                        else {
	                        	ComboBox<LocalTime> comboBoxOra = new ComboBox<>(FXCollections.observableArrayList(localTimes));
	                        	comboBoxOra.setValue(item);
	                        	comboBoxOra.valueProperty().bindBidirectional(dates.get(getIndex()).oraInizioProperty());
                            setGraphic(comboBoxOra);
	                        }
	                    }
	                    
	                };
	                return cell;
				}
			};

			Callback<TableColumn<Indisponibilita, LocalTime>, TableCell<Indisponibilita, LocalTime>> cellFineFactory = new Callback<TableColumn<Indisponibilita, LocalTime>, TableCell<Indisponibilita, LocalTime>>(){
				@Override
				public TableCell<Indisponibilita, LocalTime> call(TableColumn<Indisponibilita, LocalTime> param) {
					final TableCell<Indisponibilita, LocalTime> cell = new TableCell<Indisponibilita, LocalTime>() {

	                    @Override
	                    public void updateItem(LocalTime item, boolean empty) {
	                        super.updateItem(item, empty);
	                        if (empty) {
	                            setGraphic(null);
	                            setText(null);
	                        } 
	                        else {
	                        	ComboBox<LocalTime> comboBoxOra = new ComboBox<>(FXCollections.observableArrayList(localTimes));
	                        	comboBoxOra.setValue(item);
	                        	comboBoxOra.valueProperty().bindBidirectional(dates.get(getIndex()).oraFineProperty());
                            setGraphic(comboBoxOra);
	                        }
	                    }
	                    
	                };
	                return cell;
				}
			};
			
			TableColumn<Indisponibilita, String> itemData = new TableColumn<>("Data");
			TableColumn<Indisponibilita, LocalTime> itemOraInizio = new TableColumn<>("Ora Inizio");
			TableColumn<Indisponibilita, LocalTime> itemOraFine = new TableColumn<>("Ora Fine");
			tableView.getColumns().addAll(Arrays.asList(itemData, itemOraInizio, itemOraFine));

			itemData.setCellValueFactory(new PropertyValueFactory<>("data"));
			itemOraInizio.setCellValueFactory(new PropertyValueFactory<>("oraInizio"));
			itemOraFine.setCellValueFactory(new PropertyValueFactory<>("oraFine"));
			itemOraInizio.setSortable(false);
			itemOraFine.setSortable(false);
			itemData.setCellValueFactory(x -> new SimpleStringProperty(x.getValue().getDataString()));
			itemOraInizio.setCellFactory(cellInizioFactory);
			itemOraFine.setCellFactory(cellFineFactory);
			
			conferma.setOnAction(event -> {
				this.itemSelected.setIndisponibilita(dates.stream().collect(Collectors.toSet()));
				try {
					controller.addIndisponibilita(dates, itemSelected.getId());
				}
				catch (Exception e) {
					// TODO: handle exception
				}
				window.close();
			});
		}
		return panel;
	}
}
