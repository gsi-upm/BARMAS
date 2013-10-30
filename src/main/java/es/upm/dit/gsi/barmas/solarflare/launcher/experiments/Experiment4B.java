/*******************************************************************************
 * Copyright (c) 2013 alvarocarrera Grupo de Sistemas Inteligentes - Universidad Politécnica de Madrid. (GSI-UPM)
 * http://www.gsi.dit.upm.es/
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * 
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 * 
 * Contributors:
 *     alvarocarrera - initial API and implementation
 ******************************************************************************/
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
import es.upm.dit.gsi.barmas.solarflare.agent.advanced.assumptions.AdvancedWACentralManagerAgent;
import es.upm.dit.gsi.barmas.solarflare.agent.advanced.assumptions.AdvancedWAClassificatorAgent;
import es.upm.dit.gsi.barmas.solarflare.launcher.logging.LogConfigurator;
import es.upm.dit.gsi.barmas.solarflare.launcher.utils.SimulationConfiguration;
import es.upm.dit.gsi.barmas.solarflare.launcher.utils.SummaryCreator;
import es.upm.dit.gsi.barmas.solarflare.model.scenario.SolarFlareScenario;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.Activity;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.Area;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.BecomeHist;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.CNode;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.HistComplex;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.LargestSpotSize;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.MNode;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.PrevStatus24Hour;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.SolarFlareType;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.SpotDistribution;
import es.upm.dit.gsi.barmas.solarflare.simulation.SolarFlareClassificationSimulation;
import es.upm.dit.gsi.shanks.agent.ShanksAgent;
import es.upm.dit.gsi.shanks.exception.ShanksException;
import es.upm.dit.gsi.shanks.model.scenario.Scenario;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.solarflare.launcher.experiments.Experiment4B.java
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
public class Experiment4B implements Runnable {

	private String summaryFile;
	private long seed;
	private double threshold;
	private double beliefThreshold;
	private int mode;
	private boolean validation;

	public Experiment4B(String summaryFile, long seed, double threshold,
			double beliefThreshold, int mode, boolean validation) {
		this.summaryFile = summaryFile;
		this.seed = seed;
		this.threshold = threshold;
		this.beliefThreshold = beliefThreshold;
		this.mode = mode;
		this.validation = validation;

	}

	private void launchValidationAgent1(long seed, String summaryFile,
			double threshold, double beliefThreshold, int mode) {
		// Simulation properties
		String simulationName = "EXPERIMENT-4-validationAgent1-TH-" + threshold
				+ "-BTH-" + beliefThreshold + "-seed-" + seed + "-timestamp-"
				+ System.currentTimeMillis();

		// Logging properties
		Logger logger = Logger.getLogger(simulationName);
		Level level = Level.ALL;
		String experimentDatasetPath = "src" + File.separator + "main"
				+ File.separator + "resources" + File.separator + "exp4";
		String experimentOutputPath = "output" + File.separator
				+ simulationName;
		LogConfigurator.log2File(logger, simulationName, level,
				experimentOutputPath);

		logger.info("--> Configuring simulation...");

		Properties scenarioProperties = new Properties();
		scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.NO_GUI);
		scenarioProperties.put(SimulationConfiguration.EXPDATA,
				experimentDatasetPath);
		scenarioProperties.put(SimulationConfiguration.EXPOUTPUT,
				experimentOutputPath);
		scenarioProperties.put(SimulationConfiguration.MODE, mode);

		List<ShanksAgent> agents = new ArrayList<ShanksAgent>();

		// CENTRAL AGENT
		List<String> sensors = new ArrayList<String>();
		sensors.add(Activity.class.getSimpleName());
		sensors.add(LargestSpotSize.class.getSimpleName());
		sensors.add(Area.class.getSimpleName());
		sensors.add(BecomeHist.class.getSimpleName());
		 sensors.add(SpotDistribution.class.getSimpleName());
		// sensors.add(Evolution.class.getSimpleName());
		sensors.add(PrevStatus24Hour.class.getSimpleName());
		sensors.add(HistComplex.class.getSimpleName());
		 sensors.add(CNode.class.getSimpleName());
		 sensors.add(MNode.class.getSimpleName());
		// sensors.add(XNode.class.getSimpleName());
		SolarFlareBayesCentralAgent bayes = new SolarFlareBayesCentralAgent(
				"BayesCentral", experimentDatasetPath
						+ "/bayes/agentdataset-1.net", sensors, logger);
		agents.add(bayes);

		// Argumentation AGENTS

