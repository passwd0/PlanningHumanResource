package amministrazione.model;

import java.util.Set;

public interface HasState {
	public int getId();
	public void addIndisponibilita(Indisponibilita indisponibilita);
	public Set<Indisponibilita> getIndisponibilita();
	public void setIndisponibilita(Set<Indisponibilita> indisponibilitas);
	public void removeIndisponibilita(Indisponibilita indisponibilita);
}
