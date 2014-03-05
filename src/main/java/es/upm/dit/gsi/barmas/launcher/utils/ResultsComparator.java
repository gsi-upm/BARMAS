/**
 * es.upm.dit.gsi.barmas.launcher.utils.ResultsComparator.java
 */
package es.upm.dit.gsi.barmas.launcher.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.csvreader.CsvWriter;

import es.upm.dit.gsi.barmas.launcher.logging.LogConfigurator;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.launcher.utils.ResultsComparator.java
 * 
 * Script to compare Weka validators results with BARMAS results. This class
 * queries both tables to get the final comparison value.
 * 
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 21/02/2014
 * @version 0.1
 * 
 */
public class ResultsComparator {
	private static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
	private static final String DB_URL = "jdbc:mariadb://shannon.gsi.dit.upm.es/barmas_experiments";

	private static final String USER = "a.carrera";
	private static final String PASS = "barmas";

	private static Logger logger;
	private Connection conn;

	private List<String> classifiers;
	private HashMap<String, Integer> datasets;
	private int minAgents;
	private int maxAgents;
	private List<Double> lebas;
	private char separator;

	/**
	 * Constructor
	 * 
	 * @param maxAgents
	 * @param minAgents
	 * @param datasets
	 * @param classifiers
	 * @param lebas
	 * @param separator
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * 
	 */
	public ResultsComparator(List<String> classifiers, HashMap<String, Integer> datasets,
			int minAgents, int maxAgents, List<Double> lebas, char separator)
			throws ClassNotFoundException, SQLException {
		logger = Logger.getLogger(ResultsComparator.class.getSimpleName());
		LogConfigurator.log2File(logger, this.getClass().getSimpleName(), Level.ALL, Level.ALL,
				"../analysis");
		this.classifiers = classifiers;
		this.datasets = datasets;
		this.minAgents = minAgents;
		this.maxAgents = maxAgents;
		this.lebas = lebas;
		this.separator = separator;

		Class.forName(JDBC_DRIVER);
		// Open a connection
		logger.info("Connecting to barmas_experiments database...");
		this.conn = DriverManager.getConnection(DB_URL, USER, PASS);
	}

	public ResultSet queryWekaResults(String dataset, int leba, String classifier)
			throws SQLException {
		Statement stmt = conn.createStatement();
		String sql;
		String lowerDataset = dataset.toLowerCase();

		sql = "SELECT dataset, kfold, classifier, ratioWrong, leba from weka WHERE dataset='"
				+ lowerDataset + "' AND iteration='AVERAGE' AND leba=" + leba + " AND classifier='"
				+ classifier + "' AND agentID='BayesCentralAgent'";
		ResultSet result = stmt.executeQuery(sql);
		return result;
	}

	public ResultSet queryBayesCentralResults(String dataset, int leba) throws SQLException {
		Statement stmt = conn.createStatement();
		String sql;
		String upperDataset = dataset.toUpperCase();
		sql = "SELECT dataset, AVG(bayesCentralOk), leba from results WHERE dataset='"
				+ upperDataset + "' AND leba=" + leba;
		ResultSet result = stmt.executeQuery(sql);
		return result;
	}

	public ResultSet queryBarmasResults(String dataset, int leba, int agents) throws SQLException {
		Statement stmt = conn.createStatement();
		String sql;
		String upperDataset = dataset.toUpperCase();
		sql = "SELECT dataset, AVG(argumentationOk), dth, bth, tth, leba, argumentativeAgents as agents from results WHERE dataset='"
				+ upperDataset
				+ "' AND leba="
				+ leba
				+ " AND argumentativeAgents="
				+ agents
				+ " AND simulationid LIKE 'BarmasExperiment%' GROUP BY dth, bth, tth, leba, argumentativeAgents";
		ResultSet result = stmt.executeQuery(sql);
		return result;
	}

	public void closeConnection() throws SQLException {
		if (conn != null) {
			conn.close();
			logger.info("Connection closed.");
		}
	}

	public List<String> getClassifiersList() {
		return this.classifiers;
	}

	public Set<String> getDatasetList() {
		return datasets.keySet();
	}

