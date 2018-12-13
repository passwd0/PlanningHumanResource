package amministrazione.persistence;

import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import amministrazione.model.Auto;
import amministrazione.model.Stato;

public class AutoReader {
	private ArrayList<Auto> automezzi;
	private Reader reader;
	
	public AutoReader(Reader reader) {
		this.reader = reader;
		if (reader == null)
			throw new IllegalArgumentException("reader null");
		automezzi = new ArrayList<>();
	}
	
	public void read() {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
	        c = DriverManager
	            .getConnection("jdbc:postgresql://localhost:5432/amministrazione",
	            "luminem", "ciccio_1996");
	         c.setAutoCommit(false);
	         System.out.println("Opened database successfully");
	
	         stmt = c.createStatement();
	         ResultSet rs = stmt.executeQuery( "SELECT * FROM auto;" );
	         while ( rs.next() ) {
	        	int id = rs.getInt("id");
	            String  targa = rs.getString("nome");
	            String tipologia  = rs.getString("cognome");
	            String statoS = rs.getString("stato");
	            Stato stato = Stato.valueOf(statoS);
	            automezzi.add(new Auto(id, targa, tipologia, stato));
	         }
	         rs.close();
	         stmt.close();
	         c.close();
      } catch ( Exception e ) {
         System.err.println( e.getClass().getName()+": "+ e.getMessage() );
         System.exit(0);
      }
	}
	
	public List<Auto> getAutomezzi(){
		return automezzi;
	}
}
