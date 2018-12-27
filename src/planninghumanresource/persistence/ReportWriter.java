package planninghumanresource.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import planninghumanresource.model.Appuntamento;
import planninghumanresource.model.Auto;
import planninghumanresource.model.Macchina;
import planninghumanresource.model.Operaio;

public class ReportWriter {
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	public void writer(Writer writer, List<Appuntamento> appuntamenti) throws IOException {
		if (writer == null)
			throw new IllegalArgumentException("writer null");
 		PrintWriter printWriter = new PrintWriter(new BufferedWriter(writer));
		for (Appuntamento appuntamento : appuntamenti) {
			printWriter.println(appuntamento.getDataInizio().format(formatter)+";"+appuntamento.getDataFine().format(formatter)+";"+appuntamento.getCliente().toString()+";"+
					operaiString(appuntamento)+";"+autoString(appuntamento)+";"+macchineString(appuntamento));
		}
		printWriter.close();
	}
	
	private String operaiString(Appuntamento appuntamento) {
		List<String> result = new ArrayList<>();
		for (Operaio o : appuntamento.getOperai())
			result.add(o.toString());
		
		return result.stream().map(x -> x).collect(Collectors.joining(","));
	}
	
	private String autoString(Appuntamento appuntamento) {
		List<String> result = new ArrayList<>();
		for (Auto o : appuntamento.getAuto())
			result.add(o.toString());
		
		return result.stream().map(x -> x).collect(Collectors.joining(","));
	}
	
	private String macchineString(Appuntamento appuntamento) {
		List<String> result = new ArrayList<>();
		for (Macchina o : appuntamento.getMacchine())
			result.add(o.toString());
		
		return result.stream().map(x -> x).collect(Collectors.joining(","));
	}
}
