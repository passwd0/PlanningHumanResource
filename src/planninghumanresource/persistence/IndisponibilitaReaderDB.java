package planninghumanresource.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import planninghumanresource.controller.DBConnect;
import planninghumanresource.model.HasState;
import planninghumanresource.model.Indisponibilita;
import planninghumanresource.utils.Utils;

public class IndisponibilitaReaderDB {
private Connection c;
	
	public IndisponibilitaReaderDB() throws ClassNotFoundException, SQLException {
		c = DBConnect.connect();
	}
	
	public <T> void read(List<? extends HasState> list) {
		Statement stmt;
		try {
			stmt = c.createStatement();
			
			for (HasState o : list) {
				ResultSet rs = stmt.executeQuery("select * from indisponibilita WHERE id_risorsa = " + o.getId() + ";");
				while (rs.next()) {
					o.addIndisponibilita(get(rs));
				}
				rs.close();
			}
		     stmt.close();
		     c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private Indisponibilita get(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		String dataS = rs.getString("data");
		String oraInizioS = rs.getString("ora_inizio");
		String oraFineS = rs.getString("ora_fine");
		LocalDate data = LocalDate.parse(dataS, Utils.formatterData);
		LocalTime oraInizio = LocalTime.parse(oraInizioS, Utils.formatterOra);
		LocalTime oraFine = LocalTime.parse(oraFineS, Utils.formatterOra);
		return new Indisponibilita(id, data, oraInizio, oraFine);
	}
	
	public List<Integer> readId() {
		List<Integer> ids = new ArrayList<>();
		Statement stmt;
		try {
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT id FROM indisponibilita;" );
	         while ( rs.next() ) {
	        	int id = rs.getInt("id");
	            ids.add(id);
	         }
            rs.close();
		     stmt.close();
		     c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ids;
	}
}
