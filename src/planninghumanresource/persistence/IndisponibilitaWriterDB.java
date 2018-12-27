package planninghumanresource.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import planninghumanresource.controller.DBConnect;
import planninghumanresource.model.Indisponibilita;
import planninghumanresource.utils.Utils;

public class IndisponibilitaWriterDB {
	
	public IndisponibilitaWriterDB() throws ClassNotFoundException, SQLException {}
	
	public void add(Indisponibilita a, int idRisorsa, boolean exist) throws ClassNotFoundException, SQLException {
		Connection c = DBConnect.connect();
	    try {
			Statement stmt = c.createStatement();
			String sql = "";
			if (!exist)
				sql = "INSERT INTO indisponibilita VALUES ("+
	        			 a.getId()+","+idRisorsa+",'"+a.getDataString()+"','"+
	        			 a.getOraInizio().format(Utils.formatterOra)+"','"+
	        			 a.getOraFine().format(Utils.formatterOra)+"');";
			else
				sql = "UPDATE indisponibilita "
						+ "SET data = '"+a.getDataString()+ "', ora_inizio = '"+a.getOraInizio().format(Utils.formatterOra)+
	        			 "', ora_fine = '"+a.getOraFine().format(Utils.formatterOra)+"' WHERE id = " + a.getId() + ";";
			stmt.executeUpdate(sql);
			
			stmt.close();
			c.commit();
			c.close();
	      } catch (Exception e) {
	    	  Utils.createAlertFailWriteDB();
	      }
	}
	
	public void delete(Indisponibilita i) throws ClassNotFoundException, SQLException {
		Connection c = DBConnect.connect();
		try {
	        Statement stmt = c.createStatement();
	    	String sql = "DELETE FROM indisponibilita WHERE id = " + i.getId() + ";";
	    	stmt.executeUpdate(sql);
	    	stmt.close();
	        c.commit();
	        c.close();
		} catch (Exception e) {
			Utils.createAlertFailWriteDB();
		}
	}
}
