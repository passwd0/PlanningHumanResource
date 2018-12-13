package amministrazione.model;

import java.util.HashSet;
import java.util.Set;

public class Operaio implements Comparable<Operaio>, HasState {
	private int id;
	private String nome;
	private String cognome;
	private String mansione;
	private Stato stato;
	private Set<Indisponibilita> listIndisponibilita;
	
	public Operaio(int id, String nome, String cognome, String mansione, Stato stato) {
		if (nome == null || cognome == null || mansione == null || nome.isEmpty() || cognome.isEmpty() || mansione.isEmpty())
			throw new IllegalArgumentException();
		this.id = id;
		this.nome = nome;
		this.cognome = cognome;
		this.mansione = mansione;
		this.stato = stato;
		this.listIndisponibilita = new HashSet<>();
	}
	
	public Operaio(int id, String nome, String cognome, String mansione) {
		this(id, nome, cognome, mansione, Stato.Disponibile);
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
	
	public String getMansione() {
		return mansione;
	}

	public void setMansione(String mansione) {
		this.mansione = mansione;
	}

	public String getNome() {
		return nome;
	}

	public String getCognome() {
		return cognome;
	}
	
	public Stato getStato() {
		return stato;
	}

	public void setStato(Stato stato) {
		this.stato = stato;
	}
	
	@Override
	public String toString() {
		return cognome + " " + nome + " (" + mansione + ")";
	}

	@Override
	public int compareTo(Operaio operaio) {
		int cmpCognome = cognome.compareToIgnoreCase(operaio.cognome);
		if (cmpCognome == 0)
			return nome.compareTo(operaio.nome);
		return cmpCognome;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cognome == null) ? 0 : cognome.hashCode());
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
		Operaio other = (Operaio) obj;
		if (cognome == null) {
			if (other.cognome != null)
				return false;
		} else if (!cognome.equals(other.cognome))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}
}
