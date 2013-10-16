/**
 * es.upm.dit.gsi.barmas.simulation.SolarFlareNoGUILauncher.java
 */
package es.upm.dit.gsi.barmas.solarflare.launcher;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import es.upm.dit.gsi.barmas.solarflare.agent.SolarFlareBayesCentralAgent;
import es.upm.dit.gsi.barmas.solarflare.agent.basic.AdvancedCentralManagerAgent;
import es.upm.dit.gsi.barmas.solarflare.agent.basic.BasicClassificatorAgent;
import es.upm.dit.gsi.barmas.solarflare.launcher.logging.LogConfigurator;
import es.upm.dit.gsi.barmas.solarflare.model.scenario.SolarFlareScenario;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.Activity;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.Area;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.BecomeHist;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.CNode;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.Evolution;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.HistComplex;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.LargestSpotSize;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.MNode;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.PrevStatus24Hour;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.SpotDistribution;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.XNode;
import es.upm.dit.gsi.barmas.solarflare.simulation.SolarFlareClassificationSimulation;
import es.upm.dit.gsi.shanks.agent.ShanksAgent;
import es.upm.dit.gsi.shanks.exception.ShanksException;
import es.upm.dit.gsi.shanks.model.scenario.Scenario;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.simulation.SolarFlareNoGUILauncher.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 02/10/2013
 * @version 0.1
 * 
 */
public class SolarFlareNoGUILauncher {

	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static void main(String[] args) {

		String summaryFile = "src/main/resources/exp1/output/global-summary.csv";
		// long seed = System.currentTimeMillis();
		long seed = 0;
		// for (int i = 0; i < 10; i++) {
		// SolarFlareNoGUILauncher.launchSimulationBasic2Agents(seed,
		// summaryFile);
		// }

		SolarFlareNoGUILauncher.launchSimulationBasic2Agents(seed, summaryFile);
		SolarFlareNoGUILauncher.launchSimulationBasic3Agents(seed, summaryFile);
		SolarFlareNoGUILauncher.launchSimulationBasic4Agents(seed, summaryFile);
//		SolarFlareNoGUILauncher.launchSimulationAdvanced2Agents(seed, summaryFile);
//		SolarFlareNoGUILauncher.launchSimulationAdvanced3Agents(seed, summaryFile);
//		SolarFlareNoGUILauncher.launchSimulationAdvanced4Agents(seed, summaryFile);
	}

