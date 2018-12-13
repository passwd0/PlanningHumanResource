package amministrazione.ui;

import amministrazione.model.Appuntamento;
import amministrazione.utils.Utils;
import javafx.scene.control.ListCell;

public class AppuntamentiRisorse extends ListCell<Appuntamento> {
	public void updateItem(Appuntamento item, boolean empty) {
		super.updateItem(item, empty);
		if (item != null) {
			this.setText(item.getDataInizio().format(Utils.formatterData) + " -> " + item.toString());
		}
		else
			setText("");
	}
	
	
}
