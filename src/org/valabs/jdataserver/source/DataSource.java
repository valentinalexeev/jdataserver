package org.valabs.jdataserver.source;

import javax.json.JsonObject;

/**
 * A base class for all data sources.
 * @author valexeev
 */
public abstract class DataSource {
	protected String dataSourceName;
	
	/**
	 * Setup the data source from the configuration file.
	 * @param setupParams relevant section from the configuration JSON
	 */
	public void setup(JsonObject setupParams) {
		dataSourceName = setupParams.getString("name");
	}
	
	/**
	 * Execute the request on the data source and provide results as JSON.
	 * @param inputParams input parameters as provided by the requestor
	 * @return an object with the response
	 */
	public abstract JsonObject getData(JsonObject inputParams);
}
