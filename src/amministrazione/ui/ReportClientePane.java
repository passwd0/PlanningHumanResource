package amministrazione.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amministrazione.controller.Controller;
import amministrazione.model.Appuntamento;
import amministrazione.model.Cliente;
import amministrazione.utils.Utils;
import dataset.AppuntamentoDataSet;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

public class ReportClientePane {
	private Stage window;
	private Parent parent;
	private Cliente cliente;
	private List<Appuntamento> appuntamenti;
	private Controller controller;
	private TableView<AppuntamentoDataSet> tableViewReport;
	private String file = "report.pdf";

	public ReportClientePane(Stage window, Controller controller, Cliente cliente, LocalDate date) {
		this.window = window;
		this.cliente = cliente;
		try {
			this.appuntamenti = controller.getAppuntamentiMensile(date, cliente);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.controller = controller;
		
		parent = initPrintReport();

		window.setHeight(tableViewReport.getItems().size()* 31 + 250);
		window.setMaxHeight(900);
		window.setWidth(1300);
	}
	
	public Parent getParent() {
		return parent;
	}

	private <T> Parent initPrintReport() {
		VBox panel = new VBox(20);
		panel.setPadding(new Insets(20, 20, 20, 20));
		{
			tableViewReport = new TableView<>();
			tableViewReport.prefWidthProperty().bind(window.widthProperty());
			tableViewReport.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			
			TableColumn<AppuntamentoDataSet, LocalDateTime> itemData = new TableColumn<AppuntamentoDataSet, LocalDateTime>("Data");
			TableColumn<AppuntamentoDataSet, LocalDateTime> itemOra = new TableColumn<AppuntamentoDataSet, LocalDateTime>("Orario");
			TableColumn<AppuntamentoDataSet, String> itemOperai = new TableColumn<>("Operai");
			TableColumn<AppuntamentoDataSet, String> itemAuto = new TableColumn<>("Auto");
			TableColumn<AppuntamentoDataSet, String> itemMacchine = new TableColumn<>("Macchine");
			TableColumn<AppuntamentoDataSet, String> itemCliente = new TableColumn<>("Cliente");
			
			itemData.setMinWidth(120);
			itemData.setMaxWidth(120);
			itemOra.setMinWidth(120);
			itemOra.setMaxWidth(120);
			itemData.setStyle("-fx-alignment: CENTER");
			itemOra.setStyle("-fx-alignment: CENTER");
			itemOperai.setStyle("-fx-alignment: CENTER");
			itemAuto.setStyle("-fx-alignment: CENTER");
			itemMacchine.setStyle("-fx-alignment: CENTER");
			itemCliente.setStyle("-fx-alignment: CENTER");

			itemData.setCellValueFactory(new PropertyValueFactory<>("data"));
			itemOra.setCellValueFactory(new PropertyValueFactory<>("ora"));
			itemOperai.setCellValueFactory(new PropertyValueFactory<>("operai"));
			itemAuto.setCellValueFactory(new PropertyValueFactory<>("auto"));
			itemMacchine.setCellValueFactory(new PropertyValueFactory<>("macchine"));
			itemCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
			tableViewReport.getColumns().addAll(Arrays.asList(itemData, itemOra, itemCliente, itemOperai, itemAuto, itemMacchine));
			List<AppuntamentoDataSet> listReport = new ArrayList<>();
			for (Appuntamento a : appuntamenti) {
				listReport.add(new AppuntamentoDataSet(a));
			}

			tableViewReport.setItems(FXCollections.observableArrayList(listReport));

			TextField nomeFile = new TextField();
			nomeFile.requestFocus();
			Button pageSetupButton = new Button("Salva");
			Button open = new Button("Visualizza");
			open.setDisable(true);

			pageSetupButton.setOnAction(event -> {
				try {
					JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(listReport);
					
		            String data = "Non sono presenti appuntamenti";
		            LocalDate min;
		            LocalDate max;
		            LocalDate tempMin;
		            LocalDate tempMax;
		            if (!appuntamenti.isEmpty()) {
		            	min = appuntamenti.get(0).getDataInizio().toLocalDate();
		            	max = appuntamenti.get(0).getDataInizio().toLocalDate();
						for (Appuntamento a : appuntamenti) {
							if ((tempMin = a.getDataInizio().toLocalDate()).isBefore(min))
								min = tempMin;
							if ((tempMax = a.getDataInizio().toLocalDate()).isAfter(max))
								max = tempMax;
						}
						
						if (min.equals(max))
			            	data = Utils.dayOfWeek[min.getDayOfWeek().ordinal()] + " " + min.format(Utils.formatterData);
						else
							data = Utils.dayOfWeek[min.getDayOfWeek().ordinal()] + " " + min.format(Utils.formatterData) + " - " + Utils.dayOfWeek[max.getDayOfWeek().ordinal()] + " " + max.format(Utils.formatterData);
		            }
		            Map<String, Object> parameters = new HashMap<>();
		            parameters.put("ItemDataSource", itemsJRBean);
		            parameters.put("Meseanno", data);
		            parameters.put("Cliente", cliente.getNome());
					
		            JasperReport report = (JasperReport) JRLoader.loadObject(this.getClass().getResource("/REPORT_CLIENTE.jasper")); 
		            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
	
		            if (!nomeFile.getText().isEmpty() && !nomeFile.getText().trim().isEmpty())
		            	file = nomeFile.getText() + ".pdf";
		            
		            OutputStream outputStream = new FileOutputStream(new File(file));
		            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
		            open.setDisable(false);
		            if (!controller.getAlert().containsKey("alertSavedReport") || controller.getAlert().get("alertSavedReport"))
		            	Utils.createAlertWithOptOut(controller, "alertSavedReport", AlertType.INFORMATION, "Salvataggio", "Il report è stato salvato: " + file, "Non mostrare più", ButtonType.OK).showAndWait();
				} catch (JRException | FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			open.setOnAction(event -> {
	            if (new File(file).exists())
	            	controller.getHostServices().showDocument(file);
			});
			panel.getChildren().addAll(tableViewReport, new HBox(20, nomeFile, pageSetupButton, open));
			panel.prefHeightProperty().bind(window.heightProperty());

		}
		return panel;
	}
}