		AdvancedWACentralManagerAgent manager = new AdvancedWACentralManagerAgent(
				"Manager", experimentOutputPath, threshold, logger,
				(Integer) scenarioProperties.get(SimulationConfiguration.MODE));
		scenarioProperties.put("ManagerAgent", manager);
		AdvancedWAClassificatorAgent agent = new AdvancedWAClassificatorAgent(
				"ArgAgent1", manager, experimentDatasetPath
						+ "/bayes/agentdataset-1.net",
				SolarFlareType.class.getSimpleName(), experimentDatasetPath
						+ "/dataset/agentdataset-1.csv", sensors, threshold,
				beliefThreshold, logger);
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

	private void launchValidationAgent1KFold(long seed, String summaryFile,
			double threshold, double beliefThreshold, int mode) {
		// Simulation properties
		String simulationName = "EXPERIMENT-4-validationAgent1KFold-TH-" + threshold
				+ "-BTH-" + beliefThreshold + "-seed-" + seed + "-timestamp-"
				+ System.currentTimeMillis();
	
		// Logging properties
		Logger logger = Logger.getLogger(simulationName);
		Level level = Level.ALL;
		String experimentDatasetPath = "src" + File.separator + "main"
				+ File.separator + "resources" + File.separator + "exp4";
		String experimentOutputPath = "output" + File.separator
				+ simulationName;
		LogConfigurator.log2File(logger, simulationName, level,
				experimentOutputPath);
	
		logger.info("--> Configuring simulation...");
	
		Properties scenarioProperties = new Properties();
		scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.NO_GUI);
		scenarioProperties.put(SimulationConfiguration.EXPDATA,
				experimentDatasetPath);
		scenarioProperties.put(SimulationConfiguration.EXPOUTPUT,
				experimentOutputPath);
		scenarioProperties.put(SimulationConfiguration.MODE, mode);
	
		List<ShanksAgent> agents = new ArrayList<ShanksAgent>();
	
		// CENTRAL AGENT
		List<String> sensors = new ArrayList<String>();
		sensors.add(Activity.class.getSimpleName());
		sensors.add(LargestSpotSize.class.getSimpleName());
		sensors.add(Area.class.getSimpleName());
		sensors.add(BecomeHist.class.getSimpleName());
		 sensors.add(SpotDistribution.class.getSimpleName());
		// sensors.add(Evolution.class.getSimpleName());
		sensors.add(PrevStatus24Hour.class.getSimpleName());
		sensors.add(HistComplex.class.getSimpleName());
		 sensors.add(CNode.class.getSimpleName());
		 sensors.add(MNode.class.getSimpleName());
		// sensors.add(XNode.class.getSimpleName());
		SolarFlareBayesCentralAgent bayes = new SolarFlareBayesCentralAgent(
				"BayesCentral", experimentDatasetPath
				+ "/bayes/k-fold-10/agentdataset-1.net", sensors, logger);
		agents.add(bayes);
	
		// Argumentation AGENTS
	
		AdvancedWACentralManagerAgent manager = new AdvancedWACentralManagerAgent(
				"Manager", experimentOutputPath, threshold, logger,
				(Integer) scenarioProperties.get(SimulationConfiguration.MODE));
		scenarioProperties.put("ManagerAgent", manager);
		AdvancedWAClassificatorAgent agent = new AdvancedWAClassificatorAgent(
				"ArgAgent1", manager, experimentDatasetPath
				+ "/bayes/k-fold-10/agentdataset-1.net",
				SolarFlareType.class.getSimpleName(), experimentDatasetPath
						+ "/dataset/agentdataset-1.csv", sensors, threshold,
				beliefThreshold, logger);
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

	private void launchValidationAgent2(long seed, String summaryFile,
			double threshold, double beliefThreshold, int mode) {
		// Simulation properties
		String simulationName = "EXPERIMENT-4-validationAgent2-TH-" + threshold
				+ "-BTH-" + beliefThreshold + "-seed-" + seed + "-timestamp-"
				+ System.currentTimeMillis();

		// Logging properties
		Logger logger = Logger.getLogger(simulationName);
		Level level = Level.ALL;
		String experimentDatasetPath = "src" + File.separator + "main"
				+ File.separator + "resources" + File.separator + "exp4";
		String experimentOutputPath = "output" + File.separator
				+ simulationName;
		LogConfigurator.log2File(logger, simulationName, level,
				experimentOutputPath);

		logger.info("--> Configuring simulation...");

		Properties scenarioProperties = new Properties();
		scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.NO_GUI);
		scenarioProperties.put(SimulationConfiguration.EXPDATA,
				experimentDatasetPath);
		scenarioProperties.put(SimulationConfiguration.EXPOUTPUT,
				experimentOutputPath);
		scenarioProperties.put(SimulationConfiguration.MODE, mode);

