package planninghumanresource.ui;

import java.time.LocalTime;

import javafx.beans.property.SimpleObjectProperty;
import planninghumanresource.model.Appuntamento;

public class ItemRightPaneList {
	private SimpleObjectProperty<LocalTime> ora;
	private SimpleObjectProperty<Appuntamento> appuntamento;
	
	public ItemRightPaneList(LocalTime ora, Appuntamento appuntamento) {
		this.ora = new SimpleObjectProperty<LocalTime>(ora);
		this.appuntamento = new SimpleObjectProperty<Appuntamento>(appuntamento);
	}
	
	public LocalTime getOra() {
		return ora.get();
	}
	
	public Appuntamento getAppuntamento() {
		return appuntamento.get();
	}
}
