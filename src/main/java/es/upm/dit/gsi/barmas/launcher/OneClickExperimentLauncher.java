/**
 * es.upm.dit.gsi.barmas.launcher.OneClickExperimentLauncher.java
 */
package es.upm.dit.gsi.barmas.launcher;

import java.util.List;
import java.util.logging.Logger;

import es.upm.dit.gsi.barmas.dataset.utils.DatasetSplitter;
import es.upm.dit.gsi.barmas.launcher.utils.ConsoleOutputDisabler;
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

	private Logger logger = Logger.getLogger(OneClickExperimentLauncher.class
			.getSimpleName());

	private String simulationID;
	private String dataset;
	private String experimentDatasetsFolder;
	private String simulationOutputFolder;
	private int numberOfAgents;
	private double testRatio;
	private boolean centralApproach;
	private String summaryFile;
	private long seed;
	private int maxThreads;
	private int iterations;
	private String classificationTarget;
	private double delta;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new OneClickExperimentLauncher().click();

	}

	public OneClickExperimentLauncher() {
		ConsoleOutputDisabler.disableConsoleOutput();
	}

	/**
	 * 
	 */
	private void click() {

		simulationID = "SOLARFLARE";
		dataset = "src/main/resources/dataset/solarflare-global.csv";
		experimentDatasetsFolder = "batchSF/solarflare-simulation-input";
		simulationOutputFolder = "batchSF/solarflare-simulation-output";
		numberOfAgents = 5;
		testRatio = 0.2;
		centralApproach = true;
		summaryFile = "batchSF/solarflare-summary.csv";
		seed = 0;
		maxThreads = 4;
		iterations = 1;
		classificationTarget = "SolarFlareType";
		delta = 0.5;

		this.launchFullBatchFor(simulationID, dataset,
				experimentDatasetsFolder, simulationOutputFolder,
				numberOfAgents, testRatio, centralApproach, summaryFile, seed,
				maxThreads, iterations, classificationTarget, delta);

		// launcher.launchExperimentsFor("KOWLANCZ",
		// "src/main/resources/dataset/kowlancz/CZ02/CZ02-dataset.csv",
		// "batch/kowlancz-simulation-input",
		// "batch/kownlancz-simulation-output", 5, 0.3,
		// true, "batch/kowlancz-summary.csv", 0, 4, 1, "Diagnosis", 0.5);

	}

	public void launchFullBatchFor(String simulationID,
			String dataset, String experimentDatasetsFolder,
			String simulationOutputFolder, int agentsNumber, double ratio,
			boolean central, String summaryFile, long seed, int maxThreads,
			int iterations, String classificationTarget, double delta) {
		try {
			for (int i = 0; i < iterations; i++) {
				DatasetSplitter splitter = new DatasetSplitter();
				splitter.splitDataset(ratio, agentsNumber, dataset,
						experimentDatasetsFolder + "-iteration-" + i
								+ "/dataset", central, simulationID);
				ExperimentExecutor executor = new ExperimentExecutor();
				
				// VALIDATORS
				List<Runnable> validators = executor.getValidatorsBatch(
						simulationID, agentsNumber, summaryFile, seed,
						SimulationConfiguration.SIMULATION_MODE,
						experimentDatasetsFolder + "-iteration-" + i,
						simulationOutputFolder + "-iteration-" + i,
						experimentDatasetsFolder + "-iteration-" + i
								+ "/dataset/test-dataset.csv",
						classificationTarget);
				logger.info(validators.size()
						+ " validations are ready to execute for simulation: "
						+ simulationID);
				logger.info("---> Starting validations executions...");
				executor.executeValidators(validators, maxThreads);
				// First all individuals validators are executed and then no conflicts
				// for BN concurrent learning can occur

				// EXPERIMENTS
				List<Runnable> experiments = executor.getExperimentBatch(
						simulationID, agentsNumber, summaryFile, seed,
						SimulationConfiguration.SIMULATION_MODE,
						experimentDatasetsFolder + "-iteration-" + i,
						simulationOutputFolder + "-iteration-" + i,
						experimentDatasetsFolder + "-iteration-" + i
								+ "/dataset/test-dataset.csv",
						classificationTarget, delta);
				logger.info(experiments.size()
						+ " experiments are ready to execute for simulation: "
						+ simulationID);
				logger.info("---> Starting experiments executions...");
				executor.executeExperiments(experiments, maxThreads);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
