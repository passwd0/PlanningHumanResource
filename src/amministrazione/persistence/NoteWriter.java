package amministrazione.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import amministrazione.model.Appuntamento;

public class NoteWriter {
	public void writer(Writer writer, List<Appuntamento> appuntamenti) throws IOException {
		if (writer == null)
			throw new IllegalArgumentException("writer null");
		PrintWriter printWriter = new PrintWriter(new BufferedWriter(writer));
		for (Appuntamento a: appuntamenti){
			if (!a.getNota().isEmpty())
				printWriter.write(a.hashCode()+"\n"+a.getNota()+"\n###FINENOTA###\n");
		}
		printWriter.close();
	}
}
