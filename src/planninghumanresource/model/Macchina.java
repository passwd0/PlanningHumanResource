package planninghumanresource.model;

import java.util.HashSet;
import java.util.Set;

public class Macchina implements Comparable<Macchina>, HasState {
	private int id;
	private String nome;
	private Stato stato;
	private Set<Indisponibilita> listIndisponibilita;
	
	public Macchina(int id, String nome, Stato stato) {
		if (nome == null || nome.isEmpty())
			throw new IllegalArgumentException();
		this.id = id;
		this.nome = nome;
		this.stato = stato;
		this.listIndisponibilita = new HashSet<>();
	}
	
	public Macchina(int id, String nome) {
		this(id, nome, Stato.Disponibile);
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

	public String getNome() {
		return nome;
	}
	
	public Stato getStato() {
		return stato;
	}

	public void setStato(Stato stato) {
		this.stato = stato;
	}
	
	@Override
	public String toString() {
		return nome;
	}

	@Override
	public int compareTo(Macchina macchina) {
		return nome.compareTo(macchina.nome);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
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
		Macchina other = (Macchina) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}
}
