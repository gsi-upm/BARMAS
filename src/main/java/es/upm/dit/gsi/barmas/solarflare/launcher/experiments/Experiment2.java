/**
 * es.upm.dit.gsi.barmas.solarflare.launcher.experiments.Experiment1.java
 */
package es.upm.dit.gsi.barmas.solarflare.launcher.experiments;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.upm.dit.gsi.barmas.solarflare.agent.SolarFlareBayesCentralAgent;
import es.upm.dit.gsi.barmas.solarflare.agent.advancedWithAssumptions.AdvancedWACentralManagerAgent;
import es.upm.dit.gsi.barmas.solarflare.agent.advancedWithAssumptions.AdvancedWAClassificatorAgent;
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
 * es.upm.dit.gsi.barmas.solarflare.launcher.experiments.Experiment2.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 16/10/2013
 * @version 0.1
 * 
 */
public class Experiment2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String summaryFile = "src/main/resources/exp2/output/global-summary.csv";
		long seed = 0;

		Experiment2.launchSimulationWith2Agents(seed, summaryFile);
		Experiment2.launchSimulationWith2AgentsKFold(seed, summaryFile);
	}

	private static void launchSimulationWith2Agents(long seed,
			String summaryFile) {
		// Simulation properties
		String simulationName = "SolarFlareClassificatorScenario"
				+ "-2AgentsWithAssumptionsHigherHypothesisConflictResolution-"
				+ seed + "-timestamp-" + System.currentTimeMillis();

		// Logging properties
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		Level level = Level.ALL;
		String experimentDatasetPath = "src" + File.separator + "main"
				+ File.separator + "resources" + File.separator + "exp2";
		String experimentOutputPath = experimentDatasetPath + File.separator
				+ "output" + File.separator + simulationName;
		LogConfigurator.log2File(logger, simulationName, level,
				experimentOutputPath);

		logger.info("--> Configuring simulation...");

		Properties scenarioProperties = new Properties();
		scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.NO_GUI);
		scenarioProperties.put(SolarFlareClassificationSimulation.EXPDATA,
				experimentDatasetPath);
		scenarioProperties.put(SolarFlareClassificationSimulation.EXPOUTPUT,
				experimentOutputPath);

		List<ShanksAgent> agents = new ArrayList<ShanksAgent>();

		// CENTRAL AGENT
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
		SolarFlareBayesCentralAgent bayes = new SolarFlareBayesCentralAgent(
				"BayesCentral", experimentDatasetPath
						+ "/bayes/agentdataset-central.net", sensors);
		agents.add(bayes);

		// Argumentation AGENTS
		AdvancedWACentralManagerAgent manager = new AdvancedWACentralManagerAgent(
				"Manager", experimentOutputPath);
		scenarioProperties.put("ManagerAgent", manager);

		sensors = new ArrayList<String>();
		sensors.add(Activity.class.getSimpleName());
		sensors.add(LargestSpotSize.class.getSimpleName());
		sensors.add(Area.class.getSimpleName());
		sensors.add(BecomeHist.class.getSimpleName());
		sensors.add(SpotDistribution.class.getSimpleName());
		sensors.add(Evolution.class.getSimpleName());
		AdvancedWAClassificatorAgent agent = new AdvancedWAClassificatorAgent(
				"ArgAgent1", manager, experimentDatasetPath
						+ "/bayes/agentdataset-1.net", sensors);
		agents.add(agent);

		sensors = new ArrayList<String>();
		sensors.add(PrevStatus24Hour.class.getSimpleName());
		sensors.add(HistComplex.class.getSimpleName());
		sensors.add(CNode.class.getSimpleName());
		sensors.add(MNode.class.getSimpleName());
		sensors.add(XNode.class.getSimpleName());
		agent = new AdvancedWAClassificatorAgent("ArgAgent2", manager,
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

			SummaryCreator.makeNumbers(simulationName, experimentOutputPath
					+ File.separator + "summary.csv", summaryFile);
		} catch (ShanksException e) {
			e.printStackTrace();
		}
	}

	private static void launchSimulationWith2AgentsKFold(long seed,
			String summaryFile) {
		// Simulation properties
		String simulationName = "SolarFlareClassificatorScenario"
				+ "-2AgentsWithAssumptionsHigherHypothesisConflictResolution-"
				+ seed + "-KFold10TRAININNG-timestamp-"
				+ System.currentTimeMillis();

		// Logging properties
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		Level level = Level.ALL;
		String experimentDatasetPath = "src" + File.separator + "main"
				+ File.separator + "resources" + File.separator + "exp2";
		String experimentOutputPath = experimentDatasetPath + File.separator
				+ "output" + File.separator + simulationName;
		LogConfigurator.log2File(logger, simulationName, level,
				experimentOutputPath);

		logger.info("--> Configuring simulation...");

		Properties scenarioProperties = new Properties();
		scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.NO_GUI);
		scenarioProperties.put(SolarFlareClassificationSimulation.EXPDATA,
				experimentDatasetPath);
		scenarioProperties.put(SolarFlareClassificationSimulation.EXPOUTPUT,
				experimentOutputPath);

		List<ShanksAgent> agents = new ArrayList<ShanksAgent>();

		// CENTRAL AGENT
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
		SolarFlareBayesCentralAgent bayes = new SolarFlareBayesCentralAgent(
				"BayesCentral", experimentDatasetPath
						+ "/bayes/k-fold-10/agentdataset-central.net", sensors);
		agents.add(bayes);

		// Argumentation AGENTS
		AdvancedWACentralManagerAgent manager = new AdvancedWACentralManagerAgent(
				"Manager", experimentOutputPath);
		scenarioProperties.put("ManagerAgent", manager);

		sensors = new ArrayList<String>();
		sensors.add(Activity.class.getSimpleName());
		sensors.add(LargestSpotSize.class.getSimpleName());
		sensors.add(Area.class.getSimpleName());
		sensors.add(BecomeHist.class.getSimpleName());
		sensors.add(SpotDistribution.class.getSimpleName());
		sensors.add(Evolution.class.getSimpleName());
		AdvancedWAClassificatorAgent agent = new AdvancedWAClassificatorAgent(
				"ArgAgent1", manager, experimentDatasetPath
						+ "/bayes/k-fold-10/agentdataset-1.net", sensors);
		agents.add(agent);

		sensors = new ArrayList<String>();
		sensors.add(PrevStatus24Hour.class.getSimpleName());
		sensors.add(HistComplex.class.getSimpleName());
		sensors.add(CNode.class.getSimpleName());
		sensors.add(MNode.class.getSimpleName());
		sensors.add(XNode.class.getSimpleName());
		agent = new AdvancedWAClassificatorAgent("ArgAgent2", manager,
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

			SummaryCreator.makeNumbers(simulationName, experimentOutputPath
					+ File.separator + "summary.csv", summaryFile);
		} catch (ShanksException e) {
			e.printStackTrace();
		}
	}

}
