/**
 * 
 */
package org.valabs.jdataserver;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.valabs.jdataserver.source.DataSource;
import org.valabs.jdataserver.source.jdbc.JdbcDataSource;
import org.valabs.jdataserver.target.simple.SimpleHttpOutput;
import org.valabs.jdataserver.transform.ical.IcalTransform;
import org.valabs.jdataserver.transform.json.JsonTransform;

/**
 * @author valexeev
 *
 */
public class Main {
	private Map<String, DataSource> dataSources = new HashMap<String, DataSource>();
	
	public static final int HTTP_PORT = 9494;
	
	public Main() throws Exception {
		// read configuration
		JsonObject config = Json.createReader(new FileInputStream("jdataserver.json")).readObject();
		// create data sources
		JsonArray dataSourceConfigs = config.getJsonArray("datasources");
		for (JsonValue dataSourceConfig: dataSourceConfigs) {
			JsonObject configObj = (JsonObject) dataSourceConfig;
			// TODO: implement factory based on type parameter 
			DataSource ds = new JdbcDataSource();
			ds.setup(configObj);
			dataSources.put(configObj.getString("endpoint"), ds);
		}
		// create server
		Server server = new Server(HTTP_PORT);
		// create servlet
		ServletHandler handler = new ServletHandler();
		JsonArray targetConfigs = config.getJsonArray("targets");
		for (JsonValue obj: targetConfigs) {
			JsonObject targetConfig = (JsonObject) obj;
			OutputTransform ot;
			if ("ical".equalsIgnoreCase(targetConfig.getString("transform", "json"))) {
				ot = new IcalTransform();
			} else {
				ot = new JsonTransform();
			}
			ot.setup(targetConfig.getJsonObject("transform-options"));
			
			handler.addServletWithMapping(new ServletHolder(new SimpleHttpOutput(this, ot)), "/" + targetConfig.getString("endpoint"));			
		}
		server.setHandler(handler);
		// run
		server.start();
		server.join();
	}
	
	public DataSource getDataSource(String endpoint) {
		return dataSources.get(endpoint);
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		new Main();
	}
}
