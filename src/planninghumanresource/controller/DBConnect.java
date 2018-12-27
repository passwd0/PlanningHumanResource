package planninghumanresource.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import planninghumanresource.utils.Utils;

public class DBConnect {
    private static Connection conn;
    

    public static Connection connect() throws SQLException{
        try {
        	Class.forName("org.postgresql.Driver").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
        	e.getStackTrace();
        }
    	conn = DriverManager
    			.getConnection(Utils.url+Utils.db, Utils.user, Utils.pass);
        conn.setAutoCommit(false);
        return conn;
    }
    
    public static Connection getConnection() throws SQLException, ClassNotFoundException{
        if(conn !=null && !conn.isClosed())
            return conn;
        connect();
        return conn;
    }
    
    public static void verifyDB() {
        try {
        	DriverManager
        			.getConnection(Utils.url, Utils.user, Utils.pass);
		} catch (SQLException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.getDialogPane().applyCss();
			Node graphic = alert.getDialogPane().getGraphic();
			alert.getDialogPane().setGraphic(graphic);
			alert.setTitle("Avviare il Database!");
			alert.setHeaderText("Il Database protrebbe essere spento.\nAvvialo!");
			alert.showAndWait();
			System.exit(0);
		}
    	
    	boolean createDB = false;
    	try {
			conn = connect();
		} catch (SQLException e1) {
			ButtonType buttonTypeCreate = new ButtonType("Creare DB");
			Alert alert = new Alert(AlertType.WARNING);
			alert.getDialogPane().applyCss();
			Node graphic = alert.getDialogPane().getGraphic();
			alert.getDialogPane().getButtonTypes().clear();
			alert.getDialogPane().getButtonTypes().addAll(ButtonType.NO, buttonTypeCreate);
			alert.getDialogPane().setGraphic(graphic);
			alert.setTitle("Creare Database");
			alert.setHeaderText("Database non trovato.\nProcedere a crearne uno nuovo");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeCreate)
				createDB = true;
		}
    	
