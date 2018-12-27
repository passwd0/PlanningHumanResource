package planninghumanresource.persistence;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import planninghumanresource.model.Appuntamento;
import planninghumanresource.model.Auto;
import planninghumanresource.model.Cliente;
import planninghumanresource.model.Macchina;
import planninghumanresource.model.Operaio;
import planninghumanresource.model.Setting;

public class DataManager {
	private Setting setting;
	
	private List<Appuntamento> appuntamenti;
	private List<Cliente> clienti;
	private List<Operaio> operai;
	private List<Auto> auto;
	private List<Macchina> macchine;
	
	public DataManager(Setting setting) throws IOException, ClassNotFoundException, SQLException {
		this.setting = setting;
		
		clienti = loadClienti();
		operai = loadOperai();
		auto = loadAuto();
		macchine = loadMacchine();
		loadDisponibilita();
		appuntamenti = loadAppuntamenti();
	}
	public List<Cliente> loadClienti() throws ClassNotFoundException, SQLException {
		return new ClienteReaderDB().read();
	}
	
	public List<Auto> loadAuto() throws ClassNotFoundException, SQLException {
		return new AutoReaderDB().read();
	}
	
	public List<Macchina> loadMacchine() throws ClassNotFoundException, SQLException{
		return new MacchinaReaderDB().read();
	}
	
	public List<Operaio> loadOperai() throws ClassNotFoundException, SQLException{
		return new OperaioReaderDB().read();
	}
	
	public void add(Object o, boolean exist) throws ClassNotFoundException, SQLException {
		if (o instanceof Cliente)
			new ClienteWriterDB().add((Cliente) o, exist);
		if (o instanceof Operaio)
			new OperaioWriterDB().add((Operaio) o, exist);
		if (o instanceof Auto)
			new AutoWriterDB().add((Auto) o, exist);
		if (o instanceof Macchina)
			new MacchinaWriterDB().add((Macchina) o, exist);
	}
	
	public List<Appuntamento> loadAppuntamenti() throws ClassNotFoundException, SQLException {
		List<Appuntamento> appuntamenti = new AppuntamentoReaderDB().read(operai, auto, macchine, clienti);
		Collections.sort(appuntamenti);
		return appuntamenti;
	}
	
	public List<Appuntamento> loadAppuntamenti(File f) throws IOException {
		if (f == null)
			throw new IOException();
		AppuntamentoReader reader = new AppuntamentoReader(new FileReader(f));
		return reader.read(this);
	}
	
	private void loadDisponibilita() throws ClassNotFoundException, SQLException {
		new IndisponibilitaReaderDB().read(getOperai());
		new IndisponibilitaReaderDB().read(getAuto());
		new IndisponibilitaReaderDB().read(getMacchine());
		new IndisponibilitaReaderDB().read(getClienti());
	}
	
	public void saveAppuntamenti(List<Appuntamento> appuntamenti) throws IOException{
		new AppuntamentoWriter().writer(new FileWriter(setting.getFileAppuntamento()), appuntamenti);
	}
	
	public void saveAppuntamenti(File writer, List<Appuntamento> appuntamenti) throws IOException{
		new AppuntamentoWriter().writer(new FileWriter(writer), appuntamenti);
	}
	
	public void saveAppuntamenti(File file) throws IOException{
		new AppuntamentoWriter().writer(new FileWriter(file), appuntamenti);
	}
	
	public void saveAppuntamenti() throws IOException{
		Writer writer = new FileWriter(setting.getFileAppuntamento());
		new AppuntamentoWriter().writer(writer, appuntamenti);
	}
	
	public void saveSetting(Setting setting) throws IOException {
		Writer writer = new FileWriter("setting.txt");
		new SettingWriter().writer(writer, setting);
	}
	
	public void saveSetting() throws IOException {
		Writer writer = new FileWriter("setting.txt");
		new SettingWriter().writer(writer, setting);
	}
	
	public void saveReport(File file, List<Appuntamento> appuntamenti) throws IOException{
		Writer writer = new FileWriter(file);
		new ReportWriter().writer(writer, appuntamenti);
	}
	
	public List<Appuntamento> getAppuntamenti(){
		return appuntamenti;
	}
	
	public List<Cliente> getClienti(){
		return clienti;
	}
	
	public List<Operaio> getOperai(){
		return operai;
	}
	
	public List<Auto> getAuto(){
		return auto;
	}

	public List<Macchina> getMacchine(){
		return macchine;
	}
}
