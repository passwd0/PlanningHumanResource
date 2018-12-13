package amministrazione.ui;

import java.util.List;

import amministrazione.model.Appuntamento;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;

public class ItemAppuntamentiRisorsaSettimanale {
	private SimpleStringProperty ora;
	private SimpleListProperty<Appuntamento> lunedi;
	private SimpleListProperty<Appuntamento> martedi;
	private SimpleListProperty<Appuntamento> mercoledi;
	private SimpleListProperty<Appuntamento> giovedi;
	private SimpleListProperty<Appuntamento> venerdi;
	private SimpleListProperty<Appuntamento> sabato;
	private SimpleListProperty<Appuntamento> domenica;
	
	

	public ItemAppuntamentiRisorsaSettimanale(String ora, List<Appuntamento> lunedi, List<Appuntamento> martedi, List<Appuntamento> mercoledi, List<Appuntamento> giovedi, List<Appuntamento> venerdi,
			List<Appuntamento> sabato, List<Appuntamento> domenica) {
		this.ora = new SimpleStringProperty(ora);
		this.lunedi = new SimpleListProperty<>(FXCollections.observableArrayList(lunedi));
		this.martedi = new SimpleListProperty<>(FXCollections.observableArrayList(martedi));
		this.mercoledi = new SimpleListProperty<>(FXCollections.observableArrayList(mercoledi));
		this.giovedi = new SimpleListProperty<>(FXCollections.observableArrayList(giovedi));
		this.venerdi = new SimpleListProperty<>(FXCollections.observableArrayList(venerdi));
		this.sabato = new SimpleListProperty<>(FXCollections.observableArrayList(sabato));
		this.domenica = new SimpleListProperty<>(FXCollections.observableArrayList(domenica));
	}

	public String getOra() {
		return ora.get();
	}

	public List<Appuntamento> getLunedi() {
		return lunedi.get();
	}

	public List<Appuntamento> getMartedi() {
		return martedi.get();
	}

	public List<Appuntamento> getMercoledi() {
		return mercoledi.get();
	}

	public List<Appuntamento> getGiovedi() {
		return giovedi.get();
	}

	public List<Appuntamento> getVenerdi() {
		return venerdi.get();
	}

	public List<Appuntamento> getSabato() {
		return sabato.get();
	}

	public List<Appuntamento> getDomenica() {
		return domenica.get();
	}
	
	
	
}
