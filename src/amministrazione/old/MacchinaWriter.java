package amministrazione.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;

import amministrazione.model.Macchina;

public class MacchinaWriter {
	public void writer(Writer writer, Collection<Macchina> macchine) throws IOException {
		if (writer == null)
			throw new IllegalArgumentException("writer null");
		PrintWriter printWriter = new PrintWriter(new BufferedWriter(writer));
		for (Macchina macchina : macchine){
			printWriter.write(macchina.getNome() + ";" + macchina.getStato() + "\n");
		}
		printWriter.close();
	}
}