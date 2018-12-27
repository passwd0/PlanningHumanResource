package planninghumanresource.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import planninghumanresource.controller.DBConnect;
import planninghumanresource.model.Appuntamento;
import planninghumanresource.model.Auto;
import planninghumanresource.model.Macchina;
import planninghumanresource.model.Operaio;
import planninghumanresource.utils.Utils;

public class AppuntamentoWriterDB {
	
	public AppuntamentoWriterDB() throws ClassNotFoundException, SQLException {}
	
	public void add(Appuntamento a) throws ClassNotFoundException, SQLException {
		Connection c = DBConnect.connect();
	    try {
			Statement stmt = c.createStatement();
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO appuntamenti VALUES ("+
        			 a.getId()+",'"+
        			 a.getDataInizio().format(Utils.formatterCompleteDB)+"','"+
        			 a.getDataFine().format(Utils.formatterCompleteDB)+"'," +
        			 a.getCliente().getId()+");");
			for (Operaio o : a.getOperai())
				sql.append("INSERT INTO appuntamenti_operai (id_appuntamento, id_operaio) VALUES ("+a.getId()+","+o.getId()+");");
			for (Auto o : a.getAuto())
				sql.append("INSERT INTO appuntamenti_auto (id_appuntamento, id_auto) VALUES ("+a.getId()+","+o.getId()+");");
			for (Macchina o : a.getMacchine())
				sql.append("INSERT INTO appuntamenti_macchine (id_appuntamento, id_macchina) VALUES ("+a.getId()+","+o.getId()+");");
			if (!a.getNota().isEmpty())
				sql.append("INSERT INTO note (id_appuntamento, nota) VALUES (" + a.getId()+",'"+a.getNota()+"');");
			stmt.executeUpdate(sql.toString());
			
			stmt.close();
			c.commit();
			c.close();
	      } catch (Exception e) {
	    	  Utils.createAlertFailDB(e.getLocalizedMessage());
	      }
	}
	
	public void delete(Appuntamento a) throws ClassNotFoundException, SQLException {
		Connection c = DBConnect.connect();
		try {
	        Statement stmt = c.createStatement();
	        StringBuilder sql = new StringBuilder();
	    	sql.append("DELETE FROM appuntamenti WHERE id = " + a.getId() + ";");
	    	sql.append("DELETE FROM appuntamenti_operai WHERE id_appuntamento = " + a.getId() + ";");
	    	sql.append("DELETE FROM appuntamenti_auto WHERE id_appuntamento = " + a.getId() + ";");
	    	sql.append("DELETE FROM appuntamenti_macchine WHERE id_appuntamento = " + a.getId() + ";");
	    	sql.append("DELETE FROM note WHERE id_appuntamento = " + a.getId());
	    	stmt.executeUpdate(sql.toString());
	    	stmt.close();
	        c.commit();
	        c.close();
		} catch (Exception e) {
			Utils.createAlertFailWriteDB();
		}
	}
}
