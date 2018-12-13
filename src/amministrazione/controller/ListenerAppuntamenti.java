package amministrazione.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

import amministrazione.model.Appuntamento;
import amministrazione.model.Auto;
import amministrazione.model.Cliente;
import amministrazione.model.Macchina;
import amministrazione.model.Operaio;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;

public class ListenerAppuntamenti extends Thread {
    private PGConnection pgconn;
    private ListView<Appuntamento> listGiorno;
    private ListView<Operaio> listOperai;
    private ListView<Auto> listAuto;
    private ListView<Macchina> listMacchine;
    private ListView<Cliente> listClienti;
    private Controller controller;
    private DatePicker datePicker;

    public ListenerAppuntamenti(Connection conn, Controller controller, ListView<Appuntamento> listGiorno,
    		ListView<Operaio> listOperai, ListView<Auto> listAuto, ListView<Macchina> listMacchine,
    		ListView<Cliente> listClienti, DatePicker datePicker) throws SQLException {
        this.controller = controller;
        this.listGiorno = listGiorno;
        this.listOperai = listOperai;
        this.listAuto = listAuto;
        this.listMacchine = listMacchine;
        this.listClienti = listClienti;
        this.datePicker = datePicker;
        this.pgconn = conn.unwrap(PGConnection.class);
        Statement stmt = conn.createStatement();
        stmt.execute("LISTEN notify");
        stmt.close();
    }

    public void run()
    {
        try
        {
            while (true)
            {
                PGNotification notifications[] = pgconn.getNotifications();
                if (notifications != null)
                {
                    for (int i=0; i < notifications.length; i++) {
                    	System.out.println(notifications[i].getName() + ", " + notifications[i].getParameter());
    	        		if (notifications[i].getParameter().equals("appuntamenti")) {
	                    	Platform.runLater(() -> {
            	        		try {
									listGiorno.setItems(controller.getAppuntamentiGiorno(datePicker.getValue()));
            	        		} catch (ClassNotFoundException | SQLException e) {
									// TODO Auto-generated catch block
									e.getMessage();
								}
							});
    	        		}
    	        		if (notifications[i].getParameter().equals("operai")) {
	                    	Platform.runLater(() -> {
            	        		try {
            	        			listOperai.setItems(FXCollections.observableArrayList(controller.loadOperai()));
            	        		} catch (ClassNotFoundException | SQLException e) {
									// TODO Auto-generated catch block
									e.getMessage();
								}
							});
    	        		}
    	        		if (notifications[i].getParameter().equals("auto")) {
	                    	Platform.runLater(() -> {
            	        		try {
            	        			listAuto.setItems(FXCollections.observableArrayList(controller.loadAuto()));
            	        		} catch (ClassNotFoundException | SQLException e) {
									// TODO Auto-generated catch block
									e.getMessage();
								}
							});
    	        		}
    	        		if (notifications[i].getParameter().equals("macchine")) {
	                    	Platform.runLater(() -> {
            	        		try {
            	        			listMacchine.setItems(FXCollections.observableArrayList(controller.loadMacchine()));
            	        		} catch (ClassNotFoundException | SQLException e) {
									// TODO Auto-generated catch block
									e.getMessage();
								}
							});
    	        		}
    	        		if (notifications[i].getParameter().equals("clienti")) {
	                    	Platform.runLater(() -> {
            	        		try {
            	        			listClienti.setItems(FXCollections.observableArrayList(controller.loadClienti()));
            	        		} catch (ClassNotFoundException | SQLException e) {
									// TODO Auto-generated catch block
									e.getMessage();
								}
							});
    	        		}
                    }
                }
                Thread.sleep(500);
            }
        }
        catch (SQLException | InterruptedException e) {
			e.printStackTrace();
		}
    }
}
