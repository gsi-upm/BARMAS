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
 * @version 0.1
 * 
 */
public class OneClickExperimentLauncher {

	private Logger logger;

	private String simulationID;
	private String dataset;
	private String experimentFolder;
	private int numberOfAgents;
	private double testRatio;
	private boolean centralApproach;
	private String summaryFile;
	private long seed;
	private int maxThreads;
	private int cores;
	private int iterations;
	private String classificationTarget;
	private double delta;
	private long initTime;

	private double maxDistanceThreshold;
	private double minDistanceThreshold;
	private double maxBeliefThreshold;
	private double minBeliefThreshold;
	private double maxTrustThreshold;
	private double minTrustThreshold;
	private int maxLEBA;
	private int minLEBA;
	private int maxArgumentationRounds;

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
	}

	/**
	 * 
	 */
	private void click() {

		// this.launchZooBatch();
		// this.launchMarketingBatch();
		// this.launchNurseryBatch();
		// this.launchSolarFlareBatch();
		// this.launchKowlanCZBatch();
		this.launchMushroomBatch();
		this.launchChessBatch();
		this.launchPokerBatch();

		long finishTime = System.currentTimeMillis();
		long interval = finishTime - initTime;
		long intervalSecs = interval / 1000;
		long intervalMins = intervalSecs / 60;
		long intervalHours = intervalMins / 60;
		logger.info("All experiments have been executed in " + intervalHours
				+ " hours, " + (intervalMins % 60) + " minutes, "
				+ (intervalSecs % 60) + " seconds and " + (interval % 1000)
				+ " miliseconds. Finishing execution of simulations.");
	}

	/**
	 * 
	 */
	private void launchPokerBatch() {

		// ***********************
		// POKER SIMULACION BATCH
		// ***********************
		simulationID = "POKER";
		dataset = "src/main/resources/dataset/poker.csv";
		experimentFolder = "poker-simulation";
		numberOfAgents = 200;
		testRatio = 0.1;
		centralApproach = true;
		summaryFile = experimentFolder + "/" + experimentFolder
				+ "-summary.csv";
		seed = 0;
		iterations = 1;
		classificationTarget = "PokerHand";
		delta = 0.1;

		maxDistanceThreshold = 0.5;
		minDistanceThreshold = 0.1;
		maxBeliefThreshold = 0.5;
		minBeliefThreshold = 0.1;
		maxTrustThreshold = 0.5;
		minTrustThreshold = 0.1;
		maxLEBA = 6;
		minLEBA = 0;
		maxArgumentationRounds = 1000;

		 this.launchSmartBathAndValidatorsFor(simulationID, dataset,
		 experimentFolder, numberOfAgents, testRatio, centralApproach,
		 summaryFile, seed, maxThreads, iterations,
		 classificationTarget, delta,
		 SimulationConfiguration.SIMULATION_MODE, maxDistanceThreshold,
		 minDistanceThreshold, maxBeliefThreshold, minBeliefThreshold,
		 maxTrustThreshold, minTrustThreshold, maxLEBA, minLEBA,
		 maxArgumentationRounds);
		// ***********************
	}

	/**
	 * 
	 */
	private void launchChessBatch() {
		// ***********************
		// CHESS SIMULACION BATCH
		// ***********************
		simulationID = "CHESS";
		dataset = "src/main/resources/dataset/kr-vs-k.csv";
		experimentFolder = "chess-simulation";
		numberOfAgents = 10;
		testRatio = 0.5;
		centralApproach = true;
		summaryFile = experimentFolder + "/" + experimentFolder
				+ "-summary.csv";
		seed = 0;
		iterations = 1;
		classificationTarget = "Game";
		delta = 0.1;

		maxDistanceThreshold = 0.5;
		minDistanceThreshold = 0.1;
		maxBeliefThreshold = 0.5;
		minBeliefThreshold = 0.1;
		maxTrustThreshold = 0.5;
		minTrustThreshold = 0.1;
		maxLEBA = 6;
		minLEBA = 0;
		maxArgumentationRounds = 1000;

		 this.launchSmartBathAndValidatorsFor(simulationID, dataset,
		 experimentFolder, numberOfAgents, testRatio, centralApproach,
		 summaryFile, seed, maxThreads, iterations,
		 classificationTarget, delta,
		 SimulationConfiguration.SIMULATION_MODE, maxDistanceThreshold,
		 minDistanceThreshold, maxBeliefThreshold, minBeliefThreshold,
		 maxTrustThreshold, minTrustThreshold, maxLEBA, minLEBA,
		 maxArgumentationRounds);
		// ***********************
	}

	/**
	 * 
	 */
	private void launchMushroomBatch() {

		// ***********************
		// MUSHROOM SIMULACION BATCH
		// ***********************
		simulationID = "MUSHROOM";
		dataset = "src/main/resources/dataset/agaricus-lepiota.csv";
		experimentFolder = "mushroom-simulation";
		numberOfAgents = 10;
		testRatio = 0.1;
		centralApproach = true;
		summaryFile = experimentFolder + "/" + experimentFolder
				+ "-summary.csv";
		seed = 0;
		iterations = 1;
		classificationTarget = "mushroomPoisonous";
		delta = 0.1;

		maxDistanceThreshold = 0.5;
		minDistanceThreshold = 0.1;
		maxBeliefThreshold = 0.5;
		minBeliefThreshold = 0.1;
		maxTrustThreshold = 0.5;
		minTrustThreshold = 0.1;
		maxLEBA = 8;
		minLEBA = 0;
		maxArgumentationRounds = 200;

		this.launchSmartBathAndValidatorsFor(simulationID, dataset,
				experimentFolder, numberOfAgents, testRatio, centralApproach,
				summaryFile, seed, maxThreads, iterations,
				classificationTarget, delta,
				SimulationConfiguration.SIMULATION_MODE, maxDistanceThreshold,
				minDistanceThreshold, maxBeliefThreshold, minBeliefThreshold,
				maxTrustThreshold, minTrustThreshold, maxLEBA, minLEBA,
				maxArgumentationRounds);
		// ***********************
	}

	/**
	 * 
	 */
	private void launchKowlanCZBatch() {

		// ***********************
		// KOWLANCZ02
		// ***********************
		simulationID = "KOWLANCZ02";
		dataset = "src/main/resources/dataset/kowlancz/CZ02/CZ02-dataset.csv";
		experimentFolder = "kowlancz02-simulation";
		numberOfAgents = 5;
		testRatio = 0.2;
		centralApproach = true;
		summaryFile = experimentFolder + "/" + experimentFolder
				+ "-summary.csv";
		seed = 0;
		iterations = 1;
		classificationTarget = "Diagnosis";
		delta = 0.1;

		maxDistanceThreshold = 0.5;
		minDistanceThreshold = 0.1;
		maxBeliefThreshold = 0.5;
		minBeliefThreshold = 0.1;
		maxTrustThreshold = 0.5;
		minTrustThreshold = 0.1;
		maxLEBA = 10;
		minLEBA = 0;
		maxArgumentationRounds = 200;

		this.launchSmartBathAndValidatorsFor(simulationID, dataset,
				experimentFolder, numberOfAgents, testRatio, centralApproach,
				summaryFile, seed, maxThreads, iterations,
				classificationTarget, delta,
				SimulationConfiguration.SIMULATION_MODE, maxDistanceThreshold,
				minDistanceThreshold, maxBeliefThreshold, minBeliefThreshold,
				maxTrustThreshold, minTrustThreshold, maxLEBA, minLEBA,
				maxArgumentationRounds);
		// ***********************
	}

	/**
	 * 
	 */
	private void launchSolarFlareBatch() {

		// ***********************
		// SOLAR FLARE SIMULACION BATCH
		// ***********************
		simulationID = "SOLARFLARE";
		dataset = "src/main/resources/dataset/solarflare-global.csv";
		experimentFolder = "solarflare-simulation";
		numberOfAgents = 6;
		testRatio = 0.4;
		centralApproach = true;
		summaryFile = experimentFolder + "/" + experimentFolder
				+ "-summary.csv";
		seed = 0;
		iterations = 1;
		classificationTarget = "SolarFlareType";
		delta = 0.1;

		maxDistanceThreshold = 0.5;
		minDistanceThreshold = 0.1;
		maxBeliefThreshold = 0.5;
		minBeliefThreshold = 0.1;
		maxTrustThreshold = 0.5;
		minTrustThreshold = 0.1;
		maxLEBA = 8;
		minLEBA = 0;
		maxArgumentationRounds = 200;

		this.launchSmartBathAndValidatorsFor(simulationID, dataset,
				experimentFolder, numberOfAgents, testRatio, centralApproach,
				summaryFile, seed, maxThreads, iterations,
				classificationTarget, delta,
				SimulationConfiguration.SIMULATION_MODE, maxDistanceThreshold,
				minDistanceThreshold, maxBeliefThreshold, minBeliefThreshold,
				maxTrustThreshold, minTrustThreshold, maxLEBA, minLEBA,
				maxArgumentationRounds);
		// ***********************

	}

	/**
	 * 
	 */
	private void launchMarketingBatch() {

		// ***********************
		// MARKETING SIMULACION BATCH
		// ***********************
		simulationID = "MARKETING";
		dataset = "src/main/resources/dataset/marketing.csv";
		experimentFolder = "marketing-simulation";
		numberOfAgents = 3;
		testRatio = 0.5;
		centralApproach = true;
		summaryFile = experimentFolder + "/" + experimentFolder
				+ "-summary.csv";
		seed = 0;
		iterations = 1;
		classificationTarget = "Income";
		delta = 0.1;

		maxDistanceThreshold = 0.5;
		minDistanceThreshold = 0.1;
		maxBeliefThreshold = 0.5;
		minBeliefThreshold = 0.1;
		maxTrustThreshold = 0.5;
		minTrustThreshold = 0.1;
		maxLEBA = 7;
		minLEBA = 0;
		maxArgumentationRounds = 200;

		this.launchSmartBathAndValidatorsFor(simulationID, dataset,
				experimentFolder, numberOfAgents, testRatio, centralApproach,
				summaryFile, seed, maxThreads, iterations,
				classificationTarget, delta,
				SimulationConfiguration.SIMULATION_MODE, maxDistanceThreshold,
				minDistanceThreshold, maxBeliefThreshold, minBeliefThreshold,
				maxTrustThreshold, minTrustThreshold, maxLEBA, minLEBA,
				maxArgumentationRounds);
		// ***********************

	}

	/**
	 * 
	 */
	private void launchNurseryBatch() {

		// ***********************
		// NURSERY SIMULACION BATCH
		// ***********************
		simulationID = "NURSERY";
		dataset = "src/main/resources/dataset/nursery.csv";
		experimentFolder = "nursery-simulation";
		numberOfAgents = 3;
		testRatio = 0.3;
		centralApproach = true;
		summaryFile = experimentFolder + "/" + experimentFolder
				+ "-summary.csv";
		seed = 0;
		iterations = 1;
		classificationTarget = "Classification";
		delta = 0.1;

		maxDistanceThreshold = 0.5;
		minDistanceThreshold = 0.1;
		maxBeliefThreshold = 0.5;
		minBeliefThreshold = 0.1;
		maxTrustThreshold = 0.5;
		minTrustThreshold = 0.1;
		maxLEBA = 8;
		minLEBA = 0;
		maxArgumentationRounds = 200;

		this.launchSmartBathAndValidatorsFor(simulationID, dataset,
				experimentFolder, numberOfAgents, testRatio, centralApproach,
				summaryFile, seed, maxThreads, iterations,
				classificationTarget, delta,
				SimulationConfiguration.SIMULATION_MODE, maxDistanceThreshold,
				minDistanceThreshold, maxBeliefThreshold, minBeliefThreshold,
				maxTrustThreshold, minTrustThreshold, maxLEBA, minLEBA,
				maxArgumentationRounds);
		// ***********************
	}

	/**
	 * 
	 */
	private void launchZooBatch() {

		// ***********************
		// ZOO SIMULACION BATCH
		// ***********************
		simulationID = "ZOO";
		dataset = "src/main/resources/dataset/zoo.csv";
		experimentFolder = "zoo-simulation";
		numberOfAgents = 2;
		testRatio = 0.4;
		centralApproach = true;
		summaryFile = experimentFolder + "/" + experimentFolder
				+ "-summary.csv";
		seed = 0;
		iterations = 1;
		classificationTarget = "AnimalType";
		delta = 0.1;

		maxDistanceThreshold = 0.5;
		minDistanceThreshold = 0.1;
		maxBeliefThreshold = 0.5;
		minBeliefThreshold = 0.1;
		maxTrustThreshold = 0.5;
		minTrustThreshold = 0.1;
		maxLEBA = 50;
		minLEBA = 0;
		maxArgumentationRounds = 200;

		this.launchSmartBathAndValidatorsFor(simulationID, dataset,
				experimentFolder, numberOfAgents, testRatio, centralApproach,
				summaryFile, seed, maxThreads, iterations,
				classificationTarget, delta,
				SimulationConfiguration.SIMULATION_MODE, maxDistanceThreshold,
				minDistanceThreshold, maxBeliefThreshold, minBeliefThreshold,
				maxTrustThreshold, minTrustThreshold, maxLEBA, minLEBA,
				maxArgumentationRounds);
		// ***********************
	}

	public void launchExperimentBatchFor(String simulationID, String dataset,
			String experimentFolder, int agentsNumber, double ratio,
			boolean central, String summaryFile, long seed, int maxThreads,
			int iterations, String classificationTarget, double delta,
			int mode, boolean trustMode, int maxArgumentationRounds) {
		logger = Logger.getLogger(simulationID + "-OneClickExperimentLauncher");
		LogConfigurator.log2File(logger, simulationID
				+ "-OneClickExperimentLauncher", Level.ALL, Level.INFO,
				experimentFolder);
		logger.info("Executing experiments in " + cores + " cores.");
		long initTime = System.currentTimeMillis();
		try {
			for (int i = 0; i < iterations; i++) {
				DatasetSplitter splitter = new DatasetSplitter();
				splitter.splitDataset(
						ratio,
						agentsNumber,
						dataset,
						experimentFolder + "/input/iteration-" + i + "/dataset",
						central, simulationID, logger);
				ExperimentExecutor executor = new ExperimentExecutor();
				simulationID = simulationID + "-TESTRATIO-" + ratio
						+ "-MAXARGSROUNDS-" + maxArgumentationRounds;

				// EXPERIMENTS
				List<RunnableExperiment> experiments = executor
						.getExperimentFullBatch(simulationID, agentsNumber,
								summaryFile, seed, mode, experimentFolder
										+ "/input/iteration-" + i,
								experimentFolder + "/output/iteration-" + i,
								experimentFolder + "/input/iteration-" + i
										+ "/dataset/test-dataset.csv",
								classificationTarget, delta, i,
								maxArgumentationRounds);
				logger.info(experiments.size()
						+ " experiments are ready to execute for simulation: "
						+ simulationID);
				logger.info("---> Starting experiments executions...");
				executor.executeExperiments(experiments, maxThreads, logger);
				logger.info("<--- Finishing experiments executions...");

				long finishTime = System.currentTimeMillis();
				this.logTime(simulationID, experiments.size(), initTime,
						finishTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void launchValidationBatchFor(String simulationID, String dataset,
			String experimentFolder, int agentsNumber, double ratio,
			boolean central, String summaryFile, long seed, int maxThreads,
			int iterations, String classificationTarget, double delta, int mode) {
		logger = Logger.getLogger(simulationID + "-OneClickExperimentLauncher");
		LogConfigurator.log2File(logger, simulationID
				+ "-OneClickExperimentLauncher", Level.ALL, Level.INFO,
				experimentFolder);

		logger.info("Executing experiments in " + cores + " cores.");
		long initTime = System.currentTimeMillis();

		try {
			for (int i = 0; i < iterations; i++) {
				DatasetSplitter splitter = new DatasetSplitter();
				splitter.splitDataset(
						ratio,
						agentsNumber,
						dataset,
						experimentFolder + "/input/iteration-" + i + "/dataset",
						central, simulationID, logger);
				ExperimentExecutor executor = new ExperimentExecutor();
				simulationID = simulationID + "-TESTRATIO-" + ratio;

				// VALIDATORS
				List<RunnableExperiment> validators = executor
						.getValidatorsBatch(simulationID, agentsNumber,
								summaryFile, seed, mode, experimentFolder
										+ "/input/iteration-" + i,
								experimentFolder + "/output/iteration-" + i,
								experimentFolder + "/input/iteration-" + i
										+ "/dataset/test-dataset.csv",
								classificationTarget, i);
				logger.info(validators.size()
						+ " validations are ready to execute for simulation: "
						+ simulationID);
				logger.info("---> Starting validations executions...");
				executor.executeValidators(validators, maxThreads, logger);
				logger.info("<--- Finishing validations executions...");

				long finishTime = System.currentTimeMillis();
				this.logTime(simulationID, validators.size(), initTime,
						finishTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void launchExperimentBatchFor(String simulationID, String dataset,
			String experimentFolder, int agentsNumber, double ratio,
			boolean central, String summaryFile, long seed, int maxThreads,
			int iterations, String classificationTarget, double delta,
			int mode, int maxArgumentationRounds) {
		logger = Logger.getLogger(simulationID + "-OneClickExperimentLauncher");
		LogConfigurator.log2File(logger, simulationID
				+ "-OneClickExperimentLauncher", Level.ALL, Level.INFO,
				experimentFolder);
		logger.info("Executing experiments in " + cores + " cores.");
		long initTime = System.currentTimeMillis();

		try {
			for (int i = 0; i < iterations; i++) {
				DatasetSplitter splitter = new DatasetSplitter();
				splitter.splitDataset(
						ratio,
						agentsNumber,
						dataset,
						experimentFolder + "/input/iteration-" + i + "/dataset",
						central, simulationID, logger);
				ExperimentExecutor executor = new ExperimentExecutor();
				simulationID = simulationID + "-TESTRATIO-" + ratio
						+ "-MAXARGSROUNDS-" + maxArgumentationRounds;

				// EXPERIMENTS
				List<RunnableExperiment> experiments = executor
						.getExperimentFullBatch(simulationID, agentsNumber,
								summaryFile, seed, mode, experimentFolder
										+ "/input/iteration-" + i,
								experimentFolder + "/output/iteration-" + i,
								experimentFolder + "/input/iteration-" + i
										+ "/dataset/test-dataset.csv",
								classificationTarget, delta, i,
								maxArgumentationRounds);
				logger.info(experiments.size()
						+ " experiments are ready to execute for simulation: "
						+ simulationID);
				logger.info("---> Starting experiments executions...");
				executor.executeExperiments(experiments, maxThreads, logger);
				logger.info("<--- Finishing experiments executions...");

				long finishTime = System.currentTimeMillis();
				this.logTime(simulationID, experiments.size(), initTime,
						finishTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void launchSmartBathAndValidatorsFor(String simulationID,
			String dataset, String experimentFolder, int agentsNumber,
			double ratio, boolean central, String summaryFile, long seed,
			int maxThreads, int iterations, String classificationTarget,
			double delta, int mode, double maxDistanceThreshold,
			double minDistanceThreshold, double maxBeliefThreshold,
			double minBeliefThreshold, double maxTrustThreshold,
			double minTrustThreshold, int maxLEBA, int minLEBA,
			int maxArgumentationRounds) {
		logger = Logger.getLogger(simulationID + "-OneClickExperimentLauncher");
		LogConfigurator.log2File(logger, simulationID
				+ "-OneClickExperimentLauncher", Level.ALL, Level.INFO,
				experimentFolder);

		logger.info("Executing experiments in " + cores + " cores.");
		long initTime = System.currentTimeMillis();

		try {
			for (int i = 0; i < iterations; i++) {
				DatasetSplitter splitter = new DatasetSplitter();
				splitter.splitDataset(
						ratio,
						agentsNumber,
						dataset,
						experimentFolder + "/input/iteration-" + i + "/dataset",
						central, simulationID, logger);
				ExperimentExecutor executor = new ExperimentExecutor();
				simulationID = simulationID + "-TESTRATIO-" + ratio
						+ "-MAXARGSROUNDS-" + maxArgumentationRounds;

				// VALIDATORS
				List<RunnableExperiment> validators = executor
						.getValidatorsBatch(simulationID, agentsNumber,
								summaryFile, seed, mode, experimentFolder
										+ "/input/iteration-" + i,
								experimentFolder + "/output/iteration-" + i,
								experimentFolder + "/input/iteration-" + i
										+ "/dataset/test-dataset.csv",
								classificationTarget, i);
				logger.info(validators.size()
						+ " validations are ready to execute for simulation: "
						+ simulationID + " for iteration " + i);
				executor.executeValidators(validators, maxThreads, logger);

				// EXPERIMENTS
				List<RunnableExperiment> experiments = executor
						.getExperimentSmartBatch(simulationID, agentsNumber,
								summaryFile, seed, mode, experimentFolder
										+ "/input/iteration-" + i,
								experimentFolder + "/output/iteration-" + i,
								experimentFolder + "/input/iteration-" + i
										+ "/dataset/test-dataset.csv",
								classificationTarget, delta, i,
								maxDistanceThreshold, minDistanceThreshold,
								maxBeliefThreshold, minBeliefThreshold,
								maxTrustThreshold, minTrustThreshold, maxLEBA,
								minLEBA, maxArgumentationRounds);
				logger.info(experiments.size()
						+ " experiments are ready to execute for simulation: "
						+ simulationID + " for iteration " + i);
				logger.info("---> Starting experiments executions...");
				executor.executeExperiments(experiments, maxThreads, logger);
				logger.info("<--- Finishing experiments executions...");
				long finishTime = System.currentTimeMillis();
				this.logTime(simulationID,
						validators.size() + experiments.size(), initTime,
						finishTime);

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	public void launchFullBatchFor(String simulationID, String dataset,
			String experimentFolder, int agentsNumber, double ratio,
			boolean central, String summaryFile, long seed, int maxThreads,
			int iterations, String classificationTarget, double delta,
			int mode, int maxArgumentationRounds) {

		logger = Logger.getLogger(simulationID + "-OneClickExperimentLauncher");
		LogConfigurator.log2File(logger, simulationID
				+ "-OneClickExperimentLauncher", Level.ALL, Level.INFO,
				experimentFolder);
		long initTime = System.currentTimeMillis();
		logger.info("Executing experiments in " + cores + " cores.");

		try {
			for (int i = 0; i < iterations; i++) {
				DatasetSplitter splitter = new DatasetSplitter();
				splitter.splitDataset(
						ratio,
						agentsNumber,
						dataset,
						experimentFolder + "/input/iteration-" + i + "/dataset",
						central, simulationID, logger);
				ExperimentExecutor executor = new ExperimentExecutor();
				simulationID = simulationID + "-TESTRATIO-" + ratio
						+ "-MAXARGSROUNDS-" + maxArgumentationRounds;

				// VALIDATORS
				List<RunnableExperiment> validators = executor
						.getValidatorsBatch(simulationID, agentsNumber,
								summaryFile, seed, mode, experimentFolder
										+ "/input/iteration-" + i,
								experimentFolder + "/output/iteration-" + i,
								experimentFolder + "/input/iteration-" + i
										+ "/dataset/test-dataset.csv",
								classificationTarget, i);
				logger.info(validators.size()
						+ " validations are ready to execute for simulation: "
						+ simulationID + " for iteration " + i);
				executor.executeValidators(validators, maxThreads, logger);
				// EXPERIMENTS
				List<RunnableExperiment> experiments = executor
						.getExperimentFullBatch(simulationID, agentsNumber,
								summaryFile, seed, mode, experimentFolder
										+ "/input/iteration-" + i,
								experimentFolder + "/output/iteration-" + i,
								experimentFolder + "/input/iteration-" + i
										+ "/dataset/test-dataset.csv",
								classificationTarget, delta, i,
								maxArgumentationRounds);
				logger.info(experiments.size()
						+ " experiments are ready to execute for simulation: "
						+ simulationID + " for iteration " + i);
				logger.info("---> Starting experiments executions...");
				executor.executeExperiments(experiments, maxThreads, logger);
				logger.info("<--- Finishing experiments executions...");

				long finishTime = System.currentTimeMillis();
				this.logTime(simulationID,
						validators.size() + experiments.size(), initTime,
						finishTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void logTime(String simulationID, int numberOfExperiments,
			long initTime, long finishTime) {
		long interval = finishTime - initTime;
		long intervalSecs = interval / 1000;
		long intervalMins = intervalSecs / 60;
		long intervalHours = intervalMins / 60;
		logger.info("Simulation ID: " + simulationID + " -> "
				+ numberOfExperiments + " experiments have been executed in "
				+ intervalHours + " hours, " + (intervalMins % 60)
				+ " minutes, " + (intervalSecs % 60) + " seconds and "
				+ (interval % 1000)
				+ " miliseconds. Finishing execution of simulations.");
	}
}
