package amministrazione.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import amministrazione.controller.DBConnect;
import amministrazione.model.Macchina;
import amministrazione.model.Stato;

public class MacchinaReaderDB {
	private Connection c;
	
	public MacchinaReaderDB() throws ClassNotFoundException, SQLException {
		c = DBConnect.connect();
	}
	
	public List<Macchina> read() {
		List<Macchina> macchine = new ArrayList<>();
		Statement stmt;
		try {
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM macchine;" );
	         while ( rs.next() ) {
	        	int id = rs.getInt("id");
	            String  nome = rs.getString("nome");
	            String statoS = rs.getString("stato");
	            Stato stato = Stato.valueOf(statoS);
	            macchine.add(new Macchina(id, nome, stato));
	         }
		     rs.close();
		     stmt.close();
		     c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return macchine;
	}
}
