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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.csvreader.CsvReader;

import es.upm.dit.gsi.barmas.dataset.utils.DatasetSplitter;
import es.upm.dit.gsi.barmas.launcher.experiments.BarmasAgentValidator;
import es.upm.dit.gsi.barmas.launcher.experiments.BarmasExperiment;
import es.upm.dit.gsi.barmas.launcher.experiments.RunnableExperiment;
import es.upm.dit.gsi.barmas.launcher.logging.LogConfigurator;
import es.upm.dit.gsi.barmas.launcher.utils.ConsoleOutputDisabler;
import es.upm.dit.gsi.barmas.launcher.utils.SimulationConfiguration;

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

		ExperimentExecutor executor = new ExperimentExecutor();

		String simulationID = "SOLARFLARE";
		String dataset = "src/main/resources/dataset/solarflare-global.csv";
		String experimentFolder = "solarflare-simulation";
		int numberOfAgents = 6;
		double testRatio = 0.4;

		String summaryFile = experimentFolder + "/" + experimentFolder
				+ "-summary.csv";
		long seed = 0;
		String classificationTarget = "SolarFlareType";
		int mode = SimulationConfiguration.DEBUGGING_MODE;
		double diffThreshold = 0.1;
		double beliefThreshold = 0.1;
		double trustThreshold = 20.0;
		int leba = 1;
		int maxArgumentationRounds = 20;

		executor.executeExperiment(simulationID, diffThreshold,
				beliefThreshold, trustThreshold, numberOfAgents, leba,
				summaryFile, classificationTarget, seed, mode, testRatio,
				dataset, experimentFolder, maxArgumentationRounds);

	}

	/**
	 * @param simulationID
	 * @param diffThreshold
	 * @param beliefThreshold
	 * @param reputationMode
	 * @param agentsNumber
	 * @param lostEvidencesByAgents
	 * @param summaryFile
	 * @param classificationTarget
	 * @param seed
	 * @param mode
	 * @param ratio
	 * @param originalDataset
	 * @param experimentFolder
	 */
	public void executeExperiment(String simulationID, double diffThreshold,
			double beliefThreshold, double trustThreshold, int agentsNumber,
			int lostEvidencesByAgents, String summaryFile,
			String classificationTarget, long seed, int mode, double ratio,
			String originalDataset, String experimentFolder,
			int maxArgumentationRounds) {

		Logger logger = Logger.getLogger(ExperimentExecutor.class
				.getSimpleName());

		LogConfigurator.log2File(logger, "IndependentExperiment", Level.ALL,
				Level.INFO, experimentFolder);

		DatasetSplitter splitter = new DatasetSplitter();
		splitter.splitDataset(ratio, agentsNumber, originalDataset,
				experimentFolder + "/input/dataset", true, simulationID, logger);

		simulationID = simulationID + "-TESTRATIO-" + ratio + "-MAXARGSROUNDS-"
				+ maxArgumentationRounds;
		int tint = (int) (diffThreshold * 100);
		double roundedt = ((double) tint) / 100;
		int btint = (int) (beliefThreshold * 100);
		double rounedbt = ((double) btint) / 100;
		int fsint = (int) (trustThreshold * 100);
		double roundedfs = ((double) fsint) / 100;
		String simulationPrefix = simulationID + "-" + agentsNumber
				+ "agents-DTH-" + roundedt + "-BTH-" + rounedbt + "-LEBA-"
				+ lostEvidencesByAgents + "-TTH-" + roundedfs + "-IT-ISOLATED";
		BarmasExperiment exp = new BarmasExperiment(simulationPrefix,
				summaryFile, seed, mode, experimentFolder + "/input",
				experimentFolder + "/output", experimentFolder
						+ "/input/dataset/test-dataset.csv",
				classificationTarget, agentsNumber, lostEvidencesByAgents,
				diffThreshold, beliefThreshold, trustThreshold,
				maxArgumentationRounds);
		List<RunnableExperiment> exps = new ArrayList<RunnableExperiment>();
		exps.add(exp);
		this.executeRunnables(exps, 1, logger);
	}

	/**
	 * @param experiments
	 * @param maxThreads
	 * @param logger
	 */
	public void executeRunnables(List<RunnableExperiment> experiments,
			int maxThreads, Logger logger) {

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
							logger.info("Finished experiment! -> "
									+ thread.getName());
						}
					}
					if (!threads2Remove.isEmpty()) {
						threads.removeAll(threads2Remove);
						threads2Remove.clear();
						System.gc();
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
					+ " -> SimulationID: " + t.getName());
			if (experiment instanceof BarmasAgentValidator) {
				logger.info("Starting validator for isolated agent...");
			}
			logger.info("--> Pending experiments for this batch: "
					+ (experimentsQuantity - finishedExperiments));
		}

		logger.info("There are "
				+ threads.size()
				+ " in progress right now. They are the last simulations for this batch.");
		for (Thread thread : threads) {
			logger.info("Simulation in progress. SimluationID: "
					+ thread.getName());
		}

		while (!threads.isEmpty()) {
			List<Thread> threads2Remove = new ArrayList<Thread>();
			for (Thread thread : threads) {
				if (!thread.isAlive()) {
					threads2Remove.add(thread);
					finishedExperiments++;
					logger.info("Finished experiment! -> " + thread.getName());
					logger.info("--> Pending experiments for this batch: "
							+ (experimentsQuantity - finishedExperiments));
				}
			}
			if (!threads2Remove.isEmpty()) {
				threads.removeAll(threads2Remove);
				threads2Remove.clear();
				System.gc();
				logger.info("There are "
						+ threads.size()
						+ " in progress right now. They are the last simulations for this batch.");
				for (Thread thread : threads) {
					logger.info("Simulation in progress. SimluationID: "
							+ thread.getName());
				}
			}
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		logger.info("Finishing experiments batch with " + finishedExperiments
				+ " experiments executed.");
	}

	/**
	 * @param experiments
	 * @param maxThreads
	 * @param logger
	 */
	public void executeExperiments(List<RunnableExperiment> experiments,
			int maxThreads, Logger logger) {
		this.executeRunnables(experiments, maxThreads, logger);
	}

	/**
	 * @param experiments
	 * @param maxThreads
	 * @param logger
	 */
	public void executeValidators(List<RunnableExperiment> experiments,
			int maxThreads, Logger logger) {
		this.executeRunnables(experiments, maxThreads, logger);
	}

	public List<RunnableExperiment> getValidatorsBatch(String simulationID,
			int agentsNumber, String summaryFile, long seed, int mode,
			String experimentDatasetPath, String experimentOutputFolder,
			String testDataset, String classificationTarget, int iteration) {

		List<RunnableExperiment> experiments = new ArrayList<RunnableExperiment>();

		// Validators
		for (int i = 0; i < agentsNumber; i++) {
			String simulationPrefix = simulationID + "-Agent" + i + "-DTH-"
					+ 20.0 + "-BTH-" + 20.0 + "-LEBA-" + 0 + "-TTH-" + 20.0
					+ "-IT-" + iteration;
			BarmasAgentValidator expValidator = new BarmasAgentValidator(
					simulationPrefix, summaryFile, seed, mode, "Agent" + i,
					experimentDatasetPath + "/bayes/agent-" + i
							+ "-dataset.net", experimentDatasetPath
							+ "/dataset/agent-" + i + "-dataset.csv",
					experimentOutputFolder, testDataset, classificationTarget);
			experiments.add(expValidator);
		}
		String simulationPrefix = simulationID + "-BayesCentralAgent-DTH-"
				+ 20.0 + "-BTH-" + 20.0 + "-LEBA-" + 0 + "-TTH-" + 20.0
				+ "-IT-" + iteration;
		BarmasAgentValidator expValidator = new BarmasAgentValidator(
				simulationPrefix, summaryFile, seed, mode, "BayesCentralAgent",
				experimentDatasetPath + "/bayes/bayes-central-dataset.net",
				experimentDatasetPath + "/dataset/bayes-central-dataset.csv",
				experimentOutputFolder, testDataset, classificationTarget);
		experiments.add(expValidator);

		return experiments;
	}

	/**
	 * @param simulationID
	 * @param agentsNumber
	 * @param summaryFile
	 * @param seed
	 * @param mode
	 * @param experimentDatasetPath
	 * @param experimentOutputFolder
	 * @param testDataset
	 * @param classificationTarget
	 * @param delta
	 * @param iteration
	 * @return
	 */
	public List<RunnableExperiment> getExperimentFullBatch(String simulationID,
			int agentsNumber, String summaryFile, long seed, int mode,
			String experimentDatasetPath, String experimentOutputFolder,
			String testDataset, String classificationTarget, double delta,
			int iteration, int maxArgumentationRounds) {
		List<RunnableExperiment> experiments = new ArrayList<RunnableExperiment>();

		// Experiments
		int numberOfEvidences = this.getNumberOfEvidences(testDataset);

		// No assumptions - no trust
		double diffThreshold = 20.0;
		double beliefThreshold = 20.0;
		double trustThreshold = 20.0;
		int lostEvidencesByAgents = 0;
		int tint = (int) (diffThreshold * 100);
		double roundedt = ((double) tint) / 100;
		int btint = (int) (beliefThreshold * 100);
		double rounedbt = ((double) btint) / 100;
		int fsint = (int) (trustThreshold * 100);
		double roundedfs = ((double) fsint) / 100;
		String simulationPrefix = simulationID + "-" + agentsNumber
				+ "agents-DTH-" + roundedt + "-BTH-" + rounedbt + "-LEBA-"
				+ lostEvidencesByAgents + "-TTH-" + roundedfs + "-IT-"
				+ iteration;
		BarmasExperiment exp = new BarmasExperiment(simulationPrefix,
				summaryFile, seed, mode, experimentDatasetPath,
				experimentOutputFolder, testDataset, classificationTarget,
				agentsNumber, lostEvidencesByAgents, diffThreshold,
				beliefThreshold, trustThreshold, maxArgumentationRounds);
		experiments.add(exp);

		// + delta to ensure at least one execution without assumptions
		diffThreshold = 1.0;
		while (diffThreshold >= 0.01) {
			beliefThreshold = 0.05;
			while (beliefThreshold <= 1.0) {
				trustThreshold = 0;
				while (trustThreshold <= 1.0) {
					lostEvidencesByAgents = 0;
					while (lostEvidencesByAgents <= numberOfEvidences) {
						tint = (int) (diffThreshold * 100);
						roundedt = ((double) tint) / 100;
						btint = (int) (beliefThreshold * 100);
						rounedbt = ((double) btint) / 100;
						fsint = (int) (trustThreshold * 100);
						roundedfs = ((double) fsint) / 100;
						simulationPrefix = simulationID + "-" + agentsNumber
								+ "agents-DTH-" + roundedt + "-BTH-" + rounedbt
								+ "-LEBA-" + lostEvidencesByAgents + "-TTH-"
								+ roundedfs + "-IT-" + iteration;
						exp = new BarmasExperiment(simulationPrefix,
								summaryFile, seed, mode, experimentDatasetPath,
								experimentOutputFolder, testDataset,
								classificationTarget, agentsNumber,
								lostEvidencesByAgents, diffThreshold,
								beliefThreshold, trustThreshold,
								maxArgumentationRounds);
						experiments.add(exp);

						lostEvidencesByAgents++;
					}
					trustThreshold = trustThreshold + delta;
				}
				beliefThreshold = beliefThreshold + delta;
			}
			diffThreshold = diffThreshold - delta;
		}

		return experiments;
	}

	/**
	 * @param simulationID
	 * @param agentsNumber
	 * @param summaryFile
	 * @param seed
	 * @param mode
	 * @param experimentDatasetPath
	 * @param experimentOutputFolder
	 * @param testDataset
	 * @param classificationTarget
	 * @param delta
	 * @param iteration
	 * @param maxDistanceThreshold
	 * @param maxBeliefThreshold
	 * @param maxTrustThreshold
	 * @return
	 */
	public List<RunnableExperiment> getExperimentSmartBatchWithMinZero(
			String simulationID, int agentsNumber, String summaryFile,
			long seed, int mode, String experimentDatasetPath,
			String experimentOutputFolder, String testDataset,
			String classificationTarget, double delta, int iteration,
			double maxDistanceThreshold, double maxBeliefThreshold,
			double maxTrustThreshold, int maxLEBA, int maxArgumentationRounds) {
		return this.getExperimentSmartBatch(simulationID, agentsNumber,
				summaryFile, seed, mode, experimentDatasetPath,
				experimentOutputFolder, testDataset, classificationTarget,
				delta, iteration, maxDistanceThreshold, 0, maxBeliefThreshold,
				0.01, maxTrustThreshold, 0, maxLEBA, 0, maxArgumentationRounds);

	}

	/**
	 * @param simulationID
	 * @param agentsNumber
	 * @param summaryFile
	 * @param seed
	 * @param mode
	 * @param experimentDatasetPath
	 * @param experimentOutputFolder
	 * @param testDataset
	 * @param classificationTarget
	 * @param delta
	 * @param iteration
	 * @param maxDistanceThreshold
	 * @param minDistanceThreshold
	 * @param maxBeliefThreshold
	 * @param minBeliefThreshold
	 * @param maxTrustThreshold
	 * @param minTrustThreshold
	 * @param minLEBA
	 * @param maxLEBA
	 * @return
	 */
	public List<RunnableExperiment> getExperimentSmartBatch(
			String simulationID, int agentsNumber, String summaryFile,
			long seed, int mode, String experimentDatasetPath,
			String experimentOutputFolder, String testDataset,
			String classificationTarget, double delta, int iteration,
			double maxDistanceThreshold, double minDistanceThreshold,
			double maxBeliefThreshold, double minBeliefThreshold,
			double maxTrustThreshold, double minTrustThreshold, int maxLEBA,
			int minLEBA, int maxArgumentationRounds) {
		List<RunnableExperiment> experiments = new ArrayList<RunnableExperiment>();

		// Experiments
		int numberOfEvidences = this.getNumberOfEvidences(testDataset);
		if (maxLEBA < numberOfEvidences) {
			numberOfEvidences = maxLEBA;
		}

		String simulationPrefix;
		BarmasExperiment exp;

		// No Assumptions - No trust
		double diffThreshold = 20.0;
		double beliefThreshold = 20.0;
		double trustThreshold = 20.0;
		int tint = (int) (diffThreshold * 100);
		double roundedt = ((double) tint) / 100;
		int btint = (int) (beliefThreshold * 100);
		double rounedbt = ((double) btint) / 100;
		int fsint = (int) (trustThreshold * 100);
		double roundedfs = ((double) fsint) / 100;
		int lostEvidencesByAgents = minLEBA;
		while (lostEvidencesByAgents <= numberOfEvidences) {
			simulationPrefix = simulationID + "-" + agentsNumber
					+ "agents-DTH-" + roundedt + "-BTH-" + rounedbt + "-LEBA-"
					+ lostEvidencesByAgents + "-TTH-" + roundedfs + "-IT-"
					+ iteration;
			exp = new BarmasExperiment(simulationPrefix, summaryFile, seed,
					mode, experimentDatasetPath, experimentOutputFolder,
					testDataset, classificationTarget, agentsNumber,
					lostEvidencesByAgents, diffThreshold, beliefThreshold,
					trustThreshold, maxArgumentationRounds);
			experiments.add(exp);
		}

		// No assumptions - Trust
		diffThreshold = 20.0;
		beliefThreshold = 20.0;
		trustThreshold = minTrustThreshold;
		while (trustThreshold <= maxTrustThreshold) {
			lostEvidencesByAgents = minLEBA;
			while (lostEvidencesByAgents <= numberOfEvidences) {
				tint = (int) (diffThreshold * 100);
				roundedt = ((double) tint) / 100;
				btint = (int) (beliefThreshold * 100);
				rounedbt = ((double) btint) / 100;
				fsint = (int) (trustThreshold * 100);
				roundedfs = ((double) fsint) / 100;
				simulationPrefix = simulationID + "-" + agentsNumber
						+ "agents-DTH-" + roundedt + "-BTH-" + rounedbt
						+ "-LEBA-" + lostEvidencesByAgents + "-TTH-"
						+ roundedfs + "-IT-" + iteration;
				exp = new BarmasExperiment(simulationPrefix, summaryFile, seed,
						mode, experimentDatasetPath, experimentOutputFolder,
						testDataset, classificationTarget, agentsNumber,
						lostEvidencesByAgents, diffThreshold, beliefThreshold,
						trustThreshold, maxArgumentationRounds);
				experiments.add(exp);

				lostEvidencesByAgents++;
			}
			trustThreshold = trustThreshold + delta;
		}

		// Assumptions and No Trust
		trustThreshold = 20.0;
		diffThreshold = maxDistanceThreshold;
		while (diffThreshold >= minDistanceThreshold) {
			beliefThreshold = minBeliefThreshold;
			while (beliefThreshold <= maxBeliefThreshold) {
				if (minLEBA == 0) {
					lostEvidencesByAgents = 1;
				} else {
					lostEvidencesByAgents = minLEBA;
				}
				while (lostEvidencesByAgents <= numberOfEvidences) {
					tint = (int) (diffThreshold * 100);
					roundedt = ((double) tint) / 100;
					btint = (int) (beliefThreshold * 100);
					rounedbt = ((double) btint) / 100;
					fsint = (int) (trustThreshold * 100);
					roundedfs = ((double) fsint) / 100;
					simulationPrefix = simulationID + "-" + agentsNumber
							+ "agents-DTH-" + roundedt + "-BTH-" + rounedbt
							+ "-LEBA-" + lostEvidencesByAgents + "-TTH-"
							+ roundedfs + "-IT-" + iteration;
					exp = new BarmasExperiment(simulationPrefix, summaryFile,
							seed, mode, experimentDatasetPath,
							experimentOutputFolder, testDataset,
							classificationTarget, agentsNumber,
							lostEvidencesByAgents, diffThreshold,
							beliefThreshold, trustThreshold,
							maxArgumentationRounds);
					experiments.add(exp);

					lostEvidencesByAgents++;
				}
				beliefThreshold = beliefThreshold + delta;
			}

			diffThreshold = diffThreshold - delta;
		}

		// Assumptions and Trust
		diffThreshold = maxDistanceThreshold;
		while (diffThreshold >= minDistanceThreshold) {
			beliefThreshold = minBeliefThreshold;
			while (beliefThreshold <= maxBeliefThreshold) {
				trustThreshold = minTrustThreshold;
				while (trustThreshold <= maxTrustThreshold) {
					if (minLEBA == 0) {
						lostEvidencesByAgents = 1;
					} else {
						lostEvidencesByAgents = minLEBA;
					}
					while (lostEvidencesByAgents <= numberOfEvidences) {
						tint = (int) (diffThreshold * 100);
						roundedt = ((double) tint) / 100;
						btint = (int) (beliefThreshold * 100);
						rounedbt = ((double) btint) / 100;
						fsint = (int) (trustThreshold * 100);
						roundedfs = ((double) fsint) / 100;
						simulationPrefix = simulationID + "-" + agentsNumber
								+ "agents-DTH-" + roundedt + "-BTH-" + rounedbt
								+ "-LEBA-" + lostEvidencesByAgents + "-TTH-"
								+ roundedfs + "-IT-" + iteration;
						exp = new BarmasExperiment(simulationPrefix,
								summaryFile, seed, mode, experimentDatasetPath,
								experimentOutputFolder, testDataset,
								classificationTarget, agentsNumber,
								lostEvidencesByAgents, diffThreshold,
								beliefThreshold, trustThreshold,
								maxArgumentationRounds);
						experiments.add(exp);

						lostEvidencesByAgents++;
					}
					trustThreshold = trustThreshold + delta;
				}
				beliefThreshold = beliefThreshold + delta;
			}

			diffThreshold = diffThreshold - delta;
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
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return result;
	}

}
