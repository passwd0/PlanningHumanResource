package amministrazione.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import amministrazione.model.Indisponibilita;
import amministrazione.utils.Utils;

public class IndisponibilitaWriter {
	public <T> void writer(Writer writer, Map<T, Set<Indisponibilita>> map) throws IOException {
		if (writer == null)
			throw new IllegalArgumentException("writer null");
 		PrintWriter printWriter = new PrintWriter(new BufferedWriter(writer));
 			for (T key : map.keySet())
 				for (Indisponibilita i : map.get(key))
					printWriter.println(key.hashCode()+";"+i.getData().format(Utils.formatterData)+
 							";"+i.getOraInizio().format(Utils.formatterOra)+";"+i.getOraFine().format(Utils.formatterOra));
 				
		printWriter.close();
	}
}
