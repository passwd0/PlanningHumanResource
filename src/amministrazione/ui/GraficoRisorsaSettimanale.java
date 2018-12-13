package amministrazione.ui;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import amministrazione.controller.Controller;
import amministrazione.model.Appuntamento;
import amministrazione.model.Auto;
import amministrazione.model.Cliente;
import amministrazione.model.HasState;
import amministrazione.model.Macchina;
import amministrazione.model.Operaio;
import amministrazione.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class GraficoRisorsaSettimanale<T> {
	private Stage window;
	private Parent parent;
	private Controller controller;
	private LocalDate date;
	private HasState selezione;
	private TableView<ItemAppuntamentiRisorsaSettimanale> tableViewSettimanale;
	private List<ItemAppuntamentiRisorsaSettimanale> itemAppuntamentiRisorsaSettimanales;
	private ListView<Appuntamento> listaAppuntamentiGiornata;
	private ListView<Appuntamento> listaAppuntamentiRisorsa;
	private DatePicker datePicker;
	private TextField textFieldInizio;
	private TextField textFieldFine;
	private List<AppuntamentoColor> appuntamentoColors;
	
	public GraficoRisorsaSettimanale(Stage window, Controller controller, LocalDate date, 
			HasState selezione, TableView<ItemAppuntamentiRisorsaSettimanale> tableViewSettimanale, 
			ListView<Appuntamento> listAppuntamentiGiornata, ListView<Appuntamento> listAppuntamentiRisorsa,
			DatePicker datePicker, TextField inizioAppuntamento, TextField fineAppuntamento ) {
		this.controller = controller;
		this.date = date;
		this.window = window;
		this.selezione = selezione;
		this.tableViewSettimanale = tableViewSettimanale;
		this.listaAppuntamentiGiornata = listAppuntamentiGiornata;
		this.listaAppuntamentiRisorsa = listAppuntamentiRisorsa;
		this.datePicker = datePicker;
		this.textFieldInizio = inizioAppuntamento;
		this.textFieldFine = fineAppuntamento;
		
		try {
			parent = initGraficoRisorsaSettimanale();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Parent getParent() {
		return parent;
	}

	private Parent initGraficoRisorsaSettimanale() throws ClassNotFoundException, SQLException {
		date = LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth());
		VBox panel = new VBox(20);
		{
			panel.setPadding(new Insets(20, 20, 20, 20));
			
			itemAppuntamentiRisorsaSettimanales = new ArrayList<>();

			tableViewSettimanale = new TableView<ItemAppuntamentiRisorsaSettimanale>();
			tableViewSettimanale.getSelectionModel().setCellSelectionEnabled(true);
			tableViewSettimanale.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			tableViewSettimanale.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			tableViewSettimanale.prefHeightProperty().bind(window.heightProperty());
			Callback<TableColumn<ItemAppuntamentiRisorsaSettimanale, List<Appuntamento>>, TableCell<ItemAppuntamentiRisorsaSettimanale, List<Appuntamento>>> cellFactory = new Callback<TableColumn<ItemAppuntamentiRisorsaSettimanale, List<Appuntamento>>, TableCell<ItemAppuntamentiRisorsaSettimanale, List<Appuntamento>>>(){
				@Override
				public TableCell<ItemAppuntamentiRisorsaSettimanale, List<Appuntamento>> call(TableColumn<ItemAppuntamentiRisorsaSettimanale, List<Appuntamento>> param) {
					final TableCell<ItemAppuntamentiRisorsaSettimanale, List<Appuntamento>> cell = new TableCell<ItemAppuntamentiRisorsaSettimanale, List<Appuntamento>>() {
	                
		            	@Override
		                public void updateItem(List<Appuntamento> item, boolean empty) {
	                        super.updateItem(item, empty);
	                        if (empty) {
	                            setGraphic(null);
	                            setText(null);
	                        } else {
	                        	Boolean isStyled = false;
	                        	String[] timeToParse = getTableView().getItems().get(getIndex()).getOra().split("-");
	                        	LocalTime time = LocalTime.parse(timeToParse[0].trim());
                        			if (indisponibile(selezione, date.minusDays(date.getDayOfWeek().getValue() - 1).plusDays(getTableView().getColumns().indexOf(getTableColumn()) - 1), time, time.plusMinutes(controller.getScaglionamentoOra()))) {
                        				setStyle("-fx-background-color: gray");
                        				isStyled = true;
                        			}
	                        	
	                        	StringBuilder temp = new StringBuilder();
	                        	if (!item.isEmpty()) {
                        			String str;
		                        	for (Appuntamento a : item) {
		                        		if (time.equals(a.getDataInizio().toLocalTime()) ||
		                        				(a.getDataInizio().getMinute() % controller.getScaglionamentoOra() != 0) && 
		                        				time.equals(a.getDataInizio().minusMinutes(Math.abs(a.getDataInizio().getMinute() - controller.getScaglionamentoOra())).toLocalTime())) {
		                        			if (selezione instanceof Operaio)
		                        				temp.append((str = automezziToString(a)) + (str.trim().length()==0?"":"\n") + (str = macchineToString(a)) + (str.trim().length()==0?"":"\n") + a.getCliente());
			                        		if (selezione instanceof Auto)
			                        			temp.append((str = operaiToString(a)) + (str.trim().length()==0?"":"\n") + (str = macchineToString(a)) + (str.trim().length()==0?"":"\n") + a.getCliente());
			                        		if (selezione instanceof Macchina)
			                        			temp.append((str = operaiToString(a)) + (str.trim().length()==0?"":"\n") + (str = automezziToString(a)) + (str.trim().length()==0?"":"\n") + a.getCliente());
			                        		if (selezione instanceof Cliente)
			                        			temp.append((str = operaiToString(a)) + (str.trim().length()==0?"":"\n") + (str = automezziToString(a)) + (str.trim().length()==0?"":"\n") + (str = macchineToString(a)));
		                        		}
		                        		for (AppuntamentoColor appuntamentoColor  : appuntamentoColors)
		                        			if (appuntamentoColor.getAppuntamento().equals(a)) {
		                        				setStyle(appuntamentoColor.getColor());
		                        				isStyled = true;
		                        			}
		                        	}
	                        	}
	                        	
	                        	if (!isStyled)
	                        		setStyle("");
	                        	setGraphic(new Label(temp.toString()));	                            
	                        }
	                    }

						private boolean indisponibile(HasState item, LocalDate date, LocalTime oraI, LocalTime oraF) {
	                		if (item.getIndisponibilita().stream()
	                			.anyMatch(x -> 
	                				(date.isEqual(x.getData()) &&
	                				((x.getOraInizio().isBefore(oraF) && x.getOraFine().isAfter(oraF)) || 
	                				(x.getOraInizio().isBefore(oraI) && x.getOraFine().isAfter(oraI)) || 
	                				(!x.getOraInizio().isBefore(oraI) && !x.getOraFine().isAfter(oraF))))))
	                			return true;
	                		return false;
	                	}
	                };
	                
	                cell.setOnMouseClicked(event -> {
	                	selectMainCell();
	                });
	                
	                ContextMenu contextMenu = new ContextMenu();
	            	MenuItem menuItemDel = new MenuItem("Cancella");
	            	MenuItem menuItemAdd = new MenuItem("Aggiungi");
	            	
	            	menuItemAdd.setOnAction(event -> {
	            		datePicker.setValue(date.plusDays(tableViewSettimanale.getSelectionModel().getSelectedCells().get(0).getColumn() - 1));
	            		ObservableList<ItemAppuntamentiRisorsaSettimanale> selezionati = tableViewSettimanale.getSelectionModel().getSelectedItems();
	            		textFieldInizio.setText(selezionati.get(0).getOra().split("-")[0].trim());
	            		textFieldFine.setText(selezionati.get(0).getOra().split("-")[1].trim());
	            		window.close();
	            	});
	            	menuItemDel.setOnAction(event -> {
	            		ObservableList<Appuntamento> appuntamentiDaRimuovere = selectedCell();
	            		try {
							controller.deleteAppuntamento(appuntamentiDaRimuovere);
		            		appuntamentoColors = new ArrayList<>(controller.getAppuntamentiSettimanaColor(date));
						} catch (ClassNotFoundException | SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							try {
								tableViewSettimanale.setItems(FXCollections.observableArrayList(getAppuntamentiRisorsaSettimanale(date, selezione)));
							} catch (ClassNotFoundException | SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	            		tableViewSettimanale.refresh();
	            		listaAppuntamentiGiornata.getItems().removeAll(appuntamentiDaRimuovere);
	            		listaAppuntamentiRisorsa.getItems().removeAll(appuntamentiDaRimuovere);
	            	});
            		cell.setContextMenu(contextMenu);
            		cell.setOnContextMenuRequested(event -> {
            			contextMenu.getItems().clear();
            			if (selectedCell().isEmpty())
            				contextMenu.getItems().add(menuItemAdd);
            			else
            				contextMenu.getItems().addAll(menuItemAdd, menuItemDel);
            			contextMenu.show(cell, event.getScreenX(), event.getScreenY());
        				event.consume();
            		});
	                
	                return cell;
				}
				
				private ObservableList<Appuntamento> selectedCell(){
					TablePosition<ItemAppuntamentiRisorsaSettimanale, List<Appuntamento>> pos = tableViewSettimanale.getSelectionModel().getSelectedCells().get(0);
					ItemAppuntamentiRisorsaSettimanale item = tableViewSettimanale.getItems().get(pos.getRow());
                	ObservableList<Appuntamento> selectedAppuntamento = (ObservableList<Appuntamento>)  pos.getTableColumn().getCellObservableValue(item).getValue();
                	return selectedAppuntamento;
				}
				
				private void selectMainCell() {
					ObservableList<Appuntamento> selectedAppuntamento = selectedCell();
                	if (!selectedAppuntamento.isEmpty()) {
                		Appuntamento appuntamento = (Appuntamento) selectedAppuntamento.get(0);
    					TablePosition<ItemAppuntamentiRisorsaSettimanale, List<Appuntamento>> pos = tableViewSettimanale.getSelectionModel().getSelectedCells().get(0);
                		int indiceDaSelezionare = 0;
                		for (LocalDateTime time = controller.getInizioLavoro().atDate(date.plusDays(pos.getColumn() - 1)); controller.getFineLavoro().isAfter(time.toLocalTime()); time = time.plusMinutes(controller.getScaglionamentoOra())) {
//                			if (time.isEqual(appuntamento.getDataInizio()))
                				
                			if (!time.isBefore(appuntamento.getDataInizio()) && time.isBefore(appuntamento.getDataFine()))
                				tableViewSettimanale.getSelectionModel().select(indiceDaSelezionare, pos.getTableColumn());
                			indiceDaSelezionare++;
                		}
                		
                		
                	}
				}
				
				private String automezziToString(Appuntamento a) {
					if (a.getAuto().isEmpty())
						return "";
					return a.getAuto().stream().map(Auto::toString).collect(Collectors.joining("\n"));
				}
				
				private String operaiToString(Appuntamento a) {
					if (a.getOperai().isEmpty())
						return "";
					return a.getOperai().stream().map(Operaio::toString).collect(Collectors.joining("\n"));
				}
				
				private String macchineToString(Appuntamento a) {
					if (a.getMacchine().isEmpty())
						return "";
					return a.getMacchine().stream().map(Macchina::toString).collect(Collectors.joining("\n"));
				}
			};
			TableColumn<ItemAppuntamentiRisorsaSettimanale, List<Appuntamento>>[] items = new TableColumn[7];
			TableColumn<ItemAppuntamentiRisorsaSettimanale, String> itemOra = new TableColumn<>("Orario");
			itemOra.setCellValueFactory(new PropertyValueFactory<>("ora"));
			tableViewSettimanale.getColumns().add(itemOra);
			itemOra.setMinWidth(110);
			itemOra.setMaxWidth(110);
			itemOra.setStyle("-fx-alignment: CENTER");

			String[] weekDay = {"lunedi", "martedi", "mercoledi", "giovedi", "venerdi", "sabato", "domenica"};
			for (int i = 0; i < 7; i++) {
				items[i] = new TableColumn<ItemAppuntamentiRisorsaSettimanale, List<Appuntamento>>(Utils.dayOfWeek[i]);
				items[i].setCellFactory(cellFactory);
				items[i].setCellValueFactory(new PropertyValueFactory<>(weekDay[i]));
				tableViewSettimanale.getColumns().add(items[i]);
			}
			try {
    		appuntamentoColors = new ArrayList<>(controller.getAppuntamentiSettimanaColor(date));
			}catch (Exception e) {
				// TODO: handle exception
			}
			itemAppuntamentiRisorsaSettimanales = getAppuntamentiRisorsaSettimanale(date, selezione);
			
			tableViewSettimanale.setItems(FXCollections.observableArrayList(itemAppuntamentiRisorsaSettimanales));
			Label labelMese = new Label(Utils.formatterData.format(date.minusDays(date.getDayOfWeek().getValue() - 1)) + " - " + Utils.formatterData.format(date.minusDays(date.getDayOfWeek().getValue()).plusWeeks(1)));
			HBox boxButton = new HBox();
			{
				Button prev = new Button("<|");
				Button next = new Button("|>");
				prev.setOnAction(event -> {
					date = date.minusWeeks(1);
					LocalDate inizioSettimana = date.minusDays(date.getDayOfWeek().getValue() - 1);
					labelMese.setText(Utils.formatterData.format(inizioSettimana) + " - " + Utils.formatterData.format(inizioSettimana.plusDays(6)));
            		try {
						appuntamentoColors = new ArrayList<>(controller.getAppuntamentiSettimanaColor(date));
					} catch (ClassNotFoundException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						tableViewSettimanale.setItems(FXCollections.observableArrayList(getAppuntamentiRisorsaSettimanale(date, selezione)));
					} catch (ClassNotFoundException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				next.setOnAction(event -> {
					date = date.plusWeeks(1);
					LocalDate inizioSettimana = date.minusDays(date.getDayOfWeek().getValue() - 1);
					labelMese.setText(Utils.formatterData.format(inizioSettimana) + " - " + Utils.formatterData.format(inizioSettimana.plusDays(6)));
            		try {
						appuntamentoColors = new ArrayList<>(controller.getAppuntamentiSettimanaColor(date));
					} catch (ClassNotFoundException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						tableViewSettimanale.setItems(FXCollections.observableArrayList(getAppuntamentiRisorsaSettimanale(date, selezione)));
					} catch (ClassNotFoundException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				boxButton.getChildren().addAll(prev, next);
				
			}
			boxButton.setAlignment(Pos.CENTER);
			panel.getChildren().addAll(labelMese, tableViewSettimanale, boxButton);
		}
		return panel;
	}
	
	private List<ItemAppuntamentiRisorsaSettimanale> getAppuntamentiRisorsaSettimanale(LocalDate date, HasState selezione) throws ClassNotFoundException, SQLException{
		List<ItemAppuntamentiRisorsaSettimanale> itemAppuntamentiRisorsaSettimanales = new ArrayList<>();
		List<Appuntamento> appuntamentiSettimanali = controller.getAppuntamentiSettimana(date);
		List<LocalTime> hours = new ArrayList<>(new Utils(controller).listaOreLavorative());
		for (LocalTime hour : hours) {
			List<List<Appuntamento>> appuntamentiFasciaOraria = new ArrayList<>();
			LocalDate inizioSettimana = date.minusDays(date.getDayOfWeek().getValue() - 1);
			for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
				LocalDateTime cellaOraInizio = LocalDateTime.of(inizioSettimana, hour);
				LocalDateTime cellaOraFine = LocalDateTime.of(inizioSettimana, hour).plusMinutes(controller.getScaglionamentoOra());
				List<Appuntamento> appuntamentiOra = new ArrayList<>();
				for (Appuntamento a : appuntamentiSettimanali) {
					if ((cellaOraInizio.isAfter(a.getDataInizio()) &&  cellaOraInizio.isBefore(a.getDataFine())) || 
							(cellaOraFine.isAfter(a.getDataInizio()) && cellaOraFine.isBefore(a.getDataFine())) || 
							(!cellaOraInizio.isAfter(a.getDataInizio()) && !cellaOraFine.isBefore(a.getDataFine()))) {
								if (selezione instanceof Operaio)
									if (a.getOperai().contains(selezione))
										appuntamentiOra.add(a);
								if (selezione instanceof Auto)
									if (a.getAuto().contains(selezione))
										appuntamentiOra.add(a);
								if (selezione instanceof Macchina)
									if (a.getMacchine().contains(selezione))
										appuntamentiOra.add(a);
								if (selezione instanceof Cliente)
									if (a.getCliente().equals(selezione))
										appuntamentiOra.add(a);
							}
				}
				appuntamentiFasciaOraria.add(appuntamentiOra);
				inizioSettimana = inizioSettimana.plusDays(1);
			}
			itemAppuntamentiRisorsaSettimanales.add(new ItemAppuntamentiRisorsaSettimanale(hour.format(Utils.formatterOra) + " - " + hour.plusMinutes(controller.getScaglionamentoOra()).format(Utils.formatterOra) ,FXCollections.observableArrayList(appuntamentiFasciaOraria.get(0)),FXCollections.observableArrayList(appuntamentiFasciaOraria.get(1)),
					FXCollections.observableArrayList(appuntamentiFasciaOraria.get(2)),FXCollections.observableArrayList(appuntamentiFasciaOraria.get(3)),FXCollections.observableArrayList(appuntamentiFasciaOraria.get(4)),
							FXCollections.observableArrayList(appuntamentiFasciaOraria.get(5)),FXCollections.observableArrayList(appuntamentiFasciaOraria.get(6))));
		}
		return itemAppuntamentiRisorsaSettimanales;
	}
}
