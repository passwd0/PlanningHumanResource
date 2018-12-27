package planninghumanresource.ui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import planninghumanresource.controller.Controller;
import planninghumanresource.model.Auto;
import planninghumanresource.model.HasState;
import planninghumanresource.model.Macchina;
import planninghumanresource.model.Operaio;
import planninghumanresource.utils.Utils;

public class GraficoGiornata {
	private Stage window;
	private Parent parent;
	private TableView<ItemRisorsa> tableView;
	private Controller controller;
	private LocalDate date;
	private DatePicker datePicker;
	private int row = -1;
	private String oraSelected;
	private List<Operaio> operaiSelected = new ArrayList<>();
	private List<Auto> automezziSelected = new ArrayList<>();
	private List<Macchina> macchineSelected = new ArrayList<>();
	private ListView<Operaio> listViewOperaio;
	private ListView<Auto> listViewAuto;
	private ListView<Macchina> listViewMacchina;
	private TextField textFieldInizio;
	private ListView<LocalTime> listViewHours;
	
	public GraficoGiornata(Stage window, Controller controller, DatePicker datePicker, TableView<ItemRisorsa> tableView, TextField textFieldInizio, ListView<LocalTime> listViewHours,
			ListView<Operaio> listViewOperaio, ListView<Auto> listViewAuto, ListView<Macchina> listViewMacchina) {
		this.window = window;
		this.tableView = tableView;
		this.controller = controller;
		this.date = datePicker.getValue();
		this.datePicker = datePicker;
		this.textFieldInizio = textFieldInizio;
		this.listViewHours = listViewHours;
		this.listViewOperaio = listViewOperaio;
		this.listViewAuto = listViewAuto;
		this.listViewMacchina = listViewMacchina;
		parent = initGraficoGiornata();
	}
	
	public Parent getParent() {
		return parent;
	}

	private <T> Parent initGraficoGiornata() {
		VBox panel = new VBox(20);
		{
			panel.setPadding(new Insets(20, 20, 10, 20));
			tableView = new TableView<ItemRisorsa>();
			tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			tableView.getSelectionModel().setCellSelectionEnabled(true);

			tableView.prefHeightProperty().bind(window.heightProperty());
			
			Callback<TableColumn<ItemRisorsa, List<T>>, TableCell<ItemRisorsa, List<T>>> cellFactory = new Callback<TableColumn<ItemRisorsa, List<T>>, TableCell<ItemRisorsa, List<T>>>(){
				@Override
				public TableCell<ItemRisorsa, List<T>> call(TableColumn<ItemRisorsa, List<T>> param) {
					final TableCell<ItemRisorsa, List<T>> cell = new TableCell<ItemRisorsa, List<T>>() {

	                    @Override
	                    public void updateItem(List<T> item, boolean empty) {
	                        super.updateItem(item, empty);
	                        if (empty) {
	                            setGraphic(null);
	                            setText(null);
	                        } else {
	                        	if (!item.isEmpty() && item.get(0) instanceof LocalTime)
	                        		setGraphic(new Label(item.get(0).toString() + " - " + item.get(1).toString()));	
	                        	else{
	                        		List<T> elementiDisponibili = new ArrayList<>();
	                        		for (T o : item) {
	                        			if (o instanceof HasState) {
	                        				LocalTime oraI = tableView.getItems().get(getIndex()).getOra().get(0);
	                        				LocalTime oraF = oraI.plusMinutes(controller.getScaglionamentoOra());
	                        				if (!indisponibile(o, oraI, oraF))
	                        					elementiDisponibili.add(o);
	                        			}
	                        		}
	                        		ListView<T> listView = new ListView<>(FXCollections.observableArrayList(elementiDisponibili));
		                            listView.setPrefHeight(item.size()*23 + 10);
		                            listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<T>() {
		        			            @Override
		        			            public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
		        			            	tableView.getSelectionModel().select(getIndex(), getTableColumn());
		        			            	try {
		        			                	//<ItemRisorsa, List<T>> pos = tableView.getSelectionModel().getSelectedCells().get(0);
		        			                	ItemRisorsa item = tableView.getItems().get(getIndex());
		        			                	if (row != getIndex()) {
		        			                		row = getIndex();
		        			                		oraSelected = Utils.formatterOra.format(item.getOra().get(0));
		        			                		operaiSelected.clear();
		        			                		automezziSelected.clear();
		        			                		macchineSelected.clear();
		        			                	}
		        			            	}catch (IndexOutOfBoundsException e) {
		        								// TODO: handle exception
		        							}
		        			            	if (newValue instanceof Operaio)
		        			                	operaiSelected.add((Operaio) newValue);
		        			            	if (newValue instanceof Auto)
		        			                	automezziSelected.add((Auto) newValue);
		        			            	if (newValue instanceof Macchina)
		        			                	macchineSelected.add((Macchina) newValue);
		        			            }
		        					});
		                            setGraphic(listView);
	                        	}
	                        }
	                    }
	                    
	                    private boolean indisponibile(T item, LocalTime oraI, LocalTime oraF) {
	                		if (((HasState) item).getIndisponibilita().stream().filter(x -> x.getData().isEqual(datePicker.getValue()))
	                			.anyMatch(x -> ((x.getOraInizio().isBefore(oraF) && x.getOraFine().isAfter(oraF)) || 
	                				(x.getOraInizio().isBefore(oraI) && x.getOraFine().isAfter(oraI)) || 
	                				(!x.getOraInizio().isBefore(oraI) && !x.getOraFine().isAfter(oraF)))))
	                			return true;
	                		return false;
	                	}
	                    
	                };
	                return cell;
				}
			};
		
