package planninghumanresource.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import planninghumanresource.controller.DBConnect;
import planninghumanresource.model.Operaio;
import planninghumanresource.utils.Utils;

public class OperaioWriterDB {

	public OperaioWriterDB() throws ClassNotFoundException, SQLException {}
	
	public void add(Operaio a, boolean exist) throws ClassNotFoundException, SQLException {
		Connection c = DBConnect.connect();
	    try {
			Statement stmt = c.createStatement();
			String sql;
			if (!exist)
				sql = "INSERT INTO operai VALUES ("+
	        			 a.getId()+",'"+a.getNome()+"','"+a.getCognome()+"','"+a.getMansione()+"','"+a.getStato()+"');";
			else
				sql = "UPDATE operai SET stato = 'Disponibile' WHERE id = " + a.getId() + ";";
			stmt.executeUpdate(sql);
			
			stmt.close();
			c.commit();
			c.close();
	      } catch (Exception e) {
	    	  Utils.createAlertFailWriteDB();
	      }
	}
	
	public void delete(Operaio a) throws ClassNotFoundException, SQLException {
		Connection c = DBConnect.connect();
		try {
	        Statement stmt = c.createStatement();
	    	String sql = "UPDATE operai SET stato = 'Eliminato' WHERE id = " + a.getId() + ";";
	    	stmt.executeUpdate(sql);
	    	stmt.close();
	        c.commit();
	        c.close();
		} catch (Exception e) {
			Utils.createAlertFailWriteDB();
	      }
	}
}
