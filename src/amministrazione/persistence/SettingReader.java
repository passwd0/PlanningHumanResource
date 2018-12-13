package amministrazione.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import amministrazione.model.Setting;
import amministrazione.utils.Utils;

public class SettingReader {
	private Setting setting;
	
	public SettingReader() {
		Map<String, Boolean> mapAlert = new HashMap<>();

		JSONParser jsonParser = new JSONParser();
		try {
			String str = new BufferedReader(new FileReader("setting.txt")).readLine();
			Object object = jsonParser.parse(str);
			JSONObject jsonObject = (JSONObject) object;
			
			LocalTime inizioLavoro = LocalTime.parse(jsonObject.get("oraInizioLavorativa").toString(), Utils.formatterOra);
			LocalTime fineLavoro = LocalTime.parse(jsonObject.get("oraFineLavorativa").toString(), Utils.formatterOra);
			int scaglionamento = Integer.parseInt(jsonObject.get("scaglionamento").toString());
			File fileAppuntamento = new File(jsonObject.get("fileAppuntamento").toString());
			String colorPalette = jsonObject.get("colorPalette").toString();
			Boolean alertControlValue = Boolean.parseBoolean(jsonObject.getOrDefault("alertControl", true).toString());
			Boolean alertSureDeletionValue = Boolean.parseBoolean(jsonObject.getOrDefault("alertSureDeletion", true).toString());
			Boolean alertSavedReportValue = Boolean.parseBoolean(jsonObject.getOrDefault("alertSavedReport", true).toString());
			mapAlert.put("alertControl", alertControlValue);
			mapAlert.put("alertSureDeletion", alertSureDeletionValue);
			mapAlert.put("alertSavedReport", alertSavedReportValue);
			setting = new Setting(inizioLavoro, fineLavoro, scaglionamento, fileAppuntamento, colorPalette, mapAlert);
		}
		catch (Exception e) {
			setting = new Setting(LocalTime.of(8, 0), LocalTime.of(18, 0), 30, new File("appuntamenti.txt"), "Default", mapAlert);
		}
	}

	public Setting getSetting() {
		return setting;
	}
	
	
}
