package amministrazione.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import amministrazione.model.Cliente;

public class ClienteReader implements Load {
	private ArrayList<Cliente> clienti;
	private Reader reader;
	
	public ClienteReader(Reader reader)  {
		this.reader = reader;
		if (reader == null)
			throw new IllegalArgumentException("reader null");
		clienti = new ArrayList<>();
	}
	
	public void read() throws IOException {
		BufferedReader bufferedReader = new BufferedReader(reader);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			StringTokenizer stringTokenizer = new StringTokenizer(line, ";");
			String nome = stringTokenizer.nextToken().trim();
			String citta = stringTokenizer.nextToken().trim();
			String indirizzo = stringTokenizer.nextToken().trim();
			
			clienti.add(new Cliente(nome, citta, indirizzo));
		}
	}
	
	public List<Cliente> getClienti(){
		return clienti;
	}
}
