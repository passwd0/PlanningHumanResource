package planninghumanresource.ui;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import planninghumanresource.controller.Controller;
import planninghumanresource.controller.ListenerAppuntamenti;
import planninghumanresource.model.Appuntamento;
import planninghumanresource.model.Auto;
import planninghumanresource.model.Cliente;
import planninghumanresource.model.Macchina;
import planninghumanresource.model.Operaio;
import planninghumanresource.utils.Utils;

public class AmministrazionePane extends BorderPane {
	private Controller controller;
	private ListView<LocalTime> listHours;
	private ListView<Appuntamento> listaAppuntamentiGiorno;
	private ListView<Appuntamento> listaAppuntamentiRisorsa;
	private DatePicker datePicker;
	private TextField oraInizio;
	private TextField oraFine;
	private ListView<Operaio> listaOperai;
	private ListView<Auto> listaAuto;
	private ListView<Macchina> listaMacchine;
	private ListView<Cliente> listaClienti;
	private CheckBox checkboxRepeat;
	private TableView<ItemRisorsa> tableViewGraficoGiornata;
	private TableView<ItemAppuntamentiRisorsaSettimanale> tableViewGraficoSettimanale;
	private RipetitionPane ripetitionPane;
	
	public AmministrazionePane(Stage stage, Controller controller) {
		this.controller = controller;
		initTopPane();
		initCenterPane();
		initLeftPane();
		initRightPane();
		if (!controller.getAlert().containsKey("alertControl") || controller.getAlert().get("alertControl")) {
			Task<Void> sleeper = new Task<Void>() {
	            @Override
	            protected Void call() throws Exception {
	                try {
	                    Thread.sleep(2000);
	                } catch (InterruptedException e) {
	                }
	                return null;
	            }
	        };
	        sleeper.setOnSucceeded(event -> {
	           	Alert alert = Utils.createAlertWithOptOut(controller, "alertControl", AlertType.INFORMATION, "Info", 
                       "Per eseguire una selezione multipla tenere premuto il tasto <ctrl>", "Non chiedermelo più", ButtonType.OK);
	        	alert.show();
	        });
	        new Thread(sleeper).start();
		}
		
		try {
			Class.forName("org.postgresql.Driver");

	        Connection lConn = DriverManager.getConnection(Utils.url + Utils.db, Utils.user, Utils.pass);
	        
	        ListenerAppuntamenti listener = new ListenerAppuntamenti(lConn, controller, listaAppuntamentiGiorno, 
	        		listaOperai, listaAuto, listaMacchine, listaClienti, datePicker);
	        listener.setDaemon(true);
	        listener.start();
	
		} catch (SQLException | ClassNotFoundException  e) {
			e.getMessage();
		}
	}
	
