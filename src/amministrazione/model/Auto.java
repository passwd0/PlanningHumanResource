package amministrazione.model;

import java.util.HashSet;
import java.util.Set;

public class Auto implements Comparable<Auto>, HasState {
	private int id;
	private String targa;
	private String tipologia;
	private Stato stato;

	private Set<Indisponibilita> listIndisponibilita;
	
	public Auto(int id, String targa, String tipologia, Stato stato) {
		if (targa == null || tipologia == null || targa.isEmpty() || tipologia.isEmpty())
			throw new IllegalArgumentException();
		this.id = id;
		this.targa = targa;
		this.tipologia = tipologia;
		this.stato = stato;
		this.listIndisponibilita = new HashSet<>();
	}
	
	public Auto(int id, String targa, String tipologia) {
		this(id, targa, tipologia, Stato.Disponibile);
	}
	
	public int getId() {
		return id;
	}

	@Override
	public Set<Indisponibilita> getIndisponibilita() {
		return listIndisponibilita;
	}
	
	@Override
	public void setIndisponibilita(Set<Indisponibilita> dates) {
		listIndisponibilita = dates;
	}
	
	@Override
	public void addIndisponibilita(Indisponibilita indisponibile) {
		listIndisponibilita.add(indisponibile);
	}
	
	@Override
	public void removeIndisponibilita(Indisponibilita indisponibilita) {
		listIndisponibilita.remove(indisponibilita);
	}
	
	public String getTarga() {
		return targa;
	}
	
	public String getTipologia() {
		return tipologia;
	}
	
	public Stato getStato() {
		return stato;
	}

	public void setStato(Stato stato) {
		this.stato = stato;
	}
	
	@Override
	public String toString() {
		return tipologia + " " + targa;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((targa == null) ? 0 : targa.hashCode());
		result = prime * result + ((tipologia == null) ? 0 : tipologia.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Auto other = (Auto) obj;
		if (targa == null) {
			if (other.targa != null)
				return false;
		} else
			if (!targa.equals(other.targa))
				return false;
		if (tipologia == null) {
			if (other.tipologia != null)
				return false;
		} else if (!tipologia.equals(other.tipologia))
			return false;
		return true;
	}

	@Override
	public int compareTo(Auto that) {
		return targa.compareTo(that.targa);
	}
}