	public static void makeNumbers(String simulationName, String origPath,
			String outputPath) {
		File origFile = new File(origPath);
		File outputFile = new File(outputPath);
		// Calculate comparative data
		CsvWriter writer = null;
		try {
			if (!outputFile.exists()) {
				writer = new CsvWriter(new FileWriter(outputFile), ',');
				String[] headers = new String[8];
				headers[0] = "SimulationID";
				headers[1] = "Type";
				headers[2] = "Cases";
				headers[3] = "BayesCentralOK";
				headers[4] = "ArgumentationOK";
				headers[5] = "BayesCentralBetter";
				headers[6] = "ArgumentationBetter";
				headers[7] = "Draw";
				writer.writeRecord(headers);
			} else {
				writer = new CsvWriter(new FileWriter(outputFile, true), ',');
			}
			writer.flush();

			CsvReader reader = new CsvReader(new FileReader(origFile));
			reader.readHeaders();
			HashMap<String, HashMap<String, Integer>> counters = new HashMap<String, HashMap<String, Integer>>();
			while (reader.readRecord()) {
				String[] origdata = reader.getValues();
				if (!counters.keySet().contains(origdata[0])) {
					HashMap<String, Integer> cs = new HashMap<String, Integer>();
					cs.put("Cases", 0);
					cs.put("BayesCentralOK", 0);
					cs.put("ArgumentationOK", 0);
					cs.put("BayesCentralBetter", 0);
					cs.put("ArgumentationBetter", 0);
					cs.put("Draw", 0);
					counters.put(origdata[0], cs);
				}
				HashMap<String, Integer> counter = counters.get(origdata[0]);
				int aux = counter.get("Cases");
				counter.put("Cases", aux + 1);
				if (origdata[2].equals("1")) {
					aux = counter.get("BayesCentralOK");
					counter.put("BayesCentralOK", aux + 1);
				}
				if (origdata[3].equals("1")) {
					aux = counter.get("ArgumentationOK");
					counter.put("ArgumentationOK", aux + 1);
				}
				if (origdata[4].equals("1")) {
					aux = counter.get("BayesCentralBetter");
					counter.put("BayesCentralBetter", aux + 1);
				}
				if (origdata[5].equals("1")) {
					aux = counter.get("ArgumentationBetter");
					counter.put("ArgumentationBetter", aux + 1);
				}
				if (origdata[6].equals("1")) {
					aux = counter.get("Draw");
					counter.put("Draw", aux + 1);
				}
			}
			// Write all types & TOTAL row
			int[] total = new int[6];
			for (Entry<String, HashMap<String, Integer>> entry : counters
					.entrySet()) {
				String[] data = new String[8];
				data[0] = simulationName;
				data[1] = entry.getKey();
				HashMap<String, Integer> summaries = entry.getValue();
				data[2] = Integer.toString(summaries.get("Cases"));
				data[3] = Integer.toString(summaries.get("BayesCentralOK"));
				data[4] = Integer.toString(summaries.get("ArgumentationOK"));
				data[5] = Integer.toString(summaries.get("BayesCentralBetter"));
				data[6] = Integer
						.toString(summaries.get("ArgumentationBetter"));
				data[7] = Integer.toString(summaries.get("Draw"));

				writer.writeRecord(data);
				writer.flush();
				logger.info("New row in " + outputPath);
				String info = "";
				for (String s : data) {
					info = info + " - " + s;
				}
				logger.info(info);

				total[0] = total[0] + summaries.get("Cases");
				total[1] = total[1] + summaries.get("BayesCentralOK");
				total[2] = total[2] + summaries.get("ArgumentationOK");
				total[3] = total[3] + summaries.get("BayesCentralBetter");
				total[4] = total[4] + summaries.get("ArgumentationBetter");
				total[5] = total[5] + summaries.get("Draw");
			}
			String[] totalData = new String[8];
			totalData[0] = simulationName;
			totalData[1] = "TOTAL";
			for (int i = 0; i < total.length; i++) {
				totalData[i + 2] = Integer.toString(total[i]);
			}
			writer.writeRecord(totalData);
			writer.flush();
			logger.info("TOTAL row in " + outputPath);
			String info = "";
			for (String s : totalData) {
				info = info + " - " + s;
			}
			logger.info(info);

			String[] totalRatio = new String[8];
			totalRatio[0] = simulationName;
			totalRatio[1] = "RATIO-TOTAL";
			for (int i = 0; i < total.length; i++) {
				double aux = new Double(total[i]) / total[0];
				totalRatio[i + 2] = Double.toString(aux);
			}
			writer.writeRecord(totalRatio);
			writer.flush();
			logger.info("RATIO-TOTAL row in " + outputPath);
			info = "";
			for (String s : totalRatio) {
				info = info + " - " + s;
			}
			logger.info(info);

			writer.close();

		} catch (IOException e) {
			logger.warning("Impossible to create summary file for simulation: "
					+ simulationName + " in file: " + outputPath);
			e.printStackTrace();
		}

	}

	public static void launchSimulationBasic2Agents(long seed,
			String summaryFile) {
		// Simulation properties
		String simulationName = "SolarFlareClassificatorScenario"
				+ "-2AgentsHigherResolution-" + seed + "-timestamp-"
				+ System.currentTimeMillis();

		// Logging properties
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		Level level = Level.ALL;
		String name = "NoGUI-" + simulationName;
		String experimentDatasetPath = "src" + File.separator + "main"
				+ File.separator + "resources" + File.separator + "exp1";
		String experimentOutputPath = experimentDatasetPath + File.separator
				+ "output" + File.separator + simulationName;
		LogConfigurator.log2File(logger, name, level, experimentOutputPath);

		logger.info("--> Configuring simulation...");

		Properties scenarioProperties = new Properties();
		scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.NO_GUI);
		scenarioProperties.put(SolarFlareClassificationSimulation.EXPDATA,
				experimentDatasetPath);
		scenarioProperties.put(SolarFlareClassificationSimulation.EXPOUTPUT,
				experimentOutputPath);

		List<ShanksAgent> agents = new ArrayList<ShanksAgent>();

		// CENTRAL AGENT
		SolarFlareBayesCentralAgent bayes = new SolarFlareBayesCentralAgent(
				"BayesCentral", experimentDatasetPath
						+ "/bayes/agentdataset-central.net");
		agents.add(bayes);

		// Argumentation AGENTS
		AdvancedCentralManagerAgent manager = new AdvancedCentralManagerAgent(
				"Manager", experimentOutputPath);
		scenarioProperties.put("ManagerAgent", manager);

		List<String> sensors1 = new ArrayList<String>();
		sensors1.add(Activity.class.getSimpleName());
		sensors1.add(LargestSpotSize.class.getSimpleName());
		sensors1.add(Area.class.getSimpleName());
		sensors1.add(BecomeHist.class.getSimpleName());
		sensors1.add(SpotDistribution.class.getSimpleName());
		sensors1.add(Evolution.class.getSimpleName());
		BasicClassificatorAgent agent1 = new BasicClassificatorAgent(
				"ArgAgent1", manager, experimentDatasetPath
						+ "/bayes/agentdataset-1.net", sensors1);
		agents.add(agent1);