		List<ShanksAgent> agents = new ArrayList<ShanksAgent>();

		// CENTRAL AGENT
		List<String> sensors = new ArrayList<String>();
		sensors.add(Activity.class.getSimpleName());
		sensors.add(LargestSpotSize.class.getSimpleName());
		sensors.add(Area.class.getSimpleName());
		sensors.add(BecomeHist.class.getSimpleName());
		 sensors.add(SpotDistribution.class.getSimpleName());
		// sensors.add(Evolution.class.getSimpleName());
		sensors.add(PrevStatus24Hour.class.getSimpleName());
		sensors.add(HistComplex.class.getSimpleName());
		 sensors.add(CNode.class.getSimpleName());
		 sensors.add(MNode.class.getSimpleName());
		// sensors.add(XNode.class.getSimpleName());
		SolarFlareBayesCentralAgent bayes = new SolarFlareBayesCentralAgent(
				"BayesCentral", experimentDatasetPath
						+ "/bayes/agentdataset-2.net", sensors, logger);
		agents.add(bayes);

		// Argumentation AGENTS

		AdvancedWACentralManagerAgent manager = new AdvancedWACentralManagerAgent(
				"Manager", experimentOutputPath, threshold, logger,
				(Integer) scenarioProperties.get(SimulationConfiguration.MODE));
		scenarioProperties.put("ManagerAgent", manager);
		AdvancedWAClassificatorAgent agent = new AdvancedWAClassificatorAgent(
				"ArgAgent2", manager, experimentDatasetPath
						+ "/bayes/agentdataset-2.net",
				SolarFlareType.class.getSimpleName(), experimentDatasetPath
						+ "/dataset/agentdataset-2.csv", sensors, threshold,
				beliefThreshold, logger);
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

	private void launchValidationAgent2KFold(long seed, String summaryFile,
			double threshold, double beliefThreshold, int mode) {
		// Simulation properties
		String simulationName = "EXPERIMENT-4-validationAgent2KFold-TH-" + threshold
				+ "-BTH-" + beliefThreshold + "-seed-" + seed + "-timestamp-"
				+ System.currentTimeMillis();
	
		// Logging properties
		Logger logger = Logger.getLogger(simulationName);
		Level level = Level.ALL;
		String experimentDatasetPath = "src" + File.separator + "main"
				+ File.separator + "resources" + File.separator + "exp4";
		String experimentOutputPath = "output" + File.separator
				+ simulationName;
		LogConfigurator.log2File(logger, simulationName, level,
				experimentOutputPath);
	
		logger.info("--> Configuring simulation...");
	
		Properties scenarioProperties = new Properties();
		scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.NO_GUI);
		scenarioProperties.put(SimulationConfiguration.EXPDATA,
				experimentDatasetPath);
		scenarioProperties.put(SimulationConfiguration.EXPOUTPUT,
				experimentOutputPath);
		scenarioProperties.put(SimulationConfiguration.MODE, mode);
	
		List<ShanksAgent> agents = new ArrayList<ShanksAgent>();
	
		// CENTRAL AGENT
		List<String> sensors = new ArrayList<String>();
		sensors.add(Activity.class.getSimpleName());
		sensors.add(LargestSpotSize.class.getSimpleName());
		sensors.add(Area.class.getSimpleName());
		sensors.add(BecomeHist.class.getSimpleName());
		 sensors.add(SpotDistribution.class.getSimpleName());
		// sensors.add(Evolution.class.getSimpleName());
		sensors.add(PrevStatus24Hour.class.getSimpleName());
		sensors.add(HistComplex.class.getSimpleName());
		 sensors.add(CNode.class.getSimpleName());
		 sensors.add(MNode.class.getSimpleName());
		// sensors.add(XNode.class.getSimpleName());
		SolarFlareBayesCentralAgent bayes = new SolarFlareBayesCentralAgent(
				"BayesCentral", experimentDatasetPath + "/bayes/k-fold-10/agentdataset-2.net", sensors, logger);
		agents.add(bayes);
	
		// Argumentation AGENTS
	
