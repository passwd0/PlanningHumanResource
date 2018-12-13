package amministrazione.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;

import amministrazione.model.Cliente;

public class ClienteWriter implements Save {
	private Writer writer;
	
	public ClienteWriter(Writer writer) {
		if (writer == null)
			throw new IllegalArgumentException("writer null");
		this.writer = writer;
	}
	public void write(Collection<Cliente> clienti) throws IOException {
		PrintWriter printWriter = new PrintWriter(new BufferedWriter(writer));
		for (Cliente cliente : clienti){
			printWriter.write(cliente.getNome() + ";" + cliente.getCitta() + ";" + cliente.getIndirizzo() + "\n");
		}
		printWriter.close();
	}
}