package org.valabs.jdataserver.target.simple;

import java.io.IOException;

import javax.json.Json;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.valabs.jdataserver.Main;
import org.valabs.jdataserver.OutputTransform;
import org.valabs.jdataserver.source.DataSource;

/**
 * A simple servlet that serves data source as plain JSON text.
 * @author valexeev
 *
 */
public class SimpleHttpOutput extends HttpServlet {
	private Main dataServer;
	private OutputTransform outputTransform;
	
	public SimpleHttpOutput(Main _dataServer, OutputTransform _outputTransform) {
		dataServer = _dataServer;
		outputTransform = _outputTransform;
	}	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType(outputTransform.getMimeType());
		String endpoint = req.getQueryString();
		DataSource ds = dataServer.getDataSource(endpoint);
		
		outputTransform.transformOutput(ds.getData(Json.createObjectBuilder().build()), resp.getWriter());
	}
}