		AdvancedWACentralManagerAgent manager = new AdvancedWACentralManagerAgent(
				"Manager", experimentOutputPath, threshold, logger,
				(Integer) scenarioProperties.get(SimulationConfiguration.MODE));
		scenarioProperties.put("ManagerAgent", manager);
		AdvancedWAClassificatorAgent agent = new AdvancedWAClassificatorAgent(
				"ArgAgent2", manager, experimentDatasetPath + "/bayes/k-fold-10/agentdataset-2.net",
				SolarFlareType.class.getSimpleName(), experimentDatasetPath
						+ "/dataset/agentdataset-2.csv", sensors, threshold,
				beliefThreshold, logger);
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

	private void launchSimulationWith2Agents(long seed, String summaryFile,
			double threshold, double beliefThreshold, int mode) {
		// Simulation properties
		String simulationName = "EXPERIMENT-4-TH-" + threshold + "-BTH-"
				+ beliefThreshold + "-seed-" + seed + "-timestamp-"
				+ System.currentTimeMillis();

		// Logging properties
		Logger logger = Logger.getLogger(simulationName);
		Level level = Level.ALL;
		String experimentDatasetPath = "src" + File.separator + "main"
				+ File.separator + "resources" + File.separator + "exp4";
		String experimentOutputPath = "output" + File.separator
				+ simulationName;
		LogConfigurator.log2File(logger, simulationName, level,
				experimentOutputPath);

		logger.info("--> Configuring simulation...");

		Properties scenarioProperties = new Properties();
		scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.NO_GUI);
		scenarioProperties.put(SimulationConfiguration.EXPDATA,
				experimentDatasetPath);
		scenarioProperties.put(SimulationConfiguration.EXPOUTPUT,
				experimentOutputPath);
		scenarioProperties.put(SimulationConfiguration.MODE, mode);

		List<ShanksAgent> agents = new ArrayList<ShanksAgent>();

		// CENTRAL AGENT
		List<String> sensors = new ArrayList<String>();
		sensors.add(Activity.class.getSimpleName());
		sensors.add(LargestSpotSize.class.getSimpleName());
		sensors.add(Area.class.getSimpleName());
		sensors.add(BecomeHist.class.getSimpleName());
		 sensors.add(SpotDistribution.class.getSimpleName());
		// sensors.add(Evolution.class.getSimpleName());
		sensors.add(PrevStatus24Hour.class.getSimpleName());
		sensors.add(HistComplex.class.getSimpleName());
		 sensors.add(CNode.class.getSimpleName());
		 sensors.add(MNode.class.getSimpleName());
		// sensors.add(XNode.class.getSimpleName());
		SolarFlareBayesCentralAgent bayes = new SolarFlareBayesCentralAgent(
				"BayesCentral", experimentDatasetPath
						+ "/bayes/agentdataset-central.net", sensors, logger);
		agents.add(bayes);

		// Argumentation AGENTS

		AdvancedWACentralManagerAgent manager = new AdvancedWACentralManagerAgent(
				"Manager", experimentOutputPath, threshold, logger,
				(Integer) scenarioProperties.get(SimulationConfiguration.MODE));
		scenarioProperties.put("ManagerAgent", manager);

		sensors = new ArrayList<String>();
		sensors.add(Activity.class.getSimpleName());
		sensors.add(LargestSpotSize.class.getSimpleName());
		sensors.add(Area.class.getSimpleName());
		sensors.add(BecomeHist.class.getSimpleName());
		 sensors.add(SpotDistribution.class.getSimpleName());
		// sensors.add(Evolution.class.getSimpleName());
		AdvancedWAClassificatorAgent agent = new AdvancedWAClassificatorAgent(
				"ArgAgent1", manager, experimentDatasetPath
						+ "/bayes/agentdataset-1.net",
				SolarFlareType.class.getSimpleName(), experimentDatasetPath
						+ "/dataset/agentdataset-1.csv", sensors, threshold,
				beliefThreshold, logger);
		agents.add(agent);

		sensors = new ArrayList<String>();
		sensors.add(PrevStatus24Hour.class.getSimpleName());
		sensors.add(HistComplex.class.getSimpleName());
		 sensors.add(CNode.class.getSimpleName());
		 sensors.add(MNode.class.getSimpleName());
		// sensors.add(XNode.class.getSimpleName());
		agent = new AdvancedWAClassificatorAgent("ArgAgent2", manager,
				experimentDatasetPath + "/bayes/agentdataset-2.net",
				SolarFlareType.class.getSimpleName(), experimentDatasetPath
						+ "/dataset/agentdataset-2.csv", sensors, threshold,
				beliefThreshold, logger);
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

