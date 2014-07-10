package org.valabs.jdataserver.source.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.valabs.jdataserver.source.DataSource;

/**
 * A data source that fetches data from JDBC-compliant database.
 * 
 * @author valexeev
 */
public class JdbcDataSource extends DataSource {
	/** JDBC URL to connect to. */
	private String jdbcUrl;
	/** Query to execute. */
	private String query;
	/** Username to connect. */
	private String user;
	/** Password to use for connection. */
	private String password;

	@Override
	public void setup(JsonObject setupParams) {
		super.setup(setupParams);
		jdbcUrl = setupParams.getString("url");
		query = setupParams.getString("query");
		user = setupParams.getString("username");
		password = setupParams.getString("password");
		if (setupParams.containsKey("driver")) {
			try {
				Class.forName(setupParams.getString("driver"));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public JsonObject getData(JsonObject inputParams) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsm = null;
		JsonObjectBuilder result = Json.createObjectBuilder();
		JsonArrayBuilder rows = Json.createArrayBuilder();

		try {
			conn = DriverManager.getConnection(jdbcUrl, user, password);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			rsm = rs.getMetaData();
			while (rs.next()) {
				JsonObjectBuilder row = Json.createObjectBuilder();
				for (int i = 0; i < rsm.getColumnCount(); i++) {
					Object cell = rs.getObject(rsm.getColumnName(i + 1));
					if (cell == null) {
						row.addNull(rsm.getColumnName(i + 1));
					} else {
						row.add(rsm.getColumnName(i + 1), getValue(rsm.getColumnTypeName(i + 1), cell));
					}

				}
				rows.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		result.add("rows", rows);

		return result.build();
	}

	/**
	 * Attempt to translate cell value to string presentation. 
	 * @param columnType type name as returned by JDBC driver
	 * @param cell cell with the value, must not be null
	 * @return text presentation of the cell
	 */
	private String getValue(String columnType, Object cell) {
		switch (columnType) {
		default:
			return cell.toString();
		}
	}
}