		List<String> sensors2 = new ArrayList<String>();
		sensors2.add(PrevStatus24Hour.class.getSimpleName());
		sensors2.add(HistComplex.class.getSimpleName());
		sensors2.add(CNode.class.getSimpleName());
		sensors2.add(MNode.class.getSimpleName());
		sensors2.add(XNode.class.getSimpleName());
		BasicClassificatorAgent agent2 = new BasicClassificatorAgent(
				"ArgAgent2", manager, experimentDatasetPath
						+ "/bayes/agentdataset-2.net", sensors2);
		agents.add(agent2);

		scenarioProperties.put("AGENTS", agents);

		logger.info("--> Simulation configured");

		SolarFlareClassificationSimulation sim;
		try {
			sim = new SolarFlareClassificationSimulation(seed,
					SolarFlareScenario.class, simulationName,
					SolarFlareScenario.NORMALSTATE, scenarioProperties);

			logger.info("--> Launching simulation...");
			sim.start();
			do
				if (!sim.schedule.step(sim)) {
					break;
				}
			while (true);
			// while (sim.schedule.getSteps() < totalSteps);
			// sim.finish();

			SolarFlareNoGUILauncher.makeNumbers(simulationName,
					experimentOutputPath + File.separator + "summary.csv",
					summaryFile);
		} catch (ShanksException e) {
			e.printStackTrace();
		}
	}

	public static void launchSimulationBasic3Agents(long seed,
			String summaryFile) {
		// Simulation properties
		String simulationName = "SolarFlareClassificatorScenario"
				+ "-3AgentsHigherResolution-" + seed + "-timestamp-"
				+ System.currentTimeMillis();

		// Logging properties
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		Level level = Level.ALL;
		String name = "NoGUI-" + simulationName;
		String experimentDatasetPath = "src" + File.separator + "main"
				+ File.separator + "resources" + File.separator + "exp1";
		String experimentOutputPath = experimentDatasetPath + File.separator
				+ "output" + File.separator + simulationName;
		LogConfigurator.log2File(logger, name, level, experimentOutputPath);

		logger.info("--> Starting simulation...");

		Properties scenarioProperties = new Properties();
		scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.NO_GUI);
		scenarioProperties.put(SolarFlareClassificationSimulation.EXPDATA,
				experimentDatasetPath);
		scenarioProperties.put(SolarFlareClassificationSimulation.EXPOUTPUT,
				experimentOutputPath);

		List<ShanksAgent> agents = new ArrayList<ShanksAgent>();

		// CENTRAL AGENT
		SolarFlareBayesCentralAgent bayes = new SolarFlareBayesCentralAgent(
				"BayesCentral", experimentDatasetPath
						+ "/bayes/agentdataset-central.net");
		agents.add(bayes);

		// Argumentation AGENTS
		AdvancedCentralManagerAgent manager = new AdvancedCentralManagerAgent(
				"Manager", experimentOutputPath);
		scenarioProperties.put("ManagerAgent", manager);

		List<String> sensors1 = new ArrayList<String>();
		sensors1.add(Activity.class.getSimpleName());
		sensors1.add(LargestSpotSize.class.getSimpleName());
		sensors1.add(Area.class.getSimpleName());
		sensors1.add(BecomeHist.class.getSimpleName());
		sensors1.add(SpotDistribution.class.getSimpleName());
		sensors1.add(Evolution.class.getSimpleName());
		BasicClassificatorAgent agent1 = new BasicClassificatorAgent(
				"ArgAgent1", manager, experimentDatasetPath
						+ "/bayes/agentdataset-1.net", sensors1);
		agents.add(agent1);

		List<String> sensors2 = new ArrayList<String>();
		sensors2.add(PrevStatus24Hour.class.getSimpleName());
		sensors2.add(HistComplex.class.getSimpleName());
		BasicClassificatorAgent agent2 = new BasicClassificatorAgent(
				"ArgAgent2", manager, experimentDatasetPath
						+ "/bayes/agentdataset-2.net", sensors2);
		agents.add(agent2);

		List<String> sensors3 = new ArrayList<String>();
		sensors3.add(CNode.class.getSimpleName());
		sensors3.add(MNode.class.getSimpleName());
		sensors3.add(XNode.class.getSimpleName());
		BasicClassificatorAgent agent3 = new BasicClassificatorAgent(
				"ArgAgent3", manager,
				"src/main/resources/exp1/bayes/agentdataset-central.net",
				sensors3);
		agents.add(agent3);

		scenarioProperties.put("AGENTS", agents);

		SolarFlareClassificationSimulation sim;
		try {
			sim = new SolarFlareClassificationSimulation(seed,
					SolarFlareScenario.class, simulationName,
					SolarFlareScenario.NORMALSTATE, scenarioProperties);

			sim.start();
			do
				if (!sim.schedule.step(sim)) {
					break;
				}
			while (true);
			// while (sim.schedule.getSteps() < totalSteps);
			// sim.finish();
			SolarFlareNoGUILauncher.makeNumbers(simulationName,
					experimentOutputPath + File.separator + "summary.csv",
					summaryFile);
		} catch (ShanksException e) {
			e.printStackTrace();
		}
	}

	public static void launchSimulationBasic4Agents(long seed,
			String summaryFile) {
		// Simulation properties
		String simulationName = "SolarFlareClassificatorScenario"
				+ "-4AgentsHigherResolution-" + seed + "-timestamp-"
				+ System.currentTimeMillis();

		// Logging properties
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		Level level = Level.ALL;
		String name = "NoGUI-" + simulationName;
		String experimentDatasetPath = "src" + File.separator + "main"
				+ File.separator + "resources" + File.separator + "exp1";
		String experimentOutputPath = experimentDatasetPath + File.separator
				+ "output" + File.separator + simulationName;
		LogConfigurator.log2File(logger, name, level, experimentOutputPath);

		logger.info("--> Starting simulation...");

		Properties scenarioProperties = new Properties();
		scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.NO_GUI);
		scenarioProperties.put(SolarFlareClassificationSimulation.EXPDATA,
				experimentDatasetPath);
		scenarioProperties.put(SolarFlareClassificationSimulation.EXPOUTPUT,
				experimentOutputPath);

		List<ShanksAgent> agents = new ArrayList<ShanksAgent>();

		// CENTRAL AGENT
		SolarFlareBayesCentralAgent bayes = new SolarFlareBayesCentralAgent(
				"BayesCentral", experimentDatasetPath
						+ "/bayes/agentdataset-central.net");
		agents.add(bayes);

		// Argumentation AGENTS
		AdvancedCentralManagerAgent manager = new AdvancedCentralManagerAgent(
				"Manager", experimentOutputPath);
		scenarioProperties.put("ManagerAgent", manager);

		List<String> sensors1 = new ArrayList<String>();
		sensors1.add(Activity.class.getSimpleName());
		sensors1.add(LargestSpotSize.class.getSimpleName());
		sensors1.add(Area.class.getSimpleName());
		BasicClassificatorAgent agent1 = new BasicClassificatorAgent(
				"ArgAgent1", manager, experimentDatasetPath
						+ "/bayes/agentdataset-1.net", sensors1);
		agents.add(agent1);

		List<String> sensors2 = new ArrayList<String>();
		sensors2.add(PrevStatus24Hour.class.getSimpleName());
		sensors2.add(HistComplex.class.getSimpleName());
		BasicClassificatorAgent agent2 = new BasicClassificatorAgent(
				"ArgAgent2", manager, experimentDatasetPath
						+ "/bayes/agentdataset-2.net", sensors2);
		agents.add(agent2);

		List<String> sensors3 = new ArrayList<String>();
		sensors3.add(CNode.class.getSimpleName());
		sensors3.add(MNode.class.getSimpleName());
		sensors3.add(XNode.class.getSimpleName());
		BasicClassificatorAgent agent3 = new BasicClassificatorAgent(
				"ArgAgent3", manager,
				"src/main/resources/exp1/bayes/agentdataset-central.net",
				sensors3);
		agents.add(agent3);

		List<String> sensors4 = new ArrayList<String>();
		sensors1.add(BecomeHist.class.getSimpleName());
		sensors1.add(SpotDistribution.class.getSimpleName());
		sensors1.add(Evolution.class.getSimpleName());
		BasicClassificatorAgent agent4 = new BasicClassificatorAgent(
				"ArgAgent4", manager,
				"src/main/resources/knowledge/flare-all-data.net", sensors4);
		agents.add(agent4);

		scenarioProperties.put("AGENTS", agents);

		SolarFlareClassificationSimulation sim;
		try {
			sim = new SolarFlareClassificationSimulation(seed,
					SolarFlareScenario.class, simulationName,
					SolarFlareScenario.NORMALSTATE, scenarioProperties);

			sim.start();
			do
				if (!sim.schedule.step(sim)) {
					break;
				}
			while (true);
			// while (sim.schedule.getSteps() < totalSteps);
			// sim.finish();
			SolarFlareNoGUILauncher.makeNumbers(simulationName,
					experimentOutputPath + File.separator + "summary.csv",
					summaryFile);
		} catch (ShanksException e) {
			e.printStackTrace();
		}
	}
}
