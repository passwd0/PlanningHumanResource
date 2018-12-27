package planninghumanresource.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import planninghumanresource.controller.Controller;
import planninghumanresource.controller.DBConnect;
import planninghumanresource.model.Appuntamento;
import planninghumanresource.model.Auto;
import planninghumanresource.model.Cliente;
import planninghumanresource.model.Macchina;
import planninghumanresource.model.Operaio;
import planninghumanresource.utils.Utils;

public class AppuntamentoReaderDB {
	
	public AppuntamentoReaderDB() throws ClassNotFoundException, SQLException {}
	
	public List<Appuntamento> read(Controller controller){
		return read(controller.getAllOperai(), controller.getAllAuto(), controller.getAllMacchine(), controller.getAllClienti());
	}
	
	public List<Appuntamento> read(List<Operaio> operaiList, List<Auto> autoList, List<Macchina> macchineList, List<Cliente> clientiList){
		String sql = "SELECT appuntamenti.*, operai.id, auto.id, macchine.id, note.nota\n" + 
				"FROM (((((((appuntamenti\n" + 
				"LEFT JOIN appuntamenti_operai ON appuntamenti.id = appuntamenti_operai.id_appuntamento)\n" + 
				"LEFT JOIN operai ON appuntamenti_operai.id_operaio = operai.id)\n" + 
				"LEFT JOIN appuntamenti_auto ON appuntamenti.id = appuntamenti_auto.id_appuntamento)\n" + 
				"LEFT JOIN auto ON appuntamenti_auto.id_auto = auto.id)\n" + 
				"LEFT JOIN appuntamenti_macchine ON appuntamenti.id = appuntamenti_macchine.id_appuntamento) \n" + 
				"LEFT JOIN macchine ON appuntamenti_macchine.id_macchina = macchine.id)\n" + 
				"LEFT JOIN note ON appuntamenti.id = note.id_appuntamento);";
		return read(operaiList, autoList, macchineList, clientiList, sql);
	}
	
	public List<Appuntamento> read(Controller controller, String sql){
		return read(controller.getAllOperai(), controller.getAllAuto(), controller.getAllMacchine(), controller.getAllClienti(), sql);
	}
	
	private List<Appuntamento> read(List<Operaio> operaiList, List<Auto> autoList, List<Macchina> macchineList, List<Cliente> clientiList, String sql) {
		Connection conn = null;
		try {
			conn = DBConnect.connect();
		} catch (SQLException e) {
			e.getMessage();
		}
		Set<Appuntamento> appuntamenti = new HashSet<>();
		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			Appuntamento a = null;
			Set<Integer> operai = new HashSet<>();
			Set<Integer> auto = new HashSet<>();
			Set<Integer> macchine = new HashSet<>();
			
			while ( rs.next() ) {
				int id = rs.getInt("id");
				
				String dateTimeInizioS = rs.getString("datetime_inizio");
				String dateTimeFineS = rs.getString("datetime_fine");
				int idCliente = rs.getInt("id_cliente");
				String nota = rs.getString("nota");
				LocalDateTime dateTimeInizio = LocalDateTime.parse(dateTimeInizioS, Utils.formatterCompleteDB);
				LocalDateTime dateTimeFine = LocalDateTime.parse(dateTimeFineS, Utils.formatterCompleteDB);
				Cliente cliente = null;
				for (Cliente c : clientiList)
					if (c.getId() == idCliente)
						cliente = c;
				if (a == null) {
					a = new Appuntamento(id, dateTimeInizio, dateTimeFine, cliente, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
					a.setNota(nota!=null?nota:"");
				}
				else
					if (a.getId() != id) {
						a.setOperai(operaiList.stream().filter(x -> operai.contains(x.getId())).collect(Collectors.toList()));
						a.setAuto(autoList.stream().filter(x -> auto.contains(x.getId())).collect(Collectors.toList()));
						a.setMacchine(macchineList.stream().filter(x -> macchine.contains(x.getId())).collect(Collectors.toList()));

						appuntamenti.add(a);
						operai.clear();
						auto.clear();
						macchine.clear();
						a = new Appuntamento(id, dateTimeInizio, dateTimeFine, cliente, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
						a.setNota(nota!=null?nota:"");
					}
				int idOperaio = rs.getInt(5);
				if (!rs.wasNull())
					operai.add(idOperaio);
				int idAuto = rs.getInt(6);
				if (!rs.wasNull())
					auto.add(idAuto);
				int idMacchina = rs.getInt(7);
				if (!rs.wasNull())
					macchine.add(idMacchina);
			};
			 if (a != null) {
				 a.setOperai(operaiList.stream().filter(x -> operai.contains(x.getId())).collect(Collectors.toList()));
				 a.setAuto(autoList.stream().filter(x -> auto.contains(x.getId())).collect(Collectors.toList()));
				 a.setMacchine(macchineList.stream().filter(x -> macchine.contains(x.getId())).collect(Collectors.toList()));
		
				appuntamenti.add(a);
			 }
			 rs.close();
			 stmt.close();
			 conn.close();
		} catch (SQLException e) {
			Utils.createAlertFailDB(e.getMessage());
		}
		List<Appuntamento> result = appuntamenti.stream().collect(Collectors.toList());
		Collections.sort(result);
		return result;
	}
}
