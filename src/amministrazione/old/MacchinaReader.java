package amministrazione.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import amministrazione.model.Macchina;
import amministrazione.model.Stato;

public class MacchinaReader {
	private ArrayList<Macchina> macchine;
	private Reader reader;
	
	public MacchinaReader(Reader reader) {
		this.reader = reader;
		if (reader == null)
			throw new IllegalArgumentException("reader null");
		macchine = new ArrayList<>();
	}
	
	public void read() throws IOException {
		BufferedReader bufferedReader = new BufferedReader(reader);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			StringTokenizer stringTokenizer = new StringTokenizer(line, ";");
			String idS = stringTokenizer.nextToken().trim();
			String nome = stringTokenizer.nextToken().trim();
			String statoS = stringTokenizer.nextToken().trim();
			int id = Integer.parseInt(idS);
			Stato stato = Stato.valueOf(statoS);
			
			this.macchine.add(new Macchina(id, nome, stato));
		}
	} 
	
	public List<Macchina> getMacchine(){
		return macchine;
	}
}
