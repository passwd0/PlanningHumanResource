package planninghumanresource.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.json.simple.JSONObject;

import planninghumanresource.model.Setting;
import planninghumanresource.utils.Utils;

public class SettingWriter {
	public void writer(Writer writer, Setting setting) throws IOException {
		if (writer == null)
			throw new IllegalArgumentException("writer null");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("oraInizioLavorativa", setting.getInizioLavoro().format(Utils.formatterOra));
		jsonObject.put("oraFineLavorativa", setting.getFineLavoro().format(Utils.formatterOra));
		jsonObject.put("scaglionamento", setting.getScaglionamento());
		jsonObject.put("fileAppuntamento", setting.getFileAppuntamento().toString());
		jsonObject.put("colorPalette", setting.getColorPalette());
		
		for (String alertName : setting.getAlert().keySet())
			jsonObject.put(alertName, setting.getAlert().get(alertName));
		
		PrintWriter printWriter = new PrintWriter(new BufferedWriter(writer));
		jsonObject.writeJSONString(printWriter);
		printWriter.close();
	}
}
