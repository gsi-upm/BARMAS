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
package es.upm.dit.gsi.barmas.launcher.experiments;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.csvreader.CsvReader;

import es.upm.dit.gsi.barmas.agent.BarmasBayesCentralAgent;
import es.upm.dit.gsi.barmas.agent.BarmasClassificatorAgent;
import es.upm.dit.gsi.barmas.agent.BarmasManagerAgent;
import es.upm.dit.gsi.barmas.launcher.logging.LogConfigurator;
import es.upm.dit.gsi.barmas.launcher.utils.SimulationConfiguration;
import es.upm.dit.gsi.barmas.launcher.utils.SummaryCreator;
import es.upm.dit.gsi.barmas.model.scenario.DiagnosisScenario;
import es.upm.dit.gsi.barmas.simulation.DiagnosisSimulation;
import es.upm.dit.gsi.shanks.agent.ShanksAgent;
import es.upm.dit.gsi.shanks.exception.ShanksException;
import es.upm.dit.gsi.shanks.model.scenario.Scenario;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.launcher.experiments.kowlancz.BarmasExperiment.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 01/11/2013
 * @version 0.1
 * 
 */
public class BarmasExperiment implements RunnableExperiment {

	private String summaryFile;
	private String experimentDatasetFolder;
	private String experimentOutputFolder;
	private String testDataset;
	private long seed;
	private int mode;
	private String classificationTarget;
	private String simulationID;
	private int agentsNumber;
	private int lostEvidencesPerAgent;
	private double diffThreshold;
	private double beliefThreshold;
	private double trustThreshold;

	private Logger logger;

	/**
	 * Constructor
	 * 
	 * @param summaryFile
	 * @param seed
	 */
	public BarmasExperiment(String simulationID, String summaryFile, long seed,
			int mode, String experimentDatasetFolder,
			String experimentOutputFolder, String testDataset,
			String classificationTarget, int agentsNumber,
			int lostEvidencesPerAgent, double diffThreshold,
			double beliefThreshold, double trustThreshold) {
		this.summaryFile = summaryFile;
		this.agentsNumber = agentsNumber;
		this.seed = seed;
		this.trustThreshold = trustThreshold;
		this.mode = mode;
		this.experimentDatasetFolder = experimentDatasetFolder;
		this.experimentOutputFolder = experimentOutputFolder;
		File f = new File(experimentOutputFolder);
		if (!f.exists()) {
			f.mkdirs();
		}
		this.testDataset = testDataset;
		this.classificationTarget = classificationTarget;
		this.simulationID = simulationID;
		this.lostEvidencesPerAgent = lostEvidencesPerAgent;
		this.diffThreshold = diffThreshold;
		this.beliefThreshold = beliefThreshold;
	}

