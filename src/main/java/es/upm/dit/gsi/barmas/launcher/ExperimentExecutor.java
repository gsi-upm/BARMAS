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

	public List<Runnable> getValidatorsBatch(String simulationID,
			int agentsNumber, String summaryFile, long seed, int mode,
			String experimentDatasetPath, String experimentOutputFolder,
			String testDataset, String classificationTarget, int iteration) {

		List<Runnable> experiments = new ArrayList<Runnable>();

		// Validators
		for (int i = 0; i < agentsNumber; i++) {
			String simulationPrefix = simulationID + "-Agent" + i + "-TH-" + 2
					+ "-BTH-" + 2 + "-LEPA-" + 0 + "-IT-" + iteration;
			BarmasAgentValidator expValidator = new BarmasAgentValidator(
					simulationPrefix, summaryFile, seed, mode, "Agent" + i,
					experimentDatasetPath + "/bayes/agent-" + i
							+ "-dataset.net", experimentDatasetPath
							+ "/dataset/agent-" + i + "-dataset.csv",
					experimentOutputFolder, testDataset, classificationTarget);
			experiments.add(expValidator);
		}
		return experiments;
	}

	public List<Runnable> getExperimentBatch(String simulationID,
			int agentsNumber, String summaryFile, long seed, int mode,
			String experimentDatasetPath, String experimentOutputFolder,
			String testDataset, String classificationTarget, double delta,
			int iteration) {
		List<Runnable> experiments = new ArrayList<Runnable>();

		// Experiments
		int numberOfEvidences = this.getNumberOfEvidences(testDataset);

		// + delta to ensure at least one execution without assumptions
		double threshold = 1.0 + delta;
		while (threshold > 0) {

			double beliefThreshold = 1.0;
			while (beliefThreshold > 0) {

				int lostEvidencesPerAgent = 0;
				while (lostEvidencesPerAgent <= numberOfEvidences
						/ agentsNumber) {

					int tint = (int) (threshold * 100);
					double roundedt = tint / 100;
					int btint = (int) (beliefThreshold * 100);
					double rounedbt = btint / 100;
					String simulationPrefix = simulationID + "-" + agentsNumber
							+ "agents-TH-" + roundedt + "-BTH-" + rounedbt
							+ "-LEPA-" + lostEvidencesPerAgent + "-IT-"
							+ iteration;
					BarmasExperiment exp = new BarmasExperiment(
							simulationPrefix, summaryFile, seed, mode,
							experimentDatasetPath, experimentOutputFolder,
							testDataset, classificationTarget, agentsNumber,
							lostEvidencesPerAgent, threshold, beliefThreshold);
					experiments.add(exp);

					lostEvidencesPerAgent++;
				}

				beliefThreshold = beliefThreshold - delta;
			}

			threshold = threshold - delta;
		}
		return experiments;
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
	public void executeRunnables(List<Runnable> experiments, int maxThreads,
			boolean concurrentManagement, Logger logger) {

		int startedExperiments = 0;
		int finishedExperiments = 0;
		List<Thread> threads = new ArrayList<Thread>();
		for (Runnable experiment : experiments) {
			logger.fine("Number of simulations executing right now: "
					+ threads.size());
			while (threads.size() >= maxThreads) {
				try {
					Thread.sleep(5000);
					List<Thread> threads2Remove = new ArrayList<Thread>();
					for (Thread thread : threads) {
						if (!thread.isAlive()) {
							threads2Remove.add(thread);
							finishedExperiments++;
							logger.info("Finished experiment! -> Finished experiments: "
									+ finishedExperiments);
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
			threads.add(t);
			t.start();
			startedExperiments++;
			logger.info("Starting experiment... -> Number of launched experiments: "
					+ startedExperiments);
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
	public void executeExperiments(List<Runnable> experiments, int maxThreads,
			Logger logger) {
		this.executeRunnables(experiments, maxThreads, true, logger);
	}

	/**
	 * @param experiments
	 * @param maxThreads
	 * @param logger
	 */
	public void executeValidators(List<Runnable> experiments, int maxThreads,
			Logger logger) {
		this.executeRunnables(experiments, maxThreads, false, logger);
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
