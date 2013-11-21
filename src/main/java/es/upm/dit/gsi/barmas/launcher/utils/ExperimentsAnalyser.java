/**
 * es.upm.dit.gsi.barmas.launcher.utils.plot.ExperimentChartsGenerator.java
 */
package es.upm.dit.gsi.barmas.launcher.utils;

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
import es.upm.dit.gsi.barmas.launcher.utils.plot.Plotter;

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
public class ExperimentsAnalyser {

	private Logger logger;

	private String summaryFile;

	private HashMap<String, Integer> lebas;
	private HashMap<String, Integer> tths;
	private HashMap<String, Integer> dths;
	private HashMap<String, Integer> bths;
	private HashMap<String, Integer> agents;
	private HashMap<String, Integer> its;
	private HashMap<String, Integer> testRatios;

	private double[][][][][][][] theMatrix;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String simName = "zoo-simulation";
		String experimentFolder = "../experiments/" + simName;
		String file = experimentFolder + "/" + simName + "-summary.csv";
		ExperimentsAnalyser chartGenerator = new ExperimentsAnalyser(
				Logger.getLogger(ExperimentsAnalyser.class.getSimpleName()),
				file);
		
		String analysisOutputFolder = experimentFolder + "/output";
		chartGenerator.analyseData(analysisOutputFolder);
		
