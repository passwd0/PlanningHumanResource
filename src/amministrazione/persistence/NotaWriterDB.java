package amministrazione.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import amministrazione.controller.DBConnect;
import amministrazione.model.Appuntamento;
import amministrazione.utils.Utils;

public class NotaWriterDB {

	public NotaWriterDB() throws ClassNotFoundException, SQLException {}
	
	public void add(Appuntamento a) throws ClassNotFoundException, SQLException {
		Connection c = DBConnect.connect();
	    try {
			Statement stmt = c.createStatement();
			StringBuilder sql = new StringBuilder();
			sql.append("DELETE FROM note WHERE id_appuntamento = " + a.getId() + ";");
			sql.append("INSERT INTO note (id_appuntamento, nota) VALUES ("+a.getId()+",'"+a.getNota()+"');");
			stmt.executeUpdate(sql.toString());
			
			stmt.close();
			c.commit();
			c.close();
	      } catch (Exception e) {
	    	  Utils.createAlertFailWriteDB();
	      }
	}
	
	public void delete(Appuntamento a) throws ClassNotFoundException, SQLException {
		Connection c = DBConnect.connect();
	    try {
			Statement stmt = c.createStatement();
			StringBuilder sql = new StringBuilder();
			sql.append("DELETE FROM note WHERE id_appuntamento = " + a.getId() + ";");
			stmt.executeUpdate(sql.toString());
			
			stmt.close();
			c.commit();
			c.close();
	      } catch (Exception e) {
	    	  Utils.createAlertFailWriteDB();
	      }
	}
}
