/**
 * es.upm.dit.gsi.barmas.launcher.utils.JDBCExample.java
 */
package es.upm.dit.gsi.barmas.launcher.utils;

//STEP 1. Import required packages
import java.sql.*;
import java.util.logging.Logger;

import com.csvreader.CsvReader;

public class DBParser {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
	static final String DB_URL = "jdbc:mariadb://shannon.gsi.dit.upm.es/barmas_experiments";

	// Database credentials
	// static final String USER = "root";
	// static final String PASS = "shannondbserver";

	static final String USER = "a.carrera";
	static final String PASS = "barmas";

	Logger logger;

	public DBParser() {
		this.logger = Logger.getLogger(DBParser.class.getSimpleName());
	}

	public static void main(String[] args) {
		DBParser dbparser = new DBParser();

//		String filePath = "../experiments/zoo-simulation/zoo-simulation-summary.csv";
//		String tableName = "zoo";
//		String filePath = "../experiments/kowlancz02-simulation/kowlancz02-simulation-summary.csv";
//		String tableName = "kowlancz02";
//		String filePath = "../experiments/mushroom-simulation/mushroom-simulation-summary.csv";
//		String tableName = "mushroom";
		String filePath = "../experiments/chess-simulation/chess-simulation-summary.csv";
		String tableName = "chess";
//		String filePath = "../experiments/solarflare-simulation/solarflare-simulation-summary.csv";
//		String tableName = "solarflare";
//		String filePath = "../experiments/nursery-simulation/nursery-simulation-summary.csv";
//		String tableName = "nursery";
//		String filePath = "../experiments/marketing-simulation/marketing-simulation-summary.csv";
//		String tableName = "marketing";
		dbparser.putDataInDB(tableName, filePath);

	}// end main

	/**
	 * @param filePath
	 */
	public void putDataInDB(String tableName, String filePath) {
		Connection conn = null;
		Statement stmt = null;
		try {

			CsvReader reader = new CsvReader(filePath);

			reader.readHeaders();
			// STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			// STEP 3: Open a connection
			logger.info("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			logger.info("Connected database successfully...");
			// STEP 5: Execute a query
			logger.info("Creating table in given database...");
			stmt = conn.createStatement();

			String sql = "CREATE TABLE " + tableName + " " + "(id INTEGER not NULL, "
					+ " simulationid VARCHAR(1000), " + " type VARCHAR(255), " + " cases DOUBLE, "
					+ " bayesCentralOk DOUBLE, " + " argumentationOk DOUBLE, "
					+ " bayesCentralBetter DOUBLE, " + " argumentationBetter DOUBLE, "
					+ " bothOk DOUBLE, " + " bothWrong DOUBLE, "
					+ " globalImprovementWithArgumentation DOUBLE, " + " draw DOUBLE, "
					+ " dth DOUBLE, " + " bth DOUBLE, " + " leba INTEGER, " + " tth DOUBLE, "
					+ " iteration INTEGER, " + " argumentativeAgents INTEGER, "
					+ " testRatio DOUBLE, " + " seed BIGINT, " + " timestamp BIGINT, "
					+ " dataset VARCHAR(255), " + " maxArgumentationRounds INTEGER, "
					+ " PRIMARY KEY ( id ))";
			stmt.executeUpdate(sql);
			logger.info("Created table in given database...");

			int counter = 0;
			while (reader.readRecord()) {

				if (counter % 500 == 0) {
					logger.info("Inserting simulation " + counter);
				}
				String[] values = reader.getValues();
				sql = "INSERT INTO `" + tableName + "` VALUES (";
				sql = this.appendValue(sql, Integer.toString(counter));
				for (String value : values) {
					sql = this.appendValue(sql, value);
				}
				sql = this.closeSQL(sql);
				stmt.executeUpdate(sql);
				counter++;
			}
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
		try {
			if (stmt != null) {
				conn.close();
			}
		} catch (SQLException se) {
			// do nothing
		}
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}// end finally try
	}

	/**
	 * @param sql
	 * @param value
	 */
	private String appendValue(String sql, String value) {
		if (sql.endsWith("VALUES (")) {
			return sql + "'" + value + "'";
		} else {
			return sql + ", '" + value + "'";
		}
	}

	/**
	 * @param sql
	 */
	private String closeSQL(String sql) {
		return sql + ");";
	}

	/**
	 * @throws ClassNotFoundException
	 * 
	 */
	public void createDB() throws ClassNotFoundException {
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			// STEP 3: Open a connection
			logger.info("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			logger.info("Connected database successfully...");

			// STEP 4: Execute a query
			logger.info("Creating database...");
			stmt = conn.createStatement();

			String sql = "CREATE DATABASE barmas_experiments";
			stmt.executeUpdate(sql);
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
		try {
			if (stmt != null) {
				conn.close();
			}
		} catch (SQLException se) {
			// do nothing
		}
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}// end finally try
	}
}// end JDBCExample