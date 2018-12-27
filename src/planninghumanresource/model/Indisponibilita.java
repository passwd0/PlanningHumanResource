package planninghumanresource.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import planninghumanresource.utils.Utils;

public class Indisponibilita  implements Comparable<Indisponibilita>{
	private int id;
	private SimpleObjectProperty<LocalDate> data;
	private SimpleObjectProperty<LocalTime> oraInizio;
	private SimpleObjectProperty<LocalTime> oraFine;
	
	public Indisponibilita(int id, LocalDate data, LocalTime oraInizio, LocalTime oraFine) {
		this.id = id;
		this.data = new SimpleObjectProperty<>(data);
		this.oraInizio = new SimpleObjectProperty<>(oraInizio);
		this.oraFine = new SimpleObjectProperty<>(oraFine);
	}
	
	public int getId() {
		return id;
	}
	
	public LocalDate getData() {
		return data.get();
	}
	
	public String getDataString() {
		return data.get().format(Utils.formatterData);
	}
	
	public LocalTime getOraInizio() {
		return oraInizio.get();
	}
	
	public ObjectProperty<LocalTime> oraInizioProperty(){
		return oraInizio;
	}
	
	public ObjectProperty<LocalTime> oraFineProperty(){
		return oraFine;
	}
	
	public LocalTime getOraFine() {
		return oraFine.get();
	}
	
	public void setOraInizio(LocalTime time) {
		oraInizio.set(time);
	}
	
	public void setOraFine(LocalTime time) {
		oraInizio.set(time);
	}

	@Override
	public String toString() {
		return "Indisponibilita [data=" + data.get() + ", oraInizio=" + oraInizio.get() + ", oraFine=" + oraFine.get() + "]";
	}

	@Override
	public int compareTo(Indisponibilita o) {
		LocalDateTime questo = getData().atTime(getOraInizio());
		LocalDateTime quello = o.getData().atTime(o.getOraInizio());
		return questo.compareTo(quello);
	}
	
	
}
