package planninghumanresource.model;

import java.io.File;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class Setting {
	private LocalTime inizioLavoro;
	private LocalTime fineLavoro;
	private int scaglionamento;
	private File fileAppuntamento;
	private String colorPalette;
	private Map<String, Boolean> alert;
	
	public Setting(LocalTime inizioLavoro, LocalTime fineLavoro, int scaglionamento, 
			File fileAppuntamento, String colorPalette, Map<String, Boolean> alert) {
		this.inizioLavoro = inizioLavoro;
		this.fineLavoro = fineLavoro;
		this.scaglionamento = scaglionamento;
		this.fileAppuntamento = fileAppuntamento;
		this.colorPalette = colorPalette;
		this.alert= alert;
	}
	
	public Setting(LocalTime inizioLavoro, LocalTime fineLavoro, int scaglionamento, 
			File fileAppuntamento, String colorPalette) {
		this(inizioLavoro, fineLavoro, scaglionamento, fileAppuntamento, colorPalette, new HashMap<String, Boolean>());
	}
	
	public Map<String, Boolean> getAlert() {
		return alert;
	}

	public void setAlert(Map<String, Boolean> alert) {
		this.alert = alert;
	}
	
	public void setAlert(String name, boolean state) {
		alert.put(name, state);
	}

	public LocalTime getInizioLavoro() {
		return inizioLavoro;
	}
	
	public LocalTime getFineLavoro() {
		return fineLavoro;
	}
	public void setFineLavoro(LocalTime fineLavoro) {
		this.fineLavoro = fineLavoro;
	}
	public void setInizioLavoro(LocalTime inizioLavoro) {
		this.inizioLavoro = inizioLavoro;
	}
	public int getScaglionamento() {
		return scaglionamento;
	}
	public void setScaglionamento(int scaglionamento) {
		this.scaglionamento = scaglionamento;
	}
	public File getFileAppuntamento() {
		return fileAppuntamento;
	}
	public void setFileAppuntamento(File fileAppuntamento) {
		this.fileAppuntamento = fileAppuntamento;
	}
	
	public void setColorPalette(String colorPalette) {
		this.colorPalette = colorPalette;
	}
	
	public String getColorPalette() {
		return colorPalette;
	}
}
