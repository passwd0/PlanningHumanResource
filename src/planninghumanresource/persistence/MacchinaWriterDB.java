package planninghumanresource.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import planninghumanresource.controller.DBConnect;
import planninghumanresource.model.Macchina;
import planninghumanresource.utils.Utils;

public class MacchinaWriterDB {

	public MacchinaWriterDB() throws ClassNotFoundException, SQLException {}
	
	public void add(Macchina a, boolean exist) throws ClassNotFoundException, SQLException {
		Connection c = DBConnect.connect();
	    try {
			Statement stmt = c.createStatement();
			String sql;
			if (!exist)
				sql = "INSERT INTO macchine VALUES ("+
	        			 a.getId()+",'"+a.getNome()+"','"+a.getStato()+"');";
			else
				sql = "UPDATE macchine SET stato = 'Disponibile' WHERE id = " + a.getId() + ";";
			stmt.executeUpdate(sql);
			
			stmt.close();
			c.commit();
			c.close();
	      } catch (Exception e) {
	    	  Utils.createAlertFailWriteDB();
	      }
	}
	
	public void delete(Macchina a) throws ClassNotFoundException, SQLException {
		Connection c = DBConnect.connect();
		try {
	        Statement stmt = c.createStatement();
	    	String sql = "UPDATE macchine SET stato = 'Eliminato' WHERE id = " + a.getId() + ";";
	    	stmt.executeUpdate(sql);
	    	stmt.close();
	        c.commit();
	        c.close();
		} catch (Exception e) {
			Utils.createAlertFailWriteDB();
	      }
	}
}
