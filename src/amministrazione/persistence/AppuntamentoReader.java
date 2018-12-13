package amministrazione.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import amministrazione.model.Appuntamento;
import amministrazione.model.Auto;
import amministrazione.model.Cliente;
import amministrazione.model.Macchina;
import amministrazione.model.Operaio;
import amministrazione.utils.Utils;

public class AppuntamentoReader {
	private Reader reader;

	public AppuntamentoReader(Reader reader) {
		this.reader = reader;
		if (reader == null)
			throw new IllegalArgumentException("reader null");
	}
	
	public List<Appuntamento> read(DataManager dataManager) throws IOException {
		List<Appuntamento> appuntamenti = new ArrayList<>();
		BufferedReader bufferedReader = new BufferedReader(reader);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			String[] temp = line.split(";");
			if (temp.length >= 5) {
				String idS = temp[0].trim();
				String dataInizioS = temp[1].trim();
				String dataFineS = temp[2].trim();
				
				String clienteS = temp[3].trim();
				int clienteHash = Integer.valueOf(clienteS);
				List<Integer> autoHash = new ArrayList<>();
				List<Integer> macchineHash = new ArrayList<>();
	
				String operaiS = temp[4];
				String[] operaiSplitted = operaiS.split(",");
				List<Integer> operaiHash = new ArrayList<>();
				for (String s : operaiSplitted)
					if (!s.isEmpty())
						operaiHash.add(Integer.valueOf(s.trim()));
				if (temp.length >= 5) {
				try {
					String automezziS = temp[5];
					String[] automezziSplitted =  automezziS.split(",");
					for (String s : automezziSplitted)
						if (!s.isEmpty())
							autoHash.add(Integer.valueOf(s.trim()));
				}catch (NoSuchElementException e) {
					// TODO: handle exception
				}
				}
				if (temp.length > 6) {
					try {
						String macchineS = temp[6];
						String[] macchineSplitted = macchineS.split(",");
						for (String s : macchineSplitted)
							if (!s.isEmpty())
								macchineHash.add(Integer.valueOf(s.trim()));
					}catch (NoSuchElementException e) {
						// TODO: handle exception
					}
				}
				
				try {
					int id = Integer.parseInt(idS);
					LocalDateTime dataInizio = LocalDateTime.parse(dataInizioS, Utils.formatterComplete);
					LocalDateTime dataFine = LocalDateTime.parse(dataFineS, Utils.formatterComplete);
					Appuntamento a = new Appuntamento(id, dataInizio, dataFine, getCliente(dataManager.getClienti(), clienteHash), 
							getOperai(dataManager.getOperai(), operaiHash), getAuto(dataManager.getAuto(), autoHash), getMacchine(dataManager.getMacchine(), macchineHash));
					appuntamenti.add(a);
				}catch (Exception e) {
					e.getStackTrace();
				}
			}
		}
		return appuntamenti;
	} 
	
	private Cliente getCliente(List<Cliente> clienti, int clienteHash) {
		for (Cliente c : clienti)
			if (c.hashCode() == clienteHash)
				return c;
		return null;
	}
	
	private List<Operaio> getOperai(List<Operaio> operai, List<Integer> operaiHashes) {
		List<Operaio> hashes = new ArrayList<>();
		for (Operaio o : operai)
			for (Integer i : operaiHashes)
				if (o.hashCode() == i)
					hashes.add(o);
		return hashes;
	}
	
	private List<Auto> getAuto(List<Auto> automezzi, List<Integer> autoHashes){
		List<Auto> hashes = new ArrayList<>();
		for (Auto a : automezzi)
			for (Integer i : autoHashes)
				if (a.hashCode() == i)
					hashes.add(a);
		return hashes;
	}
	
	private List<Macchina> getMacchine(List<Macchina> macchine, List<Integer> macchineHashes){
		List<Macchina> hashes = new ArrayList<>();
		for (Macchina m : macchine)
			for (Integer i : macchineHashes)
				if (m.hashCode() == i)
					hashes.add(m);
		return hashes;
	}
}