	private void initTopPane() {
		MenuBar menubar  = new MenuBar();
		Menu menuFile = new Menu("File");
		Menu menuReport = new Menu("Report");
		
		MenuItem menuSave = new MenuItem("Salva");
		MenuItem menuOpen = new MenuItem("Apri");
		MenuItem menuSetting = new MenuItem("Impostazioni");
		
		Menu menuPeriodo = new Menu("Periodo");
		MenuItem menuCliente = new MenuItem("Cliente");
		MenuItem menuAppuntamentiGiornalieri = new MenuItem("Giornaliero");
		MenuItem menuAppuntamentiSettimanali = new MenuItem("Settimanale");
		MenuItem menuAppuntamentiMensili = new MenuItem("Mensile");
		MenuItem menuAppuntamentiAdvanced = new MenuItem("Avanzato");
		
		menuSave.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
		menuOpen.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
		menuCliente.setAccelerator(KeyCombination.keyCombination("Ctrl+P"));
		
		menuSave.setOnAction(event -> {
			try {
				File file = new File("appuntamenti"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HH:mm"))+".txt");
				controller.saveAppuntamenti(file);
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Salvataggio");
				alert.setHeaderText("File salvato: " + file.getName());
				alert.setContentText(null);

				alert.showAndWait();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		menuOpen.setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Carica File");
			File f = fileChooser.showOpenDialog(new Stage());
				try {
					controller.setFileAppuntamenti(f);
					controller.saveSetting();
					controller.setAppuntamenti(controller.loadAppuntamenti(f));
					listaAppuntamentiGiorno.setItems(controller.getAppuntamentiGiorno(datePicker.getValue()));
					listaAppuntamentiRisorsa.getItems().clear();
				} catch (IOException | NullPointerException | ClassNotFoundException | SQLException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
		});
		
		menuSetting.setOnAction(event -> {
			Stage window = new Stage();
			Parent parent = new SettingPane(window, controller).getParent();
			window.setTitle("Inserisci Impostazioni");
			window.setResizable(false);
			window.setScene(new Scene(parent, 400, 300));
			window.show();
		});
		
		menuAppuntamentiGiornalieri.setOnAction(event -> {
			Stage windowReportOutput = new Stage();
			LocalDate date = datePicker.getValue();
			String titoloData = Utils.dayOfWeek[date.getDayOfWeek().ordinal()] + " " +date.format(Utils.formatterData);
			windowReportOutput.setTitle("Report Giornaliero: " + titoloData);
			ReportPeriodoPane reportPane;
			try {
				reportPane = new ReportPeriodoPane(windowReportOutput, controller, datePicker.getValue(), controller.getAppuntamentiGiorno(datePicker.getValue()), titoloData);
				Parent parent = reportPane.getParent();
				windowReportOutput.setScene(new Scene(parent, 930, 600));
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			windowReportOutput.show();
		});
		
		menuAppuntamentiSettimanali.setOnAction(event -> {
			Stage windowReportOutput = new Stage();
			LocalDate inizioSettimana = datePicker.getValue().minusDays(datePicker.getValue().getDayOfWeek().getValue() - 1);
			String titoloData = "Lunedì " + inizioSettimana.format(Utils.formatterData) + " - Domenica " + inizioSettimana.plusDays(6).format(Utils.formatterData);
			windowReportOutput.setTitle("Report Settimanale: " + titoloData);
			ReportPeriodoPane reportPane;
			try {
				reportPane = new ReportPeriodoPane(windowReportOutput, controller, datePicker.getValue(), controller.getAppuntamentiSettimana(datePicker.getValue()), titoloData);
				Parent parent = reportPane.getParent();
				windowReportOutput.setScene(new Scene(parent, 930, 600));
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			windowReportOutput.show();
		});
		
		menuAppuntamentiMensili.setOnAction(event -> {
			Stage windowReportOutput = new Stage();
			String titoloData = Utils.mese[datePicker.getValue().getMonth().ordinal()] + " " + datePicker.getValue().getYear();
			windowReportOutput.setTitle("Report Mensile: " + titoloData);
			ReportPeriodoPane reportPane;
			try {
				reportPane = new ReportPeriodoPane(windowReportOutput, controller, datePicker.getValue(), controller.getAppuntamentiMensile(datePicker.getValue()), titoloData);
				Parent parent = reportPane.getParent();
				windowReportOutput.setScene(new Scene(parent, 930, 600));
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			windowReportOutput.show();
		});
		
		menuAppuntamentiAdvanced.setOnAction(event -> {
			Stage window = new Stage();
			Parent parent = insertAdvanceReportPane(window);
			window.setTitle("Report Avanzato");
			window.setScene(new Scene(parent, 500, 450));
			window.show();
		});
		
		menuCliente.setOnAction(event -> {
			Stage window = new Stage();
			VBox panel = new VBox(20);
			ListView<Cliente> list = new ListView<>(listaClienti.getItems());
			list.setMinHeight(150);
			list.setMinWidth(200);
			list.setPrefHeight(list.getItems().size() * 31 + 50);
			list.getPrefHeight();
			window.setHeight(list.getMinHeight()>list.getPrefHeight()?list.getMinHeight():list.getPrefHeight());
			if (window.getHeight() < 150)
				window.setHeight(150);
			list.prefHeightProperty().bind(window.heightProperty());
			list.setOnKeyPressed(event1 -> {
				if (event1.getCode() == KeyCode.ENTER) {
					Cliente cliente = list.getSelectionModel().getSelectedItem();
					Stage stage = new Stage();
					ReportClientePane reportClientePane = new ReportClientePane(window, controller, cliente, datePicker.getValue());
					stage.setTitle("Report Cliente: " + cliente.toString());
					stage.setScene(new Scene(reportClientePane.getParent()));
					stage.show();
					window.close();
				}
			});
			list.setOnMouseClicked(event1 -> {
				if (event1.getButton() == MouseButton.PRIMARY && event1.getClickCount() == 2) {
					Cliente cliente = list.getSelectionModel().getSelectedItem();
					Stage stage = new Stage();
					ReportClientePane reportClientePane = new ReportClientePane(window, controller, cliente, datePicker.getValue());
					stage.setTitle(cliente.toString());
					stage.setScene(new Scene(reportClientePane.getParent()));
					stage.show();
					window.close();
				}
			});
			panel.getChildren().add(list);
			list.setOnKeyPressed(event1 -> {
				if (event1.getCode() == KeyCode.ESCAPE)
					window.close();
			});
			window.setTitle("Cliente");
			window.setResizable(true);
			window.setScene(new Scene(panel));
			window.show();
		});
		menuFile.getItems().addAll(menuSave, menuOpen, menuSetting);
		menuPeriodo.getItems().addAll(menuAppuntamentiGiornalieri, menuAppuntamentiSettimanali, menuAppuntamentiMensili, menuAppuntamentiAdvanced);
		menuReport.getItems().addAll(menuPeriodo, menuCliente);
		menubar.getMenus().addAll(menuFile, menuReport);
		setTop(menubar);
	}
	
	private void initRightPane() {
		VBox panelLeft = new VBox();
		{
			panelLeft.setPadding(new Insets(0, 10, 0, 0));
			panelLeft.setPrefWidth(600);
			try {
				listaAppuntamentiGiorno = new ListView<>(controller.getAppuntamentiGiorno(datePicker.getValue()));
			} catch (ClassNotFoundException | SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			listaAppuntamentiGiorno.prefHeightProperty().bind(heightProperty());
			initWidget();
			listaAppuntamentiGiorno.setOnKeyPressed(event -> {
				Appuntamento appuntamento = listaAppuntamentiGiorno.getSelectionModel().getSelectedItem();
				if (event.getCode() == KeyCode.DELETE) {
					if (initAlertSureDeletion()) {
						try {
							controller.deleteAppuntamento(appuntamento);
							listaAppuntamentiRisorsa.getItems().remove(appuntamento);
							listaAppuntamentiGiorno.getItems().remove(appuntamento);
							listHours.getSelectionModel().clearSelection();
						}catch (Exception e) {
							e.getStackTrace();
						}
					}
	            }
	        });
			ContextMenu contextMenuAppuntamento = new ContextMenu();
			MenuItem menuItemAppuntamentoEdit = new MenuItem("Modifica");
			MenuItem menuItemAppuntamentoNota = new MenuItem("Nota");
			MenuItem menuItemAppuntamentoDel = new MenuItem("Cancella");
			menuItemAppuntamentoEdit.setOnAction(event -> {
				Appuntamento appuntamento = listaAppuntamentiGiorno.getSelectionModel().getSelectedItem();
				try {
					controller.deleteAppuntamento(appuntamento);
					listaAppuntamentiGiorno.getItems().remove(appuntamento);
					listaAppuntamentiRisorsa.getItems().remove(appuntamento);
				}catch (Exception e) {
					e.getStackTrace();
				}
				
				oraInizio.setText(appuntamento.getDataInizio().format(Utils.formatterOra));
				oraFine.setText(appuntamento.getDataFine().format(Utils.formatterOra));
				listaOperai.getItems().clear();
				listaAuto.getItems().clear();
				listaMacchine.getItems().clear();
				
				listaOperai.setItems(FXCollections.observableArrayList(controller.getOperai()));
				listaAuto.setItems(FXCollections.observableArrayList(controller.getAuto()));
				listaMacchine.setItems(FXCollections.observableArrayList(controller.getMacchine()));
				
				for (int i = 0; i < listaOperai.getItems().size(); i++)
					if (appuntamento.getOperai().contains(listaOperai.getItems().get(i)))
						listaOperai.getSelectionModel().select(i);
				for (int i = 0; i < listaAuto.getItems().size(); i++)
					if (appuntamento.getAuto().contains(listaAuto.getItems().get(i)))
						listaAuto.getSelectionModel().select(i);
				for (int i = 0; i < listaMacchine.getItems().size(); i++)
					if (appuntamento.getMacchine().contains(listaMacchine.getItems().get(i)))
						listaMacchine.getSelectionModel().select(i);
				for (int i = 0; i < listaClienti.getItems().size(); i++)
					if (appuntamento.getCliente().equals(listaClienti.getItems().get(i)))
						listaClienti.getSelectionModel().select(i);
			});
			menuItemAppuntamentoNota.setOnAction(event -> {
				Appuntamento appuntamento = listaAppuntamentiGiorno.getSelectionModel().getSelectedItem();
				final Stage stage = new Stage();
				stage.setTitle("Nota");
				VBox panel = new VBox(20);
				panel.setPadding(new Insets(20, 10, 20, 10));
                TextArea textAreaNote = new TextArea();
                textAreaNote.setText(appuntamento.getNota());
                Button conferma = new Button("Conferma");
                conferma.setOnAction(event1 -> {
                	stage.close();
                	try {
                		controller.addNota(appuntamento, textAreaNote.getText());
                		listaAppuntamentiGiorno.refresh();
                	}catch (Exception e) {
						// TODO: handle exception
					}
                });
                
                panel.getChildren().addAll(textAreaNote, conferma);
                stage.setScene(new Scene(panel, 300, 200));
                stage.show();
			});
			menuItemAppuntamentoDel.setOnAction(event -> {
				Appuntamento appuntamento = listaAppuntamentiGiorno.getSelectionModel().getSelectedItem();
				try {
					if (initAlertSureDeletion()) {
						controller.deleteAppuntamento(appuntamento);
						listaAppuntamentiRisorsa.getItems().remove(appuntamento);
						listaAppuntamentiGiorno.getItems().remove(appuntamento);
						listHours.getSelectionModel().clearSelection();
					}
				}catch (Exception e) {
					e.getStackTrace();
				}
			});
			
			listaAppuntamentiGiorno.setContextMenu(contextMenuAppuntamento);
			listaAppuntamentiGiorno.setOnContextMenuRequested(event -> {
				if (listaAppuntamentiGiorno.getSelectionModel().getSelectedItems().size() == 1) {
					contextMenuAppuntamento.getItems().clear();
					contextMenuAppuntamento.getItems().addAll(menuItemAppuntamentoEdit, menuItemAppuntamentoNota, menuItemAppuntamentoDel);
				}
				contextMenuAppuntamento.show(listaAppuntamentiGiorno, event.getScreenX(), event.getScreenY());
				event.consume();
			});
			panelLeft.getChildren().addAll(listaAppuntamentiGiorno);
			
		}
		VBox panelRight = new VBox();
		{
			panelRight.setPadding(new Insets(0, 0, 0, 10));
			panelRight.setPrefWidth(400);
			listaAppuntamentiRisorsa = new ListView<>();
			listaAppuntamentiRisorsa.prefHeightProperty().bind(heightProperty());
			listaAppuntamentiRisorsa.setOnKeyPressed(event -> {
	            if (event.getCode() == KeyCode.DELETE) {
	            	if (initAlertSureDeletion()) {
	            		ObservableList<Appuntamento> appuntamentiSelezionati = listaAppuntamentiRisorsa.getSelectionModel().getSelectedItems();
		            	try {
							controller.deleteAppuntamento(appuntamentiSelezionati);
							listaAppuntamentiRisorsa.getItems().removeAll(appuntamentiSelezionati);
							listaAppuntamentiGiorno.getItems().removeAll(appuntamentiSelezionati);
							listHours.getSelectionModel().clearSelection();
		            	}catch (Exception e) {
							e.getStackTrace();
						}
		            }
	            }
	        });
			listaAppuntamentiRisorsa.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.ENTER)
	                datePicker.setValue(listaAppuntamentiRisorsa.getSelectionModel().getSelectedItem().getDataInizio().toLocalDate());
			});
			listaAppuntamentiRisorsa.setOnMouseClicked(event -> {
		        if(event.getButton().equals(MouseButton.PRIMARY)){
		            if(event.getClickCount() == 2){
		                datePicker.setValue(listaAppuntamentiRisorsa.getSelectionModel().getSelectedItem().getDataInizio().toLocalDate());
			        }
			    }
			});
			
			ContextMenu contextMenuAppuntamento = new ContextMenu();
			MenuItem menuItemAppuntamentoEdit = new MenuItem("Modifica");
			MenuItem menuItemAppuntamentoDel = new MenuItem("Cancella");
			menuItemAppuntamentoEdit.setOnAction(event -> {
				Appuntamento appuntamento = listaAppuntamentiRisorsa.getSelectionModel().getSelectedItem();
				try {
					controller.deleteAppuntamento(listaAppuntamentiRisorsa.getSelectionModel().getSelectedItem());
				}catch (Exception e) {
					e.getStackTrace();
				}
				listaAppuntamentiRisorsa.getItems().remove(appuntamento);
				listaAppuntamentiGiorno.getItems().remove(appuntamento);
				
				datePicker.setValue(appuntamento.getDataInizio().toLocalDate());
				oraInizio.setText(appuntamento.getDataInizio().format(Utils.formatterOra));
				oraFine.setText(appuntamento.getDataFine().format(Utils.formatterOra));
				listaOperai.getItems().clear();
				listaAuto.getItems().clear();
				listaMacchine.getItems().clear();
				Utils utils = new Utils(controller);
				try {
					listaOperai.getItems().addAll(utils.operaiDisponibili(LocalTime.parse(oraInizio.getText()).atDate(datePicker.getValue()), LocalTime.parse(oraFine.getText()).atDate(datePicker.getValue())));
					listaAuto.getItems().addAll(utils.autoDisponibili(LocalTime.parse(oraInizio.getText()).atDate(datePicker.getValue()), LocalTime.parse(oraFine.getText()).atDate(datePicker.getValue())));
					listaMacchine.getItems().addAll(utils.macchineDisponibili(LocalTime.parse(oraInizio.getText()).atDate(datePicker.getValue()), LocalTime.parse(oraFine.getText()).atDate(datePicker.getValue())));
				}catch (Exception e) {
					// TODO: handle exception
				}
				for (int i = 0; i < listaOperai.getItems().size(); i++)
					if (appuntamento.getOperai().contains(listaOperai.getItems().get(i)))
						listaOperai.getSelectionModel().select(i);
				for (int i = 0; i < listaAuto.getItems().size(); i++)
					if (appuntamento.getAuto().contains(listaAuto.getItems().get(i)))
						listaAuto.getSelectionModel().select(i);
				for (int i = 0; i < listaMacchine.getItems().size(); i++)
					if (appuntamento.getMacchine().contains(listaMacchine.getItems().get(i)))
						listaMacchine.getSelectionModel().select(i);
				for (int i = 0; i < listaClienti.getItems().size(); i++)
					if (appuntamento.getCliente().equals(listaClienti.getItems().get(i)))
						listaClienti.getSelectionModel().select(i);
			});
			menuItemAppuntamentoDel.setOnAction(event -> {
				Appuntamento appuntamento = listaAppuntamentiRisorsa.getSelectionModel().getSelectedItem();
				if (initAlertSureDeletion()) {
					try {
						controller.deleteAppuntamento(appuntamento);
						listaAppuntamentiRisorsa.getItems().remove(appuntamento);
						listaAppuntamentiGiorno.getItems().remove(appuntamento);
					}catch (Exception e) {
						e.getStackTrace();
					}
				}
			});
			listaAppuntamentiRisorsa.setContextMenu(contextMenuAppuntamento);
			listaAppuntamentiRisorsa.setOnContextMenuRequested(event -> {
				if (listaAppuntamentiRisorsa.getSelectionModel().getSelectedItems().size() == 1) {
					contextMenuAppuntamento.getItems().clear();
					contextMenuAppuntamento.getItems().addAll(menuItemAppuntamentoEdit, menuItemAppuntamentoDel);
				}
				contextMenuAppuntamento.show(listaAppuntamentiRisorsa, event.getScreenX(), event.getScreenY());
				event.consume();
			});
			
			panelRight.getChildren().addAll(listaAppuntamentiRisorsa);
		}
		
		HBox panel = new HBox();
		{
			panel.setPadding(new Insets(20, 20, 20, 10));
			panel.getChildren().addAll(panelLeft, panelRight);
		}
		this.setRight(panel);
	}

	private void initCenterPane() {
		VBox panel = new VBox();
		{
			panel.setSpacing(20);
			panel.setPadding(new Insets(20, 10, 20, 10));
			panel.setMinWidth(300);
			HBox dateBox = new HBox(15);
			{
				Label dayOfWeek = new Label();
				dayOfWeek.setPrefWidth(60);
				datePicker = new DatePicker(LocalDate.now());
				datePicker.setShowWeekNumbers(false);
				dayOfWeek.setText(Utils.dayOfWeek[datePicker.getValue().getDayOfWeek().ordinal()].substring(0, 3));
				datePicker.setOnAction(event -> {
					dayOfWeek.setText(Utils.dayOfWeek[datePicker.getValue().getDayOfWeek().ordinal()].substring(0, 3));
					listaOperai.refresh();
					listaAuto.refresh();
					listaMacchine.refresh();
					try {
						listaAppuntamentiGiorno.setItems(controller.getAppuntamentiGiorno(datePicker.getValue()));
					} catch (ClassNotFoundException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				
				checkboxRepeat = new CheckBox();
				checkboxRepeat.setOnAction(event -> {
					if (checkboxRepeat.isSelected()) {
						Stage window = new Stage();
						ripetitionPane = new RipetitionPane(window, datePicker);
						window.setTitle("Personalizza");
						window.setResizable(false);
						window.setScene(new Scene(ripetitionPane.getParent(), 500, 600));
						window.show();
					}
				});
				
				dateBox.setAlignment(Pos.CENTER);
				dateBox.getChildren().addAll(dayOfWeek, datePicker, checkboxRepeat);
			}
			HBox hourPanel = new HBox();
			{
				oraInizio = new TextField();
				oraFine = new TextField();
				oraInizio.setEditable(false);
				oraFine.setEditable(false);
				hourPanel.getChildren().addAll(oraInizio, oraFine);
			}
			
			Insets labelInsets = new Insets(0, 0, -20, 0);
			Label labelOperaio = new Label("Operai");
			labelOperaio.setFont(Font.font(15));
			labelOperaio.setPadding(labelInsets);
			listaOperai = new ListView<>(FXCollections.observableArrayList(controller.getOperai()));
			listaOperai.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			listaOperai.setOnKeyPressed(event -> {
	            if (event.getCode() == KeyCode.DELETE) {
	            	if (initAlertSureDeletion()) {
		            	try {
							controller.deleteOperaio(listaOperai.getSelectionModel().getSelectedItems());
						}catch (Exception e) {
							e.getStackTrace();
						}
						listaOperai.getItems().removeAll(listaOperai.getSelectionModel().getSelectedItems());
		            }
	            }
	        });
			ContextMenu contextMenuOperaio = new ContextMenu();
			MenuItem menuItemOperaioDel = new MenuItem("Cancella");
			MenuItem menuItemOperaioAdd = new MenuItem("Aggiungi");
			MenuItem menuItemOperaioView = new MenuItem("Agenda");
			MenuItem menuItemOperaioStato = new MenuItem("Cambia Stato");
			
			listaOperai.setContextMenu(contextMenuOperaio);
			
			listaOperai.setOnContextMenuRequested(event -> {
				contextMenuOperaio.getItems().clear();
				contextMenuOperaio.getItems().add(menuItemOperaioAdd);
				int size = listaOperai.getSelectionModel().getSelectedItems().size();
				if (size != 0)
					contextMenuOperaio.getItems().addAll(menuItemOperaioDel);
				if (size == 1) {
					contextMenuOperaio.getItems().addAll(menuItemOperaioStato, menuItemOperaioView);
				}
				contextMenuOperaio.show(listaOperai, event.getScreenX(), event.getScreenY());
				event.consume();
			});
			
			listaOperai.setOnMouseClicked(event -> {
				listaAppuntamentiRisorsa.getItems().clear();
				listaAppuntamentiRisorsa.getItems().addAll(controller.getAppuntamentoRisorsa(listaOperai.getSelectionModel().getSelectedItem()));
				
				listaAppuntamentiRisorsa.setCellFactory(new Callback<ListView<Appuntamento>, ListCell<Appuntamento>>() {
					
					@Override
					public ListCell<Appuntamento> call(ListView<Appuntamento> param) {
						return new AppuntamentiRisorse();
					}
				});
			});
			
			initFunctionOperai(menuItemOperaioStato, menuItemOperaioDel, menuItemOperaioAdd, menuItemOperaioView);
			
			Label labelAuto = new Label("Auto");
			labelAuto.setFont(Font.font(15));
			labelAuto.setPadding(labelInsets);
			listaAuto = new ListView<>(FXCollections.observableArrayList(controller.getAuto()));
			listaAuto.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			listaAuto.setOnKeyPressed(event -> {
	            if (event.getCode() == KeyCode.DELETE) {
	            	if (initAlertSureDeletion()) {
		            	try {
							controller.deleteAuto(listaAuto.getSelectionModel().getSelectedItems());
						}catch (Exception e) {
							e.getStackTrace();
						}
						listaAuto.getItems().removeAll(listaAuto.getSelectionModel().getSelectedItems());
		            }
	            }
	        });
			ContextMenu contextMenuAuto = new ContextMenu();
			MenuItem menuItemAutoDel = new MenuItem("Cancella");
			MenuItem menuItemAutoAdd = new MenuItem("Aggiungi");
			MenuItem menuItemAutoView = new MenuItem("Agenda");
			MenuItem menuItemAutoStato = new MenuItem("Cambia Stato");
			
			listaAuto.setContextMenu(contextMenuAuto);
			listaAuto.setOnContextMenuRequested(event -> {
				contextMenuAuto.getItems().clear();
				contextMenuAuto.getItems().add(menuItemAutoAdd);
				int size = listaAuto.getSelectionModel().getSelectedItems().size();
				if (size != 0)
					contextMenuAuto.getItems().addAll(menuItemAutoDel);
				if (size == 1) {
					contextMenuAuto.getItems().addAll(menuItemAutoStato, menuItemAutoView);
				}
				contextMenuAuto.show(listaAuto, event.getScreenX(), event.getScreenY());
				event.consume();
			});
			
			listaAuto.setOnMouseClicked(event -> {
				listaAppuntamentiRisorsa.getItems().clear();
				listaAppuntamentiRisorsa.getItems().addAll(controller.getAppuntamentoRisorsa(listaAuto.getSelectionModel().getSelectedItem()));
				
				listaAppuntamentiRisorsa.setCellFactory(new Callback<ListView<Appuntamento>, ListCell<Appuntamento>>() {
					
					@Override
					public ListCell<Appuntamento> call(ListView<Appuntamento> param) {
						return new AppuntamentiRisorse();
					}
				});
			});
			
			initFunctionAuto(menuItemAutoStato, menuItemAutoDel, menuItemAutoAdd, menuItemAutoView);
			
			Label labelMacchina = new Label("Macchine");
			labelMacchina.setFont(Font.font(15));
			labelMacchina.setPadding(labelInsets);
			listaMacchine = new ListView<>(FXCollections.observableArrayList(controller.getMacchine()));
			listaMacchine.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			listaMacchine.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.DELETE) {
					if (initAlertSureDeletion()) {
						try {
							controller.deleteMacchina(listaMacchine.getSelectionModel().getSelectedItems());
						}catch (Exception e) {
							e.getStackTrace();
						}
						listaMacchine.getItems().removeAll(listaMacchine.getSelectionModel().getSelectedItems());
		            }
				}
			});
			
			listaMacchine.setOnMouseClicked(event -> {
				listaAppuntamentiRisorsa.getItems().clear();
				listaAppuntamentiRisorsa.getItems().addAll(controller.getAppuntamentoRisorsa(listaMacchine.getSelectionModel().getSelectedItem()));
				
				listaAppuntamentiRisorsa.setCellFactory(new Callback<ListView<Appuntamento>, ListCell<Appuntamento>>() {
					
					@Override
					public ListCell<Appuntamento> call(ListView<Appuntamento> param) {
						return new AppuntamentiRisorse();
					}
				});
			});
			ContextMenu contextMenuMacchina = new ContextMenu();
			MenuItem menuItemMacchinaDel = new MenuItem("Cancella");
			MenuItem menuItemMacchinaAdd = new MenuItem("Aggiungi");
			MenuItem menuItemMacchinaView = new MenuItem("Agenda");
			MenuItem menuItemMacchinaStato = new MenuItem("Cambia Stato");
			
			listaMacchine.setContextMenu(contextMenuMacchina);
			listaMacchine.setOnContextMenuRequested(event -> {
				contextMenuMacchina.getItems().clear();
				contextMenuMacchina.getItems().add(menuItemMacchinaAdd);
				int size = listaMacchine.getSelectionModel().getSelectedItems().size();
				if (size != 0)
					contextMenuMacchina.getItems().addAll(menuItemMacchinaDel);
				if (size == 1) {
					contextMenuMacchina.getItems().addAll(menuItemMacchinaStato, menuItemMacchinaView);
				}
				contextMenuMacchina.show(listaMacchine, event.getScreenX(), event.getScreenY());
				event.consume();
			});
			
			initFunctionMacchine(menuItemMacchinaStato, menuItemMacchinaDel, menuItemMacchinaAdd, menuItemMacchinaView);
			
			Label labelCliente = new Label("Clienti");
			labelCliente.setFont(Font.font(15));
			labelCliente.setPadding(labelInsets);
			listaClienti = new ListView<>(FXCollections.observableArrayList(controller.getClienti()));
			listaClienti.setOnKeyPressed(event -> {
	            if (event.getCode() == KeyCode.DELETE) {
	            	if (initAlertSureDeletion()) {
		            	try {
							controller.deleteCliente(listaClienti.getSelectionModel().getSelectedItem());
						}catch (Exception e) {
							e.getStackTrace();
						}
						listaClienti.getItems().removeAll(listaClienti.getSelectionModel().getSelectedItems());
		            }
	            }
	        });
			ContextMenu contextMenuCliente = new ContextMenu();
			MenuItem menuItemClienteDel = new MenuItem("Cancella");
			MenuItem menuItemClienteAdd = new MenuItem("Aggiungi");
			MenuItem menuItemClienteView = new MenuItem("Agenda");
			MenuItem menuItemClienteStato = new MenuItem("Cambia Stato");
			
			listaClienti.setContextMenu(contextMenuCliente);
			listaClienti.setOnContextMenuRequested(event -> {
				contextMenuCliente.getItems().clear();
				contextMenuCliente.getItems().add(menuItemClienteAdd);
				int size = listaClienti.getSelectionModel().getSelectedItems().size();
				if (size != 0)
					contextMenuCliente.getItems().addAll(menuItemClienteDel);
				if (size == 1) {
					contextMenuCliente.getItems().addAll(menuItemClienteStato, menuItemClienteView);
				}
				contextMenuCliente.show(listaClienti, event.getScreenX(), event.getScreenY());
				event.consume();
			});
			
			listaClienti.setOnMouseClicked(event -> {
				listaAppuntamentiRisorsa.getItems().clear();
				listaAppuntamentiRisorsa.getItems().addAll(controller.getAppuntamentoRisorsa(listaClienti.getSelectionModel().getSelectedItem()));
				
				listaAppuntamentiRisorsa.setCellFactory(new Callback<ListView<Appuntamento>, ListCell<Appuntamento>>() {
					
					@Override
					public ListCell<Appuntamento> call(ListView<Appuntamento> param) {
						return new AppuntamentiRisorse();
					}
				});
			});
			initFunctionCliente(menuItemClienteStato, menuItemClienteDel, menuItemClienteAdd, menuItemClienteView);
			
			HBox boxButton = new HBox(20);
			{
				Button buttonInserisci = new Button("Inserisci");
				buttonInserisci.setOnAction(event -> {
					if (!oraInizio.getText().isEmpty() && !oraFine.getText().isEmpty() && !listaOperai.getSelectionModel().isEmpty()) {
						LocalDateTime dataInizio = LocalDateTime.of(datePicker.getValue(), LocalTime.parse(oraInizio.getText().toString(), Utils.formatterOra));
						LocalDateTime dataFine = LocalDateTime.of(datePicker.getValue(), LocalTime.parse(oraFine.getText().toString(), Utils.formatterOra));
						if (!checkboxRepeat.isSelected()) {
							try {
								controller.addAppuntamento(dataInizio, dataFine, listaClienti.getSelectionModel().getSelectedItem(), FXCollections.observableArrayList(listaOperai.getSelectionModel().getSelectedItems()), FXCollections.observableArrayList(listaAuto.getSelectionModel().getSelectedItems()), FXCollections.observableArrayList(listaMacchine.getSelectionModel().getSelectedItems()));
							}catch (Exception e) {
								e.getStackTrace();
							}
						}
						else {
							for (LocalDate i : ripetitionPane.getDateAppuntamentiMultipli())
								try {
									controller.addAppuntamento(i.atTime(dataInizio.toLocalTime()), i.atTime(dataFine.toLocalTime()), listaClienti.getSelectionModel().getSelectedItem(), FXCollections.observableArrayList(listaOperai.getSelectionModel().getSelectedItems()), FXCollections.observableArrayList(listaAuto.getSelectionModel().getSelectedItems()), FXCollections.observableArrayList(listaMacchine.getSelectionModel().getSelectedItems()));
								}catch(Exception e) {
									e.getStackTrace();
								}
						}
						oraInizio.clear();
						oraFine.clear();
						checkboxRepeat.setSelected(false);
						listHours.getSelectionModel().clearSelection();
						listaClienti.getSelectionModel().clearSelection();
						listaOperai.getItems().clear();
						listaOperai.getItems().addAll(controller.getOperai());
						listaMacchine.getItems().clear();
						listaMacchine.getItems().addAll(controller.getMacchine());
						listaAuto.getItems().clear();
						listaAuto.getItems().addAll(controller.getAuto());
						try {
							listaAppuntamentiGiorno.setItems(controller.getAppuntamentiGiorno(datePicker.getValue()));
						} catch (ClassNotFoundException | SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				
				Button buttonGraficoGiornata = new Button("Grafico Giornata");
				buttonGraficoGiornata.setOnAction(event -> {
					Stage window = new Stage();
					GraficoGiornata graficoGiornata = new GraficoGiornata(window, controller, datePicker, tableViewGraficoGiornata, 
							oraInizio, listHours, listaOperai, listaAuto, listaMacchine);
					window.setTitle("Grafico Giornata");
					window.setScene(new Scene(graficoGiornata.getParent(), 1230, 600));
					window.show();
				});
				
			boxButton.getChildren().addAll(buttonInserisci, buttonGraficoGiornata);
			}
			panel.getChildren().addAll(dateBox, hourPanel, labelCliente, listaClienti, labelOperaio, listaOperai, labelAuto, listaAuto, labelMacchina, listaMacchine, boxButton);
		}
		this.setCenter(panel);
	}

	private void initLeftPane() {
		VBox panel = new VBox();
		{
			panel.setSpacing(20);
			panel.setPadding(new Insets(20, 20, 10, 20));
			panel.setPrefWidth(150);
			List<LocalTime> hours = new Utils(controller).listaOreLavorative();
			listHours = new ListView<>(FXCollections.observableArrayList(hours));
			listHours.prefHeightProperty().bind(heightProperty());
			listHours.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			listHours.setOnMouseClicked(event -> {
				if (listHours.getSelectionModel().getSelectedItems().size() > 2) {
					listHours.getSelectionModel().clearAndSelect(listHours.getSelectionModel().getSelectedIndex());
					oraInizio.clear();
					oraFine.clear();
				}
				if (listHours.getSelectionModel().getSelectedItems().size() == 1) {
					oraInizio.setText(listHours.getSelectionModel().getSelectedItem().toString());
					oraFine.clear();
					List<Operaio> operaiSelected = FXCollections.observableArrayList(listaOperai.getSelectionModel().getSelectedItems());
					List<Auto> autoSelected = FXCollections.observableArrayList(listaAuto.getSelectionModel().getSelectedItems());
					List<Macchina> macchineSlected = FXCollections.observableArrayList(listaMacchine.getSelectionModel().getSelectedItems());
					listaOperai.getItems().clear();
					listaAuto.getItems().clear();
					listaMacchine.getItems().clear();
					listaOperai.getItems().addAll(controller.getOperai());
					listaAuto.getItems().addAll(controller.getAuto());
					listaMacchine.getItems().addAll(controller.getMacchine());
					for (Operaio o : operaiSelected)
						listaOperai.getSelectionModel().select(o);
					for (Auto a : autoSelected)
						listaAuto.getSelectionModel().select(a);
					for (Macchina m : macchineSlected)
						listaMacchine.getSelectionModel().select(m);
				}
				if (listHours.getSelectionModel().getSelectedItems().size() == 2) {
					oraInizio.setText(listHours.getSelectionModel().getSelectedItems().get(0).toString());
					oraFine.setText(listHours.getSelectionModel().getSelectedItems().get(1).toString());
					List<Operaio> operaiSelected = FXCollections.observableArrayList(listaOperai.getSelectionModel().getSelectedItems());
					List<Auto> autoSelected = FXCollections.observableArrayList(listaAuto.getSelectionModel().getSelectedItems());
					List<Macchina> macchineSlected = FXCollections.observableArrayList(listaMacchine.getSelectionModel().getSelectedItems());
					listaOperai.getItems().clear();
					listaOperai.getItems().addAll(controller.getOperai());
					listaOperai.setCellFactory(new Callback<ListView<Operaio>, ListCell<Operaio>>() {
						
						@Override
						public ListCell<Operaio> call(ListView<Operaio> param) {
							ListViewColour<Operaio> listViewColourOperai = new ListViewColour<Operaio>(controller, oraInizio, oraFine, datePicker);
							return listViewColourOperai;
						}
					});
					listaAuto.getItems().clear();
					listaAuto.getItems().addAll(controller.getAuto());
					listaAuto.setCellFactory(new Callback<ListView<Auto>, ListCell<Auto>>() {
						
						@Override
						public ListCell<Auto> call(ListView<Auto> param) {

							return new ListViewColour<Auto>(controller, oraInizio, oraFine, datePicker);
						}
					});
					listaMacchine.getItems().clear();
					listaMacchine.getItems().addAll(controller.getMacchine());
					listaMacchine.setCellFactory(new Callback<ListView<Macchina>, ListCell<Macchina>>() {
						
						@Override
						public ListCell<Macchina> call(ListView<Macchina> param) {
							return new ListViewColour<Macchina>(controller, oraInizio, oraFine, datePicker);
						}
					});
					for (Operaio o : operaiSelected)
						listaOperai.getSelectionModel().select(o);
					for (Auto a : autoSelected)
						listaAuto.getSelectionModel().select(a);
					for (Macchina m : macchineSlected)
						listaMacchine.getSelectionModel().select(m);
					
				}
			});
			panel.getChildren().addAll(listHours);
		}
		this.setLeft(panel);
	}
	
	private Parent insertOperaioPane(Stage window) {
		window.setResizable(false);
		VBox panel = new VBox(20);
		{
			panel.setPadding(new Insets(20, 20, 10, 20));
			
			TextField entryNome;
			TextField entryCognome;
			TextField entryMansione;
			HBox boxNome = new HBox(20);
			{
				Label labelNome= new Label("Nome");
				labelNome.setPrefWidth(100);
				entryNome= new TextField();
				boxNome.getChildren().addAll(labelNome, entryNome);
			}
			HBox boxCognome = new HBox(20);
			{
				Label labelCognome = new Label("Cognome");
				labelCognome.setPrefWidth(100);
				entryCognome = new TextField();
				boxCognome.getChildren().addAll(labelCognome, entryCognome);
			}
			HBox boxMansione = new HBox(20);
			{
				Label labelMansione = new Label("Mansione");
				labelMansione.setPrefWidth(100);
				entryMansione = new TextField();
				boxMansione.getChildren().addAll(labelMansione, entryMansione);
			}
			Button inserisciAuto = new Button("Inserisci");
			inserisciAuto.setOnAction(event -> {
				if (!entryNome.getText().isEmpty() && !entryCognome.getText().isEmpty() && !entryMansione.getText().isEmpty()) {
					try {
						controller.addOperaio(entryNome.getText(), entryCognome.getText(), entryMansione.getText());
						listaOperai.setItems(FXCollections.observableArrayList(controller.getOperai()));
						window.close();
					} catch (ClassNotFoundException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}	
			});
			panel.getChildren().addAll(boxNome, boxCognome, boxMansione, inserisciAuto);
			panel.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.ESCAPE)
					window.close();
			});
		}
		return panel;
	}
	
	private Parent InsertAutoPane(Stage window) {
		window.setResizable(false);
		VBox panel = new VBox();
		{
			panel.setSpacing(20);
			panel.setPadding(new Insets(20, 20, 10, 20));
			
			TextField entryTarga;
			TextField entryTipologia;
			HBox boxTarga = new HBox(20);
			{
				Label labelTarga = new Label("Targa");
				labelTarga.setPrefWidth(100);
				entryTarga = new TextField();
				boxTarga.getChildren().addAll(labelTarga, entryTarga);
			}
			HBox boxTipologia = new HBox(20);
			{
				Label labelTipologia = new Label("Tipologia");
				labelTipologia.setPrefWidth(100);
				entryTipologia = new TextField();
				boxTipologia.getChildren().addAll(labelTipologia, entryTipologia);
			}
			
			Button inserisciAuto = new Button("Inserisci");
			inserisciAuto.setOnAction(event -> {
				try {
					controller.addAuto(entryTarga.getText(), entryTipologia.getText());
					listaAuto.setItems(FXCollections.observableArrayList(controller.getAuto()));
					window.close();
				} catch (SQLException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			panel.getChildren().addAll(boxTarga, boxTipologia, inserisciAuto);
			panel.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.ESCAPE)
					window.close();
			});
		}
		return panel;
	}
	
	private Parent insertAdvanceReportPane(Stage window) {
		window.setMaxHeight(700);
		double height = 0;
		GridPane grid = new GridPane();
		grid.setVgap(15);
		grid.setHgap(10);
		grid.setPadding(new Insets(20, 20, 20, 20));
		{
			
			Label labelData = new Label("Data Inizio");
			grid.add(labelData, 0, 0);
			DatePicker dateInizio;
			HBox boxInizio = new HBox();
			{
				dateInizio = new DatePicker();
				dateInizio.setShowWeekNumbers(false);
				dateInizio.setValue(LocalDate.now());
				boxInizio.getChildren().addAll(dateInizio);
			}
			grid.add(boxInizio, 1, 0);
			CheckBox cbConfermaInizio = new CheckBox();
			grid.add(cbConfermaInizio, 2, 0);
			Label labelDataFine = new Label("Data Fine");
			grid.add(labelDataFine, 0, 1);
			DatePicker dateFine;
			HBox boxFine = new HBox();
			{
				dateFine = new DatePicker();
				dateFine.setShowWeekNumbers(false);
				dateFine.setValue(LocalDate.now());
				boxFine.getChildren().addAll(dateFine);
			}
			grid.add(boxFine, 1, 1);
			CheckBox cbConfermaFine = new CheckBox();
			grid.add(cbConfermaFine, 2, 1);
			Insets labelInsets = new Insets(0, 0, -20, 0);
			Label labelOperaio = new Label("Operai");
			labelOperaio.setPadding(labelInsets);
			ListView<Operaio> listaOperai = new ListView<>(FXCollections.observableArrayList(controller.getOperai()));
			listaOperai.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			height += listaOperai.getItems().size()*31;
			listaOperai.setPrefHeight(listaOperai.getItems().size()*31);
			CheckBox cbConfermaOperaio = new CheckBox();
			labelOperaio.setAlignment(Pos.CENTER);
			grid.add(labelOperaio, 0, 2); grid.add(listaOperai, 1, 2); grid.add(cbConfermaOperaio, 2, 2);
			Label labelAuto = new Label("Auto");
			labelAuto.setPadding(labelInsets);
			ListView<Auto> listaAuto = new ListView<>(FXCollections.observableArrayList(controller.getAuto()));
			listaAuto.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			height += listaAuto.getItems().size()*31;
			listaAuto.setPrefHeight(listaAuto.getItems().size()*31);
			CheckBox cbConfermaAuto = new CheckBox();
			grid.addRow(3, labelAuto, listaAuto, cbConfermaAuto);
			Label labelMacchine = new Label("Macchine");
			labelMacchine.setPadding(labelInsets);
			ListView<Macchina> listaMacchine = new ListView<>(FXCollections.observableArrayList(controller.getMacchine()));
			listaMacchine.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			height += listaMacchine.getItems().size()*31;
			listaMacchine.setPrefHeight(listaMacchine.getItems().size()*31);
			CheckBox cbConfermaMacchina = new CheckBox();
			grid.addRow(4, labelMacchine, listaMacchine, cbConfermaMacchina);
			Label labelClienti = new Label("Clienti");
			labelClienti.setPadding(labelInsets);
			ListView<Cliente> listaClienti = new ListView<>(FXCollections.observableArrayList(controller.getClienti()));
			height += listaClienti.getItems().size()*31;
			listaClienti.setPrefHeight(listaClienti.getItems().size()*31);
			listaOperai.setMaxHeight(150);
			listaMacchine.setMaxHeight(150);
			listaAuto.setMaxHeight(150);
			listaClienti.setMaxHeight(150);
			CheckBox cbConfermaClienti = new CheckBox();
			grid.addRow(5, labelClienti, listaClienti, cbConfermaClienti);
		
			Button buttonConferma = new Button("Conferma");
			buttonConferma.setOnAction(event -> {
				List<Appuntamento> appuntamentiFiltrati = controller.reportAvanzatoFilter(dateInizio.getValue(), cbConfermaInizio.isSelected(), 
						dateFine.getValue(), cbConfermaFine.isSelected(), 
								listaOperai.getSelectionModel().getSelectedItems(), cbConfermaOperaio.isSelected(), 
								listaAuto.getSelectionModel().getSelectedItems(), cbConfermaAuto.isSelected(), listaMacchine.getSelectionModel().getSelectedItems(), cbConfermaMacchina.isSelected(), 
								listaClienti.getSelectionModel().getSelectedItems(), cbConfermaClienti.isSelected());
				window.close();
				Stage windowReportOutput = new Stage();
				windowReportOutput.setTitle("Report Avanzato");
				ReportPeriodoPane reportPane = new ReportPeriodoPane(windowReportOutput, controller, datePicker.getValue(), appuntamentiFiltrati, Utils.getMinMaxData(appuntamentiFiltrati));
				Parent parent = reportPane.getParent();
				windowReportOutput.setScene(new Scene(parent, 930, 600));
				windowReportOutput.show();
			});
			grid.add(buttonConferma, 2, 6);

			listaOperai.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.ESCAPE)
					window.close();
			});
			listaAuto.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.ESCAPE)
					window.close();
			});
			listaMacchine.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.ESCAPE)
					window.close();
			});
			listaClienti.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.ESCAPE)
					window.close();
			});
			grid.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.ESCAPE)
					window.close();
			});
		}
		window.setHeight(height + 250);
		return grid;
	}
	
	private Parent InsertMacchinaPane(Stage window) {
		window.setResizable(false);
		VBox panel = new VBox(20);
		{
			panel.setPadding(new Insets(20, 20, 10, 20));
			
			TextField entryNome;
			HBox boxNome = new HBox(20);
			{
				Label labelNome= new Label("Nome");
				labelNome.setPrefWidth(100);
				entryNome= new TextField();
				boxNome.getChildren().addAll(labelNome, entryNome);
			}
			Button inserisciMacchina = new Button("Inserisci");
			inserisciMacchina.setOnAction(event -> {
				if (!entryNome.getText().isEmpty()) {
					try {
						controller.addMacchina(entryNome.getText());
						listaMacchine.setItems(FXCollections.observableArrayList(controller.getMacchine()));
						window.close();
					} catch (ClassNotFoundException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			panel.getChildren().addAll(boxNome, inserisciMacchina);
			panel.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.ESCAPE)
					window.close();
			});
		}
		return panel;
			
	}

	private Parent InsertClientePane(Stage window) {
		window.setResizable(false);
		VBox panel = new VBox();
		{
			panel.setSpacing(20);
			panel.setPadding(new Insets(20, 20, 10, 20));
			
			TextField entryNome;
			TextField entryCitta;
			TextField entryIndirizzo;
			HBox boxNome = new HBox(20);
			{
				Label labelNome = new Label("Nome");
				labelNome.setPrefWidth(100);
				entryNome = new TextField();
				boxNome.getChildren().addAll(labelNome, entryNome);
			}
			HBox boxCitta = new HBox(20);
			{
				Label labelCitta = new Label("Città");
				labelCitta.setPrefWidth(100);
				entryCitta = new TextField();
				boxCitta.getChildren().addAll(labelCitta, entryCitta);
			}
			HBox boxIndirizzo = new HBox(20);
			{
				Label labelIndirizzo = new Label("Indirizzo");
				labelIndirizzo.setPrefWidth(100);
				entryIndirizzo = new TextField();
				boxIndirizzo.getChildren().addAll(labelIndirizzo, entryIndirizzo);
			}
			
			Button inserisciCliente= new Button("Inserisci");
			inserisciCliente.setOnAction(event -> {
				if (!entryNome.getText().isEmpty() && !entryCitta.getText().isEmpty() && !entryIndirizzo.getText().isEmpty()) {
					try {
						controller.addCliente(entryNome.getText(), entryCitta.getText(), entryIndirizzo.getText());
						listaClienti.setItems(FXCollections.observableArrayList(controller.getClienti()));
						window.close();
					} catch (ClassNotFoundException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			panel.getChildren().addAll(boxNome, boxCitta, boxIndirizzo, inserisciCliente);
			panel.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.ESCAPE)
					window.close();
			});
		}
		return panel;
	}
	
	private void initFunctionOperai(MenuItem stato, MenuItem del, MenuItem add, MenuItem view) {
		stato.setOnAction(event -> {
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			StatoPane periodoPane = new StatoPane(window, controller, listaOperai.getSelectionModel().getSelectedItem());
			window.setTitle("Personalizza");
			window.setResizable(false);
			window.setScene(new Scene(periodoPane.getParent()));
			window.showAndWait();
		});
		
		del.setOnAction(event -> {
			if (initAlertSureDeletion()){
				try {
					controller.deleteOperaio(listaOperai.getSelectionModel().getSelectedItems());
				}catch (Exception e) {
					e.getStackTrace();
				}
				listaOperai.getItems().removeAll(listaOperai.getSelectionModel().getSelectedItems());
			}
		});
		add.setOnAction(event -> {
			Stage window = new Stage();
			Parent parent = insertOperaioPane(window);
			window.setTitle("Inserisci Operaio");
			window.setScene(new Scene(parent));
			window.show();
		});
		view.setOnAction(event -> {
			Stage window = new Stage();
			GraficoRisorsaSettimanale<Operaio> graficoRisorsaSettimanale = new GraficoRisorsaSettimanale<>(window, controller, datePicker.getValue(), 
					listaOperai.getSelectionModel().getSelectedItem(), tableViewGraficoSettimanale, listaAppuntamentiGiorno, listaAppuntamentiRisorsa,
					datePicker, oraInizio, oraFine);
			window.setTitle("Appuntamenti Settimana " + listaOperai.getSelectionModel().getSelectedItem());
			window.setScene(new Scene(graficoRisorsaSettimanale.getParent(), 1350, 600));
			window.show();
		});
	}
	
	private void initFunctionAuto(MenuItem stato, MenuItem del, MenuItem add, MenuItem view) {
		stato.setOnAction(event -> {
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			StatoPane periodoPane = new StatoPane(window, controller, listaAuto.getSelectionModel().getSelectedItem());
			window.setTitle("Personalizza");
			window.setResizable(false);
			window.setScene(new Scene(periodoPane.getParent()));
			window.showAndWait();
		});
		
		del.setOnAction(event -> {
			if (initAlertSureDeletion()) {
				try {
				controller.deleteAuto(listaAuto.getSelectionModel().getSelectedItems());
				}catch (Exception e) {
					e.getStackTrace();
				}
				listaAuto.getItems().removeAll(listaAuto.getSelectionModel().getSelectedItems());
			}
		});
		add.setOnAction(event -> {
			Stage window = new Stage();
			Parent parent = InsertAutoPane(window);
			window.setTitle("Inserisci Auto");
			window.setScene(new Scene(parent));
			window.show();
		});
		view.setOnAction(event -> {
			Stage window = new Stage();
			GraficoRisorsaSettimanale<Auto> graficoRisorsaSettimanale = new GraficoRisorsaSettimanale<>(window, controller, datePicker.getValue(), 
					listaAuto.getSelectionModel().getSelectedItem(), tableViewGraficoSettimanale, listaAppuntamentiGiorno, listaAppuntamentiRisorsa,
					datePicker, oraInizio, oraFine);
			window.setTitle("Appuntamenti Settimana " + listaAuto.getSelectionModel().getSelectedItem());
			window.setScene(new Scene(graficoRisorsaSettimanale.getParent(), 1350, 600));
			window.show();
		});
	}
	
	public void initFunctionMacchine(MenuItem stato, MenuItem del, MenuItem add, MenuItem view) {
		stato.setOnAction(event -> {
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			StatoPane periodoPane = new StatoPane(window, controller, listaMacchine.getSelectionModel().getSelectedItem());
			window.setTitle("Personalizza");
			window.setResizable(false);
			window.setScene(new Scene(periodoPane.getParent()));
			window.showAndWait();
			
		});
		
		del.setOnAction(event -> {
			if (initAlertSureDeletion()) {
				try {
					controller.deleteMacchina(listaMacchine.getSelectionModel().getSelectedItems());
					}catch (Exception e) {
						e.getStackTrace();
					}
				listaMacchine.getItems().removeAll(listaMacchine.getSelectionModel().getSelectedItems());
			}
		});
		add.setOnAction(event -> {
			Stage window = new Stage();
			Parent parent = InsertMacchinaPane(window);
			window.setTitle("Inserisci Macchina");
			window.setScene(new Scene(parent));
			window.show();
		});
		view.setOnAction(event -> {
			Stage window = new Stage();
			GraficoRisorsaSettimanale<Macchina> graficoRisorsaSettimanale = new GraficoRisorsaSettimanale<>(window, controller, datePicker.getValue(),
					listaMacchine.getSelectionModel().getSelectedItem(), tableViewGraficoSettimanale, listaAppuntamentiGiorno, listaAppuntamentiRisorsa,
					datePicker, oraInizio, oraFine);
			window.setTitle("Appuntamenti Settimana" + listaMacchine.getSelectionModel().getSelectedItem());
			window.setScene(new Scene(graficoRisorsaSettimanale.getParent(), 1350, 600));
			window.show();
		});
	}
	
	private void initFunctionCliente(MenuItem stato, MenuItem del, MenuItem add, MenuItem view) {
		stato.setOnAction(event -> {
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			StatoPane periodoPane = new StatoPane(window, controller, listaClienti.getSelectionModel().getSelectedItem());
			window.setTitle("Personalizza");
			window.setResizable(false);
			window.setScene(new Scene(periodoPane.getParent()));
			window.showAndWait();
		});
		
		del.setOnAction(event -> {
			if (initAlertSureDeletion()) {
				try {
					controller.deleteCliente(listaClienti.getSelectionModel().getSelectedItem());
					}catch (Exception e) {
						e.getStackTrace();
					}
				listaClienti.getItems().removeAll(listaClienti.getSelectionModel().getSelectedItems());
			}
		});
		add.setOnAction(event -> {
			Stage window = new Stage();
			Parent parent = InsertClientePane(window);
			window.setTitle("Inserisci Cliente");
			window.setScene(new Scene(parent));
			window.show();
		});
		view.setOnAction(event -> {
			Stage window = new Stage();
			GraficoRisorsaSettimanale<Cliente> graficoRisorsaSettimanale = new GraficoRisorsaSettimanale<>(window, controller, datePicker.getValue(), listaClienti.getSelectionModel().getSelectedItem(),
					tableViewGraficoSettimanale, listaAppuntamentiGiorno, listaAppuntamentiRisorsa,
					datePicker, oraInizio, oraFine);
			window.setTitle("Appuntamenti Settimana " + listaClienti.getSelectionModel().getSelectedItem());
			window.setScene(new Scene(graficoRisorsaSettimanale.getParent(), 1350, 600));
			window.show();
		});
	}
		
	private boolean initAlertSureDeletion() {
		ButtonType buttonSi = new ButtonType("Sì");
		if (!controller.getAlert().containsKey("alertSureDeletion") || controller.getAlert().get("alertSureDeletion")) {
			Alert alert = Utils.createAlertWithOptOut(controller, "alertSureDeletion", AlertType.CONFIRMATION, "Attenzione", 
	                "Sicuro di voler cancellare?", "Non chiedermelo più", buttonSi, ButtonType.NO);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonSi){
			    return true;
			} 
			return false;
		}
		return true;
	}
	
	private void initWidget() {
		listaAppuntamentiGiorno.setCellFactory(new Callback<ListView<Appuntamento>, ListCell<Appuntamento>>() {
			
			@Override
			public ListCell<Appuntamento> call(ListView<Appuntamento> param) {
				return new ListViewMainAppuntamento(controller, listaAppuntamentiGiorno);
			}
		});
	}
}
