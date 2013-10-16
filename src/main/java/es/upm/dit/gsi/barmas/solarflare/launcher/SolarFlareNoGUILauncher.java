/**
 * es.upm.dit.gsi.barmas.simulation.SolarFlareNoGUILauncher.java
 */
package es.upm.dit.gsi.barmas.solarflare.launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.upm.dit.gsi.barmas.solarflare.agent.SolarFlareBayesCentralAgent;
import es.upm.dit.gsi.barmas.solarflare.agent.advanced.AdvancedCentralManagerAgent;
import es.upm.dit.gsi.barmas.solarflare.agent.advanced.AdvancedClassificatorAgent;
import es.upm.dit.gsi.barmas.solarflare.agent.basic.BasicCentralManagerAgent;
import es.upm.dit.gsi.barmas.solarflare.agent.basic.BasicClassificatorAgent;
import es.upm.dit.gsi.barmas.solarflare.launcher.logging.LogConfigurator;
import es.upm.dit.gsi.barmas.solarflare.launcher.utils.SummaryCreator;
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

//		SolarFlareNoGUILauncher.launchSimulationBasic1Agent1(seed, summaryFile);
//		SolarFlareNoGUILauncher.launchSimulationBasic1Agent2(seed, summaryFile);
//
//		SolarFlareNoGUILauncher.launchSimulationBasic2AgentsKFold(seed,
//				summaryFile);
//
		SolarFlareNoGUILauncher.launchSimulationBasic2Agents(seed, summaryFile);
