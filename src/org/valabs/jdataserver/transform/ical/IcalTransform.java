package org.valabs.jdataserver.transform.ical;

import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.valabs.jdataserver.OutputTransform;

/**
 * Output data as iCalendar file.
 * Datasource is expected to have at least DTSTART, DTEND and SUMMARY fields.
 * @author valexeev
 *
 */
public class IcalTransform extends OutputTransform {
	
	private static final String MIME_TYPE = "text/calendar; charset=utf-8";
	private String tzId;
	private JsonArray alarms = null;
	
	@Override
	public void setup(JsonObject setupParams) {
		if (setupParams != null) {
			if (setupParams.containsKey("tzid")) {
				tzId = setupParams.getString("tzid");
			}
			if (setupParams.containsKey("alarm")) {
				alarms = (JsonArray) setupParams.getJsonArray("alarm");
			}
		}
	}

	@Override
	public void transformOutput(JsonObject result, Writer output) throws IOException {
		writeIcalHeader(output);
		
		JsonArray rows = result.getJsonArray("rows");
		for (JsonValue obj: rows) {
			JsonObject row = (JsonObject) obj;
			writeIcalEvent(output, row);
		}
		
		writeIcalFooter(output);
	}
	
	private void writeIcalHeader(Writer output) throws IOException {
		output.write("BEGIN:VCALENDAR\n");
		output.write("VERSION:2.0\n");
		output.write("PRODID:-//VALabs//jdataserver//EN\n");
	}

	private void writeIcalFooter(Writer output) throws IOException {
		output.write("END:VCALENDAR\n");
	}
	
	private void writeIcalEvent(Writer output, JsonObject row) throws IOException {
		output.write("BEGIN:VEVENT\n");
		String summary = row.getString("SUMMARY");
		String dtStart = row.getString("DTSTART");
		String dtEnd = row.getString("DTEND");
		output.write(MessageFormat.format("UID:{0}-{1}-{2}@jdataserver.org\n", summary.replaceAll(" ", "_"), dtStart, dtEnd));
		output.write(MessageFormat.format("SUMMARY:{0}\n", summary));
		if (tzId != null) {
			output.write(MessageFormat.format("DTSTART;TZID={0}:{1}\n", tzId, dtStart));
			output.write(MessageFormat.format("DTEND;TZID={0}:{1}\n", tzId, dtEnd));		
		} else {
			output.write(MessageFormat.format("DTSTART:{0}\n", dtStart));
			output.write(MessageFormat.format("DTEND:{0}\n", dtEnd));
		}
		if (alarms != null) {
			for (JsonValue val: alarms) {
				writeIcalAlarm(output, ((JsonString) val).getString(), summary);
			}
		}
		output.write("END:VEVENT\n");		
	}
	
	private void writeIcalAlarm(Writer output, String alarmSpec, String description) throws IOException {
		output.write("BEGIN:VALARM\n");
		output.write("ACTION:DISPLAY\n");
		output.write(MessageFormat.format("DESCRIPTION:{0}\n", description));
		output.write(MessageFormat.format("TRIGGER:{0}\n", alarmSpec));
		output.write("END:VALARM\n");
	}

	@Override
	public String getMimeType() {
		return MIME_TYPE;
	}

}