			TableColumn<ItemRisorsa, List<T>> itemOra = new TableColumn<ItemRisorsa, List<T>>("Orario");
			TableColumn<ItemRisorsa, List<T>> itemOperai = new TableColumn<ItemRisorsa, List<T>>("Operai");
			TableColumn<ItemRisorsa, List<T>> itemAuto = new TableColumn<ItemRisorsa, List<T>>("Auto");
			TableColumn<ItemRisorsa, List<T>> itemMacchine = new TableColumn<ItemRisorsa, List<T>>("Macchine");

			itemOra.setStyle("-fx-alignment: CENTER");
			itemOra.setMaxWidth(120);
			itemOra.setMinWidth(120);
			
			itemOra.setCellValueFactory(new PropertyValueFactory<>("ora"));
			itemOperai.setCellValueFactory(new PropertyValueFactory<>("operai"));
			itemAuto.setCellValueFactory(new PropertyValueFactory<>("auto"));
			itemMacchine.setCellValueFactory(new PropertyValueFactory<>("macchine"));
			itemOra.setCellFactory(cellFactory);
			itemOperai.setCellFactory(cellFactory);
			itemAuto.setCellFactory(cellFactory);
			itemMacchine.setCellFactory(cellFactory);
			tableView.getColumns().addAll(Arrays.asList(itemOra, itemOperai, itemAuto, itemMacchine));
			
			tableView.setItems(FXCollections.observableArrayList(getItemRisorse(date)));
			
			Label labelGiorno = new Label(Utils.formatterData.format(date));
			HBox boxButton = new HBox();
			{
				Button prev = new Button("<|");
				Button next = new Button("|>");
				prev.setOnAction(event -> {
					date = date.minusDays(1);
					labelGiorno.setText(Utils.formatterData.format(date));
					tableView.setItems(FXCollections.observableArrayList(getItemRisorse(date)));
				});
				next.setOnAction(event -> {
					date = date.plusDays(1);
					labelGiorno.setText(Utils.formatterData.format(date));
					tableView.setItems(FXCollections.observableArrayList(getItemRisorse(date)));
				});
				boxButton.getChildren().addAll(prev, next);
				
			}
			boxButton.setAlignment(Pos.CENTER);
			Button buttonConferma = new Button("Conferma");
			buttonConferma.setOnAction(event -> {
				listViewHours.getSelectionModel().clearAndSelect(row);
				listViewOperaio.getSelectionModel().clearSelection();
				listViewAuto.getSelectionModel().clearSelection();
				listViewMacchina.getSelectionModel().clearSelection();
				
				datePicker.setValue(date);
				textFieldInizio.setText(oraSelected);
				for (Operaio o : operaiSelected)
					listViewOperaio.getSelectionModel().select(o);
				for (Auto a : automezziSelected)
					listViewAuto.getSelectionModel().select(a);
				for (Macchina m : macchineSelected)
					listViewMacchina.getSelectionModel().select(m);

			});

			panel.getChildren().addAll(labelGiorno, tableView, boxButton, buttonConferma);
			
		}
		return panel;
	}
	
	private List<ItemRisorsa> getItemRisorse(LocalDate date) {
		Utils utils = new Utils(controller);
		List<ItemRisorsa> itemRisorse = new ArrayList<>();
		for (LocalTime time: utils.listaOreLavorative()) {
			LocalDateTime dateTime = date.atTime(time);
			try {
				itemRisorse.add(new ItemRisorsa(Arrays.asList(time, time.plusMinutes(controller.getScaglionamentoOra())), utils.operaiDisponibili(dateTime, dateTime.plusMinutes(controller.getScaglionamentoOra())), 
						utils.autoDisponibili(dateTime, dateTime.plusMinutes(controller.getScaglionamentoOra())), utils.macchineDisponibili(dateTime, dateTime.plusMinutes(controller.getScaglionamentoOra()))));
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		return itemRisorse;
		}
}
