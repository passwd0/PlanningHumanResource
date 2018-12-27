package planninghumanresource.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import planninghumanresource.controller.DBConnect;
import planninghumanresource.model.Cliente;
import planninghumanresource.utils.Utils;

public class ClienteWriterDB {
	public ClienteWriterDB() throws ClassNotFoundException, SQLException {}
	
	public void add(Cliente c, boolean exist) throws ClassNotFoundException, SQLException {
		Connection conn = DBConnect.connect();
	    try {
			Statement stmt = conn.createStatement();
			String sql;
			if (!exist)
				sql = "INSERT INTO clienti VALUES ("+
					c.getId()+",'"+c.getNome()+"','"+c.getCitta()+"','"+c.getIndirizzo()+"','"+c.getStato()+"');";
			else
				sql = "UPDATE clienti SET stato = 'Disponibile' WHERE id = " + c.getId() + ";";
			stmt.executeUpdate(sql);
			
			stmt.close();
			conn.commit();
			conn.close();
	      } catch (Exception e) {
	    	  Utils.createAlertFailWriteDB();	      }
	}
	
	public void delete(Cliente c) throws ClassNotFoundException, SQLException {
		Connection conn = DBConnect.connect();
		try {
	        Statement stmt = conn.createStatement();
	    	String sql = "UPDATE clienti SET stato = 'Eliminato' WHERE id = " + c.getId() + ";";
	    	stmt.executeUpdate(sql);
	    	stmt.close();
	        conn.commit();
	        conn.close();
		} catch (Exception e) {
			Utils.createAlertFailWriteDB();	      }
	}
}
