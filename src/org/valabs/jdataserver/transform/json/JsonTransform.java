package org.valabs.jdataserver.transform.json;

import java.io.Writer;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;

import org.valabs.jdataserver.OutputTransform;

public class JsonTransform extends OutputTransform {
	
	public static final String MIME_TYPE = "application/json; charset=utf-8";
	
	@Override
	public void setup(JsonObject setupParams) {
	}

	@Override
	public void transformOutput(JsonObject result, Writer output) {
		JsonWriter jw = Json.createWriter(output);
		jw.writeObject(result);
	}

	@Override
	public String getMimeType() {
		return MIME_TYPE;
	}

}