		String chartOutputFolder = experimentFolder + "/output/charts";
		 chartGenerator.generateAndSaveAllChartsAndExit(chartOutputFolder);
	}

	public ExperimentsAnalyser(Logger logger, String summaryFile) {
		this.logger = logger;
		this.summaryFile = summaryFile;
		this.buildTheMatrix();
	}

	public void analyseData(String outputFolder) {
		File f = new File(outputFolder);
		if (!f.exists()) {
			f.mkdirs();
		}
		this.averageImprovements(outputFolder);
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
			}
			this.saveGlobalImprovementDelaunayChartsForIteration(summaryFile,
					chartOutputFolder);

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
	 * @param outputFolder
	 */
	private void averageImprovements(String outputFolder) {
		try {
			int columns = 3;
			String[] headers = new String[columns];
			headers[0] = "Variable";
			headers[1] = "Value";
			headers[2] = "AvgGlobalImp";

			// Write little info file
			CsvWriter writer = new CsvWriter(new FileWriter(new File(
					outputFolder + "/analysis.csv")), ',');
			writer.writeRecord(headers);
			writer.flush();

			logger.info("Analysis in progress...");
			this.writeAnalysisForVariable(writer, columns, lebas, "LEBA");
			this.writeAnalysisForVariable(writer, columns, tths, "TTH");
			this.writeAnalysisForVariable(writer, columns, dths, "DTH");
			this.writeAnalysisForVariable(writer, columns, bths, "BTH");
			this.writeAnalysisForVariable(writer, columns, agents, "AGENTS");
			this.writeAnalysisForVariable(writer, columns, its, "ITERATION");
			this.writeAnalysisForVariable(writer, columns, testRatios,
					"TESTRATIOS");
			this.writeAnalysisForConfigurationParams(writer, columns);

			writer.close();

			logger.info("Analysis finished! :)");
		} catch (FileNotFoundException e) {
			logger.severe(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			logger.severe(e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * @param writer
	 * @param columns
	 * @throws IOException
	 */
	private void writeAnalysisForConfigurationParams(CsvWriter writer,
			int columns) throws IOException {
		String name = "TTH-DTH-BTH";
		for (int tth = 0; tth < tths.size(); tth++) {
			for (int dth = 0; dth < dths.size(); dth++) {
				for (int bth = 0; bth < bths.size(); bth++) {
					String[] row = new String[columns];
					row[0] = name;
					row[1] = this.getStringForPosInTheMatrix(tths, tth) + "-"
							+ this.getStringForPosInTheMatrix(dths, dth) + "-"
							+ this.getStringForPosInTheMatrix(bths, bth);
					row[2] = this.getAvgImpValueForConfigurationsParams(tth,
							dth, bth);
					writer.writeRecord(row);
				}
			}
		}
	}

	/**
	 * @param tth
	 * @param dth
	 * @param bth
	 * @return
	 */
	private String getAvgImpValueForConfigurationsParams(int tth, int dth,
			int bth) {
		int counter = 0;
		double sum = 0;
		for (int leba = 0; leba < lebas.size(); leba++) {
			for (int agent = 0; agent < agents.size(); agent++) {
				for (int it = 0; it < its.size(); it++) {
					for (int testRatio = 0; testRatio < testRatios.size(); testRatio++) {
						sum = sum
								+ this.getImpFromTheMatrix(theMatrix, leba,
										tth, dth, bth, agent, it, testRatio);
						counter++;
					}
				}
			}
		}
		return Double.toString(sum / counter);
	}

	/**
	 * @param writer
	 * @param columns
	 * @throws IOException
	 * 
	 */
	private void writeAnalysisForVariable(CsvWriter writer, int columns,
			HashMap<String, Integer> map, String name) throws IOException {
		double[] avgImps = this.getAvgImpValueFor(map);
		double avgGlobal = 0;
		for (int i = 0; i < avgImps.length; i++) {
			String[] row = new String[columns];
			row[0] = name;
			row[1] = this.getStringForPosInTheMatrix(map, i);
			row[2] = Double.toString(avgImps[i]);
			writer.writeRecord(row);
			avgGlobal = avgGlobal + avgImps[i];
		}
		avgGlobal = avgGlobal / avgImps.length;
		String[] row = new String[columns];
		row[0] = name;
		row[1] = "AVERAGE";
		row[2] = Double.toString(avgGlobal);
		writer.writeRecord(row);
	}

	/**
	 * 
	 * @param map
	 * @return
	 */
	private double[] getAvgImpValueFor(HashMap<String, Integer> map) {
		if (map == lebas) {
			return this.getAvgImpValueForLEBA();
		} else if (map == tths) {
			return this.getAvgImpValueForTTHS();
		} else if (map == dths) {
			return this.getAvgImpValueForDTHS();
		} else if (map == bths) {
			return this.getAvgImpValueForBTHS();
		} else if (map == agents) {
			return this.getAvgImpValueForAGENTS();
		} else if (map == its) {
			return this.getAvgImpValueForITS();
		} else if (map == testRatios) {
			return this.getAvgImpValueForRATIOS();
		}
		return null;
	}

	/**
	 * @return
	 */
	private double[] getAvgImpValueForRATIOS() {

		double[] avgs = new double[testRatios.size()];
		for (int testRatio = 0; testRatio < testRatios.size(); testRatio++) {
			int counter = 0;
			double sum = 0;
			for (int leba = 0; leba < lebas.size(); leba++) {
				for (int tth = 0; tth < tths.size(); tth++) {
					for (int dth = 0; dth < dths.size(); dth++) {
						for (int bth = 0; bth < bths.size(); bth++) {
							for (int agent = 0; agent < agents.size(); agent++) {
								for (int it = 0; it < its.size(); it++) {
									sum = sum
											+ this.getImpFromTheMatrix(
													theMatrix, leba, tth, dth,
													bth, agent, it, testRatio);
									counter++;
								}
							}
						}
					}
				}
			}
			avgs[testRatio] = sum / counter;
		}
		return avgs;
	}

	/**
	 * @return
	 */
	private double[] getAvgImpValueForITS() {

		double[] avgs = new double[its.size()];
		for (int it = 0; it < its.size(); it++) {
			int counter = 0;
			double sum = 0;
			for (int leba = 0; leba < lebas.size(); leba++) {
				for (int tth = 0; tth < tths.size(); tth++) {
					for (int dth = 0; dth < dths.size(); dth++) {
						for (int bth = 0; bth < bths.size(); bth++) {
							for (int agent = 0; agent < agents.size(); agent++) {
								for (int testRatio = 0; testRatio < testRatios
										.size(); testRatio++) {
									sum = sum
											+ this.getImpFromTheMatrix(
													theMatrix, leba, tth, dth,
													bth, agent, it, testRatio);
									counter++;
								}
							}
						}
					}
				}
			}
			avgs[it] = sum / counter;
		}
		return avgs;
	}

	/**
	 * @return
	 */
	private double[] getAvgImpValueForAGENTS() {

		double[] avgs = new double[agents.size()];
		for (int agent = 0; agent < agents.size(); agent++) {
			int counter = 0;
			double sum = 0;
			for (int leba = 0; leba < lebas.size(); leba++) {
				for (int tth = 0; tth < tths.size(); tth++) {
					for (int dth = 0; dth < dths.size(); dth++) {
						for (int bth = 0; bth < bths.size(); bth++) {
							for (int it = 0; it < its.size(); it++) {
								for (int testRatio = 0; testRatio < testRatios
										.size(); testRatio++) {
									sum = sum
											+ this.getImpFromTheMatrix(
													theMatrix, leba, tth, dth,
													bth, agent, it, testRatio);
									counter++;
								}
							}
						}
					}
				}
			}
			avgs[agent] = sum / counter;
		}
		return avgs;
	}

	/**
	 * @return
	 */
	private double[] getAvgImpValueForBTHS() {

		double[] avgs = new double[bths.size()];
		for (int bth = 0; bth < bths.size(); bth++) {
			int counter = 0;
			double sum = 0;
			for (int leba = 0; leba < lebas.size(); leba++) {
				for (int tth = 0; tth < tths.size(); tth++) {
					for (int dth = 0; dth < dths.size(); dth++) {
						for (int agent = 0; agent < agents.size(); agent++) {
							for (int it = 0; it < its.size(); it++) {
								for (int testRatio = 0; testRatio < testRatios
										.size(); testRatio++) {
									sum = sum
											+ this.getImpFromTheMatrix(
													theMatrix, leba, tth, dth,
													bth, agent, it, testRatio);
									counter++;
								}
							}
						}
					}
				}
			}
			avgs[bth] = sum / counter;
		}
		return avgs;
	}

	/**
	 * @return
	 */
	private double[] getAvgImpValueForDTHS() {

		double[] avgs = new double[dths.size()];
		for (int dth = 0; dth < dths.size(); dth++) {
			int counter = 0;
			double sum = 0;
			for (int leba = 0; leba < lebas.size(); leba++) {
				for (int tth = 0; tth < tths.size(); tth++) {
					for (int bth = 0; bth < bths.size(); bth++) {
						for (int agent = 0; agent < agents.size(); agent++) {
							for (int it = 0; it < its.size(); it++) {
								for (int testRatio = 0; testRatio < testRatios
										.size(); testRatio++) {
									sum = sum
											+ this.getImpFromTheMatrix(
													theMatrix, leba, tth, dth,
													bth, agent, it, testRatio);
									counter++;
								}
							}
						}
					}
				}
			}
			avgs[dth] = sum / counter;
		}
		return avgs;
	}

	/**
	 * @return
	 */
	private double[] getAvgImpValueForTTHS() {

		double[] avgs = new double[tths.size()];
		for (int tth = 0; tth < tths.size(); tth++) {
			int counter = 0;
			double sum = 0;
			for (int leba = 0; leba < lebas.size(); leba++) {
				for (int dth = 0; dth < dths.size(); dth++) {
					for (int bth = 0; bth < bths.size(); bth++) {
						for (int agent = 0; agent < agents.size(); agent++) {
							for (int it = 0; it < its.size(); it++) {
								for (int testRatio = 0; testRatio < testRatios
										.size(); testRatio++) {
									sum = sum
											+ this.getImpFromTheMatrix(
													theMatrix, leba, tth, dth,
													bth, agent, it, testRatio);
									counter++;
								}
							}
						}
					}
				}
			}
			avgs[tth] = sum / counter;
		}
		return avgs;
	}

	/**
	 * @return
	 */
	private double[] getAvgImpValueForLEBA() {

		double[] avgs = new double[lebas.size()];
		for (int leba = 0; leba < lebas.size(); leba++) {
			int counter = 0;
			double sum = 0;
			for (int tth = 0; tth < tths.size(); tth++) {
				for (int dth = 0; dth < dths.size(); dth++) {
					for (int bth = 0; bth < bths.size(); bth++) {
						for (int agent = 0; agent < agents.size(); agent++) {
							for (int it = 0; it < its.size(); it++) {
								for (int testRatio = 0; testRatio < testRatios
										.size(); testRatio++) {
									sum = sum
											+ this.getImpFromTheMatrix(
													theMatrix, leba, tth, dth,
													bth, agent, it, testRatio);
									counter++;
								}
							}
						}
					}
				}
			}
			avgs[leba] = sum / counter;
		}
		return avgs;
	}

	/**
	 * 
	 */
	private void buildTheMatrix() {
		try {
			CsvReader reader = new CsvReader(new FileReader(new File(
					summaryFile)));
			reader.readHeaders();

			List<String[]> experimentResultsRatios = new ArrayList<String[]>();
			while (reader.readRecord()) {
				String[] row = reader.getValues();
				if (row[0].contains(BarmasExperiment.class.getSimpleName())) {
					experimentResultsRatios.add(row);
				}
			}
			reader.close();

			logger.info("Getting parameters for The Matrix...");
			List<String> lebaList = new ArrayList<String>();
			List<String> tthList = new ArrayList<String>();
			List<String> dthList = new ArrayList<String>();
			List<String> bthList = new ArrayList<String>();
			List<String> itList = new ArrayList<String>();
			List<String> agentsList = new ArrayList<String>();
			List<String> ratiosList = new ArrayList<String>();

			for (String[] row : experimentResultsRatios) {
				this.checkAndAdd(lebaList, row[13]);
				this.checkAndAdd(tthList, row[14]);
				this.checkAndAdd(dthList, row[11]);
				this.checkAndAdd(bthList, row[12]);
				this.checkAndAdd(itList, row[15]);
				this.checkAndAdd(agentsList, row[16]);
				this.checkAndAdd(ratiosList, row[17]);
			}

			this.lebas = this.getSortedMap(lebaList);
			this.tths = this.getSortedMap(tthList);
			this.dths = this.getSortedMap(dthList);
			this.bths = this.getSortedMap(bthList);
			this.agents = this.getSortedMap(agentsList);
			this.its = this.getSortedMap(itList);
			this.testRatios = this.getSortedMap(ratiosList);

			logger.info("Creating The Matrix");
			this.theMatrix = new double[lebas.size()][tths.size()][dths.size()][bths
					.size()][agents.size()][its.size()][testRatios.size()];
			for (int leba = 0; leba < lebas.size(); leba++) {
				for (int tth = 0; tth < tths.size(); tth++) {
					for (int dth = 0; dth < dths.size(); dth++) {
						for (int bth = 0; bth < bths.size(); bth++) {
							for (int agent = 0; agent < agents.size(); agent++) {
								for (int it = 0; it < its.size(); it++) {
									for (int testRatio = 0; testRatio < testRatios
											.size(); testRatio++) {
										this.addToTheMatrix(theMatrix, leba,
												tth, dth, bth, agent, it,
												testRatio, Double.MIN_VALUE);
									}
								}
							}
						}
					}
				}
			}
			logger.info("Filling The Matrix...");
			for (String[] row : experimentResultsRatios) {
				String globalImp = row[9];
				double imp = Double.valueOf(globalImp);
				int lebaPos = lebas.get(row[13]);
				int tthPos = tths.get(row[14]);
				int dthPos = dths.get(row[11]);
				int bthPos = bths.get(row[12]);
				int itPos = its.get(row[15]);
				int agentsPos = agents.get(row[16]);
				int testRatiosPos = testRatios.get(row[17]);
				this.addToTheMatrix(theMatrix, lebaPos, tthPos, dthPos, bthPos,
						agentsPos, itPos, testRatiosPos, imp);
			}
			reader.close();
		} catch (Exception e) {
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
	 * @param chartOutputFolder
	 * @param summaryFile
	 * @param chartOutputFolder
	 * @param iterations
	 */
	public void generateAndSaveAllChartsAndExit(String chartOutputFolder) {

		this.generateAndSaveAllCharts(this.summaryFile, chartOutputFolder);
		logger.info("All Charts generated. Execution finished successfully.");
		System.exit(0);
	}

	/**
	 * @param summaryFile
	 * @param chartFolder
	 * @param iteration
	 */
	private void saveGlobalImprovementDelaunayChartsForIteration(
			String summaryFile, String chartFolder) {

		try {
			CsvReader reader = new CsvReader(new FileReader(new File(
					summaryFile)));
			reader.readHeaders();
			String[] headers = reader.getHeaders();

			List<String[]> experimentResultsRatios = new ArrayList<String[]>();
			while (reader.readRecord()) {
				String[] row = reader.getValues();
				experimentResultsRatios.add(row);
			}
			reader.close();

			// Write little info file
			CsvWriter writer = new CsvWriter(new FileWriter(new File(
					chartFolder + "/experiments.csv")), ',');
			writer.writeRecord(headers);
			for (String[] row : experimentResultsRatios) {
				writer.writeRecord(row);
			}
			writer.flush();
			writer.close();

			logger.info("Painting...");
			// Build cylinders and save cylinders charts
			Plotter plotter = new Plotter(logger);

			this.saveChartsLEBAvsTTH(plotter, chartFolder);
			this.saveChartsLEBAvsDTH(plotter, chartFolder);
			this.saveChartsLEBAvsBTH(plotter, chartFolder);
			this.saveChartsTTHvsBTH(plotter, chartFolder);
			this.saveChartsBTHvsDTH(plotter, chartFolder);
			this.saveChartsTTHvsDTH(plotter, chartFolder);

			// TODO add charts for agentsNumber
			// TODO add charts for iterations

		} catch (FileNotFoundException e) {
			logger.severe(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			logger.severe(e.getMessage());
			System.exit(1);
		}

	}

	/**
	 * @param plotter
	 * @param chartFolder
	 */
	private void saveChartsTTHvsDTH(Plotter plotter, String chartFolder) {
		List<Coord3d> globalCoords = new ArrayList<Coord3d>();
		double dthValue = 0;
		double bthValue = 0;
		double tthValue = 0;
		for (int agent = 0; agent < agents.size(); agent++) {
			int agentValue = (int) this
					.getValueForPosInTheMatrix(agents, agent);
			for (int leba = 0; leba < lebas.size(); leba++) {
				double lebaValue = this.getValueForPosInTheMatrix(lebas, leba);
				for (int bth = 0; bth < bths.size(); bth++) {
					bthValue = this.getValueForPosInTheMatrix(bths, bth);
					for (int it = 0; it < its.size(); it++) {
						int itValue = (int) this.getValueForPosInTheMatrix(its,
								it);
						for (int ratio = 0; ratio < testRatios.size(); ratio++) {
							double ratioValue = this.getValueForPosInTheMatrix(
									testRatios, ratio);
							List<Coord3d> coords = new ArrayList<Coord3d>();
							for (int tth = 0; tth < tths.size(); tth++) {
								tthValue = this.getValueForPosInTheMatrix(tths,
										tth);
								for (int dth = 0; dth < dths.size(); dth++) {
									dthValue = this.getValueForPosInTheMatrix(
											dths, dth);
									double imp = this.getImpFromTheMatrix(
											theMatrix, leba, tth, dth, bth,
											agent, it, ratio);
									if (imp != Double.MIN_VALUE) {
										if (dthValue == 2.0 || bthValue == 2.0) {
											if (dthValue == 2.0
													&& bthValue == 2.0) {
												Coord3d coord = new Coord3d(
														tthValue, dthValue, imp);
												coords.add(coord);
												// globalCoords.add(coord);
											}
										} else if (tthValue != 2.0) {
											Coord3d coord = new Coord3d(
													tthValue, dthValue, imp);
											coords.add(coord);
											globalCoords.add(coord);
										}
									}

								}
							}
							// screenshot
							if (lebaValue != 0) { // TODO esto no debería estar
													// creo, pero necesito datos
													// de un batch completo para
													// comprobarlo
								if (dthValue != 2.0 || bthValue != 2.0) {
									String[] axisLabels = new String[3];
									axisLabels[0] = "TTH";
									axisLabels[1] = "DTH";
									axisLabels[2] = "ImprovementRatio";
									plotter.saveDelaunaySurface3DChart(
											chartFolder
													+ "/globalImprovement-TTHvsDTH"
													+ "-LEBA-" + leba + "-BTH-"
													+ bthValue + "-Agents-"
													+ agentValue + "-IT-"
													+ itValue + "-TestRatio-"
													+ ratioValue + ".png",
											axisLabels, coords,
											ViewPositionMode.FREE, null);
								}
							}
						}
					}
				}
			}
		}
		String[] axisLabels = new String[3];
		axisLabels[0] = "TTH";
		axisLabels[1] = "DTH";
		axisLabels[2] = "ImprovementRatio";
		plotter.saveScatter3DChart(chartFolder
				+ "/globalImprovement-TTHvsDTH-Plotter.png", axisLabels,
				globalCoords, 10, ViewPositionMode.FREE, null);
	}

	/**
	 * @param plotter
	 * @param chartFolder
	 */
	private void saveChartsBTHvsDTH(Plotter plotter, String chartFolder) {
		List<Coord3d> globalCoords = new ArrayList<Coord3d>();
		double dthValue = 0;
		for (int agent = 0; agent < agents.size(); agent++) {
			int agentValue = (int) this
					.getValueForPosInTheMatrix(agents, agent);
			for (int leba = 0; leba < lebas.size(); leba++) {
				double lebaValue = this.getValueForPosInTheMatrix(lebas, leba);
				for (int tth = 0; tth < tths.size(); tth++) {
					double tthValue = this.getValueForPosInTheMatrix(tths, tth);
					for (int it = 0; it < its.size(); it++) {
						int itValue = (int) this.getValueForPosInTheMatrix(its,
								it);
						for (int ratio = 0; ratio < testRatios.size(); ratio++) {
							double ratioValue = this.getValueForPosInTheMatrix(
									testRatios, ratio);
							List<Coord3d> coords = new ArrayList<Coord3d>();
							for (int dth = 0; dth < dths.size(); dth++) {
								dthValue = this.getValueForPosInTheMatrix(dths,
										dth);
								for (int bth = 0; bth < bths.size(); bth++) {
									double bthValue = this
											.getValueForPosInTheMatrix(bths,
													bth);
									double imp = this.getImpFromTheMatrix(
											theMatrix, leba, tth, dth, bth,
											agent, it, ratio);
									if (imp != Double.MIN_VALUE) {
										Coord3d coord = new Coord3d(bthValue,
												dthValue, imp);
										coords.add(coord);
										globalCoords.add(coord);
									}
								}

							}
							// screenshot
							if (lebaValue != 0) { // TODO esto no debería estar
													// creo, pero necesito datos
													// de un batch completo para
													// comprobarlo
								if (dthValue != 2.0) {
									String[] axisLabels = new String[3];
									axisLabels[0] = "BTH";
									axisLabels[1] = "DTH";
									axisLabels[2] = "ImprovementRatio";
									plotter.saveDelaunaySurface3DChart(
											chartFolder
													+ "/globalImprovement-BTHvsDTH"
													+ "-LEBA-" + leba + "-TTH-"
													+ tthValue + "-Agents-"
													+ agentValue + "-IT-"
													+ itValue + "-TestRatio-"
													+ ratioValue + ".png",
											axisLabels, coords,
											ViewPositionMode.FREE, null);
								}
							}
						}
					}
				}
			}
		}
		String[] axisLabels = new String[3];
		axisLabels[0] = "BTH";
		axisLabels[1] = "DTH";
		axisLabels[2] = "ImprovementRatio";
		plotter.saveScatter3DChart(chartFolder
				+ "/globalImprovement-BTHvsDTH-Plotter.png", axisLabels,
				globalCoords, 10, ViewPositionMode.FREE, null);
	}

	/**
	 * @param plotter
	 * @param chartFolder
	 */
	private void saveChartsTTHvsBTH(Plotter plotter, String chartFolder) {
		List<Coord3d> globalCoords = new ArrayList<Coord3d>();
		double dthValue = 0;
		double bthValue = 0;
		double tthValue = 0;
		for (int agent = 0; agent < agents.size(); agent++) {
			int agentValue = (int) this
					.getValueForPosInTheMatrix(agents, agent);
			for (int leba = 0; leba < lebas.size(); leba++) {
				double lebaValue = this.getValueForPosInTheMatrix(lebas, leba);
				for (int dth = 0; dth < dths.size(); dth++) {
					dthValue = this.getValueForPosInTheMatrix(dths, dth);
					for (int it = 0; it < its.size(); it++) {
						int itValue = (int) this.getValueForPosInTheMatrix(its,
								it);
						for (int ratio = 0; ratio < testRatios.size(); ratio++) {
							double ratioValue = this.getValueForPosInTheMatrix(
									testRatios, ratio);
							List<Coord3d> coords = new ArrayList<Coord3d>();
							for (int tth = 0; tth < tths.size(); tth++) {
								tthValue = this.getValueForPosInTheMatrix(tths,
										tth);

								for (int bth = 0; bth < bths.size(); bth++) {
									bthValue = this.getValueForPosInTheMatrix(
											bths, bth);
									double imp = this.getImpFromTheMatrix(
											theMatrix, leba, tth, dth, bth,
											agent, it, ratio);
									if (imp != Double.MIN_VALUE) {
										if (dthValue == 2.0 || bthValue == 2.0) {
											if (dthValue == 2.0
													&& bthValue == 2.0) {
												Coord3d coord = new Coord3d(
														tthValue, bthValue, imp);
												coords.add(coord);
												// globalCoords.add(coord);
											}
										} else if (tthValue != 2.0) {
											Coord3d coord = new Coord3d(
													tthValue, bthValue, imp);
											coords.add(coord);
											globalCoords.add(coord);
										}
									}

								}
							}
							// screenshot
							if (lebaValue != 0) { // TODO esto no debería estar
													// creo, pero necesito datos
													// de un batch completo para
													// comprobarlo
								if (dthValue != 2.0 || bthValue != 2.0) {
									String[] axisLabels = new String[3];
									axisLabels[0] = "TTH";
									axisLabels[1] = "BTH";
									axisLabels[2] = "ImprovementRatio";
									plotter.saveDelaunaySurface3DChart(
											chartFolder
													+ "/globalImprovement-TTHvsBTH"
													+ "-LEBA-" + leba + "-DTH-"
													+ dthValue + "-Agents-"
													+ agentValue + "-IT-"
													+ itValue + "-TestRatio-"
													+ ratioValue + ".png",
											axisLabels, coords,
											ViewPositionMode.FREE, null);
								}
							}
						}
					}
				}
			}
		}
		String[] axisLabels = new String[3];
		axisLabels[0] = "TTH";
		axisLabels[1] = "BTH";
		axisLabels[2] = "ImprovementRatio";
		plotter.saveScatter3DChart(chartFolder
				+ "/globalImprovement-TTHvsBTH-Plotter.png", axisLabels,
				globalCoords, 10, ViewPositionMode.FREE, null);
	}

	/**
	 * @param plotter
	 * @param chartFolder
	 */
	private void saveChartsLEBAvsBTH(Plotter plotter, String chartFolder) {
		List<Coord3d> globalCoords = new ArrayList<Coord3d>();
		double dthValue = 0;
		double bthValue = 0;
		for (int agent = 0; agent < agents.size(); agent++) {
			int agentValue = (int) this
					.getValueForPosInTheMatrix(agents, agent);
			for (int tth = 0; tth < tths.size(); tth++) {
				double tthValue = this.getValueForPosInTheMatrix(tths, tth);
				for (int dth = 0; dth < dths.size(); dth++) {
					dthValue = this.getValueForPosInTheMatrix(dths, dth);
					for (int it = 0; it < its.size(); it++) {
						int itValue = (int) this.getValueForPosInTheMatrix(its,
								it);
						for (int ratio = 0; ratio < testRatios.size(); ratio++) {
							double ratioValue = this.getValueForPosInTheMatrix(
									testRatios, ratio);
							List<Coord3d> coords = new ArrayList<Coord3d>();
							for (int leba = 0; leba < lebas.size(); leba++) {
								double lebaValue = this
										.getValueForPosInTheMatrix(lebas, leba);

								for (int bth = 0; bth < bths.size(); bth++) {
									bthValue = this.getValueForPosInTheMatrix(
											bths, bth);
									double imp = this.getImpFromTheMatrix(
											theMatrix, leba, tth, dth, bth,
											agent, it, ratio);
									if (imp != Double.MIN_VALUE) {
										if (dthValue != 2.0 && tthValue != 2.0
												&& bthValue != 2.0) {
											Coord3d coord = new Coord3d(
													lebaValue, bthValue, imp);
											coords.add(coord);
											globalCoords.add(coord);
										} else if (dthValue == 2.0
												&& bthValue == 2.0
												&& tthValue == 2.0) {
											Coord3d coord = new Coord3d(
													lebaValue, bthValue, imp);
											coords.add(coord);
											// globalCoords.add(coord);
										}
									}
								}
							}
							// screenshot
							if (tthValue != 2.0 && dthValue != 2.0) {
								String[] axisLabels = new String[3];
								axisLabels[0] = "LEBA";
								axisLabels[1] = "BTH";
								axisLabels[2] = "ImprovementRatio";
								plotter.saveDelaunaySurface3DChart(chartFolder
										+ "/globalImprovement-LEBAvsDTH"
										+ "-TTH-" + tthValue + "-DTH-"
										+ dthValue + "-Agents-" + agentValue
										+ "-IT-" + itValue + "-TestRatio-"
										+ ratioValue + ".png", axisLabels,
										coords, ViewPositionMode.FREE, null);
							}
						}
					}
				}
			}
		}
		String[] axisLabels = new String[3];
		axisLabels[0] = "LEBA";
		axisLabels[1] = "BTH";
		axisLabels[2] = "ImprovementRatio";
		plotter.saveScatter3DChart(chartFolder
				+ "/globalImprovement-LEBAvsBTH-Plotter.png", axisLabels,
				globalCoords, 10, ViewPositionMode.FREE, null);
	}

	/**
	 * LEBAvsDTH with the rest of variables constants
	 * 
	 * @param plotter
	 * @param chartFolder
	 */
	private void saveChartsLEBAvsDTH(Plotter plotter, String chartFolder) {
		List<Coord3d> globalCoords = new ArrayList<Coord3d>();
		double dthValue = 0;
		for (int agent = 0; agent < agents.size(); agent++) {
			int agentValue = (int) this
					.getValueForPosInTheMatrix(agents, agent);
			for (int tth = 0; tth < tths.size(); tth++) {
				double tthValue = this.getValueForPosInTheMatrix(tths, tth);
				for (int bth = 0; bth < bths.size(); bth++) {
					double bthValue = this.getValueForPosInTheMatrix(bths, bth);
					for (int it = 0; it < its.size(); it++) {
						int itValue = (int) this.getValueForPosInTheMatrix(its,
								it);
						for (int ratio = 0; ratio < testRatios.size(); ratio++) {
							double ratioValue = this.getValueForPosInTheMatrix(
									testRatios, ratio);
							List<Coord3d> coords = new ArrayList<Coord3d>();
							for (int leba = 0; leba < lebas.size(); leba++) {
								double lebaValue = this
										.getValueForPosInTheMatrix(lebas, leba);
								for (int dth = 0; dth < dths.size(); dth++) {
									dthValue = this.getValueForPosInTheMatrix(
											dths, dth);
									double imp = this.getImpFromTheMatrix(
											theMatrix, leba, tth, dth, bth,
											agent, it, ratio);
									if (imp != Double.MIN_VALUE) {
										if (dthValue != 2.0 && tthValue != 2.0
												&& bthValue != 2.0) {
											Coord3d coord = new Coord3d(
													lebaValue, dthValue, imp);
											coords.add(coord);
											globalCoords.add(coord);
										} else if (dthValue == 2.0
												&& bthValue == 2.0
												&& tthValue == 2.0) {
											Coord3d coord = new Coord3d(
													lebaValue, dthValue, imp);
											coords.add(coord);
											// globalCoords.add(coord);
										}
									}
								}
							}
							// screenshot
							if (tthValue != 2.0 && bthValue != 2.0) {
								String[] axisLabels = new String[3];
								axisLabels[0] = "LEBA";
								axisLabels[1] = "DTH";
								axisLabels[2] = "ImprovementRatio";
								plotter.saveDelaunaySurface3DChart(chartFolder
										+ "/globalImprovement-LEBAvsDTH"
										+ "-TTH-" + tthValue + "-BTH-"
										+ bthValue + "-Agents-" + agentValue
										+ "-IT-" + itValue + "-TestRatio-"
										+ ratioValue + ".png", axisLabels,
										coords, ViewPositionMode.FREE, null);
							}
						}
					}
				}
			}
		}
		String[] axisLabels = new String[3];
		axisLabels[0] = "LEBA";
		axisLabels[1] = "DTH";
		axisLabels[2] = "ImprovementRatio";
		plotter.saveScatter3DChart(chartFolder
				+ "/globalImprovement-LEBAvsDTH-Plotter.png", axisLabels,
				globalCoords, 10, ViewPositionMode.FREE, null);
	}

	/**
	 * LEBAvsTTH with the rest of variables constants
	 * 
	 * @param plotter
	 * @param chartFolder
	 */
	private void saveChartsLEBAvsTTH(Plotter plotter, String chartFolder) {
		List<Coord3d> globalCoords = new ArrayList<Coord3d>();
		for (int agent = 0; agent < agents.size(); agent++) {
			int agentValue = (int) this
					.getValueForPosInTheMatrix(agents, agent);
			for (int dth = 0; dth < dths.size(); dth++) {
				double dthValue = this.getValueForPosInTheMatrix(dths, dth);
				for (int bth = 0; bth < bths.size(); bth++) {
					double bthValue = this.getValueForPosInTheMatrix(bths, bth);
					for (int it = 0; it < its.size(); it++) {
						int itValue = (int) this.getValueForPosInTheMatrix(its,
								it);
						for (int ratio = 0; ratio < testRatios.size(); ratio++) {
							double ratioValue = this.getValueForPosInTheMatrix(
									testRatios, ratio);
							List<Coord3d> coords = new ArrayList<Coord3d>();
							for (int leba = 0; leba < lebas.size(); leba++) {
								double lebaValue = this
										.getValueForPosInTheMatrix(lebas, leba);
								for (int tth = 0; tth < tths.size(); tth++) {
									double tthValue = this
											.getValueForPosInTheMatrix(tths,
													tth);
									double imp = this.getImpFromTheMatrix(
											theMatrix, leba, tth, dth, bth,
											agent, it, ratio);
									if (imp != Double.MIN_VALUE) {
										if (dthValue == 2.0 || bthValue == 2.0) {
											if (dthValue == 2.0
													&& bthValue == 2.0) {
												Coord3d coord = new Coord3d(
														lebaValue, tthValue,
														imp);
												coords.add(coord);
												// globalCoords.add(coord);
											}
										} else if (tthValue != 2.0) {
											Coord3d coord = new Coord3d(
													lebaValue, tthValue, imp);
											coords.add(coord);
											globalCoords.add(coord);
										}
									}
								}
							}
							// screenshot
							if (dthValue == 2.0 || bthValue == 2.0) {
								if ((dthValue == 2.0 && bthValue == 2.0)) {
									String[] axisLabels = new String[3];
									axisLabels[0] = "LEBA";
									axisLabels[1] = "TTH";
									axisLabels[2] = "ImprovementRatio";
									plotter.saveDelaunaySurface3DChart(
											chartFolder
													+ "/globalImprovement-LEBAvsTTH"
													+ "-DTH-" + dthValue
													+ "-BTH-" + bthValue
													+ "-Agents-" + agentValue
													+ "-IT-" + itValue
													+ "-TestRatio-"
													+ ratioValue + ".png",
											axisLabels, coords,
											ViewPositionMode.FREE, null);
								}
							} else {
								String[] axisLabels = new String[3];
								axisLabels[0] = "LEBA";
								axisLabels[1] = "TTH";
								axisLabels[2] = "ImprovementRatio";
								plotter.saveDelaunaySurface3DChart(chartFolder
										+ "/globalImprovement-LEBAvsTTH"
										+ "-DTH-" + dthValue + "-BTH-"
										+ bthValue + "-Agents-" + agentValue
										+ "-IT-" + itValue + "-TestRatio-"
										+ ratioValue + ".png", axisLabels,
										coords, ViewPositionMode.FREE, null);
							}
						}
					}
				}
			}
		}
		String[] axisLabels = new String[3];
		axisLabels[0] = "LEBA";
		axisLabels[1] = "TTH";
		axisLabels[2] = "ImprovementRatio";
		plotter.saveScatter3DChart(chartFolder
				+ "/globalImprovement-LEBAvsTTH-Plotter.png", axisLabels,
				globalCoords, 10, ViewPositionMode.FREE, null);
	}

	/**
	 * @param theMatrix
	 * @param lebaPos
	 * @param tthPos
	 * @param dthPos
	 * @param bthPos
	 * @param agentsPos
	 * @param itPos
	 * @param testRatiosPos
	 * @param imp
	 */
	private void addToTheMatrix(double[][][][][][][] theMatrix, int lebaPos,
			int tthPos, int dthPos, int bthPos, int agentsPos, int itPos,
			int testRatiosPos, double imp) {
		theMatrix[lebaPos][tthPos][dthPos][bthPos][agentsPos][itPos][testRatiosPos] = imp;

	}

	/**
	 * @param theMatrix
	 * @param leba
	 * @param tth
	 * @param dth
	 * @param bth
	 * @param agent
	 * @param it
	 * @param ratio
	 * @return
	 */
	private double getImpFromTheMatrix(double[][][][][][][] theMatrix,
			int leba, int tth, int dth, int bth, int agent, int it, int ratio) {
		return theMatrix[leba][tth][dth][bth][agent][it][ratio];
	}

	/**
	 * @param map
	 * @param pos
	 * @return
	 */
	private double getValueForPosInTheMatrix(HashMap<String, Integer> map,
			int pos) {
		for (Entry<String, Integer> entry : map.entrySet()) {
			if (pos == entry.getValue()) {
				String key = entry.getKey();
				double value = Double.valueOf(key);
				return value;
			}
		}
		return Double.MAX_VALUE;
	}

	/**
	 * @param map
	 * @param pos
	 * @return
	 */
	private String getStringForPosInTheMatrix(HashMap<String, Integer> map,
			int pos) {
		for (Entry<String, Integer> entry : map.entrySet()) {
			if (pos == entry.getValue()) {
				String key = entry.getKey();
				return key;
			}
		}
		return "";
	}

	/**
	 * @param list
	 */
	private HashMap<String, Integer> getSortedMap(List<String> list) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		List<String> values = new ArrayList<String>();
		values.addAll(list);
		String[] sortedList = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			String minValue = this.getMinValue(values);
			values.remove(minValue);
			sortedList[i] = minValue;
		}
		for (int i = 0; i < sortedList.length; i++) {
			map.put(sortedList[i], i);
		}
		return map;

	}

	/**
	 * @param values
	 * @return
	 */
	private String getMinValue(List<String> values) {
		double min = Double.MAX_VALUE;
		String minValue = "";
		for (String value : values) {
			double v = new Double(value);
			if (v < min) {
				min = v;
				minValue = value;
			}
		}
		return minValue;
	}

	/**
	 * @param lebaList
	 * @param value
	 */
	private void checkAndAdd(List<String> lebaList, String value) {
		if (!lebaList.contains(value)) {
			lebaList.add(value);
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
			axisLabels[2] = "SuccessRatio";
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
