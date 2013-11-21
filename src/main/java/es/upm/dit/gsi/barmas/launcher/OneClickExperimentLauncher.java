/**
 * es.upm.dit.gsi.barmas.launcher.OneClickExperimentLauncher.java
 */
package es.upm.dit.gsi.barmas.launcher;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.upm.dit.gsi.barmas.dataset.utils.DatasetSplitter;
import es.upm.dit.gsi.barmas.launcher.experiments.RunnableExperiment;
import es.upm.dit.gsi.barmas.launcher.logging.LogConfigurator;
import es.upm.dit.gsi.barmas.launcher.utils.SimulationConfiguration;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.launcher.OneClickExperimentLauncher.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 01/11/2013
 * @version 0.2
 * 
 */
public class OneClickExperimentLauncher {

	private Logger logger;

	private int maxThreads;
	private int maxLearningThreads;
	private int cores;
	private long initTime;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new OneClickExperimentLauncher().click();

	}

	public OneClickExperimentLauncher() {
		this.initTime = System.currentTimeMillis();
		this.cores = Runtime.getRuntime().availableProcessors();

		switch (this.cores) {
		case 8:
			// Shannon
			maxThreads = cores * 30;
			break;
		case 4:
			// Mystra
			maxThreads = cores * 20;
			break;
		default:
			// Other
			maxThreads = cores * 10;
			break;
		}

		this.maxLearningThreads = 1;
	}

	/**
	 * 
	 */
	private void click() {

		// this.launchZooBatch();
		// this.launchMarketingBatch();
		// this.launchNurseryBatch();
		// this.launchSolarFlareBatch();
		this.launchKowlanCZBatch();
		// this.launchMushroomBatch();
		// this.launchChessBatch();
		// this.launchPokerBatch();

		long finishTime = System.currentTimeMillis();
		long interval = finishTime - initTime;
		long intervalSecs = interval / 1000;
		long intervalMins = intervalSecs / 60;
		long intervalHours = intervalMins / 60;
		logger.info("All experiments have been executed in " + intervalHours + " hours, "
				+ (intervalMins % 60) + " minutes, " + (intervalSecs % 60) + " seconds and "
				+ (interval % 1000) + " miliseconds. Finishing execution of simulations.");
	}

	/**
	 * 
	 */
	private void launchPokerBatch() {

		// ***********************
		// POKER SIMULACION BATCH
		// ***********************
		String simulationID = "POKER";
		String dataset = "src/main/resources/dataset/poker.csv";
		String simName = "poker-simulation";
		String experimentFolder = "../experiments/" + simName;
		double testRatio = 0.1;
		boolean centralApproach = true;
		String summaryFile = experimentFolder + "/" + simName + "-summary.csv";
		long seed = 0;
		int iterations = 1;
		String classificationTarget = "PokerHand";
		double delta = 0.1;

		double maxDistanceThreshold = 0.5;
		double minDistanceThreshold = 0.1;
		double maxBeliefThreshold = 0.5;
		double minBeliefThreshold = 0.1;
		double maxTrustThreshold = 0.5;
		double minTrustThreshold = 0.1;
		int maxLEBA = 6;
		int minLEBA = 0;
		int maxArgumentationRounds = 1000;
		int maxNumberOfAgents = 4;
		int minNumberOfAgents = 2;

		long totalExps = this.launchSmartBathAndValidatorsForAgentRange(simulationID, dataset,
				experimentFolder, maxNumberOfAgents, minNumberOfAgents, testRatio, centralApproach,
				summaryFile, seed, maxThreads, iterations, classificationTarget, delta,
				SimulationConfiguration.SIMULATION_MODE, maxDistanceThreshold,
				minDistanceThreshold, maxBeliefThreshold, minBeliefThreshold, maxTrustThreshold,
				minTrustThreshold, maxLEBA, minLEBA, maxArgumentationRounds);
		logger.info("Total number of experiments executed in all iterations and with all parameters for "
				+ simulationID + " simulation = " + totalExps);
		// ***********************
	}

	/**
	 * 
	 */
	private void launchChessBatch() {
		// ***********************
		// CHESS SIMULACION BATCH
		// ***********************
		String simulationID = "CHESS";
		String dataset = "src/main/resources/dataset/kr-vs-k.csv";
		String simName = "chess-simulation";
		String experimentFolder = "../experiments/" + simName;
		double testRatio = 0.5;
		boolean centralApproach = true;
		String summaryFile = experimentFolder + "/" + simName + "-summary.csv";
		long seed = 0;
		int iterations = 1;
		String classificationTarget = "Game";
		double delta = 0.1;

		double maxDistanceThreshold = 0.5;
		double minDistanceThreshold = 0.1;
		double maxBeliefThreshold = 0.5;
		double minBeliefThreshold = 0.1;
		double maxTrustThreshold = 0.5;
		double minTrustThreshold = 0.1;
		int maxLEBA = 6;
		int minLEBA = 0;
		int maxArgumentationRounds = 1000;
		int maxNumberOfAgents = 4;
		int minNumberOfAgents = 2;

		long totalExps = this.launchSmartBathAndValidatorsForAgentRange(simulationID, dataset,
				experimentFolder, maxNumberOfAgents, minNumberOfAgents, testRatio, centralApproach,
				summaryFile, seed, maxThreads, iterations, classificationTarget, delta,
				SimulationConfiguration.SIMULATION_MODE, maxDistanceThreshold,
				minDistanceThreshold, maxBeliefThreshold, minBeliefThreshold, maxTrustThreshold,
				minTrustThreshold, maxLEBA, minLEBA, maxArgumentationRounds);
		logger.info("Total number of experiments executed in all iterations and with all parameters for "
				+ simulationID + " simulation = " + totalExps);
		// ***********************
	}

	/**
	 * 
	 */
	private void launchMushroomBatch() {

		// ***********************
		// MUSHROOM SIMULACION BATCH
		// ***********************
		String simulationID = "MUSHROOM";
		String dataset = "src/main/resources/dataset/agaricus-lepiota.csv";
		String simName = "mushroom-simulation";
		String experimentFolder = "../experiments/" + simName;
		boolean centralApproach = true;
		String summaryFile = experimentFolder + "/" + simName + "-summary.csv";
		long seed = 0;
		double testRatio = 0.4;
		int iterations = 1;
		// int kfold = 3;
		String classificationTarget = "mushroomPoisonous";
		double delta = 0.1;

		double maxDistanceThreshold = 0.5;
		double minDistanceThreshold = 0.1;
		double maxBeliefThreshold = 0.5;
		double minBeliefThreshold = 0.1;
		double maxTrustThreshold = 0.5;
		double minTrustThreshold = 0.1;
		int maxLEBA = 8;
		int minLEBA = 0;
		int maxArgumentationRounds = 200;
		int maxNumberOfAgents = 4;
		int minNumberOfAgents = 2;

		long totalExps = this.launchSmartBathAndValidatorsForAgentRange(simulationID, dataset,
				experimentFolder, maxNumberOfAgents, minNumberOfAgents, testRatio, centralApproach,
				summaryFile, seed, maxThreads, iterations, classificationTarget, delta,
				SimulationConfiguration.SIMULATION_MODE, maxDistanceThreshold,
				minDistanceThreshold, maxBeliefThreshold, minBeliefThreshold, maxTrustThreshold,
				minTrustThreshold, maxLEBA, minLEBA, maxArgumentationRounds);
		logger.info("Total number of experiments executed in all iterations and with all parameters for "
				+ simulationID + " simulation = " + totalExps);
		// ***********************
	}

	/**
	 * 
	 */
	private void launchKowlanCZBatch() {

		// ***********************
		// KOWLANCZ02
		// ***********************
		String simulationID = "KOWLANCZ02";
		String dataset = "src/main/resources/dataset/kowlancz/CZ02/CZ02-dataset.csv";
		String simName = "kowlancz02-simulation";
		String experimentFolder = "../experiments/" + simName;
		boolean centralApproach = true;
		String summaryFile = experimentFolder + "/" + simName + "-summary.csv";
		long seed = 0;
		// double testRatio = 0.4;
		// int iterations = 1;
		int kfold = 3;
		String classificationTarget = "Diagnosis";
		double delta = 0.1;

		double maxDistanceThreshold = 0.4;
		double minDistanceThreshold = 0.2;
		double maxBeliefThreshold = 0.2;
		double minBeliefThreshold = 0.1;
		double maxTrustThreshold = 0.6;
		double minTrustThreshold = 0.1;
		int maxLEBA = 10;
		int minLEBA = 0;
		int maxArgumentationRounds = 200;
		int maxNumberOfAgents = 4;
		int minNumberOfAgents = 2;

		long totalExps = this.launchSmartBathAndValidatorsForAgentRangeKFold(simulationID, dataset,
				experimentFolder, maxNumberOfAgents, minNumberOfAgents, centralApproach,
				summaryFile, seed, maxThreads, kfold, classificationTarget, delta,
				SimulationConfiguration.SIMULATION_MODE, maxDistanceThreshold,
				minDistanceThreshold, maxBeliefThreshold, minBeliefThreshold, maxTrustThreshold,
				minTrustThreshold, maxLEBA, minLEBA, maxArgumentationRounds);
		logger.info("Total number of experiments executed in all iterations and with all parameters for "
				+ simulationID + " simulation = " + totalExps);
		// ***********************
	}

	/**
	 * 
	 */
	private void launchSolarFlareBatch() {

		// ***********************
		// SOLAR FLARE SIMULACION BATCH
		// ***********************
		String simulationID = "SOLARFLARE";
		String dataset = "src/main/resources/dataset/solarflare.csv";
		String simName = "solarflare-simulation";
		String experimentFolder = "../experiments/" + simName;
		boolean centralApproach = true;
		String summaryFile = experimentFolder + "/" + simName + "-summary.csv";
		long seed = 0;
		// testRatio = 0.4;
		// iterations = 1;
		int kfold = 2;
		String classificationTarget = "SolarFlareType";
		double delta = 0.1;

		double maxDistanceThreshold = 0.5;
		double minDistanceThreshold = 0.1;
		double maxBeliefThreshold = 0.5;
		double minBeliefThreshold = 0.1;
		double maxTrustThreshold = 0.5;
		double minTrustThreshold = 0.1;
		int maxLEBA = 20;
		int minLEBA = 0;
		int maxArgumentationRounds = 200;
		int maxNumberOfAgents = 2;
		int minNumberOfAgents = 2;

		long totalExps = this.launchSmartBathAndValidatorsForAgentRangeKFold(simulationID, dataset,
				experimentFolder, maxNumberOfAgents, minNumberOfAgents, centralApproach,
				summaryFile, seed, maxThreads, kfold, classificationTarget, delta,
				SimulationConfiguration.SIMULATION_MODE, maxDistanceThreshold,
				minDistanceThreshold, maxBeliefThreshold, minBeliefThreshold, maxTrustThreshold,
				minTrustThreshold, maxLEBA, minLEBA, maxArgumentationRounds);
		logger.info("Total number of experiments executed in all iterations and with all parameters for "
				+ simulationID + " simulation = " + totalExps);
		// ***********************

	}

	/**
	 * 
	 */
	private void launchMarketingBatch() {

		// ***********************
		// MARKETING SIMULACION BATCH
		// ***********************
		String simulationID = "MARKETING";
		String dataset = "src/main/resources/dataset/marketing.csv";
		String simName = "marketing-simulation";
		String experimentFolder = "../experiments/" + simName;
		double testRatio = 0.5;
		boolean centralApproach = true;
		String summaryFile = experimentFolder + "/" + simName + "-summary.csv";
		long seed = 0;
		int iterations = 1;
		String classificationTarget = "Income";
		double delta = 0.1;

		double maxDistanceThreshold = 0.5;
		double minDistanceThreshold = 0.1;
		double maxBeliefThreshold = 0.5;
		double minBeliefThreshold = 0.1;
		double maxTrustThreshold = 0.5;
		double minTrustThreshold = 0.1;
		int maxLEBA = 7;
		int minLEBA = 0;
		int maxArgumentationRounds = 200;
		int maxNumberOfAgents = 4;
		int minNumberOfAgents = 2;

		long totalExps = this.launchSmartBathAndValidatorsForAgentRange(simulationID, dataset,
				experimentFolder, maxNumberOfAgents, minNumberOfAgents, testRatio, centralApproach,
				summaryFile, seed, maxThreads, iterations, classificationTarget, delta,
				SimulationConfiguration.SIMULATION_MODE, maxDistanceThreshold,
				minDistanceThreshold, maxBeliefThreshold, minBeliefThreshold, maxTrustThreshold,
				minTrustThreshold, maxLEBA, minLEBA, maxArgumentationRounds);
		logger.info("Total number of experiments executed in all iterations and with all parameters for "
				+ simulationID + " simulation = " + totalExps);
		// ***********************

	}

	/**
	 * 
	 */
	private void launchNurseryBatch() {

		// ***********************
		// NURSERY SIMULACION BATCH
		// ***********************
		String simulationID = "NURSERY";
		String dataset = "src/main/resources/dataset/nursery.csv";
		String simName = "nursery-simulation";
		String experimentFolder = "../experiments/" + simName;
		double testRatio = 0.5;
		boolean centralApproach = true;
		String summaryFile = experimentFolder + "/" + simName + "-summary.csv";
		long seed = 0;
		int iterations = 1;
		String classificationTarget = "Classification";
		double delta = 0.1;

		double maxDistanceThreshold = 0.5;
		double minDistanceThreshold = 0.1;
		double maxBeliefThreshold = 0.5;
		double minBeliefThreshold = 0.1;
		double maxTrustThreshold = 0.5;
		double minTrustThreshold = 0.1;
		int maxLEBA = 8;
		int minLEBA = 0;
		int maxArgumentationRounds = 200;
		int maxNumberOfAgents = 4;
		int minNumberOfAgents = 2;

		long totalExps = this.launchSmartBathAndValidatorsForAgentRange(simulationID, dataset,
				experimentFolder, maxNumberOfAgents, minNumberOfAgents, testRatio, centralApproach,
				summaryFile, seed, maxThreads, iterations, classificationTarget, delta,
				SimulationConfiguration.SIMULATION_MODE, maxDistanceThreshold,
				minDistanceThreshold, maxBeliefThreshold, minBeliefThreshold, maxTrustThreshold,
				minTrustThreshold, maxLEBA, minLEBA, maxArgumentationRounds);
		logger.info("Total number of experiments executed in all iterations and with all parameters for "
				+ simulationID + " simulation = " + totalExps);
		// ***********************
	}

	/**
	 * 
	 */
	private void launchZooBatch() {

		// ***********************
		// ZOO SIMULACION BATCH
		// ***********************
		String simulationID = "ZOO";
		String dataset = "src/main/resources/dataset/zoo.csv";
		String simName = "zoo-simulation";
		String experimentFolder = "../experiments/" + simName;
		boolean centralApproach = true;
		String summaryFile = experimentFolder + "/" + simName + "-summary.csv";
		long seed = 0;
		// testRatio = 0.1;
		// iterations = 10;
		int kfold = 3;
		String classificationTarget = "AnimalType";
		double delta = 0.1;

		double maxDistanceThreshold = 0.5;
		double minDistanceThreshold = 0.1;
		double maxBeliefThreshold = 0.5;
		double minBeliefThreshold = 0.1;
		double maxTrustThreshold = 0.5;
		double minTrustThreshold = 0.1;
		int maxLEBA = 20;
		int minLEBA = 0;
		int maxArgumentationRounds = 200;
		int maxNumberOfAgents = 5;
		int minNumberOfAgents = 2;

		long totalExps = this.launchSmartBathAndValidatorsForAgentRangeKFold(simulationID, dataset,
				experimentFolder, maxNumberOfAgents, minNumberOfAgents, centralApproach,
				summaryFile, seed, maxThreads, kfold, classificationTarget, delta,
				SimulationConfiguration.SIMULATION_MODE, maxDistanceThreshold,
				minDistanceThreshold, maxBeliefThreshold, minBeliefThreshold, maxTrustThreshold,
				minTrustThreshold, maxLEBA, minLEBA, maxArgumentationRounds);
		logger.info("Total number of experiments executed in all iterations and with all parameters for "
				+ simulationID + " simulation = " + totalExps);
		// ***********************
	}

	public long launchValidationBatchFor(String simulationID, String dataset,
			String experimentFolder, int agentsNumber, double ratio, boolean central,
			String summaryFile, long seed, int maxThreads, int iterations,
			String classificationTarget, double delta, int mode) {
		logger = Logger.getLogger(simulationID + "-OneClickExperimentLauncher");
		LogConfigurator.log2File(logger, simulationID + "-OneClickExperimentLauncher", Level.ALL,
				Level.INFO, experimentFolder);

		logger.info("Executing experiments in " + cores + " cores.");
		long initTime = System.currentTimeMillis();
		long counter = 0;
		simulationID = simulationID + "-TESTRATIO-" + ratio;
		try {
			for (int i = 0; i < iterations; i++) {
				DatasetSplitter splitter = new DatasetSplitter();
				splitter.splitDataset(ratio, agentsNumber, dataset, experimentFolder
						+ "/input/iteration-" + i + "/dataset", central, simulationID, logger, i);
				ExperimentExecutor executor = new ExperimentExecutor();
				simulationID = simulationID + "-TESTRATIO-" + ratio;

				// VALIDATORS
				List<RunnableExperiment> validators = executor.getValidatorsBatch(simulationID,
						agentsNumber, summaryFile, seed, mode, experimentFolder
								+ "/input/iteration-" + i + "/dataset/" + ratio + "testRatio",
						experimentFolder + "/output/iteration-" + i, experimentFolder
								+ "/input/iteration-" + i + "/dataset/" + ratio
								+ "testRatio/test-dataset.csv", classificationTarget, i, ratio);
				logger.info(validators.size()
						+ " validations are ready to execute for simulation: " + simulationID);
				logger.info("---> Starting validations executions...");
				executor.executeValidators(validators, maxLearningThreads, logger);
				logger.info("<--- Finishing validations executions...");

				long finishTime = System.currentTimeMillis();
				this.logTime(simulationID, validators.size(), initTime, finishTime);
				counter = counter + validators.size();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return counter;
	}

	public long launchSmartBathAndValidatorsForAgentRangeKFold(String simulationID, String dataset,
			String experimentFolder, int maxAgentsNumber, int minAgentsNumber, boolean central,
			String summaryFile, long seed, int maxThreads, int kfold, String classificationTarget,
			double delta, int mode, double maxDistanceThreshold, double minDistanceThreshold,
			double maxBeliefThreshold, double minBeliefThreshold, double maxTrustThreshold,
			double minTrustThreshold, int maxLEBA, int minLEBA, int maxArgumentationRounds) {

		double ratio = 1 / (double) kfold;
		long counter = this.launchSmartBathAndValidatorsForAgentRange(simulationID, dataset,
				experimentFolder, maxAgentsNumber, minAgentsNumber, ratio, central, summaryFile,
				seed, maxThreads, kfold, classificationTarget, delta, mode, maxDistanceThreshold,
				minDistanceThreshold, maxBeliefThreshold, minBeliefThreshold, maxTrustThreshold,
				minTrustThreshold, maxLEBA, minLEBA, maxArgumentationRounds);

		return counter;
	}

	public long launchSmartBathAndValidatorsForAgentRangeForConcreteIteration(String simulationID,
			String dataset, String experimentFolder, int maxAgentsNumber, int minAgentsNumber,
			double ratio, boolean central, String summaryFile, long seed, int maxThreads,
			int iteration, String classificationTarget, double delta, int mode,
			double maxDistanceThreshold, double minDistanceThreshold, double maxBeliefThreshold,
			double minBeliefThreshold, double maxTrustThreshold, double minTrustThreshold,
			int maxLEBA, int minLEBA, int maxArgumentationRounds) {
		int agentsNumber = minAgentsNumber;
		long counter = 0;
		while (agentsNumber <= maxAgentsNumber) {
			long aux = this.launchSmartBathAndValidatorsForConcreteIteration(simulationID, dataset,
					experimentFolder, agentsNumber, ratio, central, summaryFile, seed, maxThreads,
					iteration, classificationTarget, delta, mode, maxDistanceThreshold,
					minDistanceThreshold, maxBeliefThreshold, minBeliefThreshold,
					maxTrustThreshold, minTrustThreshold, maxLEBA, minLEBA, maxArgumentationRounds);
			agentsNumber++;
			counter = counter + aux;
		}
		return counter;

	}

	public long launchSmartBathAndValidatorsForAgentRange(String simulationID, String dataset,
			String experimentFolder, int maxAgentsNumber, int minAgentsNumber, double ratio,
			boolean central, String summaryFile, long seed, int maxThreads, int iterations,
			String classificationTarget, double delta, int mode, double maxDistanceThreshold,
			double minDistanceThreshold, double maxBeliefThreshold, double minBeliefThreshold,
			double maxTrustThreshold, double minTrustThreshold, int maxLEBA, int minLEBA,
			int maxArgumentationRounds) {
		int agentsNumber = minAgentsNumber;
		long counter = 0;
		while (agentsNumber <= maxAgentsNumber) {
			long aux = this.launchSmartBathAndValidatorsFor(simulationID, dataset,
					experimentFolder, agentsNumber, ratio, central, summaryFile, seed, maxThreads,
					iterations, classificationTarget, delta, mode, maxDistanceThreshold,
					minDistanceThreshold, maxBeliefThreshold, minBeliefThreshold,
					maxTrustThreshold, minTrustThreshold, maxLEBA, minLEBA, maxArgumentationRounds);
			agentsNumber++;
			counter = counter + aux;
		}
		return counter;
	}

	public long launchSmartBathAndValidatorsForConcreteIteration(String simulationID,
			String dataset, String experimentFolder, int agentsNumber, double ratio,
			boolean central, String summaryFile, long seed, int maxThreads, int iteration,
			String classificationTarget, double delta, int mode, double maxDistanceThreshold,
			double minDistanceThreshold, double maxBeliefThreshold, double minBeliefThreshold,
			double maxTrustThreshold, double minTrustThreshold, int maxLEBA, int minLEBA,
			int maxArgumentationRounds) {
		try {
			DatasetSplitter splitter = new DatasetSplitter();
			splitter.splitDataset(ratio, agentsNumber, dataset, experimentFolder
					+ "/input/iteration-" + iteration + "/dataset", central, simulationID, logger,
					iteration);
			ExperimentExecutor executor = new ExperimentExecutor();

			// VALIDATORS
			List<RunnableExperiment> validators = executor.getValidatorsBatch(simulationID,
					agentsNumber, summaryFile, seed, mode, experimentFolder + "/input/iteration-"
							+ iteration + "/dataset/" + ratio + "testRatio", experimentFolder
							+ "/output/iteration-" + iteration, experimentFolder
							+ "/input/iteration-" + iteration + "/dataset/" + ratio
							+ "testRatio/test-dataset.csv", classificationTarget, iteration, ratio);
			logger.info(validators.size() + " validations are ready to execute for simulation: "
					+ simulationID + " for iteration " + iteration);
			executor.executeValidators(validators, maxLearningThreads, logger);

			// EXPERIMENTS
			List<RunnableExperiment> experiments = executor.getExperimentSmartBatch(simulationID,
					agentsNumber, summaryFile, seed, mode, experimentFolder + "/input/iteration-"
							+ iteration + "/dataset/" + ratio + "testRatio", experimentFolder
							+ "/output/iteration-" + iteration, experimentFolder
							+ "/input/iteration-" + iteration + "/dataset/" + ratio
							+ "testRatio/test-dataset.csv", classificationTarget, delta, iteration,
					maxDistanceThreshold, minDistanceThreshold, maxBeliefThreshold,
					minBeliefThreshold, maxTrustThreshold, minTrustThreshold, maxLEBA, minLEBA,
					maxArgumentationRounds);
			logger.info(experiments.size() + " experiments are ready to execute for simulation: "
					+ simulationID + " for iteration " + iteration);
			logger.info("---> Starting experiments executions...");
			executor.executeExperiments(experiments, maxThreads, logger);
			logger.info("<--- Finishing experiments executions...");
			long finishTime = System.currentTimeMillis();
			long experimentsCount = validators.size() + experiments.size();
			this.logTime(simulationID, experimentsCount, initTime, finishTime);

			return experimentsCount;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return Long.MIN_VALUE;
	}

	public long launchSmartBathAndValidatorsFor(String simulationID, String dataset,
			String experimentFolder, int agentsNumber, double ratio, boolean central,
			String summaryFile, long seed, int maxThreads, int iterations,
			String classificationTarget, double delta, int mode, double maxDistanceThreshold,
			double minDistanceThreshold, double maxBeliefThreshold, double minBeliefThreshold,
			double maxTrustThreshold, double minTrustThreshold, int maxLEBA, int minLEBA,
			int maxArgumentationRounds) {
		logger = Logger.getLogger(simulationID + "-OneClickExperimentLauncher");
		LogConfigurator.log2File(logger, simulationID + "-OneClickExperimentLauncher", Level.ALL,
				Level.INFO, experimentFolder);

		logger.info("Executing experiments in " + cores + " cores.");
		long experimentsCount = 0;

		int ratioint = (int) (ratio * 100);
		double roundedratio = ((double) ratioint) / 100;
		simulationID = simulationID + "-TESTRATIO-" + roundedratio + "-MAXARGSROUNDS-"
				+ maxArgumentationRounds;
		try {
			for (int i = 0; i < iterations; i++) {
				long count = this.launchSmartBathAndValidatorsForConcreteIteration(simulationID,
						dataset, experimentFolder, agentsNumber, roundedratio, central,
						summaryFile, seed, maxThreads, i, classificationTarget, delta, mode,
						maxDistanceThreshold, minDistanceThreshold, maxBeliefThreshold,
						minBeliefThreshold, maxTrustThreshold, minTrustThreshold, maxLEBA, minLEBA,
						maxArgumentationRounds);
				experimentsCount = experimentsCount + count;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return experimentsCount;

	}

	private void logTime(String simulationID, long numberOfExperiments, long initTime,
			long finishTime) {
		long interval = finishTime - initTime;
		long intervalSecs = interval / 1000;
		long intervalMins = intervalSecs / 60;
		long intervalHours = intervalMins / 60;
		logger.info("Simulation ID: " + simulationID + " -> " + numberOfExperiments
				+ " experiments have been executed in " + intervalHours + " hours, "
				+ (intervalMins % 60) + " minutes, " + (intervalSecs % 60) + " seconds and "
				+ (interval % 1000) + " miliseconds. Finishing execution of simulations.");
	}
}
