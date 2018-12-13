package amministrazione.utils;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import amministrazione.controller.Controller;
import amministrazione.model.Appuntamento;
import amministrazione.model.Auto;
import amministrazione.model.Macchina;
import amministrazione.model.Operaio;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;

public class Utils {
	private Controller controller;
	public final static String[] dayOfWeek = new String[] {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"}; 
	public final static String[] mese = new String[] {"Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"};
	public static DateTimeFormatter formatterOra = DateTimeFormatter.ofPattern("HH:mm");
	public static DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	public static DateTimeFormatter formatterComplete = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	public static DateTimeFormatter formatterDataDB = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public static DateTimeFormatter formatterCompleteDB = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	public Utils(Controller controller) {
		this.controller = controller;
	}

	public List<Operaio> operaiDisponibili(LocalDateTime oraInizio, LocalDateTime oraFine) throws ClassNotFoundException, SQLException {
		Set<Operaio> operaiOccupati = new HashSet<>();

		if (oraInizio!=null && oraFine!=null) {
			for (Appuntamento a : controller.getAppuntamentiGiorno(oraInizio.toLocalDate())) {
				operaiOccupati.addAll(controller.getOperai().stream().filter(x -> (
						a.getOperai().contains(x) && 
						((oraInizio.isAfter(a.getDataInizio()) &&  oraInizio.isBefore(a.getDataFine())) || 
						(oraFine.isAfter(a.getDataInizio()) && oraFine.isBefore(a.getDataFine())) || 
						(!oraInizio.isAfter(a.getDataInizio()) && !oraFine.isBefore(a.getDataFine()))))).collect(Collectors.toSet()));
			}
		}
		List<Operaio> operaiDisponibili = new ArrayList<>(controller.getOperai());
		operaiDisponibili.removeAll(operaiOccupati);
	return operaiDisponibili;
	}
	
	public List<Auto> autoDisponibili(LocalDateTime oraInizio, LocalDateTime oraFine) throws ClassNotFoundException, SQLException {
		Set<Auto> autoOccupati = new HashSet<>();

		if (oraInizio!=null && oraFine!=null) {
			for (Appuntamento a : controller.getAppuntamentiGiorno(oraInizio.toLocalDate())) {
				autoOccupati.addAll(controller.getAuto().stream().filter(x -> (
						a.getAuto().contains(x) && 
						((oraInizio.isAfter(a.getDataInizio()) &&  oraInizio.isBefore(a.getDataFine())) || 
						(oraFine.isAfter(a.getDataInizio()) && oraFine.isBefore(a.getDataFine())) ||  
						(!oraInizio.isAfter(a.getDataInizio()) && !oraFine.isBefore(a.getDataFine()))))).collect(Collectors.toSet()));
			}
		}
		List<Auto> autoDisponibili = new ArrayList<>(controller.getAuto());
		autoDisponibili.removeAll(autoOccupati);
	return autoDisponibili;
	}
	
	public List<Macchina> macchineDisponibili(LocalDateTime oraInizio, LocalDateTime oraFine) throws ClassNotFoundException, SQLException {
		Set<Macchina> macchineOccupati = new HashSet<>();

		if (oraInizio!=null && oraFine!=null){
			for (Appuntamento a : controller.getAppuntamentiGiorno(oraInizio.toLocalDate())) {
				macchineOccupati.addAll(controller.getMacchine().stream().filter(x -> (
						a.getMacchine().contains(x) && 
						((oraInizio.isAfter(a.getDataInizio()) &&  oraInizio.isBefore(a.getDataFine())) || 
						(oraFine.isAfter(a.getDataInizio()) && oraFine.isBefore(a.getDataFine())) || 
						(!oraInizio.isAfter(a.getDataInizio()) && !oraFine.isBefore(a.getDataFine()))))).collect(Collectors.toSet()));
			}
		}
		List<Macchina> macchineDisponibili = new ArrayList<>(controller.getMacchine());
		macchineDisponibili.removeAll(macchineOccupati);
	return macchineDisponibili;
	}
	
	public List<LocalTime> listaOreLavorative(){
		List<LocalTime> hours = new ArrayList<>();
		for (LocalTime i = controller.getInizioLavoro(); !i.isAfter(controller.getFineLavoro()); i = i.plusMinutes(controller.getScaglionamentoOra())) {
			hours.add(i);
		}
		return hours;
	}
	
	public static Alert createAlertWithOptOut(Controller controller, String alertName, AlertType type, String title, String headerText, 
			String message, ButtonType... buttonTypes) {
		Alert alert = new Alert(type);
		// Need to force the alert to layout in order to grab the graphic,
		// as we are replacing the dialog pane with a custom pane
		alert.getDialogPane().applyCss();
		Node graphic = alert.getDialogPane().getGraphic();
		// Create a new dialog pane that has a checkbox instead of the hide/show details button
		// Use the supplied callback for the action of the checkbox
		alert.setDialogPane(new DialogPane() {
			@Override
			protected Node createDetailsButton() {
				CheckBox optOut = new CheckBox(message);
				//optOut.setOnAction(e -> optOutAction.accept(optOut.isSelected()));
				optOut.setOnAction(e -> {
					controller.setAlert(alertName, !optOut.isSelected());
					try {
						controller.saveSetting();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});
				return optOut;
			}
		});
		alert.getDialogPane().getButtonTypes().addAll(buttonTypes);
		//alert.getDialogPane().setContentText(message);
		// Fool the dialog into thinking there is some expandable content
		// a Group won't take up any space if it has no children
		alert.getDialogPane().setExpandableContent(new Group());
		alert.getDialogPane().setExpanded(true);
		// Reset the dialog graphic using the default style
		alert.getDialogPane().setGraphic(graphic);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		return alert;
	}
	
	public static void createAlertFailDB(String headerText) {
		Alert alert = new Alert(AlertType.ERROR, headerText, ButtonType.CLOSE);
		alert.setTitle("ERRORE");
		alert.show();
	}
	
	public static void createAlertFailReadDB() {
		createAlertFailDB("Errore di lettura nel DB");
	}
	
	public static void createAlertFailWriteDB() {
		createAlertFailDB("Errore di scrittura nel DB");
	}
	
	public static String getMinMaxData(List<Appuntamento> appuntamenti) {
		LocalDate min, max;
		LocalDate tempMin, tempMax;
		String date = "";
        String[] dayOfWeek = new String[] {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"}; 
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
            	date = dayOfWeek[min.getDayOfWeek().ordinal()] + " " + min.format(Utils.formatterData);
			else
				date = dayOfWeek[min.getDayOfWeek().ordinal()] + " " + min.format(Utils.formatterData) + " - " + dayOfWeek[max.getDayOfWeek().ordinal()] + " " + max.format(Utils.formatterData);
        }
        return date;
	}
}
