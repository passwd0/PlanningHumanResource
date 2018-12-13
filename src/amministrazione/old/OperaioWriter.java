package amministrazione.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;

import amministrazione.model.Operaio;

public class OperaioWriter {
	public void writer(Writer writer, Collection<Operaio> operai) throws IOException {
		if (writer == null)
			throw new IllegalArgumentException("writer null");
		PrintWriter printWriter = new PrintWriter(new BufferedWriter(writer));
		for (Operaio operaio : operai){
			printWriter.write(operaio.getNome() + ";" + operaio.getCognome() + ";" + operaio.getMansione() + ";" + operaio.getStato().name() + "\n");
		}
		printWriter.close();
	}
}