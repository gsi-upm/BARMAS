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
package es.upm.dit.gsi.barmas.launcher;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.csvreader.CsvReader;

import es.upm.dit.gsi.barmas.launcher.experiments.BarmasAgentValidator;
import es.upm.dit.gsi.barmas.launcher.experiments.BarmasExperiment;
import es.upm.dit.gsi.barmas.launcher.experiments.RunnableExperiment;
import es.upm.dit.gsi.barmas.launcher.utils.ConsoleOutputDisabler;

/**
 * Project: barmas File: es.upm.dit.gsi.barmas.launcher.ExperimentExecutor.java
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
public class ExperimentExecutor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Because unbbayes print a lot of things in console...
		ConsoleOutputDisabler.disableConsoleOutput();

		// String summaryFile = "test/global-summary.csv";
		// long seed = 0;
		// int mode = SimulationConfiguration.DEBUGGING_MODE;
		// // int mode = SimulationConfiguration.SIMULATION_MODE;
		// List<Runnable> experiments = new ArrayList<Runnable>();

		// **************************
		// SOLAR FLARE EXPERIMENTS
		// **************************
		// ExperimentExecutor.addSolarFlareExperiments(experiments, mode, seed,
		// summaryFile);
		// ****************************************

		// **************************
		// KOWLANCZ EXPERIMENTS
		// **************************
		// ExperimentExecutor.addKowlanCZExperiments(experiments, mode, seed,
		// summaryFile);
		// ****************************************

		// Lauch experiments
		// int maxThreads = new Integer(args[0]);
		// ExperimentExecutor executor = new ExperimentExecutor();
		// executor.executeExperiments(experiments, maxThreads);

	}

	/**
	 * @param testDataset
	 * @return
	 */
	private int getNumberOfEvidences(String testDataset) {
		int result = 0;
		try {
			CsvReader reader = new CsvReader(new FileReader(new File(
					testDataset)));
			reader.readHeaders();
			String[] headers = reader.getHeaders();
			result = headers.length - 1;
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return result;
	}

	/**
	 * @param experiments
	 * @param maxThreads
	 * @param logger
	 */
	public void executeRunnables(List<RunnableExperiment> experiments,
			int maxThreads, boolean concurrentManagement, Logger logger) {

		int startedExperiments = 0;
		int finishedExperiments = 0;
		int experimentsQuantity = experiments.size();
		List<Thread> threads = new ArrayList<Thread>();
		for (RunnableExperiment experiment : experiments) {
			logger.info("Number of simulations executing right now: "
					+ threads.size());
			while (threads.size() >= maxThreads) {
				try {
					Thread.sleep(5000);
					List<Thread> threads2Remove = new ArrayList<Thread>();
					for (Thread thread : threads) {
						if (!thread.isAlive()) {
							threads2Remove.add(thread);
							finishedExperiments++;
							logger.info("Finished experiment! -> Pending experiments for this batch: "
									+ (experimentsQuantity - finishedExperiments));
						}
					}
					if (!threads2Remove.isEmpty()) {
						for (Thread t : threads2Remove) {
							threads.remove(t);
						}
						threads2Remove.clear();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}

			Thread t = new Thread(experiment);
			t.setName(experiment.getSimualtionID());
			threads.add(t);
			t.start();
			startedExperiments++;
			logger.info("Starting experiment number: " + startedExperiments
					+ " --> Pending experiments for this batch: "
					+ (experimentsQuantity - finishedExperiments));
			if (concurrentManagement) {
				try {
					Thread.sleep(5000);
					// To avoid concurrent learning process
					// The first experiment learns all BNs
					// And the followings do not have to learn anything :)
					concurrentManagement = false;
					// This executes only once.
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}

		while (!threads.isEmpty()) {
			List<Thread> threads2Remove = new ArrayList<Thread>();
			for (Thread thread : threads) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(1);
				}
				if (!thread.isAlive()) {
					threads2Remove.add(thread);
					finishedExperiments++;
					logger.info("Finished experiment! -> Pending experiments for this batch: "
							+ (experimentsQuantity - finishedExperiments));
				} else {
					logger.info("Execution in progress for simulation with ID: "
							+ thread.getName());
					logger.info("-> Pending experiments for this batch: "
							+ (experimentsQuantity - finishedExperiments));
				}
			}
			if (!threads2Remove.isEmpty()) {
				for (Thread t : threads2Remove) {
					threads.remove(t);
				}
				threads2Remove.clear();
				logger.info("Number of simulations executing right now: "
						+ threads.size());
			}
		}

		logger.info("Finishing experiments batch with " + finishedExperiments
				+ " experiments executed.");
	}

	//
	// /**
	// * @param experiments
	// * @param mode
	// * @param seed
	// * @param summaryFile
	// */
	// private static void addSolarFlareExperiments(List<Runnable> experiments,
	// int mode, long seed, String summaryFile) {
	// Experiment1 exp1 = new Experiment1(summaryFile, seed, mode, true);
	// experiments.add(exp1);
	//
	// Experiment3 exp3 = new Experiment3(summaryFile, seed, mode, true);
	// experiments.add(exp3);
	// Experiment3A exp3a = new Experiment3A(summaryFile, seed, mode, true);
	// experiments.add(exp3a);
	// Experiment3B exp3b = new Experiment3B(summaryFile, seed, mode, true);
	// experiments.add(exp3b);
	// Experiment3C exp3c = new Experiment3C(summaryFile, seed, mode, true);
	// experiments.add(exp3c);
	//
	// double threshold = 1;
	// double beliefThreshold = 1;
	// boolean validated = false;
	// // double delta = 0.05;
	// double delta = 0.2;
	//
	// // double threshold = 0.2;
	// // double beliefThreshold = 0.1;
	// // boolean validated = true;
	//
	// while (threshold > 0.01) {
	// while (beliefThreshold > 0.01) {
	// // Experiment2 exp2 = new Experiment2(summaryFile, seed,
	// // threshold, beliefThreshold, mode, !validated);
	// // experiments.add(exp2);
	// Experiment4 exp4 = new Experiment4(summaryFile, seed,
	// threshold, beliefThreshold, mode, !validated);
	// experiments.add(exp4);
	// Experiment4A exp4a = new Experiment4A(summaryFile, seed,
	// threshold, beliefThreshold, mode, !validated);
	// experiments.add(exp4a);
	// Experiment4B exp4b = new Experiment4B(summaryFile, seed,
	// threshold, beliefThreshold, mode, !validated);
	// experiments.add(exp4b);
	// Experiment4C exp4c = new Experiment4C(summaryFile, seed,
	// threshold, beliefThreshold, mode, !validated);
	// experiments.add(exp4c);
	// validated = true;
	// beliefThreshold = beliefThreshold - delta;
	// }
	// beliefThreshold = 1;
	// threshold = threshold - delta;
	// }
	// }

	/**
	 * @param experiments
	 * @param maxThreads
	 * @param logger
	 */
	public void executeExperiments(List<RunnableExperiment> experiments,
			int maxThreads, Logger logger) {
		this.executeRunnables(experiments, maxThreads, true, logger);
	}

	/**
	 * @param experiments
	 * @param maxThreads
	 * @param logger
	 */
	public void executeValidators(List<RunnableExperiment> experiments,
			int maxThreads, Logger logger) {
		this.executeRunnables(experiments, maxThreads, false, logger);
	}

	public List<RunnableExperiment> getValidatorsBatch(String simulationID,
			int agentsNumber, String summaryFile, long seed, int mode,
			String experimentDatasetPath, String experimentOutputFolder,
			String testDataset, String classificationTarget, int iteration) {

		List<RunnableExperiment> experiments = new ArrayList<RunnableExperiment>();

		// Validators
		for (int i = 0; i < agentsNumber; i++) {
			String simulationPrefix = simulationID + "-Agent" + i + "-TH-"
					+ 2.0 + "-BTH-" + 2.0 + "-LEPA-" + 0 + "-IT-" + iteration
					+ "-TRUSTMODE-OFF";
			BarmasAgentValidator expValidator = new BarmasAgentValidator(
					simulationPrefix, summaryFile, seed, mode, "Agent" + i,
					experimentDatasetPath + "/bayes/agent-" + i
							+ "-dataset.net", experimentDatasetPath
							+ "/dataset/agent-" + i + "-dataset.csv",
					experimentOutputFolder, testDataset, classificationTarget);
			experiments.add(expValidator);
		}
		String simulationPrefix = simulationID + "-BayesCentralAgent-TH-" + 2.0
				+ "-BTH-" + 2.0 + "-LEPA-" + 0 + "-IT-" + iteration
				+ "-TRUSTMODE-OFF";
		BarmasAgentValidator expValidator = new BarmasAgentValidator(
				simulationPrefix, summaryFile, seed, mode, "BayesCentralAgent",
				experimentDatasetPath + "/bayes/bayes-central-dataset.net",
				experimentDatasetPath + "/dataset/bayes-central-dataset.csv",
				experimentOutputFolder, testDataset, classificationTarget);
		experiments.add(expValidator);

		return experiments;
	}

	public List<RunnableExperiment> getExperimentBatch(String simulationID,
			int agentsNumber, String summaryFile, long seed, int mode,
			String experimentDatasetPath, String experimentOutputFolder,
			String testDataset, String classificationTarget, double delta,
			int iteration) {
		List<RunnableExperiment> experiments = this.getExperimentBatch(
				simulationID, agentsNumber, summaryFile, seed, mode,
				experimentDatasetPath, experimentOutputFolder, testDataset,
				classificationTarget, delta, iteration, true);
		experiments.addAll(this.getExperimentBatch(simulationID, agentsNumber,
				summaryFile, seed, mode, experimentDatasetPath,
				experimentOutputFolder, testDataset, classificationTarget,
				delta, iteration, false));
		return experiments;
	}

	/**
	 * @param simulationID
	 * @param agentsNumber
	 * @param summaryFile
	 * @param seed
	 * @param mode
	 * @param string
	 * @param string2
	 * @param string3
	 * @param classificationTarget
	 * @param delta
	 * @param i
	 * @param reputationMode
	 * @return
	 */
	public List<RunnableExperiment> getExperimentBatch(String simulationID,
			int agentsNumber, String summaryFile, long seed, int mode,
			String experimentDatasetPath, String experimentOutputFolder,
			String testDataset, String classificationTarget, double delta,
			int iteration, boolean reputationMode) {
		List<RunnableExperiment> experiments = new ArrayList<RunnableExperiment>();

		// Experiments
		int numberOfEvidences = this.getNumberOfEvidences(testDataset);

		boolean NOASSUMPTION_SIMULATION_CREATED = false;

		// + delta to ensure at least one execution without assumptions
		double threshold = 1.0;
		while (threshold > 0) {

			double beliefThreshold = 0.05;
			while (beliefThreshold <= 1.0) {
				int lostEvidencesPerAgent = 0;
				if (NOASSUMPTION_SIMULATION_CREATED) {
					lostEvidencesPerAgent = 1;
				}
				while (lostEvidencesPerAgent <= numberOfEvidences
						/ agentsNumber) {
					int tint = (int) (threshold * 100);
					double roundedt = ((double) tint) / 100;
					int btint = (int) (beliefThreshold * 100);
					double rounedbt = ((double) btint) / 100;
					String reputationModeString = "";
					if (reputationMode) {
						reputationModeString = "ON";
					} else {
						reputationModeString = "OFF";
					}
					String simulationPrefix = simulationID + "-" + agentsNumber
							+ "agents-TH-" + roundedt + "-BTH-" + rounedbt
							+ "-LEPA-" + lostEvidencesPerAgent + "-IT-"
							+ iteration + "-TRUSTMODE-" + reputationModeString;
					BarmasExperiment exp = new BarmasExperiment(
							simulationPrefix, summaryFile, seed, mode,
							experimentDatasetPath, experimentOutputFolder,
							testDataset, classificationTarget, agentsNumber,
							lostEvidencesPerAgent, threshold, beliefThreshold,
							reputationMode);
					experiments.add(exp);
					if (lostEvidencesPerAgent == 0) {
						NOASSUMPTION_SIMULATION_CREATED = true;
					}

					lostEvidencesPerAgent++;
				}

				beliefThreshold = beliefThreshold + delta;
			}

			threshold = threshold - delta;
		}
		return experiments;
	}

	// /**
	// * @param experiments
	// * @param mode
	// * @param seed
	// * @param summaryFile
	// */
	// private static void addKowlanCZExperiments(List<Runnable> experiments,
	// int mode, long seed, String summaryFile) {
	//
	// String experimentDatasetPath = "src/main/resources/kowlancz-CZ02/exp1";
	// String experimentOutputFolder = "test/kowlancz2-output";
	// String testDataset = experimentDatasetPath
	// + "/dataset/test-dataset.csv";
	// String classificationTarget = "Diagnosis";
	// int agentsNumber = 4;
	// int lostEvidencesPerAgent = 0;
	// String simulationID = "KOWLANCZ02-" + agentsNumber + "agents";
	// double NOASSUMPTIONS = 2;
	// BarmasExperiment expargbasic = new BarmasExperiment(simulationID,
	// summaryFile, seed, mode, experimentDatasetPath,
	// experimentOutputFolder, testDataset, classificationTarget,
	// agentsNumber, lostEvidencesPerAgent, NOASSUMPTIONS,
	// NOASSUMPTIONS);
	// // experiments.add(expargbasic);
	//
	// agentsNumber = 3;
	// for (int i = 0; i < agentsNumber; i++) {
	// simulationID = "KOWLANCZ02-Agent" + i;
	// BarmasAgentValidator expValidator = new BarmasAgentValidator(
	// simulationID, summaryFile, seed, mode, "Agent" + i,
	// experimentDatasetPath + "/bayes/agent-" + i
	// + "-dataset.net", experimentDatasetPath
	// + "/dataset/agent-" + i + "-dataset.csv",
	// experimentOutputFolder, testDataset, classificationTarget);
	// // experiments.add(expValidator);
	// }
	//
	// experimentDatasetPath = "src/main/resources/kowlancz-CZ02/exp3-solar";
	// testDataset = experimentDatasetPath + "/dataset/test-dataset.csv";
	// classificationTarget = "SolarFlareType";
	// agentsNumber = 2;
	// simulationID = "solarflare-" + agentsNumber + "agents";
	// expargbasic = new BarmasExperiment(simulationID, summaryFile, seed,
	// mode, experimentDatasetPath, experimentOutputFolder,
	// testDataset, classificationTarget, agentsNumber,
	// lostEvidencesPerAgent, NOASSUMPTIONS, NOASSUMPTIONS);
	// experiments.add(expargbasic);
	//
	// }

}
