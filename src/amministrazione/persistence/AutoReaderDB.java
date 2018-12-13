package amministrazione.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import amministrazione.controller.DBConnect;
import amministrazione.model.Auto;
import amministrazione.model.Stato;

public class AutoReaderDB {
	private Connection c;
	
	public AutoReaderDB() throws ClassNotFoundException, SQLException {
		c = DBConnect.connect();
	}
	
	public List<Auto> read() {
		List<Auto> automezzi = new ArrayList<>();
		Statement stmt;
		try {
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM auto;" );
		 while ( rs.next() ) {
			int id = rs.getInt("id");
		    String  targa = rs.getString("targa");
		    String tipologia  = rs.getString("tipologia");
		    String statoS = rs.getString("stato");
		        Stato stato = Stato.valueOf(statoS);
		        automezzi.add(new Auto(id, targa, tipologia, stato));
		     }
		     rs.close();
		     stmt.close();
		     c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return automezzi;
	}
}