	/**
	 * 
	 */
	private void createSimulationInfoFile(String simulationOutputFolder) {

		try {
			String fileName = simulationOutputFolder + "/simulation.info";

			FileWriter fw = new FileWriter(new File(fileName));
			fw.write("Simulation ID: " + simulationID + "\n");
			fw.write("Seed: " + seed + "\n");
			fw.write("Number of Agents: " + agentsNumber + "\n");
			if (mode == SimulationConfiguration.DEBUGGING_MODE) {
				fw.write("Mode: DEBUGGING MODE\n");
			} else {
				fw.write("Mode: SIMULATION MODE\n");
			}
			fw.write("Global Summary File: " + summaryFile + "\n");
			fw.write("Experiment Dataset Folder: " + experimentDatasetFolder
					+ "\n");
			fw.write("Simulation Output Folder: " + simulationOutputFolder
					+ "\n");
			fw.write("Test dataset: " + testDataset + "\n");
			fw.write("Classification Target: " + classificationTarget + "\n");
			fw.write("Lost evidences per agent: " + lostEvidencesPerAgent
					+ "\n");
			fw.write("Difference between Prob. Distribution Threshold: "
					+ diffThreshold + "\n");
			fw.write("Belief Threshold: " + beliefThreshold + "\n");
			fw.write("Trust Threshold: " + trustThreshold + "\n");
			fw.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void launchExperiment(String simulationID, long seed,
			String summaryFile, int mode, String experimentDatasetFolder,
			String experimentOutputFolder, String testDataset,
			String classificationTarget, int agentsNumber,
			int lostEvidencesPerAgent, double diffThreshold,
			double beliefThreshold, double trustThreshold) {
		// Simulation properties
		String simulationName = "";
		if (simulationID == null || simulationID.equals("")) {
			simulationName = this.getClass().getSimpleName() + "-seed-" + seed
					+ "-timestamp-" + System.currentTimeMillis();
		} else {
			simulationName = this.getClass().getSimpleName() + "-"
					+ simulationID + "-seed-" + seed + "-timestamp-"
					+ System.currentTimeMillis();
		}

		// Logging properties
		this.logger = Logger.getLogger(simulationName);
		Level fileHandlerLevel = Level.WARNING;
		Level consoleHandlerLevel = Level.WARNING;
		if (mode == SimulationConfiguration.DEBUGGING_MODE) {
			consoleHandlerLevel = Level.INFO;
			fileHandlerLevel = Level.ALL;
		}
		String experimentOutputPath = experimentOutputFolder + File.separator
				+ simulationName;
		LogConfigurator.log2File(logger, "simulation-logs", fileHandlerLevel,
				consoleHandlerLevel, experimentOutputPath);

		logger.info("Creating simulation info file...");
		this.createSimulationInfoFile(experimentOutputPath);
		logger.info("--> Configuring simulation...");

		Properties scenarioProperties = new Properties();
		scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.NO_GUI);
		scenarioProperties.put(SimulationConfiguration.EXPDATA,
				experimentDatasetFolder);
		scenarioProperties
				.put(SimulationConfiguration.TESTDATASET, testDataset);
		scenarioProperties.put(SimulationConfiguration.EXPOUTPUT,
				experimentOutputPath);
		scenarioProperties.put(SimulationConfiguration.CLASSIFICATIONTARGET,
				classificationTarget);
		scenarioProperties.put(SimulationConfiguration.MODE, mode);
		scenarioProperties.put(SimulationConfiguration.REPUTATIONMODE,
				Boolean.toString(trustThreshold <= 1));

		List<ShanksAgent> agents = new ArrayList<ShanksAgent>();

		String[] headers = null;
		try {
			CsvReader reader = new CsvReader(new FileReader(new File(
					testDataset)));
			reader.readHeaders();
			headers = reader.getHeaders();
			reader.close();
		} catch (IOException e) {
			logger.severe("Impossible to read test dataset file.");
			logger.severe(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		// CENTRAL AGENT
		List<String> sensors = new ArrayList<String>();

		for (int i = 0; i < headers.length - 1; i++) {
			sensors.add(headers[i]);
		}
		for (int i = 0; i <= agentsNumber * lostEvidencesPerAgent; i++) {
			if (i < headers.length - 1) {
				sensors.remove(headers[i]);
			}
		}
		BarmasBayesCentralAgent bayes = new BarmasBayesCentralAgent(
				"BayesCentral", classificationTarget, experimentDatasetFolder
						+ "/bayes/bayes-central-dataset.net",
				experimentDatasetFolder + "/dataset/bayes-central-dataset.csv",
				sensors, logger);
		agents.add(bayes);

		// Argumentation Manager AGENTS
		BarmasManagerAgent manager = new BarmasManagerAgent("Manager",
				experimentOutputPath, diffThreshold, logger,
				(Integer) scenarioProperties.get(SimulationConfiguration.MODE),
				classificationTarget, trustThreshold);
		scenarioProperties.put("ManagerAgent", manager);

		// Argumentation AGENTS
		for (int agentNum = 0; agentNum < agentsNumber; agentNum++) {
			sensors = new ArrayList<String>();
			for (int i = 0; (i * agentsNumber) + agentNum < headers.length - 1; i++) {
				sensors.add(headers[(i * agentsNumber) + agentNum]);
			}
			for (int i = 0; i < lostEvidencesPerAgent; i++) {
				if (i < sensors.size()) {
					sensors.remove(headers[(i * agentsNumber) + agentNum]);
				}
			}
			BarmasClassificatorAgent agent = new BarmasClassificatorAgent(
					"ArgAgent" + agentNum, manager, classificationTarget,
					experimentDatasetFolder + "/bayes/agent-" + agentNum
							+ "-dataset.net", experimentDatasetFolder
							+ "/dataset/agent-" + agentNum + "-dataset.csv",
					sensors, diffThreshold, beliefThreshold, trustThreshold,
					logger);
			agents.add(agent);
		}

		scenarioProperties.put("AGENTS", agents);

		logger.info("--> Simulation configured");

		DiagnosisSimulation sim;
		try {
			sim = new DiagnosisSimulation(seed, DiagnosisScenario.class,
					simulationName, DiagnosisScenario.NORMALSTATE,
					scenarioProperties);

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
			logger.warning(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			this.launchExperiment(simulationID, seed, summaryFile, mode,
					experimentDatasetFolder, experimentOutputFolder,
					testDataset, classificationTarget, agentsNumber,
					lostEvidencesPerAgent, diffThreshold, beliefThreshold,
					trustThreshold);
		} catch (Exception e) {
			logger.severe("Experiment finished unexpectedly...");
			logger.severe(e.getMessage());
			System.exit(1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.launcher.experiments.RunnableExperiment#getSimualtionID
	 * ()
	 */
	@Override
	public String getSimualtionID() {
		return this.simulationID;
	}

}
