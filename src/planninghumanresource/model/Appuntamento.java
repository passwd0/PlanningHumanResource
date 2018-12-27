package planninghumanresource.model;

import java.time.LocalDateTime;
import java.util.List;

import planninghumanresource.utils.Utils;

public class Appuntamento implements Comparable<Appuntamento> {
	private int id;
	private LocalDateTime dataInizio;
	private LocalDateTime dataFine;
	private Cliente cliente;
	private List<Operaio> operai;
	private List<Auto> automezzi;
	private List<Macchina> macchine;
	private String nota;

	public Appuntamento(int id, LocalDateTime dataInizio, LocalDateTime dataFine, Cliente cliente, List<Operaio> operai, List<Auto> automezzi, List<Macchina> macchine, String nota) {
		if (dataInizio == null || dataFine == null || cliente == null || operai == null || automezzi == null || macchine == null)
			throw new IllegalArgumentException();
		this.id = id;
		this.dataInizio = dataInizio;
		this.dataFine = dataFine;
		this.cliente = cliente;
		this.operai = operai;
		this.automezzi = automezzi;
		this.macchine = macchine;
		this.nota = nota;
	}
	
	public Appuntamento(int id, LocalDateTime dataInizio, LocalDateTime dataFine, Cliente cliente, List<Operaio> operai, List<Auto> automezzi, List<Macchina> macchine) {
		this(id, dataInizio, dataFine, cliente, operai, automezzi, macchine, "");
	}

	public int getId() {
		return id;
	}
	
	public LocalDateTime getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(LocalDateTime dataInizio) {
		this.dataInizio = dataInizio;
	}

	public LocalDateTime getDataFine() {
		return dataFine;
	}

	public void setDataFine(LocalDateTime dataFine) {
		this.dataFine = dataFine;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	public List<Operaio> getOperai() {
		return operai;
	}

	public void setOperai(List<Operaio> operai) {
		this.operai = operai;
	}

	public List<Auto> getAuto() {
		return automezzi;
	}

	public void setAuto(List<Auto> automezzi) {
		this.automezzi = automezzi;
	}

	public List<Macchina> getMacchine() {
		return macchine;
	}

	public void setMacchine(List<Macchina> macchine) {
		this.macchine = macchine;
	}
	
	
	public String getNota() {
		return nota;
	}

	public void setNota(String nota) {
		this.nota = nota;
	}
	
	public void addMacchina(Macchina macchina) {
		if (!macchine.contains(macchina))
			macchine.add(macchina);
	}
	
	public void addAuto(Auto auto) {
		if (!automezzi.contains(auto))
			automezzi.add(auto);
	}
	
	@Override
	public String toString() {
		return getDataInizio().format(Utils.formatterOra) + " - " + getDataFine().format(Utils.formatterOra) +
				"\nOperai: " + stringOperai() + "\nAuto: " + 
				stringAuto() + "\nMacchine: " +stringMacchina() + "\nCliente: " + cliente.getNome();
	}
	
	private String stringOperai() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < getOperai().size(); i++) {
			stringBuilder.append(getOperai().get(i).getCognome() + " " + getOperai().get(i).getNome() + " (" + getOperai().get(i).getMansione() + ")");
			if (i < getOperai().size() - 1)
				stringBuilder.append(",\n");
		}
		return stringBuilder.toString();
	}
	
	private String stringMacchina() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < getMacchine().size(); i++) {
				stringBuilder.append(getMacchine().get(i).getNome());
				if (i < getMacchine().size() - 1)
					stringBuilder.append(",\n");
			}
		return stringBuilder.toString();
	}
	
	private String stringAuto() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < getAuto().size(); i++) {
				stringBuilder.append(getAuto().get(i).getTarga() + " " + getAuto().get(i).getTipologia());
				if (i < getAuto().size() - 1)
					stringBuilder.append(",\n");
			}
		return stringBuilder.toString();
	}

	@Override
	public int compareTo(Appuntamento appuntamento) {
		return dataInizio.compareTo(appuntamento.dataInizio);
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Appuntamento other = (Appuntamento) obj;
		if (automezzi == null) {
			if (other.automezzi != null)
				return false;
		} else if (!automezzi.equals(other.automezzi))
			return false;
		if (cliente == null) {
			if (other.cliente != null)
				return false;
		} else if (!cliente.equals(other.cliente))
			return false;
		if (dataFine == null) {
			if (other.dataFine != null)
				return false;
		} else if (!dataFine.equals(other.dataFine))
			return false;
		if (dataInizio == null) {
			if (other.dataInizio != null)
				return false;
		} else if (!dataInizio.equals(other.dataInizio))
			return false;
		if (macchine == null) {
			if (other.macchine != null)
				return false;
		} else if (!macchine.equals(other.macchine))
			return false;
		if (nota == null) {
			if (other.nota != null)
				return false;
		} else if (!nota.equals(other.nota))
			return false;
		if (operai == null) {
			if (other.operai != null)
				return false;
		} else if (!operai.equals(other.operai))
			return false;
		return true;
	}
}