//		SolarFlareNoGUILauncher.launchSimulationBasic3Agents(seed, summaryFile);
//		SolarFlareNoGUILauncher.launchSimulationBasic4Agents(seed, summaryFile);

		SolarFlareNoGUILauncher.launchSimulationAdvanced2Agents(seed,
				summaryFile);
		// SolarFlareNoGUILauncher.launchSimulationAdvanced3Agents(seed,
		// summaryFile);
		// SolarFlareNoGUILauncher.launchSimulationAdvanced4Agents(seed,
		// summaryFile);
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
		BasicCentralManagerAgent manager = new BasicCentralManagerAgent(
				"Manager", experimentOutputPath);
		scenarioProperties.put("ManagerAgent", manager);

		List<String> sensors = new ArrayList<String>();
		sensors.add(Activity.class.getSimpleName());
		sensors.add(LargestSpotSize.class.getSimpleName());
		sensors.add(Area.class.getSimpleName());
		sensors.add(BecomeHist.class.getSimpleName());
		sensors.add(SpotDistribution.class.getSimpleName());
		sensors.add(Evolution.class.getSimpleName());
		BasicClassificatorAgent agent = new BasicClassificatorAgent(
				"ArgAgent1", manager, experimentDatasetPath
						+ "/bayes/agentdataset-1.net", sensors);
		agents.add(agent);

		sensors = new ArrayList<String>();
		sensors.add(PrevStatus24Hour.class.getSimpleName());
		sensors.add(HistComplex.class.getSimpleName());
		sensors.add(CNode.class.getSimpleName());
		sensors.add(MNode.class.getSimpleName());
		sensors.add(XNode.class.getSimpleName());
		agent = new BasicClassificatorAgent("ArgAgent2", manager,
				experimentDatasetPath + "/bayes/agentdataset-2.net", sensors);
		agents.add(agent);

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

			SummaryCreator.makeNumbers(simulationName,
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
		BasicCentralManagerAgent manager = new BasicCentralManagerAgent(
				"Manager", experimentOutputPath);
		scenarioProperties.put("ManagerAgent", manager);

		List<String> sensors = new ArrayList<String>();
		sensors.add(Activity.class.getSimpleName());
		sensors.add(LargestSpotSize.class.getSimpleName());
		sensors.add(Area.class.getSimpleName());
		sensors.add(BecomeHist.class.getSimpleName());
		sensors.add(SpotDistribution.class.getSimpleName());
		sensors.add(Evolution.class.getSimpleName());
		BasicClassificatorAgent agent = new BasicClassificatorAgent(
				"ArgAgent1", manager, experimentDatasetPath
						+ "/bayes/agentdataset-1.net", sensors);
		agents.add(agent);

		sensors = new ArrayList<String>();
		sensors.add(PrevStatus24Hour.class.getSimpleName());
		sensors.add(HistComplex.class.getSimpleName());
		agent = new BasicClassificatorAgent("ArgAgent2", manager,
				experimentDatasetPath + "/bayes/agentdataset-2.net", sensors);
		agents.add(agent);

		sensors = new ArrayList<String>();
		sensors.add(CNode.class.getSimpleName());
		sensors.add(MNode.class.getSimpleName());
		sensors.add(XNode.class.getSimpleName());
		agent = new BasicClassificatorAgent("ArgAgent3", manager,
				"src/main/resources/exp1/bayes/agentdataset-central.net",
				sensors);
		agents.add(agent);

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
			SummaryCreator.makeNumbers(simulationName,
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
		BasicCentralManagerAgent manager = new BasicCentralManagerAgent(
				"Manager", experimentOutputPath);
		scenarioProperties.put("ManagerAgent", manager);

		List<String> sensors = new ArrayList<String>();
		sensors.add(Activity.class.getSimpleName());
		sensors.add(LargestSpotSize.class.getSimpleName());
		sensors.add(Area.class.getSimpleName());
		BasicClassificatorAgent agent = new BasicClassificatorAgent(
				"ArgAgent1", manager, experimentDatasetPath
						+ "/bayes/agentdataset-1.net", sensors);
		agents.add(agent);

		sensors = new ArrayList<String>();
		sensors.add(PrevStatus24Hour.class.getSimpleName());
		sensors.add(HistComplex.class.getSimpleName());
		agent = new BasicClassificatorAgent("ArgAgent2", manager,
				experimentDatasetPath + "/bayes/agentdataset-2.net", sensors);
		agents.add(agent);

		sensors = new ArrayList<String>();
		sensors.add(CNode.class.getSimpleName());
		sensors.add(MNode.class.getSimpleName());
		sensors.add(XNode.class.getSimpleName());
		agent = new BasicClassificatorAgent("ArgAgent3", manager,
				"src/main/resources/exp1/bayes/agentdataset-central.net",
				sensors);
		agents.add(agent);

		sensors = new ArrayList<String>();
		sensors.add(BecomeHist.class.getSimpleName());
		sensors.add(SpotDistribution.class.getSimpleName());
		sensors.add(Evolution.class.getSimpleName());
		agent = new BasicClassificatorAgent("ArgAgent4", manager,
				"src/main/resources/knowledge/flare-all-data.net", sensors);
		agents.add(agent);

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
			SummaryCreator.makeNumbers(simulationName,
					experimentOutputPath + File.separator + "summary.csv",
					summaryFile);
		} catch (ShanksException e) {
			e.printStackTrace();
		}
	}

	public static void launchSimulationBasic1Agent1(long seed,
			String summaryFile) {
		// Simulation properties
		String simulationName = "SolarFlareClassificatorScenario"
				+ "-1AgentsHigherResolution-" + seed + "-timestamp-"
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
		BasicCentralManagerAgent manager = new BasicCentralManagerAgent(
				"Manager", experimentOutputPath);
		scenarioProperties.put("ManagerAgent", manager);

		List<String> sensors = new ArrayList<String>();
		sensors.add(Activity.class.getSimpleName());
		sensors.add(LargestSpotSize.class.getSimpleName());
		sensors.add(Area.class.getSimpleName());
		sensors.add(BecomeHist.class.getSimpleName());
		sensors.add(SpotDistribution.class.getSimpleName());
		sensors.add(Evolution.class.getSimpleName());
		sensors.add(PrevStatus24Hour.class.getSimpleName());
		sensors.add(HistComplex.class.getSimpleName());
		sensors.add(CNode.class.getSimpleName());
		sensors.add(MNode.class.getSimpleName());
		sensors.add(XNode.class.getSimpleName());
		BasicClassificatorAgent agent = new BasicClassificatorAgent(
				"ArgAgent1", manager, experimentDatasetPath
						+ "/bayes/agentdataset-1.net", sensors);
		agents.add(agent);

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

			SummaryCreator.makeNumbers(simulationName,
					experimentOutputPath + File.separator + "summary.csv",
					summaryFile);
		} catch (ShanksException e) {
			e.printStackTrace();
		}
	}

	public static void launchSimulationBasic1Agent2(long seed,
			String summaryFile) {
		// Simulation properties
		String simulationName = "SolarFlareClassificatorScenario"
				+ "-1AgentsHigherResolution-" + seed + "-timestamp-"
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
		BasicCentralManagerAgent manager = new BasicCentralManagerAgent(
				"Manager", experimentOutputPath);
		scenarioProperties.put("ManagerAgent", manager);

		List<String> sensors = new ArrayList<String>();
		sensors.add(Activity.class.getSimpleName());
		sensors.add(LargestSpotSize.class.getSimpleName());
		sensors.add(Area.class.getSimpleName());
		sensors.add(BecomeHist.class.getSimpleName());
		sensors.add(SpotDistribution.class.getSimpleName());
		sensors.add(Evolution.class.getSimpleName());
		sensors.add(PrevStatus24Hour.class.getSimpleName());
		sensors.add(HistComplex.class.getSimpleName());
		sensors.add(CNode.class.getSimpleName());
		sensors.add(MNode.class.getSimpleName());
		sensors.add(XNode.class.getSimpleName());
		BasicClassificatorAgent agent = new BasicClassificatorAgent(
				"ArgAgent2", manager, experimentDatasetPath
						+ "/bayes/agentdataset-2.net", sensors);
		agents.add(agent);

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

			SummaryCreator.makeNumbers(simulationName,
					experimentOutputPath + File.separator + "summary.csv",
					summaryFile);
		} catch (ShanksException e) {
			e.printStackTrace();
		}
	}

	public static void launchSimulationBasic2AgentsKFold(long seed,
			String summaryFile) {
		// Simulation properties
		String simulationName = "SolarFlareClassificatorScenario"
				+ "-2AgentsKFold-HigherResolution-" + seed + "-timestamp-"
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
						+ "/bayes/k-fold-10/agentdataset-central.net");
		agents.add(bayes);

		// Argumentation AGENTS
		BasicCentralManagerAgent manager = new BasicCentralManagerAgent(
				"Manager", experimentOutputPath);
		scenarioProperties.put("ManagerAgent", manager);

		List<String> sensors = new ArrayList<String>();
		sensors.add(Activity.class.getSimpleName());
		sensors.add(LargestSpotSize.class.getSimpleName());
		sensors.add(Area.class.getSimpleName());
		sensors.add(BecomeHist.class.getSimpleName());
		sensors.add(SpotDistribution.class.getSimpleName());
		sensors.add(Evolution.class.getSimpleName());
		BasicClassificatorAgent agent = new BasicClassificatorAgent(
				"ArgAgent1", manager, experimentDatasetPath
						+ "/bayes/k-fold-10/agentdataset-1.net", sensors);
		agents.add(agent);

		sensors = new ArrayList<String>();
		sensors.add(PrevStatus24Hour.class.getSimpleName());
		sensors.add(HistComplex.class.getSimpleName());
		sensors.add(CNode.class.getSimpleName());
		sensors.add(MNode.class.getSimpleName());
		sensors.add(XNode.class.getSimpleName());
		agent = new BasicClassificatorAgent("ArgAgent2", manager,
				experimentDatasetPath + "/bayes/k-fold-10/agentdataset-2.net",
				sensors);
		agents.add(agent);

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

			SummaryCreator.makeNumbers(simulationName,
					experimentOutputPath + File.separator + "summary.csv",
					summaryFile);
		} catch (ShanksException e) {
			e.printStackTrace();
		}
	}

	public static void launchSimulationAdvanced2Agents(long seed,
			String summaryFile) {
		// Simulation properties
		String simulationName = "SolarFlareClassificatorScenario"
				+ "-2AdvancedAgentsHigherResolution-" + seed + "-timestamp-"
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

		List<String> sensors = new ArrayList<String>();
		sensors.add(Activity.class.getSimpleName());
		sensors.add(LargestSpotSize.class.getSimpleName());
		sensors.add(Area.class.getSimpleName());
		sensors.add(BecomeHist.class.getSimpleName());
		sensors.add(SpotDistribution.class.getSimpleName());
		sensors.add(Evolution.class.getSimpleName());
		AdvancedClassificatorAgent agent = new AdvancedClassificatorAgent(
				"ArgAgent1", manager, experimentDatasetPath
						+ "/bayes/agentdataset-1.net", sensors);
		agents.add(agent);

		sensors = new ArrayList<String>();
		sensors.add(PrevStatus24Hour.class.getSimpleName());
		sensors.add(HistComplex.class.getSimpleName());
		sensors.add(CNode.class.getSimpleName());
		sensors.add(MNode.class.getSimpleName());
		sensors.add(XNode.class.getSimpleName());
		agent = new AdvancedClassificatorAgent("ArgAgent2", manager,
				experimentDatasetPath + "/bayes/agentdataset-2.net", sensors);
		agents.add(agent);

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

			SummaryCreator.makeNumbers(simulationName,
					experimentOutputPath + File.separator + "summary.csv",
					summaryFile);
		} catch (ShanksException e) {
			e.printStackTrace();
		}

	}

}
