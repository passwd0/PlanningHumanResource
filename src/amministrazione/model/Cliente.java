package amministrazione.model;

import java.util.HashSet;
import java.util.Set;

public class Cliente implements Comparable<Cliente>, HasState {
	private int id;
	private String nome;
	private String indirizzo;
	private String citta;
	private Stato stato;
	private Set<Indisponibilita> listIndisponibilita;
	
	public Cliente(int id, String nome, String citta, String indirizzo, Stato stato) {
		if (nome == null || citta == null || indirizzo == null || nome.isEmpty() || citta.isEmpty() || indirizzo.isEmpty())
			throw new IllegalArgumentException();
		this.id = id;
		this.nome = nome;
		this.indirizzo = indirizzo;
		this.citta = citta;
		this.stato = stato;
		this.listIndisponibilita = new HashSet<>();
	}
	
	public Cliente(int id, String nome, String citta, String indirizzo) {
		this(id, nome, citta, indirizzo, Stato.Disponibile);
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

	public String getIndirizzo() {
		return indirizzo;
	}

	public String getCitta() {
		return citta;
	}
	
	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public void setCitta(String citta) {
		this.citta = citta;
	}
	
	public Stato getStato() {
		return stato;
	}

	public void setStato(Stato stato) {
		this.stato = stato;
	}

	@Override
	public String toString() {
		return nome + " " + citta + ", " + indirizzo;
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
		Cliente other = (Cliente) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}

	@Override
	public int compareTo(Cliente that) {
		return nome.compareTo(that.nome);
	}
}
