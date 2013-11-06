/**
 * es.upm.dit.gsi.barmas.launcher.utils.plot.ExperimentChartsGenerator.java
 */
package es.upm.dit.gsi.barmas.launcher.utils.plot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Cylinder;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import es.upm.dit.gsi.barmas.launcher.experiments.BarmasAgentValidator;
import es.upm.dit.gsi.barmas.launcher.experiments.BarmasExperiment;
import es.upm.dit.gsi.barmas.launcher.logging.LogConfigurator;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.launcher.utils.plot.ExperimentChartsGenerator.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 05/11/2013
 * @version 0.1
 * 
 */
public class ExperimentChartsGenerator {

	private Logger logger;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ExperimentChartsGenerator chartGenerator = new ExperimentChartsGenerator(
				Logger.getLogger(ExperimentChartsGenerator.class
						.getSimpleName()));
		String experimentFolder = "solarflare-simulation";
		String summaryFile = experimentFolder + "/" + experimentFolder
				+ "-summary.csv";
		chartGenerator.generateAndSaveAllChartsAndExit(summaryFile,
				experimentFolder + "/output/charts");
	}

	public ExperimentChartsGenerator(Logger logger) {
		this.logger = logger;
	}

	/**
	 * @param summaryFile
	 * @param chartOutputFolder
	 * @param iterations
	 */
	public void generateAndSaveAllCharts(String summaryFile,
			String chartOutputFolder) {

		LogConfigurator.log2File(logger, "ExperimentChartsGenerator",
				Level.ALL, Level.INFO, chartOutputFolder);

		CsvReader reader;
		try {
			reader = new CsvReader(new FileReader(new File(summaryFile)));
			reader.readHeaders();

			int itsNum = 0;
			while (reader.readRecord()) {
				String[] row = reader.getValues();
				String[] splits = row[0].split("-");
				for (int i = 0; i < splits.length; i++) {
					if (splits[i].equals("IT")) {
						int aux = new Integer(splits[++i]);
						if (aux >= itsNum) {
							itsNum = aux + 1;
						}
					}
				}
			}

			reader.close();

			for (int i = 0; i < itsNum; i++) {
				this.saveValidationCylinderChartForIteration(summaryFile,
						chartOutputFolder, i);
				this.saveGlobalImprovementDelaunayChartsForIteration(
						summaryFile, chartOutputFolder, i);
			}

			if (itsNum > 1) {
				// Save all global charts
				// Validation global with cylinders
				this.saveValidationCylinderChart(summaryFile, chartOutputFolder);
				// TODO think in interesting scatter plots
			}

		} catch (FileNotFoundException e) {
			logger.severe(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			logger.severe(e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * @param summaryFile
	 * @param chartOutputFolder
	 */
	private void saveValidationCylinderChart(String summaryFile,
			String chartOutputFolder) {

		String globalChartFolder = chartOutputFolder + "/global";
		File chartFolderFile = new File(globalChartFolder);
		if (!chartFolderFile.isDirectory() || !chartFolderFile.exists()) {
			chartFolderFile.mkdirs();
		}

		try {
			CsvReader reader = new CsvReader(new FileReader(new File(
					summaryFile)));
			reader.readHeaders();
			String[] headers = reader.getHeaders();

			List<String[]> validationResults = new ArrayList<String[]>();
			while (reader.readRecord()) {
				String[] row = reader.getValues();
				if (row[0].contains(BarmasAgentValidator.class.getSimpleName())) {
					validationResults.add(row);
				}
			}
			reader.close();

			// Write little info file
			CsvWriter writer = new CsvWriter(new FileWriter(new File(
					globalChartFolder + "/global-validations.csv")), ',');
			writer.writeRecord(headers);
			for (String[] row : validationResults) {
				writer.writeRecord(row);
			}
			writer.flush();
			writer.close();

			// Build cylinders and save cylinders charts
			Plotter plotter = new Plotter(logger);
			List<Cylinder> cylinders = new ArrayList<Cylinder>();
			HashMap<Integer, Float> mins = new HashMap<Integer, Float>();
			HashMap<Integer, Float> maxs = new HashMap<Integer, Float>();
			for (String[] row : validationResults) {
				float ratio = new Float(row[3]);
				if (row[0].contains("-BayesCentralAgent-")) {
					int agentNumberInteger = -1;
					if (!mins.containsKey(agentNumberInteger)) {
						mins.put(agentNumberInteger, ratio);
						maxs.put(agentNumberInteger, ratio);
					} else if (ratio < mins.get(agentNumberInteger)) {
						mins.put(agentNumberInteger, ratio);
					} else if (ratio > maxs.get(agentNumberInteger)) {
						maxs.put(agentNumberInteger, ratio);
					}
				} else {
					String simulationID = row[0];
					String[] splits = simulationID.split("-");
					for (String split : splits) {
						String aux = "Agent";
						if (split.startsWith(aux)) {
							String agentNumber = split.substring(aux.length());
							int agentNumberInteger = new Integer(agentNumber);
							if (!mins.containsKey(agentNumberInteger)) {
								mins.put(agentNumberInteger, ratio);
								maxs.put(agentNumberInteger, ratio);
							} else if (ratio < mins.get(agentNumberInteger)) {
								mins.put(agentNumberInteger, ratio);
							} else if (ratio > maxs.get(agentNumberInteger)) {
								maxs.put(agentNumberInteger, ratio);
							}
							break;
						}
					}
				}
			}

			float radius = 0.5f;
			for (int i = -1; i < mins.size() - 1; i++) {
				Coord3d baseCentre = new Coord3d(i * 2, i * 2,
						100 * mins.get(i));
				float height = (maxs.get(i) - mins.get(i)) * 100;
				if (height == 0) {
					height = 0.01f;
				}
				Cylinder cylinder = plotter.getCylinder(baseCentre, height,
						radius);
				cylinder.setColorMapper(new ColorMapper(new ColorMapRainbow(),
						new Color(height, height, height)));
				cylinders.add(cylinder);
			}

			logger.info("Generating global validation chart");
			String[] axisLabels = new String[3];
			axisLabels[0] = "";
			axisLabels[1] = "Agent Number";
			axisLabels[2] = "Ratio";
			// plotter.saveCylinder3DChart(globalChartFolder
			// + "/global-validations.png", axisLabels, cylinders,
			// ViewPositionMode.FREE, null);
			plotter.saveCylinder3DChart(globalChartFolder
					+ "/global-validations.png", axisLabels, cylinders,
					ViewPositionMode.PROFILE, new Coord3d(0, 1, 0));

		} catch (FileNotFoundException e) {
			logger.severe(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			logger.severe(e.getMessage());
			System.exit(1);
		}

	}

	/**
	 * @param summaryFile
	 * @param chartOutputFolder
	 * @param iterations
	 */
	public void generateAndSaveAllChartsAndExit(String summaryFile,
			String chartOutputFolder) {

		this.generateAndSaveAllCharts(summaryFile, chartOutputFolder);
		logger.info("All Charts generated. Execution finished successfully.");
		System.exit(0);
	}

	/**
	 * @param summaryFile
	 * @param chartFolder
	 * @param iteration
	 */
	private void saveGlobalImprovementDelaunayChartsForIteration(
			String summaryFile, String chartFolder, int iteration) {
		String iterationChartFolder = chartFolder + "/iteration-" + iteration;
		File chartFolderFile = new File(iterationChartFolder);
		if (!chartFolderFile.isDirectory() || !chartFolderFile.exists()) {
			chartFolderFile.mkdirs();
		}

		try {
			CsvReader reader = new CsvReader(new FileReader(new File(
					summaryFile)));
			reader.readHeaders();
			String[] headers = reader.getHeaders();

			List<String[]> experimentResultsRatios = new ArrayList<String[]>();
			while (reader.readRecord()) {
				String[] row = reader.getValues();
				if (row[0].contains(BarmasExperiment.class.getSimpleName())
						&& row[0].contains("-IT-" + iteration)) {
					experimentResultsRatios.add(row);
				}
			}
			reader.close();

			// Write little info file
			CsvWriter writer = new CsvWriter(new FileWriter(new File(
					iterationChartFolder + "/experiments.csv")), ',');
			writer.writeRecord(headers);
			for (String[] row : experimentResultsRatios) {
				writer.writeRecord(row);
			}
			writer.flush();
			writer.close();

			// Build cylinders and save cylinders charts
			Plotter plotter = new Plotter(logger);
			HashMap<Double, List<Coord3d>> bthConstantGlobalImpCoordsNoTrust = new HashMap<Double, List<Coord3d>>();
			HashMap<Double, List<Coord3d>> thConstantGlobalImpCoordsNoTrust = new HashMap<Double, List<Coord3d>>();
			HashMap<Integer, List<Coord3d>> lepaConstantGlobalImpCoordsNoTrust = new HashMap<Integer, List<Coord3d>>();
			HashMap<Double, List<Coord3d>> bthConstantGlobalImpCoordsTrust = new HashMap<Double, List<Coord3d>>();
			HashMap<Double, List<Coord3d>> thConstantGlobalImpCoordsTrust = new HashMap<Double, List<Coord3d>>();
			HashMap<Integer, List<Coord3d>> lepaConstantGlobalImpCoordsTrust = new HashMap<Integer, List<Coord3d>>();

			for (String[] row : experimentResultsRatios) {
				String simulationID = row[0];
				if (simulationID.contains("-TRUSTMODE-OFF-")) {
					String[] splits = simulationID.split("-");
					double threshold = -1;
					double beliefThreshold = -1;
					int lepa = -1;
					double globalImprovementRatio = new Double(row[9]);
					for (int i = 0; i < splits.length; i++) {
						if (splits[i].equals("TH")) {
							threshold = new Double(splits[++i]);
							if (!thConstantGlobalImpCoordsNoTrust
									.containsKey(threshold)) {
								thConstantGlobalImpCoordsNoTrust.put(threshold,
										new ArrayList<Coord3d>());
							}
						} else if (splits[i].equals("BTH")) {
							beliefThreshold = new Double(splits[++i]);
							if (!bthConstantGlobalImpCoordsNoTrust
									.containsKey(beliefThreshold)) {
								bthConstantGlobalImpCoordsNoTrust.put(
										beliefThreshold,
										new ArrayList<Coord3d>());
							}
						} else if (splits[i].equals("LEPA")) {
							lepa = new Integer(splits[++i]);
							if (!lepaConstantGlobalImpCoordsNoTrust
									.containsKey(lepa)) {
								lepaConstantGlobalImpCoordsNoTrust.put(lepa,
										new ArrayList<Coord3d>());
							}
						}
					}

					Coord3d point = null;
					point = new Coord3d(beliefThreshold, lepa,
							globalImprovementRatio);
					thConstantGlobalImpCoordsNoTrust.get(threshold).add(point);
					point = new Coord3d(threshold, lepa, globalImprovementRatio);
					bthConstantGlobalImpCoordsNoTrust.get(beliefThreshold).add(
							point);
					point = new Coord3d(threshold, beliefThreshold,
							globalImprovementRatio);
					lepaConstantGlobalImpCoordsNoTrust.get(lepa).add(point);
				} else {
					String[] splits = simulationID.split("-");
					double threshold = -1;
					double beliefThreshold = -1;
					int lepa = -1;
					double globalImprovementRatio = new Double(row[9]);
					for (int i = 0; i < splits.length; i++) {
						if (splits[i].equals("TH")) {
							threshold = new Double(splits[++i]);
							if (!thConstantGlobalImpCoordsTrust
									.containsKey(threshold)) {
								thConstantGlobalImpCoordsTrust.put(threshold,
										new ArrayList<Coord3d>());
							}
						} else if (splits[i].equals("BTH")) {
							beliefThreshold = new Double(splits[++i]);
							if (!bthConstantGlobalImpCoordsTrust
									.containsKey(beliefThreshold)) {
								bthConstantGlobalImpCoordsTrust.put(
										beliefThreshold,
										new ArrayList<Coord3d>());
							}
						} else if (splits[i].equals("LEPA")) {
							lepa = new Integer(splits[++i]);
							if (!lepaConstantGlobalImpCoordsTrust
									.containsKey(lepa)) {
								lepaConstantGlobalImpCoordsTrust.put(lepa,
										new ArrayList<Coord3d>());
							}
						}
					}

					Coord3d point = null;
					point = new Coord3d(beliefThreshold, lepa,
							globalImprovementRatio);
					thConstantGlobalImpCoordsTrust.get(threshold).add(point);
					point = new Coord3d(threshold, lepa, globalImprovementRatio);
					bthConstantGlobalImpCoordsTrust.get(beliefThreshold).add(
							point);
					point = new Coord3d(threshold, beliefThreshold,
							globalImprovementRatio);
					lepaConstantGlobalImpCoordsTrust.get(lepa).add(point);
				}
			}

			logger.info("Generating success chart for iteration " + iteration);

			String[] axisLabels = new String[3];
			axisLabels[0] = "Threshold";
			axisLabels[1] = "LEPA";
			axisLabels[2] = "Ratio";
			for (Entry<Double, List<Coord3d>> entry : bthConstantGlobalImpCoordsNoTrust
					.entrySet()) {
				plotter.getDelaunayChart(entry.getValue());
				plotter.saveDelaunaySurface3DChart(iterationChartFolder
						+ "/globalImprovement-BTH-" + entry.getKey() + "-TRUSTMODE-OFF.png",
						axisLabels, entry.getValue(), ViewPositionMode.FREE,
						null);
			}
			for (Entry<Double, List<Coord3d>> entry : bthConstantGlobalImpCoordsTrust
					.entrySet()) {
				plotter.getDelaunayChart(entry.getValue());
				plotter.saveDelaunaySurface3DChart(iterationChartFolder
						+ "/globalImprovement-BTH-" + entry.getKey() + "-TRUSTMODE-ON.png",
						axisLabels, entry.getValue(), ViewPositionMode.FREE,
						null);
			}

			axisLabels[0] = "BeliefThreshold";
			for (Entry<Double, List<Coord3d>> entry : thConstantGlobalImpCoordsNoTrust
					.entrySet()) {
				plotter.saveDelaunaySurface3DChart(iterationChartFolder
						+ "/globalImprovement-TH-" + entry.getKey() + "-TRUSTMODE-OFF.png",
						axisLabels, entry.getValue(), ViewPositionMode.FREE,
						null);
			}

			axisLabels[0] = "BeliefThreshold";
			for (Entry<Double, List<Coord3d>> entry : thConstantGlobalImpCoordsTrust
					.entrySet()) {
				plotter.saveDelaunaySurface3DChart(iterationChartFolder
						+ "/globalImprovement-TH-" + entry.getKey() + "-TRUSTMODE-ON.png",
						axisLabels, entry.getValue(), ViewPositionMode.FREE,
						null);
			}

			axisLabels[0] = "Threshold";
			axisLabels[1] = "BeliefThreshold";
			for (Entry<Integer, List<Coord3d>> entry : lepaConstantGlobalImpCoordsNoTrust
					.entrySet()) {
				plotter.saveDelaunaySurface3DChart(iterationChartFolder
						+ "/globalImprovement-LEPA-" + entry.getKey() + "-TRUSTMODE-OFF.png",
						axisLabels, entry.getValue(), ViewPositionMode.FREE,
						null);
			}
			for (Entry<Integer, List<Coord3d>> entry : lepaConstantGlobalImpCoordsTrust
					.entrySet()) {
				plotter.saveDelaunaySurface3DChart(iterationChartFolder
						+ "/globalImprovement-LEPA-" + entry.getKey() + "-TRUSTMODE-ON.png",
						axisLabels, entry.getValue(), ViewPositionMode.FREE,
						null);
			}

		} catch (FileNotFoundException e) {
			logger.severe(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			logger.severe(e.getMessage());
			System.exit(1);
		}

	}

	/**
	 * @param summaryFile
	 * @param chartFolder
	 * @param iteration
	 */
	private void saveValidationCylinderChartForIteration(String summaryFile,
			String chartFolder, int iteration) {
		String iterationChartFolder = chartFolder + "/iteration-" + iteration;
		File chartFolderFile = new File(iterationChartFolder);
		if (!chartFolderFile.isDirectory() || !chartFolderFile.exists()) {
			chartFolderFile.mkdirs();
		}

		try {
			CsvReader reader = new CsvReader(new FileReader(new File(
					summaryFile)));
			reader.readHeaders();
			String[] headers = reader.getHeaders();

			List<String[]> validationResults = new ArrayList<String[]>();
			while (reader.readRecord()) {
				String[] row = reader.getValues();
				if (row[0].contains(BarmasAgentValidator.class.getSimpleName())
						&& row[0].contains("-IT-" + iteration)) {
					validationResults.add(row);
				}
			}
			reader.close();

			// Write little info file
			CsvWriter writer = new CsvWriter(new FileWriter(new File(
					iterationChartFolder + "/validations.csv")), ',');
			writer.writeRecord(headers);
			for (String[] row : validationResults) {
				writer.writeRecord(row);
			}
			writer.flush();
			writer.close();

			// Build cylinders and save cylinders charts
			Plotter plotter = new Plotter(logger);
			List<Cylinder> cylinders = new ArrayList<Cylinder>();
			for (String[] row : validationResults) {
				Coord3d baseCentre = null;
				if (row[0].contains("-BayesCentralAgent-")) {
					baseCentre = new Coord3d(-2, -2, 0);
				} else {
					String simulationID = row[0];
					String[] splits = simulationID.split("-");
					for (String split : splits) {
						String aux = "Agent";
						if (split.startsWith(aux)) {
							String agentNumber = split.substring(aux.length());
							int agentNumberInteger = new Integer(agentNumber);
							baseCentre = new Coord3d(agentNumberInteger * 2,
									agentNumberInteger * 2, 0);
							break;
						}
					}
				}

				float height = new Float(row[3]);
				float radius = 0.5f;
				Cylinder cylinder = plotter.getCylinder(baseCentre, height,
						radius);
				cylinder.setColorMapper(new ColorMapper(new ColorMapRainbow(),
						new Color(height * 200, height * 200, height * 200)));
				cylinders.add(cylinder);
			}

			logger.info("Generating validations chart for iteration "
					+ iteration);
			String[] axisLabels = new String[3];
			axisLabels[0] = "";
			axisLabels[1] = "Agent Number";
			axisLabels[2] = "Ratio";
			plotter.saveCylinder3DChart(iterationChartFolder
					+ "/validations.png", axisLabels, cylinders,
					ViewPositionMode.PROFILE, new Coord3d(0, 1, 0));

		} catch (FileNotFoundException e) {
			logger.severe(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			logger.severe(e.getMessage());
			System.exit(1);
		}

	}

}
