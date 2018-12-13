package amministrazione.persistence;

import java.io.IOException;
import java.util.Collection;

import amministrazione.model.Cliente;

public interface Save {
	public void write(Collection<Cliente> clienti) throws IOException;
}
