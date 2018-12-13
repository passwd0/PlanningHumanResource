package amministrazione.ui;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import amministrazione.model.Appuntamento;
import amministrazione.model.Operaio;
import amministrazione.model.Auto;
import amministrazione.model.Cliente;
import amministrazione.model.Macchina;

public class AppuntamentoColor {
	private String[] colors = new String[] {"-fx-background-color: rgba(0, 102, 51, 0.2);",
			"-fx-background-color: rgba(153, 102, 51, 0.2);", "-fx-background-color: rgba(56, 176, 209, 0.2);",
			"-fx-background-color: rgba(153, 102, 209, 0.2);","-fx-background-color: rgba(255, 153, 51, 0.2);",
			"-fx-background-color: rgba(153, 0, 0, 0.2);",    "-fx-background-color: rgba(255, 0, 0, 0.2);",};
	private String color;
	private Appuntamento appuntamento;

	public AppuntamentoColor(Appuntamento appuntamento) {
		this.appuntamento = appuntamento;
		color = colors[new Random().nextInt(colors.length)];
		// TODO Auto-generated constructor stub
	}
	
	public String getColor() {
		return color;
	}
	
	public Appuntamento getAppuntamento() {
		return appuntamento;
	}
	
	public LocalDateTime getDataInizio() {
		return appuntamento.getDataInizio();
	}
	
	public LocalDateTime getDataFine() {
		return appuntamento.getDataFine();
	}
	
	public List<Operaio> getOperai() {
		return appuntamento.getOperai();
	}
	
	public List<Auto> getAuto(){
		return appuntamento.getAuto();
	}
	
	public List<Macchina> getMacchine(){
		return appuntamento.getMacchine();
	}
	
	public Cliente getCliente(){
		return appuntamento.getCliente();
	}

}
