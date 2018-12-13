package amministrazione.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;

import amministrazione.model.Auto;

public class AutoWriter {
	public void writer(Writer writer, Collection<Auto> automobili) throws IOException {
		if (writer == null)
			throw new IllegalArgumentException("writer null");
		PrintWriter printWriter = new PrintWriter(new BufferedWriter(writer));
		for (Auto auto : automobili){
			printWriter.write(auto.getTarga() + ";" + auto.getTipologia() + ";" + auto.getStato().name() + "\n");
		}
		printWriter.close();
	}
}
