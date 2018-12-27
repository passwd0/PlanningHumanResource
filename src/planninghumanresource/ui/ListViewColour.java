package planninghumanresource.ui;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import planninghumanresource.controller.Controller;
import planninghumanresource.model.Appuntamento;
import planninghumanresource.model.Auto;
import planninghumanresource.model.HasState;
import planninghumanresource.model.Macchina;
import planninghumanresource.model.Operaio;

public class ListViewColour<T> extends ListCell<T> {
	private Controller controller;
	private TextField oraInizio;
	private TextField oraFine;
	private DatePicker datePicker;

	public ListViewColour(Controller controller, TextField oraInizio, TextField oraFine, DatePicker datePicker) {
		super();
		this.controller = controller;
		this.oraInizio = oraInizio;
		this.oraFine = oraFine;
		this.datePicker = datePicker;
	}

	public void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		if (item != null) {
			if (item instanceof Operaio) {
				if (!oraInizio.getText().isEmpty() && !oraFine.getText().isEmpty() && indisponibile(item))
					setStyle("-fx-background-color: gray");
				else
					setStyle("");
				if (!operaiDisponibili().contains(item))
					setStyle(getStyle() + "; -fx-text-fill: red");
			}
			if (item instanceof Auto) {
				if (!oraInizio.getText().isEmpty() && !oraFine.getText().isEmpty() && indisponibile(item))
					setStyle("-fx-background-color: gray");
				else
					setStyle("");
				if (!autoDisponibili().contains(item))
					setStyle(getStyle() + "; -fx-text-fill: red");
			}
			if (item instanceof Macchina) {
				if (!oraInizio.getText().isEmpty() && !oraFine.getText().isEmpty() && indisponibile(item))
					setStyle("-fx-background-color: gray");
				else
					setStyle("");
				if (!macchineDisponibili().contains(item))
					setStyle(getStyle() + "; -fx-text-fill: red");	
			}
			setText(item.toString());
		}
		else
			setText("");
	}
	
	private boolean indisponibile(T item) {
		LocalTime oraI = LocalTime.parse(oraInizio.getText());
		LocalTime oraF = LocalTime.parse(oraFine.getText());
		if (((HasState) item).getIndisponibilita().stream().filter(x -> x.getData().isEqual(datePicker.getValue()))
			.anyMatch(x -> ((x.getOraInizio().isBefore(oraF) && x.getOraFine().isAfter(oraF)) || 
				(x.getOraInizio().isBefore(oraI) && x.getOraFine().isAfter(oraI)) || 
				(!x.getOraInizio().isBefore(oraI) && !x.getOraFine().isAfter(oraF)))))
			return true;
		return false;
	}

	private List<Operaio> operaiDisponibili() {
		Set<Operaio> operaiOccupati = new HashSet<>();

		if (oraInizio.getText()!=null && oraFine.getText()!=null && !oraInizio.getText().isEmpty() && !oraFine.getText().isEmpty()) {
			LocalDateTime oraI = LocalTime.parse(oraInizio.getText()).atDate(datePicker.getValue());
			LocalDateTime oraF = LocalTime.parse(oraFine.getText()).atDate(datePicker.getValue());
			
			try {
				for (Appuntamento a : controller.getAppuntamentiGiorno(datePicker.getValue())) {
					operaiOccupati.addAll(controller.getOperai().stream().filter(x -> (
							a.getOperai().contains(x) && 
							((oraI.isAfter(a.getDataInizio()) &&  oraI.isBefore(a.getDataFine())) || 
							(oraF.isAfter(a.getDataInizio()) && oraF.isBefore(a.getDataFine())) || 
							(!oraI.isAfter(a.getDataInizio()) && !oraF.isBefore(a.getDataFine()))))).collect(Collectors.toSet()));
				}
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<Operaio> operaiDisponibili = new ArrayList<>(controller.getOperai());
		operaiDisponibili.removeAll(operaiOccupati);
	return operaiDisponibili;
	}
	
	private List<Auto> autoDisponibili() {
		Set<Auto> autoOccupati = new HashSet<>();

		if (oraInizio.getText()!=null && oraFine.getText()!=null && !oraInizio.getText().isEmpty() && !oraFine.getText().isEmpty()) {
			LocalDateTime oraI = LocalTime.parse(oraInizio.getText()).atDate(datePicker.getValue());
			LocalDateTime oraF = LocalTime.parse(oraFine.getText()).atDate(datePicker.getValue());
			
			try {
				for (Appuntamento a : controller.getAppuntamentiGiorno(datePicker.getValue())) {
					autoOccupati.addAll(controller.getAuto().stream().filter(x -> (
							a.getAuto().contains(x) && 
							((oraI.isAfter(a.getDataInizio()) &&  oraI.isBefore(a.getDataFine())) || 
							(oraF.isAfter(a.getDataInizio()) && oraF.isBefore(a.getDataFine())) ||  
							(!oraI.isAfter(a.getDataInizio()) && !oraF.isBefore(a.getDataFine()))))).collect(Collectors.toSet()));
				}
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<Auto> autoDisponibili = new ArrayList<>(controller.getAuto());
		autoDisponibili.removeAll(autoOccupati);
	return autoDisponibili;
	}
	
	private List<Macchina> macchineDisponibili() {
		Set<Macchina> macchineOccupati = new HashSet<>();

		if (oraInizio.getText()!=null && oraFine.getText()!=null && !oraInizio.getText().isEmpty() && !oraFine.getText().isEmpty()) {
			LocalDateTime oraI = LocalTime.parse(oraInizio.getText()).atDate(datePicker.getValue());
			LocalDateTime oraF = LocalTime.parse(oraFine.getText()).atDate(datePicker.getValue());
			
			try {
				for (Appuntamento a : controller.getAppuntamentiGiorno(datePicker.getValue())) {
					macchineOccupati.addAll(controller.getMacchine().stream().filter(x -> (
							a.getMacchine().contains(x) && 
							((oraI.isAfter(a.getDataInizio()) &&  oraI.isBefore(a.getDataFine())) || 
							(oraF.isAfter(a.getDataInizio()) && oraF.isBefore(a.getDataFine())) || 
							(!oraI.isAfter(a.getDataInizio()) && !oraF.isBefore(a.getDataFine()))))).collect(Collectors.toSet()));
				}
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<Macchina> macchineDisponibili = new ArrayList<>(controller.getMacchine());
		macchineDisponibili.removeAll(macchineOccupati);
	return macchineDisponibili;
	}
	
}
