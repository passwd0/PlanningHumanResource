package amministrazione.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import amministrazione.model.Appuntamento;
import amministrazione.model.Auto;
import amministrazione.model.Macchina;
import amministrazione.model.Operaio;
import amministrazione.utils.Utils;

public class AppuntamentoWriter {

	public void writer(Writer writer, List<Appuntamento> appuntamenti) throws IOException {
		if (writer == null)
			throw new IllegalArgumentException("writer null");
 		PrintWriter printWriter = new PrintWriter(new BufferedWriter(writer));
		for (Appuntamento appuntamento : appuntamenti) {
			printWriter.println(appuntamento.hashCode()+";"+appuntamento.getDataInizio().format(Utils.formatterComplete)+";"+appuntamento.getDataFine().format(Utils.formatterComplete)+";"+appuntamento.getCliente().hashCode()+";"+
					hashOperaiString(appuntamento)+";"+hashAutoString(appuntamento)+";"+hashMacchineString(appuntamento));
		}
		printWriter.close();
	}
	
	private String hashOperaiString(Appuntamento appuntamento) {
		List<String> hashes = new ArrayList<>();
		for (Operaio o : appuntamento.getOperai())
			hashes.add(String.valueOf(o.hashCode()));
		
		return hashes.stream().map(x -> x).collect(Collectors.joining(","));
	}
	
	private String hashAutoString(Appuntamento appuntamento) {
		List<String> hashes = new ArrayList<>();
		for (Auto o : appuntamento.getAuto())
			hashes.add(String.valueOf(o.hashCode()));
		
		return hashes.stream().map(x -> x).collect(Collectors.joining(","));
	}
	
	private String hashMacchineString(Appuntamento appuntamento) {
		List<String> hashes = new ArrayList<>();
		for (Macchina o : appuntamento.getMacchine())
			hashes.add(String.valueOf(o.hashCode()));
		
		return hashes.stream().map(x -> x).collect(Collectors.joining(","));
	}
	
	
}