	private void launchSimulationWith2AgentsKFold(long seed,
			String summaryFile, double threshold, double beliefThreshold,
			int mode) {
		// Simulation properties
		String simulationName = "EXPERIMENT-4-TH-" + threshold + "-BTH-"
				+ beliefThreshold + "-seed-" + seed
				+ "-KFold10TRAININNG-timestamp-" + System.currentTimeMillis();

		// Logging properties
		Logger logger = Logger.getLogger(simulationName);
		Level level = Level.ALL;
		String experimentDatasetPath = "src" + File.separator + "main"
				+ File.separator + "resources" + File.separator + "exp4";
		String experimentOutputPath = "output" + File.separator
				+ simulationName;
		LogConfigurator.log2File(logger, simulationName, level,
				experimentOutputPath);

		logger.info("--> Configuring simulation...");

		Properties scenarioProperties = new Properties();
		scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.NO_GUI);
		scenarioProperties.put(SimulationConfiguration.EXPDATA,
				experimentDatasetPath);
		scenarioProperties.put(SimulationConfiguration.EXPOUTPUT,
				experimentOutputPath);
		scenarioProperties.put(SimulationConfiguration.MODE, mode);

		List<ShanksAgent> agents = new ArrayList<ShanksAgent>();

		// CENTRAL AGENT
		List<String> sensors = new ArrayList<String>();
		sensors.add(Activity.class.getSimpleName());
		sensors.add(LargestSpotSize.class.getSimpleName());
		sensors.add(Area.class.getSimpleName());
		sensors.add(BecomeHist.class.getSimpleName());
		 sensors.add(SpotDistribution.class.getSimpleName());
		// sensors.add(Evolution.class.getSimpleName());
		sensors.add(PrevStatus24Hour.class.getSimpleName());
		sensors.add(HistComplex.class.getSimpleName());
		 sensors.add(CNode.class.getSimpleName());
		 sensors.add(MNode.class.getSimpleName());
		// sensors.add(XNode.class.getSimpleName());
		SolarFlareBayesCentralAgent bayes = new SolarFlareBayesCentralAgent(
				"BayesCentral", experimentDatasetPath
						+ "/bayes/k-fold-10/agentdataset-central.net", sensors,
				logger);
		agents.add(bayes);

		// Argumentation AGENTS

		AdvancedWACentralManagerAgent manager = new AdvancedWACentralManagerAgent(
				"Manager", experimentOutputPath, threshold, logger,
				(Integer) scenarioProperties.get(SimulationConfiguration.MODE));
		scenarioProperties.put("ManagerAgent", manager);

		sensors = new ArrayList<String>();
		sensors.add(Activity.class.getSimpleName());
		sensors.add(LargestSpotSize.class.getSimpleName());
		sensors.add(Area.class.getSimpleName());
		sensors.add(BecomeHist.class.getSimpleName());
		 sensors.add(SpotDistribution.class.getSimpleName());
		// sensors.add(Evolution.class.getSimpleName());
		AdvancedWAClassificatorAgent agent = new AdvancedWAClassificatorAgent(
				"ArgAgent1", manager, experimentDatasetPath
						+ "/bayes/k-fold-10/agentdataset-1.net",
				SolarFlareType.class.getSimpleName(), experimentDatasetPath
						+ "/dataset/agentdataset-1.csv", sensors, threshold,
				beliefThreshold, logger);
		agents.add(agent);

		sensors = new ArrayList<String>();
		sensors.add(PrevStatus24Hour.class.getSimpleName());
		sensors.add(HistComplex.class.getSimpleName());
		sensors.add(CNode.class.getSimpleName());
		sensors.add(MNode.class.getSimpleName());
		// sensors.add(XNode.class.getSimpleName());
		agent = new AdvancedWAClassificatorAgent("ArgAgent2", manager,
				experimentDatasetPath + "/bayes/k-fold-10/agentdataset-2.net",
				SolarFlareType.class.getSimpleName(), experimentDatasetPath
						+ "/dataset/agentdataset-2.csv", sensors, threshold,
				beliefThreshold, logger);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if (this.validation) {
		this.launchValidationAgent1(seed, summaryFile, threshold,
				beliefThreshold, mode);
		this.launchValidationAgent1KFold(seed, summaryFile, threshold,
				beliefThreshold, mode);
		this.launchValidationAgent2(seed, summaryFile, threshold,
				beliefThreshold, mode);
		this.launchValidationAgent2KFold(seed, summaryFile, threshold,
				beliefThreshold, mode);
		}
		this.launchSimulationWith2Agents(seed, summaryFile, threshold,
				beliefThreshold, mode);
		this.launchSimulationWith2AgentsKFold(seed, summaryFile, threshold,
				beliefThreshold, mode);
	}

}