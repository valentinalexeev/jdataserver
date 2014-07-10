package org.valabs.jdataserver;

import java.io.IOException;
import java.io.Writer;

import javax.json.JsonObject;

/**
 * A base class for output transformation.
 * @author valexeev
 *
 */
public abstract class OutputTransform {
	public abstract void setup(JsonObject setupParams);
	public abstract void transformOutput(JsonObject result, Writer output) throws IOException;
	public abstract String getMimeType();
}
