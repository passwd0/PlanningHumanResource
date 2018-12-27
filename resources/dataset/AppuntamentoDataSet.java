package dataset;

import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import planninghumanresource.model.Appuntamento;
import planninghumanresource.utils.Utils;

public class AppuntamentoDataSet {
	private SimpleStringProperty data;
	private SimpleStringProperty ora;
	private SimpleStringProperty cliente;
	private SimpleStringProperty operai;
	private SimpleStringProperty auto;
	private SimpleStringProperty macchine;
	
	public AppuntamentoDataSet(Appuntamento a) {
		data = new SimpleStringProperty(a.getDataInizio().toLocalDate().format(Utils.formatterData));
		ora = new SimpleStringProperty(a.getDataInizio().toLocalTime() + " - " + a.getDataFine().toLocalTime());
		cliente = new SimpleStringProperty(a.getCliente().toString());
		operai = new SimpleStringProperty(a.getOperai().stream().map(x -> x.toString()).collect(Collectors.joining("\n")));
		auto = new SimpleStringProperty(a.getAuto().stream().map(x -> x.toString()).collect(Collectors.joining("\n")));
		macchine = new SimpleStringProperty(a.getMacchine().stream().map(x -> x.toString()).collect(Collectors.joining("\n")));
	}

	public String getCliente() {
		return cliente.get();
	}

	public String getData() {
		return data.get();
	}
	
	public String getOra() {
		return ora.get();
	}

	public String getOperai() {
		return operai.get();
	}

	public String getAuto() {
		return auto.get();
	}

	public String getMacchine() {
		return macchine.get();
	}
}
