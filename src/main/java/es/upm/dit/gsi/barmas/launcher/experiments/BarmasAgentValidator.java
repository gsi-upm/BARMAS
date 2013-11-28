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
 * es.upm.dit.gsi.barmas.launcher.experiments.kowlancz.AgentValidator.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 31/10/2013
 * @version 0.1
 * 
 */
public class BarmasAgentValidator implements RunnableExperiment {

	private String summaryFile;
	private long seed;
	private int mode;
	private String agentID;
	private String bnFile;
	private String datasetFile;
	private String experimentOutputFolder;
	private String testDataset;
	private String classificationTarget;
	private String simulationID;
	private int maxArgumentationRounds;

	private Logger logger;

	/**
	 * Constructor
	 * 
	 * @param summaryFile
	 * @param seed
	 */
	public BarmasAgentValidator(String simulationID, String summaryFile, long seed, int mode,
			String agentID, String bnFile, String datasetFile, String experimentOutputFolder,
			String testDataset, String classificationTarget) {
		this.summaryFile = summaryFile;
		this.seed = seed;
		this.mode = mode;
		this.agentID = agentID;
		this.bnFile = bnFile;
		this.datasetFile = datasetFile;
		this.experimentOutputFolder = experimentOutputFolder;
		this.testDataset = testDataset;
		this.simulationID = simulationID;
		this.classificationTarget = classificationTarget;
		this.maxArgumentationRounds = 10;
	}

	private void launchValidationAgent(String simulationID, long seed, String summaryFile,
			int mode, String agentID, String bnFile, String datasetFile,
			String experimentOutputFolder, String testDataset, String classificationTarget,
			int maxArgumentationRounds) {
		// Simulation properties
		String simulationName = "";
		if (simulationID == null || simulationID.equals("")) {
			simulationName = this.getClass().getSimpleName() + "-seed-" + seed + "-timestamp-"
					+ System.currentTimeMillis();
		} else {
			simulationName = this.getClass().getSimpleName() + "-" + simulationID + "-seed-" + seed
					+ "-timestamp-" + System.currentTimeMillis();
		}
		// Logging properties
		this.logger = Logger.getLogger(simulationName);
		Level fileHandlerLevel = Level.WARNING;
		Level consoleHandlerLevel = Level.WARNING;
		if (mode == SimulationConfiguration.DEBUGGING_MODE) {
			consoleHandlerLevel = Level.INFO;
			fileHandlerLevel = Level.ALL;
		}
		String experimentOutputPath = experimentOutputFolder + File.separator + simulationName;
		LogConfigurator.log2File(logger, "simulation-logs", fileHandlerLevel, consoleHandlerLevel,
				experimentOutputPath);

		logger.info("Creating simulation info file...");
		this.createSimulationInfoFile(experimentOutputPath);
		logger.info("--> Configuring simulation...");

		Properties scenarioProperties = new Properties();
		scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.NO_GUI);
		scenarioProperties.put(SimulationConfiguration.TESTDATASET, testDataset);
		scenarioProperties.put(SimulationConfiguration.EXPOUTPUT, experimentOutputPath);
		scenarioProperties.put(SimulationConfiguration.CLASSIFICATIONTARGET, classificationTarget);
		scenarioProperties.put(SimulationConfiguration.MODE, mode);

		List<ShanksAgent> agents = new ArrayList<ShanksAgent>();

		String[] headers = null;
		try {
			CsvReader reader = new CsvReader(new FileReader(new File(testDataset)));
			reader.readHeaders();
			headers = reader.getHeaders();
			reader.close();
		} catch (IOException e) {
			logger.severe("Impossible to read test dataset file.");
			logger.severe(e.getMessage());
			System.exit(1);
		}

		// CENTRAL AGENT
		List<String> sensors = new ArrayList<String>();

		for (int i = 0; i < headers.length - 1; i++) {
			sensors.add(headers[i]);
		}
		BarmasBayesCentralAgent bayes = new BarmasBayesCentralAgent("BayesCentral",
				classificationTarget, bnFile, datasetFile, sensors, logger);
		agents.add(bayes);

		// Argumentation AGENTS
		double NOREPUTATION = 20;
		double NOASSUMPTIONS = 20; // impossible to generate assumptions with
									// diffThreshold > 1
		BarmasManagerAgent manager = new BarmasManagerAgent("Manager", experimentOutputPath,
				NOASSUMPTIONS, logger,
				(Integer) scenarioProperties.get(SimulationConfiguration.MODE),
				classificationTarget, NOREPUTATION, maxArgumentationRounds);
		scenarioProperties.put("ManagerAgent", manager);
		BarmasClassificatorAgent agent = new BarmasClassificatorAgent(agentID, manager,
				classificationTarget, bnFile, datasetFile, sensors, NOASSUMPTIONS, NOASSUMPTIONS,
				NOREPUTATION, logger);
		agents.add(agent);

		scenarioProperties.put("AGENTS", agents);

		logger.info("--> Simulation configured");

		DiagnosisSimulation sim;
		try {
			sim = new DiagnosisSimulation(seed, DiagnosisScenario.class, simulationName,
					DiagnosisScenario.NORMALSTATE, scenarioProperties);

			logger.info("--> Launching simulation...");
			sim.start();
			do
				if (!sim.schedule.step(sim)) {
					break;
				}
			while (true);
			// while (sim.schedule.getSteps() < totalSteps);
			// sim.finish();

			SummaryCreator.makeNumbers(simulationName, experimentOutputPath + File.separator
					+ "summary.csv", summaryFile);
		} catch (ShanksException e) {
			logger.severe(e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * @param experimentOutputPath
	 */
	private void createSimulationInfoFile(String experimentOutputPath) {
		try {
			String fileName = experimentOutputPath + "/simulation.info";

			FileWriter fw = new FileWriter(new File(fileName));
			fw.write("Simulation ID: " + simulationID + "\n");
			fw.write("Seed: " + seed + "\n");
			fw.write("Number of Agents: " + 1 + " (Validation process)\n");
			if (mode == SimulationConfiguration.DEBUGGING_MODE) {
				fw.write("Mode: DEBUGGING MODE\n");
			} else {
				fw.write("Mode: SIMULATION MODE\n");
			}
			fw.write("Global Summary File: " + summaryFile + "\n");
			fw.write("Experiment Dataset File: " + testDataset + "\n");
			fw.write("Simulation Output Folder: " + experimentOutputPath + "\n");
			fw.write("Test dataset: " + testDataset + "\n");
			fw.write("Classification Target: " + classificationTarget + "\n");
			fw.close();

		} catch (Exception e) {
			logger.severe(e.getMessage());
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
			this.launchValidationAgent(simulationID, seed, summaryFile, mode, agentID, bnFile,
					datasetFile, experimentOutputFolder, testDataset, classificationTarget,
					maxArgumentationRounds);
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