    	Statement s;
		try {
			try {
	        	Class.forName("org.postgresql.Driver").newInstance();
	        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
	        	// TODO Auto-generated catch block
	        	e.printStackTrace();
	        }
			Connection c = null;
	        try {
	        	c = DriverManager
	        			.getConnection(Utils.url, Utils.user, Utils.pass);
			} catch (SQLException e) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.getDialogPane().applyCss();
				Node graphic = alert.getDialogPane().getGraphic();
				alert.getDialogPane().setGraphic(graphic);
				alert.setTitle("Avviare il Database");
				alert.setHeaderText("Il Database protrebbe essere spento.\nAvvialo!");
				alert.showAndWait();
				System.exit(0);
			}
	        if (createDB) {
		        c.setAutoCommit(true);
				s = c.createStatement();
				s.executeUpdate("CREATE DATABASE amministrazione;");
		    	s.close();
	        }
	        c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (createDB) {
			List<String> tables = new ArrayList<>(Arrays.asList("appuntamenti", "appuntamenti_auto", "appuntamenti_macchine", 
	    			"appuntamenti_operai", "auto", "clienti", "indisponibilita", "macchine", "note", "operai", "notify",
	    			"trigger_appuntamenti", "trigger_operai", "trigger_auto", "trigger_macchine", "trigger_clienti"));
			try {
				conn = connect();
				for (String table : tables) {
				    	createTables(table);
				}
				conn.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
    }

	private static void createTables(String table) throws SQLException {
		Statement stmt = conn.createStatement();
		if (table.equals("appuntamenti"))
			createAppuntamenti(stmt);
		if (table.equals("appuntamenti_auto"))
			createAppuntamentiAuto(stmt);
		if (table.equals("appuntamenti_macchine"))
			createAppuntamentiMacchine(stmt);
		if (table.equals("appuntamenti_operai"))
			createAppuntamentiOperai(stmt);
		if (table.equals("auto"))
			createAuto(stmt);
		if (table.equals("clienti"))
			createClienti(stmt);
		if (table.equals("indisponibilita"))
			createIndisponibilita(stmt);
		if (table.equals("macchine"))
			createMacchine(stmt);
		if (table.equals("note"))
			createNote(stmt);
		if (table.equals("operai"))
			createOperai(stmt);
		if (table.equals("notify"))
			createNotify(stmt);
		if (table.equals("trigger_appuntamenti"))
			createTriggerAppuntamenti(stmt);
		if (table.equals("trigger_operai"))
			createTriggerOperai(stmt);
		if (table.equals("trigger_auto"))
			createTriggerAuto(stmt);
		if (table.equals("trigger_macchine"))
			createTriggerMacchine(stmt);
		if (table.equals("trigger_clienti"))
			createTriggerClienti(stmt);
		conn.commit();
	}
	
	private static void createNotify(Statement stmt) throws SQLException {
		String sql = "create or replace function \"notify\"()\n" +
				"returns trigger as $$\n" + 
				"declare\n" + 
				"begin\n" + 
				"perform pg_notify('notify', TG_ARGV[0]);\n" + 
				"return new;\n" + 
				"end;\n" + 
				"$$ language plpgsql;";
		stmt.executeUpdate(sql);
		stmt.close();
	}
	
	private static void createTriggerAppuntamenti(Statement stmt) throws SQLException {
		String sql = "create trigger \"notify_appuntamenti\"\n" + 
				"after insert or update or delete on appuntamenti\n" + 
				"for each row execute procedure \"notify\"(appuntamenti);";
		stmt.executeUpdate(sql);
		stmt.close();
	}
	
	private static void createTriggerOperai(Statement stmt) throws SQLException {
		String sql = "create trigger \"notify_operai\"\n" + 
				"after insert or update or delete on operai\n" + 
				"for each row execute procedure \"notify\"(operai);";
		stmt.executeUpdate(sql);
		stmt.close();
	}
	
	private static void createTriggerAuto(Statement stmt) throws SQLException {
		String sql = "create trigger \"notify_auto\"\n" + 
				"after insert or update or delete on auto\n" + 
				"for each row execute procedure \"notify\"(auto);";
		stmt.executeUpdate(sql);
		stmt.close();
	}
	
	private static void createTriggerMacchine(Statement stmt) throws SQLException {
		String sql = "create trigger \"notify_macchine\"\n" + 
				"after insert or update or delete on macchine\n" + 
				"for each row execute procedure \"notify\"(macchine);";
		stmt.executeUpdate(sql);
		stmt.close();
	}
	
	private static void createTriggerClienti(Statement stmt) throws SQLException {
		String sql = "create trigger \"notify_clienti\"\n" + 
				"after insert or update or delete on clienti\n" + 
				"for each row execute procedure \"notify\"(clienti);";
		stmt.executeUpdate(sql);
		stmt.close();
	}

	/*private static void createNotifyAppuntamenti(Statement stmt) throws SQLException {
		String sql = "create or replace function 'notify_appuntamenti'()\n" +
				"returns trigger as $$\n" +
				"declare\n" +
				"begin\n" +
				"perform pg_notify('notify_appuntamenti', 'testo payload');\n" +
				"return new;\n" +
				"end;\n" +
				"$$ language plpgsql;";
		stmt.executeUpdate(sql);
		stmt.close();
	}*/

	private static void createAppuntamentiMacchine(Statement stmt) throws SQLException {
		String sql = "CREATE TABLE appuntamenti_macchine (" +
				"id serial NOT NULL, id_appuntamento integer, id_macchina integer,"+
				"PRIMARY KEY(id));";
		stmt.executeUpdate(sql);
		stmt.close();
	}
	private static void createAppuntamentiAuto(Statement stmt) throws SQLException {
		String sql = "CREATE TABLE appuntamenti_auto (" +
				"id serial NOT NULL, id_appuntamento integer, id_auto integer,"+
				"PRIMARY KEY(id));";
		stmt.executeUpdate(sql);
		stmt.close();
	}
	private static void createAppuntamentiOperai(Statement stmt) throws SQLException {
		String sql = "CREATE TABLE appuntamenti_operai (" +
				"id serial NOT NULL, id_appuntamento integer, id_operaio integer,"+
				"PRIMARY KEY(id));";
		stmt.executeUpdate(sql);
		stmt.close();
	}
	private static void createIndisponibilita(Statement stmt) throws SQLException {
		String sql = "CREATE TABLE indisponibilita (" +
				"id integer NOT NULL, id_risorsa integer, data varchar(15), ora_inizio varchar(10), ora_fine varchar(10),"+
				"PRIMARY KEY(id));";
		stmt.executeUpdate(sql);
		stmt.close();
	}
	private static void createNote(Statement stmt) throws SQLException {
		String sql = "CREATE TABLE note (" +
				"id serial," +
				"id_appuntamento integer NOT NULL, nota varchar(60),"+
				"PRIMARY KEY(id));";
		stmt.executeUpdate(sql);
		stmt.close();
	}
	private static void createOperai(Statement stmt) throws SQLException {
		String sql = "CREATE TABLE operai (" +
				"id integer NOT NULL, nome varchar(15), cognome varchar(20), mansione varchar(30),"
				+ "stato varchar(20),"+
				"PRIMARY KEY(id));";
		stmt.executeUpdate(sql);
		stmt.close();
	}
	private static void createMacchine(Statement stmt) throws SQLException {
			String sql = "CREATE TABLE macchine (" +
					"id integer NOT NULL, nome varchar(20), stato varchar(20),"+
					"PRIMARY KEY(id));";
			stmt.executeUpdate(sql);
			stmt.close();
	}
	private static void createClienti(Statement stmt) throws SQLException {
		String sql = "CREATE TABLE clienti (" +
				"id integer NOT NULL, nome varchar(30), indirizzo varchar(30), "+
				"citta varchar(20), stato varchar(20),"+
				"PRIMARY KEY(id));";
		stmt.executeUpdate(sql);
		stmt.close();
	}
	private static void createAuto(Statement stmt) throws SQLException {
		String sql = "CREATE TABLE auto (" +
				"id integer NOT NULL, targa varchar(7), tipologia varchar(15), stato varchar(20),"+
				"PRIMARY KEY(id));";
		stmt.executeUpdate(sql);
		stmt.close();
	}
	private static void createAppuntamenti(Statement stmt) throws SQLException {
		String sql = "CREATE TABLE appuntamenti (" +
				"id integer NOT NULL, " + 
				"datetime_inizio timestamp," + 
				"datetime_fine timestamp," + 
				"id_cliente integer," + 
				"PRIMARY KEY(id));";
		stmt.executeUpdate(sql);
		stmt.close();
	}
}
