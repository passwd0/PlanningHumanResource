package amministrazione.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import amministrazione.model.Operaio;
import amministrazione.model.Stato;

public class OperaioReader {
	private ArrayList<Operaio> operai;
	private Reader reader;
	
	public OperaioReader(Reader reader) {
		this.reader = reader;
		if (reader == null)
			throw new IllegalArgumentException("reader null");
		this.operai = new ArrayList<>();
	}
	
	public void read() throws IOException {
		BufferedReader bufferedReader = new BufferedReader(reader);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			StringTokenizer stringTokenizer = new StringTokenizer(line, ";");
			String nome = stringTokenizer.nextToken().trim();
			String cognome = stringTokenizer.nextToken().trim();
			String mansione = stringTokenizer.nextToken().trim();
			String statoS = stringTokenizer.nextToken().trim();
			Stato stato = Stato.valueOf(statoS);
			
			this.operai.add(new Operaio(nome, cognome, mansione, stato));
		}
	}
	
	public ArrayList<Operaio> getOperai(){
		return operai;
	}
}
