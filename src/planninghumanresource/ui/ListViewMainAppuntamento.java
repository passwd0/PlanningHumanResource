package planninghumanresource.ui;

import java.util.stream.Collectors;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import planninghumanresource.controller.Controller;
import planninghumanresource.model.Appuntamento;
import planninghumanresource.model.Auto;
import planninghumanresource.model.Macchina;
import planninghumanresource.model.Operaio;
import planninghumanresource.utils.Utils;

public class ListViewMainAppuntamento extends ListCell<Appuntamento> {
	private Controller controller;
	private ListView<Appuntamento> listview;
	
	public ListViewMainAppuntamento(Controller controller, ListView<Appuntamento> listview) {
		this.controller = controller;
		this.listview = listview;
	}
	
	public void updateItem(Appuntamento item, boolean empty) {
		super.updateItem(item, empty);
		if (item != null) {
			HBox panel = new HBox(40);
			Label hour = new Label(item.getDataInizio().format(Utils.formatterOra) + " - " + item.getDataFine().format(Utils.formatterOra));
			hour.setFont(Font.font(16));
			hour.setStyle("-fx-alignment: CENTER;");
			GridPane grid = new GridPane();
			grid.setVgap(3);
			grid.setHgap(10);
			Label operaio = new Label("Operai:");
			Label auto = new Label("Auto:");
			Label macchina = new Label("Macchine:");
			Label cliente = new Label("Cliente:");
			Label infoOperaio = new Label(operaiToString(item));
			Label infoAuto = new Label(automezziToString(item));
			Label infoMacchina = new Label(macchineToString(item));
			Label infoCliente = new Label(item.getCliente().toString());
			grid.addColumn(0, cliente, operaio, auto, macchina);
			grid.addColumn(1, infoCliente, infoOperaio, infoAuto, infoMacchina);
			panel.getChildren().addAll(hour, grid);
			if (!item.getNota().isEmpty()) {
				Image imgNote = new Image("note.png");
				ImageView imgView = new ImageView(imgNote);
				imgView.setPickOnBounds(true);
				imgView.setFitWidth(20);
				imgView.setFitHeight(20);
				imgView.setOnMouseClicked(event -> {
					final Stage stage = new Stage();
					stage.setTitle("Nota");
					VBox panelNote = new VBox(20);
					panelNote.setPadding(new Insets(20, 10, 20, 10));
	                TextArea textAreaNote = new TextArea();
	                textAreaNote.setText(item.getNota());
	                Button conferma = new Button("Conferma");
	                conferma.setOnAction(event1 -> {
	                	item.setNota(textAreaNote.getText());
	                	stage.close();
	                	try {
	                		controller.addNota(item, textAreaNote.getText());
	                		listview.refresh();
	                	}catch (Exception e) {
							// TODO: handle exception
						}
	                });
	                
	                panelNote.getChildren().addAll(textAreaNote, conferma);
	                stage.setScene(new Scene(panelNote, 300, 200));
	                stage.show();
				});
				Region r = new Region();
				HBox.setHgrow(r, Priority.ALWAYS);
			    panel.getChildren().addAll(r, imgView);
			}
			setGraphic(panel);
		}
		else
			setGraphic(null);
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
}
