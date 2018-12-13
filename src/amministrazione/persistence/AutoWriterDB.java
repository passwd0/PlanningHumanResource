package amministrazione.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import amministrazione.controller.DBConnect;
import amministrazione.model.Auto;
import amministrazione.utils.Utils;

public class AutoWriterDB {
	public AutoWriterDB() throws ClassNotFoundException, SQLException {}
	
	public void add(Auto a, boolean exist) throws ClassNotFoundException, SQLException {
		Connection c = DBConnect.connect();
	    try {
			Statement stmt = c.createStatement();
			String sql;
			if (!exist)
				sql = "INSERT INTO auto VALUES ("+
					a.getId()+",'"+a.getTarga()+"','"+a.getTipologia()+"','"+a.getStato()+"');";
			else
				sql = "UPDATE auto SET stato = 'Disponibile' WHERE id = " + a.getId() + ";";
			stmt.executeUpdate(sql);
			
			stmt.close();
			c.commit();
			c.close();
	      } catch (Exception e) {
	    	  Utils.createAlertFailWriteDB();
	      }
	}
	
	public void delete(Auto a) throws ClassNotFoundException, SQLException {
		Connection c = DBConnect.connect();
		try {
	        Statement stmt = c.createStatement();
	    	String sql = "UPDATE auto SET stato = 'Eliminato' WHERE id = " + a.getId() + ";";
	    	stmt.executeUpdate(sql);
	    	stmt.close();
	        c.commit();
	        c.close();
		} catch (Exception e) {
			Utils.createAlertFailWriteDB();
		}
	}
}
