package amministrazione.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import amministrazione.controller.DBConnect;
import amministrazione.model.Operaio;
import amministrazione.model.Stato;

public class OperaioReaderDB {
	private Connection c;
	
	public OperaioReaderDB() throws ClassNotFoundException, SQLException {
		c = DBConnect.connect();
	}
	
	public List<Operaio> read() {
		List<Operaio> operai = new ArrayList<>();
		Statement stmt;
		try {
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM operai;" );
	         while ( rs.next() ) {
	        	int id_risorsa = rs.getInt("id");
	            String  nome = rs.getString("nome");
	            String cognome  = rs.getString("cognome");
	            String  mansione = rs.getString("mansione");
	            String statoS = rs.getString("stato");
	            Stato stato = Stato.valueOf(statoS);
	            operai.add(new Operaio(id_risorsa, nome, cognome, mansione, stato));
	         }
		     rs.close();
		     stmt.close();
		     c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return operai;
	}
}