	public void buildResultsFile() throws IOException, SQLException {
		logger.info("Writing results file...");

		for (double lebaPct : lebas) {
			CsvWriter writer = new CsvWriter(new FileWriter("../analysis/results-leba-" + lebaPct
					+ ".csv"), separator);
			int headersLength = 1 + (maxAgents - minAgents + 1) + 1
					+ this.getClassifiersList().size();
			String[] headers = new String[headersLength];
			int index = 0;
			headers[index++] = "Dataset";
			for (int i = minAgents; i <= maxAgents; i++) {
				headers[index++] = "BarmasAgents" + i;
			}
			headers[index++] = "BayesSearch";
			List<String> classifiers = this.getClassifiersList();
			for (String classifier : classifiers) {
				headers[index++] = classifier;
			}
			writer.writeRecord(headers);

			for (String dataset : datasets.keySet()) {
				int leba = (int) Math.rint(lebaPct * this.datasets.get(dataset));
				logger.info("LEBA for lebaPct " + lebaPct + " = " + leba + " for dataset "
						+ dataset);
				if (leba > 10) {
					logger.warning("High leba -> not calculated for that option -> Changing leba to 10.");
					leba = 10;
				}
				String[] row = new String[headersLength];
				index = 0;
				double er;
				row[index++] = dataset;
				for (int agents = minAgents; agents <= maxAgents; agents++) {
					er = this.getBARMASErrorRate(dataset, agents, leba);
					row[index++] = Double.toString(er);
				}
				er = this.getBayesCentralErrorRate(dataset, leba);
				row[index++] = Double.toString(er);
				for (String classifier : classifiers) {
					er = this.getClassifierErrorRate(dataset, classifier, leba);
					row[index++] = Double.toString(er);
				}
				writer.writeRecord(row);
			}

			writer.close();
		}
		this.closeConnection();
	}

	/**
	 * @param dataset
	 * @param classifier
	 * @param leba
	 * @return
	 * @throws SQLException
	 */
	private double getClassifierErrorRate(String dataset, String classifier, int leba)
			throws SQLException {
		ResultSet result = this.queryWekaResults(dataset, leba, classifier);
		double min = 100;
		while (result.next()) {
			double succesRatio = result.getDouble(4);
			if (succesRatio < min) {
				min = succesRatio;
			}
		}
		double roundedER = this.round(min, 2);
		logger.info("Error rate for classifier " + classifier + " with leba=" + leba
				+ " and dataset " + dataset + " -->> ER=" + roundedER);
		return roundedER;
	}

	/**
	 * @param dataset
	 * @param leba
	 * @return
	 * @throws SQLException
	 */
	private double getBayesCentralErrorRate(String dataset, int leba) throws SQLException {
		ResultSet result = this.queryBayesCentralResults(dataset, leba);
		double max = 0;
		while (result.next()) {
			double succesRatio = result.getDouble(2);
			if (succesRatio > max) {
				max = succesRatio;
			}
		}
		double roundedER = this.round(1 - max, 2);
		logger.info("Error rate for BayesSearch with leba=" + leba + " and dataset " + dataset
				+ " -->> ER=" + roundedER);
		return roundedER;
	}

	/**
	 * @param dataset
	 * @param agents
	 * @param leba2
	 * @return
	 * @throws SQLException
	 */
	private double getBARMASErrorRate(String dataset, int agents, int leba) throws SQLException {

		ResultSet result = this.queryBarmasResults(dataset, leba, agents);
		double max = 0;
		while (result.next()) {
			double succesRatio = result.getDouble(2);
			if (succesRatio > max) {
				max = succesRatio;
			}
		}
		double roundedER = this.round(1 - max, 2);
		logger.info("Error rate for BARMAS-Agents-" + agents + " with leba=" + leba
				+ " and dataset " + dataset + " -->> ER=" + roundedER);
		return roundedER;
	}

	/**
	 * @param number
	 * @param digits
	 * @return
	 */
	private double round(double number, int digits) {
		int aux = (int) Math.pow(10, digits);
		return Math.rint(number * aux) / aux;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			List<String> classifiers = new ArrayList<String>();
			classifiers.add("J48");
			classifiers.add("LADTree");
			classifiers.add("NBTree");
			classifiers.add("PART");
			classifiers.add("SMO");

			HashMap<String, Integer> datasets = new HashMap<String, Integer>();
			datasets.put("Zoo", 16);
			datasets.put("Solarflare", 11);
			datasets.put("Marketing", 13);
			datasets.put("Nursery", 9);
			datasets.put("Mushroom", 22);
			datasets.put("Chess", 6);
			// datasets.put("Poker",10);
			datasets.put("KOWLANCZ02", 27);

			List<Double> lebas = new ArrayList<Double>();
			lebas.add(0.0);
			lebas.add(0.25);
			lebas.add(0.50);
			// lebas.add(0.75);
			// lebas.add(1.0);
			// lebas.add(0.1);
			// lebas.add(0.3);
			// lebas.add(0.4);
			// lebas.add(0.6);

			int minAgents = 2;
			int maxAgents = 4;

			char separator = '&';

			ResultsComparator rc = new ResultsComparator(classifiers, datasets, minAgents,
					maxAgents, lebas, separator);
			rc.buildResultsFile();
		} catch (ClassNotFoundException e) {
			logger.severe("Exception: " + e.getMessage());
			e.printStackTrace();
		} catch (SQLException e) {
			logger.severe("Exception: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.severe("Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
