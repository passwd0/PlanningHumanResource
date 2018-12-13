package amministrazione.ui;

import java.time.LocalTime;
import java.util.List;

import amministrazione.model.Auto;
import amministrazione.model.Macchina;
import amministrazione.model.Operaio;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class ItemRisorsa {
	private SimpleListProperty<LocalTime> ora;
	private SimpleListProperty<Operaio> operai;
	private SimpleListProperty<Auto> automezzi;
	private SimpleListProperty<Macchina> macchine;
	
	public ItemRisorsa(List<LocalTime> ora, List<Operaio> operai, List<Auto> automezzi, List<Macchina> macchine) {
		this.ora = new SimpleListProperty<LocalTime>(FXCollections.observableArrayList(ora));
		this.operai = new SimpleListProperty<Operaio>(FXCollections.observableArrayList(operai));
		this.automezzi = new SimpleListProperty<Auto>(FXCollections.observableArrayList(automezzi));
		this.macchine = new SimpleListProperty<Macchina>(FXCollections.observableArrayList(macchine));
	}
	
	public List<LocalTime> getOra() {
		return ora.get();
	}

	public List<Operaio> getOperai() {
		return operai.get();
	}

	public List<Auto> getAuto() {
		return automezzi.get();
	}

	public List<Macchina> getMacchine() {
		return macchine.get();
	}
}
