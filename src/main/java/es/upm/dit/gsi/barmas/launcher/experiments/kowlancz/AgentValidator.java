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
package es.upm.dit.gsi.barmas.launcher.experiments.kowlancz;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.csvreader.CsvReader;

import es.upm.dit.gsi.barmas.agent.AdvancedCentralManagerAgent;
import es.upm.dit.gsi.barmas.agent.AdvancedClassificatorAgent;
import es.upm.dit.gsi.barmas.agent.DiagnosisBayesCentralAgent;
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
 * es.upm.dit.gsi.barmas.solarflare.launcher.experiments.Experiment3C.java
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
public class AgentValidator implements Runnable {

	private String summaryFile;
	private long seed;
	private int mode;
	private int agentsNumber;

	/**
	 * Constructor
	 * 
	 * @param summaryFile
	 * @param seed
	 */
	public AgentValidator(String summaryFile, long seed, int mode,
			int agentsNumber) {
		this.summaryFile = summaryFile;
		this.seed = seed;
		this.mode = mode;
		this.agentsNumber = agentsNumber;
	}

	private void launchValidationAgent(long seed, String summaryFile, int mode,
			int agentid) {
		// Simulation properties
		String simulationName = "Validator-Agent" + agentid
				+ "-seed-" + seed + "-timestamp-" + System.currentTimeMillis();
		String classificationTarget = "Diagnosis";
		// Logging properties
		Logger logger = Logger.getLogger(simulationName);
		Level level = Level.ALL;
		String experimentDatasetPath = "src/main/resources/kowlancz-CZ02/exp1";
		String experimentOutputPath = "kowlancz-output/" + simulationName;
		String testDataset = experimentDatasetPath + "/dataset/test-dataset.csv";
		LogConfigurator.log2File(logger, simulationName, level,
				experimentOutputPath);

		logger.info("--> Configuring simulation...");

		Properties scenarioProperties = new Properties();
		scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.NO_GUI);
		scenarioProperties.put(SimulationConfiguration.EXPDATA,
				experimentDatasetPath);
		scenarioProperties.put(SimulationConfiguration.TESTDATASET,
				testDataset);
		scenarioProperties.put(SimulationConfiguration.EXPOUTPUT,
				experimentOutputPath);
		scenarioProperties.put(SimulationConfiguration.CLASSIFICATIONTARGET,
				classificationTarget);
		scenarioProperties.put(SimulationConfiguration.MODE, mode);

		List<ShanksAgent> agents = new ArrayList<ShanksAgent>();

		String[] headers = null;
		try {
			CsvReader reader = new CsvReader(new FileReader(new File(
					testDataset)));
			reader.readHeaders();
			headers = reader.getHeaders();
		} catch (IOException e) {
			logger.severe("Impossible to read test dataset file.");
			e.printStackTrace();
			System.exit(1);
		}

		// CENTRAL AGENT
		List<String> sensors = new ArrayList<String>();

		for (int i = 0; i < headers.length - 1; i++) {
			sensors.add(headers[i]);
		}
		DiagnosisBayesCentralAgent bayes = new DiagnosisBayesCentralAgent(
				"BayesCentral", classificationTarget, experimentDatasetPath
						+ "/bayes/agent-" + agentid + "-dataset.net", sensors,
				logger);
		agents.add(bayes);

		// Argumentation AGENTS
		AdvancedCentralManagerAgent manager = new AdvancedCentralManagerAgent(
				"Manager", experimentOutputPath, logger,
				(Integer) scenarioProperties.get(SimulationConfiguration.MODE),
				classificationTarget);
		scenarioProperties.put("ManagerAgent", manager);
		AdvancedClassificatorAgent agent = new AdvancedClassificatorAgent(
				"ArgAgent" + agentid, manager, classificationTarget,
				experimentDatasetPath + "/bayes/agent-" + agentid
						+ "-dataset.net", sensors, logger);
		agents.add(agent);

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
		this.launchValidationAgent(seed, summaryFile, mode, this.agentsNumber);
	}

}
