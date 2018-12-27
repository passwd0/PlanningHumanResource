package planninghumanresource.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import planninghumanresource.controller.DBConnect;
import planninghumanresource.model.Cliente;
import planninghumanresource.model.Stato;


public class ClienteReaderDB {
	private Connection c;
	
	public ClienteReaderDB() throws ClassNotFoundException, SQLException {
		c = DBConnect.connect();
	}
	
	public List<Cliente> read() {
		List<Cliente> clienti = new ArrayList<>();
		Statement stmt;
		try {
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM clienti;" );
	         while ( rs.next() ) {
	        	int id = rs.getInt("id");
	            String  nome = rs.getString("nome");
	            String citta  = rs.getString("citta");
	            String  indirizzo = rs.getString("indirizzo");
	            String statoS = rs.getString("stato");
	            Stato stato = Stato.valueOf(statoS);
	            clienti.add(new Cliente(id, nome, citta, indirizzo, stato));
	         }
		     rs.close();
		     stmt.close();
		     c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return clienti;
	}
}
