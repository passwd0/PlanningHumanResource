package planninghumanresource.ui;

import javafx.scene.control.ListCell;
import planninghumanresource.model.Appuntamento;
import planninghumanresource.utils.Utils;

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
