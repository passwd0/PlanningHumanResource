package planninghumanresource.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import planninghumanresource.model.Appuntamento;

public class NoteReader {
	private Reader reader;
	
	public NoteReader(Reader reader) {
		this.reader = reader;
		if (reader == null)
			throw new IllegalArgumentException("reader null");
	}
	
	public void read(List<Appuntamento> appuntamenti) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(reader);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			long appuntamentoHash = Long.parseLong(line.trim());
			String note = "";
			if ((line = bufferedReader.readLine()) != null && !line.equals("###FINENOTA###"))
				note += line;
			while ((line = bufferedReader.readLine()) != null && !line.equals("###FINENOTA###")) {
				note += "\n"+line;
			}
			for (Appuntamento a : appuntamenti)
				if (a.hashCode() == appuntamentoHash)
					a.setNota(note);
		}
	}
}