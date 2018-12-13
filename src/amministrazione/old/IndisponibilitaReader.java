package amministrazione.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.StringTokenizer;

import amministrazione.controller.Controller;
import amministrazione.model.Auto;
import amministrazione.model.Cliente;
import amministrazione.model.HasState;
import amministrazione.model.Indisponibilita;
import amministrazione.model.Macchina;
import amministrazione.model.Operaio;
import amministrazione.utils.Utils;

public class IndisponibilitaReader {
	private Reader reader;
	private List<Operaio> o;
	private List<Auto> a;
	private List<Macchina> m;
	private List<Cliente> c;
	
	public IndisponibilitaReader(Reader reader, List<Operaio> o, List<Auto> a, List<Macchina> m, List<Cliente> c) throws IOException {
		if (reader == null)
			throw new IllegalArgumentException("reader null");
		this.reader = reader;
		this.o = o;
		this. a = a;
		this.m = m;
		this.c = c;
		
		read();
	}
	
	public IndisponibilitaReader(Reader reader, Controller controller) throws IOException {
		this(reader, controller.getOperai(), controller.getAuto(), controller.getMacchine(), controller.getClienti());
	}
	
	private void read() throws IOException{
		BufferedReader bufferedReader = new BufferedReader(reader);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			StringTokenizer stringTokenizer = new StringTokenizer(line, ";");
			if (stringTokenizer.countTokens() == 4) {
				
				String hashS = stringTokenizer.nextToken().trim();
				String dataS = stringTokenizer.nextToken().trim();
				String oraInizioS = stringTokenizer.nextToken().trim();
				String oraFineS = stringTokenizer.nextToken().trim();
			
				try {
					LocalDate data = LocalDate.parse(dataS, Utils.formatterData);
					LocalTime oraInizio = LocalTime.parse(oraInizioS, Utils.formatterOra);
					LocalTime oraFine = LocalTime.parse(oraFineS, Utils.formatterOra);
	
					long hash = Long.parseLong(hashS);
					
					HasState obj = decodeHash(hash);
					if (obj != null) 
						obj.addIndisponibilita(new Indisponibilita(data, oraInizio, oraFine));
					
				}
				catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}
	
	private HasState decodeHash(long hash) {
		for (Operaio o : this.o)
			if (o.hashCode() == hash)
				return o;
		for (Auto a : this.a)
			if (a.hashCode() == hash)
				return a;
		for (Macchina m : this.m)
			if (m.hashCode() == hash)
				return m;
		for (Cliente c : this.c)
			if (c.hashCode() == hash)
				return c;
		return null;
	}
}
