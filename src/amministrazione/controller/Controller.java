package amministrazione.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import amministrazione.model.Appuntamento;
import amministrazione.model.Auto;
import amministrazione.model.Cliente;
import amministrazione.model.Indisponibilita;
import amministrazione.model.Macchina;
import amministrazione.model.Operaio;
import amministrazione.model.Setting;
import amministrazione.model.Stato;
import amministrazione.persistence.AppuntamentoReaderDB;
import amministrazione.persistence.AppuntamentoWriter;
import amministrazione.persistence.AppuntamentoWriterDB;
import amministrazione.persistence.AutoWriterDB;
import amministrazione.persistence.ClienteWriterDB;
import amministrazione.persistence.DataManager;
import amministrazione.persistence.IndisponibilitaReaderDB;
import amministrazione.persistence.IndisponibilitaWriterDB;
import amministrazione.persistence.MacchinaWriterDB;
import amministrazione.persistence.NotaWriterDB;
import amministrazione.persistence.OperaioWriterDB;
import amministrazione.persistence.ReportWriter;
import amministrazione.persistence.SettingWriter;
import amministrazione.ui.AppuntamentoColor;
import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Controller {
	private List<Appuntamento> appuntamenti;
	private List<Operaio> operai;
	private List<Macchina> macchine;
	private List<Auto> automezzi;
	private List<Cliente> clienti;
	private Setting setting;
	private HostServices hostServices;
	private DataManager dataManager;
	
	public Controller(Setting setting) throws IOException, ClassNotFoundException, SQLException {
		dataManager = new DataManager(setting);
		this.setting = setting;
		
		this.clienti = dataManager.getClienti();
		this.operai = dataManager.getOperai();
		this.automezzi = dataManager.getAuto();
		this.macchine = dataManager.getMacchine();
		
		this.appuntamenti = dataManager.loadAppuntamenti();

		/*LocalDate data = LocalDate.of(2017, 9, 1);
		for (int i = 0; i < 100; i++) {
			addAppuntamento(
					data.plusDays(new Random().nextInt(25)).atStartOfDay().plusHours(new Random().nextInt(21)), 
					data.plusDays(new Random().nextInt(25)).atStartOfDay().plusHours(new Random().nextInt(21)),
					clienti.get(new Random().nextInt(clienti.size())), 
					FXCollections.observableArrayList(operai.get(new Random().nextInt(operai.size()))),
					FXCollections.observableArrayList(automezzi.get(new Random().nextInt(automezzi.size()))), 
					FXCollections.observableArrayList(macchine.get(new Random().nextInt(macchine.size()))));
		}*/
	}
	
	public HostServices getHostServices() {
		return hostServices;
	}
	
	public void setHostServices(HostServices hostServices) {
		this.hostServices = hostServices;
	}
	
	public int getScaglionamentoOra() {
		return setting.getScaglionamento();
	}

	public LocalTime getInizioLavoro() {
		return setting.getInizioLavoro();
	}

	public LocalTime getFineLavoro() {
		return setting.getFineLavoro();
	}
	
	public File getFileAppuntamento() {
		return setting.getFileAppuntamento();
	}
	
	public void setAppuntamenti(List<Appuntamento> appuntamenti) {
		this.appuntamenti = appuntamenti;
	}
	
	public void setAlert(String name, boolean state) {
		setting.setAlert(name, state);
	}
	
	public Map<String, Boolean> getAlert(){
		return setting.getAlert();
	}
	
	public Setting getSetting() {
		return setting;
	}
	
	public void setFileAppuntamenti(File file) {
		setting.setFileAppuntamento(file);
	}
	
	public List<Operaio> loadOperai() throws ClassNotFoundException, SQLException{
		operai = dataManager.loadOperai();
		return getOperai();
	}
	
	public List<Auto> loadAuto() throws ClassNotFoundException, SQLException{
		automezzi = dataManager.loadAuto();
		return getAuto();
	}
	
	public List<Macchina> loadMacchine() throws ClassNotFoundException, SQLException{
		macchine = dataManager.loadMacchine();
		return getMacchine();
	}
	
	public List<Cliente> loadClienti() throws ClassNotFoundException, SQLException{
		clienti = dataManager.loadClienti();
		return getClienti();
	}

	public List<Cliente> getClienti() {
		List<Cliente> clienti = new ArrayList<>();
		for (Cliente c : this.clienti)
			if (c.getStato() != Stato.Eliminato)
				clienti.add(c);
		Collections.sort(clienti);
		return clienti;
	}
	
	public List<Auto> getAuto(){
		List<Auto> automezzi = new ArrayList<>();
		for (Auto a : this.automezzi)
			if (a.getStato() != Stato.Eliminato)
				automezzi.add(a);
		Collections.sort(automezzi);
		return automezzi;
	}
	
	public List<Operaio> getOperai(){
		List<Operaio> operai = new ArrayList<>();
		for (Operaio o : this.operai)
			if (o.getStato() != Stato.Eliminato)
				operai.add(o);
		Collections.sort(operai);
		return operai;
	}
	
	public List<Macchina> getMacchine(){
		List<Macchina> macchine = new ArrayList<>();
		for (Macchina m : this.macchine)
			if (m.getStato() != Stato.Eliminato)
				macchine.add(m);
		Collections.sort(macchine);
		return macchine;
	}
	
	public List<Operaio> getAllOperai(){
		return operai;
	}
	
	public List<Auto> getAllAuto(){
		return automezzi;
	}
	
	public List<Macchina> getAllMacchine(){
		return macchine;
	}
	
	public List<Cliente> getAllClienti(){
		return clienti;
	}
	/*public ObservableList<Appuntamento> getAppuntamentiGiorno(LocalDate date){
		List<Appuntamento> appuntamentiFiltred = new ArrayList<>();
		for (Appuntamento appuntamento : appuntamenti) {
			if (date.isEqual(appuntamento.getDataInizio().toLocalDate()))
				appuntamentiFiltred.add(appuntamento);
		}
		return FXCollections.observableArrayList(appuntamentiFiltred);
	}*/
	
	public ObservableList<Appuntamento> getAppuntamentiGiorno(LocalDate date) throws ClassNotFoundException, SQLException{
		String sql = "SELECT appuntamenti.*, operai.id, auto.id, macchine.id, note.nota\n" + 
				"FROM (((((((appuntamenti\n" + 
				"LEFT JOIN appuntamenti_operai ON appuntamenti.id = appuntamenti_operai.id_appuntamento)\n" + 
				"LEFT JOIN operai ON appuntamenti_operai.id_operaio = operai.id)\n" + 
				"LEFT JOIN appuntamenti_auto ON appuntamenti.id = appuntamenti_auto.id_appuntamento)\n" + 
				"LEFT JOIN auto ON appuntamenti_auto.id_auto = auto.id)\n" + 
				"LEFT JOIN appuntamenti_macchine ON appuntamenti.id = appuntamenti_macchine.id_appuntamento) \n" + 
				"LEFT JOIN macchine ON appuntamenti_macchine.id_macchina = macchine.id)\n" + 
				"LEFT JOIN note ON appuntamenti.id = note.id_appuntamento)\n" + 
				"WHERE '"+ date.atStartOfDay()+"' <= datetime_inizio AND '"+date.atStartOfDay().plusDays(1)+"' > datetime_inizio";
		return FXCollections.observableArrayList(new AppuntamentoReaderDB().read(this, sql));
	}
	
	/*public List<AppuntamentoColor> getAppuntamentiSettimanaColor(LocalDate date){
		LocalDate inizioSettimana = date.minusDays(date.getDayOfWeek().getValue() - 1);
		List<AppuntamentoColor> appuntamentiFiltred = new ArrayList<>();
		for (Appuntamento appuntamento : appuntamenti) {
			if ((!appuntamento.getDataInizio().toLocalDate().isBefore(inizioSettimana) && appuntamento.getDataInizio().toLocalDate().isBefore(inizioSettimana.plusWeeks(1))))
				appuntamentiFiltred.add(new AppuntamentoColor(appuntamento));
		}
		return appuntamentiFiltred;
	}*/
	
	public ObservableList<AppuntamentoColor> getAppuntamentiSettimanaColor(LocalDate date) throws ClassNotFoundException, SQLException {
		List<AppuntamentoColor> appuntamentiFiltred = new ArrayList<>();
		LocalDate inizioSettimana = date.minusDays(date.getDayOfWeek().getValue() - 1);
		String sql = "SELECT appuntamenti.*, operai.id, auto.id, macchine.id, note.nota\n" + 
				"FROM (((((((appuntamenti\n" + 
				"LEFT JOIN appuntamenti_operai ON appuntamenti.id = appuntamenti_operai.id_appuntamento)\n" + 
				"LEFT JOIN operai ON appuntamenti_operai.id_operaio = operai.id)\n" + 
				"LEFT JOIN appuntamenti_auto ON appuntamenti.id = appuntamenti_auto.id_appuntamento)\n" + 
				"LEFT JOIN auto ON appuntamenti_auto.id_auto = auto.id)\n" + 
				"LEFT JOIN appuntamenti_macchine ON appuntamenti.id = appuntamenti_macchine.id_appuntamento) \n" + 
				"LEFT JOIN macchine ON appuntamenti_macchine.id_macchina = macchine.id)\n" + 
				"LEFT JOIN note ON appuntamenti.id = note.id_appuntamento)\n" + 
				"WHERE '"+ inizioSettimana.atStartOfDay()+"' <= datetime_inizio AND '"+inizioSettimana.atStartOfDay().plusWeeks(1)+"' > datetime_inizio";
		for (Appuntamento appuntamento : new AppuntamentoReaderDB().read(this, sql)) {
			if ((!appuntamento.getDataInizio().toLocalDate().isBefore(inizioSettimana) && appuntamento.getDataInizio().toLocalDate().isBefore(inizioSettimana.plusWeeks(1))))
				appuntamentiFiltred.add(new AppuntamentoColor(appuntamento));
		}
		return FXCollections.observableArrayList(appuntamentiFiltred);
	}
	
	/*public List<Appuntamento> getAppuntamentiSettimana(LocalDate date){
		LocalDate inizioSettimana = date.minusDays(date.getDayOfWeek().getValue() - 1);
		List<Appuntamento> appuntamentiFiltred = new ArrayList<>();
		for (Appuntamento appuntamento : appuntamenti) {
			if ((!appuntamento.getDataInizio().toLocalDate().isBefore(inizioSettimana) && appuntamento.getDataInizio().toLocalDate().isBefore(inizioSettimana.plusWeeks(1))))
				appuntamentiFiltred.add(appuntamento);
		}
		return appuntamentiFiltred;
	}*/
	public List<Appuntamento> getAppuntamentiSettimana(LocalDate date) throws ClassNotFoundException, SQLException{
		LocalDate inizioSettimana = date.minusDays(date.getDayOfWeek().getValue() - 1);
		String sql = "SELECT appuntamenti.*, operai.id, auto.id, macchine.id, note.nota\n" + 
				"FROM (((((((appuntamenti\n" + 
				"LEFT JOIN appuntamenti_operai ON appuntamenti.id = appuntamenti_operai.id_appuntamento)\n" + 
				"LEFT JOIN operai ON appuntamenti_operai.id_operaio = operai.id)\n" + 
				"LEFT JOIN appuntamenti_auto ON appuntamenti.id = appuntamenti_auto.id_appuntamento)\n" + 
				"LEFT JOIN auto ON appuntamenti_auto.id_auto = auto.id)\n" + 
				"LEFT JOIN appuntamenti_macchine ON appuntamenti.id = appuntamenti_macchine.id_appuntamento) \n" + 
				"LEFT JOIN macchine ON appuntamenti_macchine.id_macchina = macchine.id)\n" + 
				"LEFT JOIN note ON appuntamenti.id = note.id_appuntamento)\n" + 
				"WHERE '"+ inizioSettimana.atStartOfDay()+"' <= datetime_inizio AND '"+inizioSettimana.atStartOfDay().plusWeeks(1)+"' > datetime_inizio";
		return new AppuntamentoReaderDB().read(this, sql);
	}
	
	/*public List<Appuntamento> getAppuntamentiMensile(LocalDate date){
		LocalDate inizioMese = date.minusDays(date.getDayOfMonth() - 1);
		List<Appuntamento> appuntamentiFiltred = new ArrayList<>();
		for (Appuntamento appuntamento : appuntamenti) {
			if ((!appuntamento.getDataInizio().toLocalDate().isBefore(inizioMese) && 
					appuntamento.getDataInizio().toLocalDate().isBefore(inizioMese.plusMonths(1))))
				appuntamentiFiltred.add(appuntamento);
		}
		return appuntamentiFiltred;
	}*/
	
	public List<Appuntamento> getAppuntamentiMensile(LocalDate date) throws ClassNotFoundException, SQLException{
		LocalDate inizioMese = date.minusDays(date.getDayOfMonth() - 1);
		String sql = "SELECT appuntamenti.*, operai.id, auto.id, macchine.id, note.nota\n" + 
				"FROM (((((((appuntamenti\n" + 
				"LEFT JOIN appuntamenti_operai ON appuntamenti.id = appuntamenti_operai.id_appuntamento)\n" + 
				"LEFT JOIN operai ON appuntamenti_operai.id_operaio = operai.id)\n" + 
				"LEFT JOIN appuntamenti_auto ON appuntamenti.id = appuntamenti_auto.id_appuntamento)\n" + 
				"LEFT JOIN auto ON appuntamenti_auto.id_auto = auto.id)\n" + 
				"LEFT JOIN appuntamenti_macchine ON appuntamenti.id = appuntamenti_macchine.id_appuntamento) \n" + 
				"LEFT JOIN macchine ON appuntamenti_macchine.id_macchina = macchine.id)\n" + 
				"LEFT JOIN note ON appuntamenti.id = note.id_appuntamento)\n" + 
				"WHERE '"+ inizioMese.atStartOfDay()+"' <= datetime_inizio AND '"+inizioMese.atStartOfDay().plusMonths(1)+"' > datetime_inizio";
		return new AppuntamentoReaderDB().read(this, sql);
	}
	
	public List<Appuntamento> getAppuntamentiMensile(LocalDate date, Cliente cliente) throws ClassNotFoundException, SQLException{
		LocalDate inizioMese = date.minusDays(date.getDayOfMonth() - 1);
		String sql = "SELECT appuntamenti.*, operai.id, auto.id, macchine.id, note.nota\n" + 
				"FROM (((((((appuntamenti\n" + 
				"LEFT JOIN appuntamenti_operai ON appuntamenti.id = appuntamenti_operai.id_appuntamento)\n" + 
				"LEFT JOIN operai ON appuntamenti_operai.id_operaio = operai.id)\n" + 
				"LEFT JOIN appuntamenti_auto ON appuntamenti.id = appuntamenti_auto.id_appuntamento)\n" + 
				"LEFT JOIN auto ON appuntamenti_auto.id_auto = auto.id)\n" + 
				"LEFT JOIN appuntamenti_macchine ON appuntamenti.id = appuntamenti_macchine.id_appuntamento) \n" + 
				"LEFT JOIN macchine ON appuntamenti_macchine.id_macchina = macchine.id)\n" + 
				"LEFT JOIN note ON appuntamenti.id = note.id_appuntamento)\n" + 
				"WHERE id_cliente = "+cliente.getId()+" AND '"+inizioMese.atStartOfDay()+"' <= datetime_inizio AND '"+inizioMese.atStartOfDay().plusMonths(1)+"' > datetime_inizio";
		return new AppuntamentoReaderDB().read(this, sql);
	}
	
	public <T> List<Appuntamento> getAppuntamentoRisorsa(T object){
		List<Appuntamento> result = new ArrayList<>();
			for (Appuntamento a : appuntamenti) {
				if (object instanceof Operaio) {
					if (a.getOperai().contains(object))
						result.add(a);
				}
				if (object instanceof Macchina) {
					if (a.getMacchine().contains(object))
						result.add(a);
				}
				if (object instanceof Auto) {
					if (a.getAuto().contains(object))
						result.add(a);
				}
				if (object instanceof Cliente) {
					if (a.getCliente().equals(object))
						result.add(a);
				}
		}
		return result;
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
	
	public Indisponibilita createIndisponibilita(LocalDate data, LocalTime oraInizio, LocalTime oraFine) throws ClassNotFoundException, SQLException {
		List<Integer> ids = new IndisponibilitaReaderDB().readId();
		int id = 0;
		do {
			id = new Random().nextInt();
		}while (ids.contains(id));
		return new Indisponibilita(id, data, oraInizio, oraFine);
	}
	
	public void addIndisponibilita(List<Indisponibilita> listIndisponibilita, int idRisorsa) throws ClassNotFoundException, SQLException {
		for (Indisponibilita i : listIndisponibilita)
			addIndisponibilita(i, idRisorsa);
	}
	
	public void addIndisponibilita(Indisponibilita i, int idRisorsa) throws ClassNotFoundException, SQLException {
		boolean exist = new IndisponibilitaReaderDB().readId().stream().anyMatch(x -> x.intValue() == i.getId());
		new IndisponibilitaWriterDB().add(i, idRisorsa, exist);
	}
	
	public void removeIndisponibilita(Indisponibilita i) throws ClassNotFoundException, SQLException {
		new IndisponibilitaWriterDB().delete(i);
	}
	
	public void addNota(Appuntamento appuntamento, String nota) throws ClassNotFoundException, SQLException {
		appuntamento.setNota(nota);
		if (!nota.isEmpty())
			new NotaWriterDB().add(appuntamento);
		else
			new NotaWriterDB().delete(appuntamento);
	}
	
	public List<Appuntamento> loadAppuntamenti(File f) throws IOException {
		return dataManager.loadAppuntamenti(f);
	}
	
	public void addAppuntamento(Appuntamento appuntamento) throws ClassNotFoundException, SQLException {
		if (!appuntamenti.contains(appuntamento)) {
			this.appuntamenti.add(appuntamento);
			Collections.sort(appuntamenti);
			new AppuntamentoWriterDB().add(appuntamento);
		}
	}
	
	public void addAppuntamento(LocalDateTime dataInizio, LocalDateTime dataFine, Cliente cliente, 
			List<Operaio> operai, List<Auto> automezzi, List<Macchina> macchine) throws ClassNotFoundException, SQLException {
		boolean found = false;
		int id = 0;
		do {
			found = false;
			id = new Random().nextInt();
			for (Appuntamento a : appuntamenti) {
				if (a.getId() == id)
					found = true;
			}
		} while (found);
		addAppuntamento(new Appuntamento(id, dataInizio, dataFine, cliente, operai, automezzi, macchine));
	}
	
	public void addOperaio(Operaio operaio) throws ClassNotFoundException, SQLException{
		if (!operai.stream().anyMatch(x -> x.equals(operaio))) {
			this.operai.add(operaio);
			dataManager.add(operaio, false);
		}
		else {
			for (Operaio o : operai)
				if (o.equals(operaio))
					if (o.getStato() == Stato.Eliminato) {
						o.setStato(Stato.Disponibile);
						dataManager.add(o, true);
					}
		}
	}
	
	public void addOperaio(String nome, String cognome, String mansione) throws ClassNotFoundException, SQLException{
		boolean found = false;
		int id = 0;
		do {
			found = false;
			id = new Random().nextInt();
			for (Operaio o : operai) {
				if (o.getId() == id)
					found = true;
			}
		} while (found);
		addOperaio(new Operaio(id, nome, cognome, mansione));
	}
	
	public void addMacchina(Macchina macchina) throws ClassNotFoundException, SQLException{
		if (!macchine.stream().anyMatch(x -> x.equals(macchina))) {
			this.macchine.add(macchina);
			dataManager.add(macchina, false);
		}
		else {
			for (Macchina o : macchine)
				if (o.equals(macchina))
					if (o.getStato() == Stato.Eliminato) {
						o.setStato(Stato.Disponibile);
						dataManager.add(o, true);
					}
		}
	}
	
	public void addMacchina(String nome) throws ClassNotFoundException, SQLException{
		boolean found = false;
		int id = 0;
		do {
			found = false;
			id = new Random().nextInt();
			for (Macchina m : macchine) {
				if (m.getId() == id)
					found = true;
			}
		} while (found);
		addMacchina(new Macchina(id, nome));
	}
	
	public void addAuto(Auto auto) throws ClassNotFoundException, SQLException{
		if (!automezzi.stream().anyMatch(x -> x.equals(auto))) {
			this.automezzi.add(auto);
			dataManager.add(auto, false);
		}
		else {
			for (Auto o : automezzi)
				if (o.equals(auto))
					if (o.getStato() == Stato.Eliminato) {
						o.setStato(Stato.Disponibile);
						dataManager.add(o, true);
					}
		}
	}
	
	public void addAuto(String targa, String tipologia) throws ClassNotFoundException, SQLException{
		boolean found = false;
		int id = 0;
		do {
			found = false;
			id = new Random().nextInt();
			for (Auto a : automezzi) {
				if (a.getId() == id)
					found = true;
			}
		} while (found);
		addAuto(new Auto(id, targa, tipologia));
	}
	
	public void addCliente(Cliente cliente) throws ClassNotFoundException, SQLException{
		if (!clienti.stream().anyMatch(x -> x.equals(cliente))) {
			this.clienti.add(cliente);
			dataManager.add(cliente, false);
		}
		else {
			for (Cliente o : clienti)
				if (o.equals(cliente))
					if (o.getStato() == Stato.Eliminato) {
						o.setStato(Stato.Disponibile);
						dataManager.add(o, true);
					}
		}
	}
	
	public void addCliente(String nome, String citta, String indirizzo) throws ClassNotFoundException, SQLException {
		boolean found = false;
		int id = 0;
		do {
			found = false;
			id = new Random().nextInt();
			for (Cliente a : clienti) {
				if (a.getId() == id)
					found = true;
			}
		} while (found);
		addCliente(new Cliente(id, nome, citta, indirizzo));
	}
	
	public void deleteOperaio(List<Operaio> operai) throws ClassNotFoundException, SQLException{
		for (Operaio o : operai)
			deleteOperaio(o);
	}
	
	public void deleteOperaio(Operaio operaio) throws ClassNotFoundException, SQLException{
		for (Operaio o : operai)
			if (o.equals(operaio))
				o.setStato(Stato.Eliminato);
		new OperaioWriterDB().delete(operaio);
	}
	
	 public void deleteMacchina(List<Macchina> macchine) throws ClassNotFoundException, SQLException{
		for (Macchina m : macchine)
			deleteMacchina(m);
	}
	
	 public void deleteMacchina(Macchina macchina) throws ClassNotFoundException, SQLException{
		 for (Macchina o : macchine)
				if (o.equals(macchina))
					o.setStato(Stato.Eliminato);
		new MacchinaWriterDB().delete(macchina);
	}
	
	public void deleteAuto(List<Auto> auto) throws ClassNotFoundException, SQLException {
		for (Auto a : auto)
			deleteAuto(a);
	}
		
	public void deleteAuto(Auto auto) throws ClassNotFoundException, SQLException {
		for (Auto o : automezzi)
			if (o.equals(auto))
				o.setStato(Stato.Eliminato);
		new AutoWriterDB().delete(auto);
	}
	
	public void deleteCliente(Cliente cliente) throws ClassNotFoundException, SQLException{
		for (Cliente o : clienti)
			if (o.equals(cliente))
				o.setStato(Stato.Eliminato);
		new ClienteWriterDB().delete(cliente);
	}
	
	public void deleteAppuntamento(List<Appuntamento> appuntamenti) throws ClassNotFoundException, SQLException {
		for (Appuntamento a : appuntamenti)
			deleteAppuntamento(a);
	}
	
	public boolean deleteAppuntamento(Appuntamento appuntamento) throws ClassNotFoundException, SQLException{
		if (this.appuntamenti.remove(appuntamento)) {
			new AppuntamentoWriterDB().delete(appuntamento);
			return true;
		}
		return false;
	}
	
	public List<Appuntamento> reportAvanzatoFilter(LocalDate inizio, boolean cbInizio, LocalDate fine, boolean cbFine, List<Operaio> operai, boolean cbOperai, 
			List<Auto> auto, boolean cbAuto, List<Macchina> macchine, boolean cbMacchine, List<Cliente> clienti, boolean cbClienti){
		List<Appuntamento> appuntamentiFiltrati = new ArrayList<>();
		for (Appuntamento a : appuntamenti) {
			boolean aggiungi = true;
			if (cbInizio)
				if (a.getDataInizio().toLocalDate().isBefore(inizio))
					aggiungi = false;
			if (cbFine)
				if (a.getDataFine().toLocalDate().isAfter(fine))
					aggiungi = false;
			if (cbOperai)
				if (!a.getOperai().containsAll(operai))
					aggiungi = false;
			if (cbAuto)
				if (!a.getAuto().containsAll(auto))
					aggiungi = false;
			if (cbMacchine)
				if (!a.getMacchine().containsAll(macchine))
					aggiungi = false;
			if (cbClienti)
				if (!clienti.contains(a.getCliente()))
					aggiungi = false;
			if (aggiungi)
				appuntamentiFiltrati.add(a);
		}
		return appuntamentiFiltrati;
	}
}
