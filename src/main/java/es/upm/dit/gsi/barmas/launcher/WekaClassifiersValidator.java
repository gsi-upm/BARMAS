/**
 * es.upm.dit.gsi.barmas.launcher.WekaClassifiersValidator.java
 */
package es.upm.dit.gsi.barmas.launcher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.csvreader.CsvWriter;

import es.upm.dit.gsi.barmas.launcher.logging.LogConfigurator;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.RBFNetwork;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.rules.ConjunctiveRule;
import weka.classifiers.rules.DTNB;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.PART;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.BFTree;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.J48graft;
import weka.classifiers.trees.LADTree;
import weka.classifiers.trees.LMT;
import weka.classifiers.trees.NBTree;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.SimpleCart;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.launcher.WekaClassifiersValidator.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 17/02/2014
 * @version 0.1
 * 
 */
public class WekaClassifiersValidator {

	private Logger logger;
	private String dataset;
	private String inputFolder;
	private String outputFolder;
	private String resultsFilePath;
	private CsvWriter writer;
	private int folds;
	private int minAgents;
	private int maxAgents;
	private int columns;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		String dataset = "solarflare";
		String simName = "solarflare-simulation";
		String inputFolder = "../experiments-without-weka/" + simName + "/input";
		String outputFolder = "../experiments-without-weka/" + simName + "/weka";
		int folds = 10;
		int maxAgents = 5;
		int minAgents = 2;

		WekaClassifiersValidator validator = new WekaClassifiersValidator(dataset, inputFolder,
				outputFolder, folds, minAgents, maxAgents);
		validator.validateAllWekaClassifiers();

	}

	/**
	 * Constructor
	 * 
	 * @param maxAgents2
	 * 
	 * @throws IOException
	 * 
	 */
	public WekaClassifiersValidator(String simulationID, String inputPath, String outputPath,
			int folds, int minAgents, int maxAgents) throws IOException {
		this.dataset = simulationID;
		this.inputFolder = inputPath;
		this.outputFolder = outputPath;
		this.logger = Logger.getLogger("WekaClassifierValidator");
		LogConfigurator.log2File(logger, "WekaClassifierValidator", Level.ALL, Level.INFO,
				this.outputFolder);

		logger.info("--> Configuring WekaClassifierValidator...");
		this.resultsFilePath = outputPath + "/weka-results.csv";
		this.columns = 8;
		this.folds = folds;
		this.minAgents = minAgents;
		this.maxAgents = maxAgents;
		File dir = new File(this.outputFolder);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}
		this.writer = new CsvWriter(new FileWriter(resultsFilePath), ',');
		String[] headers = new String[this.columns];
		headers[0] = "dataset";
		headers[1] = "kfold";
		headers[2] = "classifier";
		headers[3] = "iteration";
		headers[4] = "ratioOk";
		headers[5] = "ratioWrong";
		headers[6] = "agentID";
		headers[7] = "agents";
		writer.writeRecord(headers);
		logger.info("<-- WekaClassifierValidator configured");
	}

	/**
	 * @throws Exception
	 * 
	 */
	private void validateAllWekaClassifiers() throws Exception {
		List<Classifier> classifiers = this.getNewClassifiers();
		logger.info("Validating all classifiers for dataset: " + this.dataset);

		for (Classifier classifier : classifiers) {
			this.validateClassifier(classifier);
		}

		logger.info("All classifiers validated.");
	}

	/**
	 * V
	 * 
	 * @throws Exception
	 */
	private void validateClassifier(Classifier classifier) throws Exception {

		String classifierName = classifier.getClass().getSimpleName();

		logger.info("--> Starting validation for classfier " + classifierName);
		int ratioint = (int) ((1 / (double) folds) * 100);
		double roundedratio = ((double) ratioint) / 100;
		String[] row;

		// Central Agent
		double[] results = new double[2];
		for (int iteration = 0; iteration < this.folds; iteration++) {
			String inputPath = this.inputFolder + "/" + roundedratio + "testRatio/iteration-"
					+ iteration;
			Instances testData = this.getDataFromCSV(inputPath + "/test-dataset.csv");
			Instances trainData = this.getDataFromCSV(inputPath + "/bayes-central-dataset.csv");
			double[] pcts = this.getValidation(classifier, trainData, testData);
			results[0] = results[0] + pcts[0];
			results[1] = results[1] + pcts[1];

			row = new String[this.columns];
			row[0] = this.dataset;
			row[1] = Integer.toString(this.folds);
			row[2] = classifierName;
			row[3] = Integer.toString(iteration);
			row[4] = Double.toString(pcts[0]);
			row[5] = Double.toString(pcts[1]);
			row[6] = "BayesCentralAgent";
			row[7] = "1";
			writer.writeRecord(row);
		}

		row = new String[this.columns];
		row[0] = this.dataset;
		row[1] = Integer.toString(this.folds);
		row[2] = classifierName;
		row[3] = "AVERAGE";
		row[4] = Double.toString(results[0] / this.folds);
		row[5] = Double.toString(results[1] / this.folds);
		row[6] = "BayesCentralAgent";
		row[7] = "1";
		writer.writeRecord(row);

		logger.info("Validation for BayesCentralAgent dataset with " + classifierName
				+ " done for dataset: " + this.dataset);
		writer.flush();

		// Agents combinations
		for (int i = this.minAgents; i < this.maxAgents; i++) {
			HashMap<Integer, Double> successRatio = new HashMap<Integer, Double>();
			HashMap<Integer, Double> wrongRatio = new HashMap<Integer, Double>();
			for (int j = 0; j < i; j++) {
				successRatio.put(j, 0.0);
				wrongRatio.put(j, 0.0);
			}
			for (int iteration = 0; iteration < this.folds; iteration++) {
				String inputPath = this.inputFolder + "/" + roundedratio + "testRatio/iteration-"
						+ iteration;
				Instances testData = this.getDataFromCSV(inputPath + "/test-dataset.csv");
				for (int j = 0; j < i; j++) {
					Instances trainData = this.getDataFromCSV(inputPath + "/" + i + "agents/agent-"
							+ j + "-dataset.csv");
					double[] pcts = this.getValidation(classifier, trainData, testData);
					successRatio.put(j, successRatio.get(j) + pcts[0]);
					wrongRatio.put(j, wrongRatio.get(j) + pcts[1]);

					row = new String[this.columns];
					row[0] = this.dataset;
					row[1] = Integer.toString(this.folds);
					row[2] = classifierName;
					row[3] = Integer.toString(iteration);
					row[4] = Double.toString(pcts[0]);
					row[5] = Double.toString(pcts[1]);
					row[6] = "Agent" + j;
					row[7] = Integer.toString(i);
					writer.writeRecord(row);
				}

				writer.flush();
			}

			for (int j = 0; j < i; j++) {
				row = new String[this.columns];
				row[0] = this.dataset;
				row[1] = Integer.toString(this.folds);
				row[2] = classifierName;
				row[3] = "AVERAGE";
				row[4] = Double.toString(successRatio.get(j) / this.folds);
				row[5] = Double.toString(wrongRatio.get(j) / this.folds);
				row[6] = "Agent" + j;
				row[7] = Integer.toString(i);
				writer.writeRecord(row);

				logger.info("Validation for Agent" + j + " dataset (for " + i
						+ " agents configuration) with " + classifierName + " done for dataset: "
						+ this.dataset);
			}

			writer.flush();
		}

		logger.info("<-- Validation for classfier " + classifierName + " done for dataset: "
				+ this.dataset);
	}

	/**
	 * @param cls
	 * @param trainingData
	 * @param testData
	 * @return [0] = pctCorrect, [1] = pctIncorrect
	 * @throws Exception
	 */
	public double[] getValidation(Classifier cls, Instances trainingData, Instances testData)
			throws Exception {

		cls.buildClassifier(trainingData);

		Evaluation eval = new Evaluation(trainingData);
		eval.evaluateModel(cls, testData);

		double[] results = new double[2];
		results[0] = eval.pctCorrect() / 100;
		results[1] = eval.pctIncorrect() / 100;
		return results;
	}

	/**
	 * @return a list of all WEKA classifiers
	 */
	private List<Classifier> getNewClassifiers() {
		Classifier classifier;
		List<Classifier> classifiers = new ArrayList<Classifier>();

		// LADTree
		classifier = new LADTree();
		classifiers.add(classifier);

		// REPTree
		classifier = new REPTree();
		classifiers.add(classifier);

		// NBTree
		classifier = new NBTree();
		classifiers.add(classifier);

		// SimpleLogistic
		classifier = new SimpleLogistic();
		classifiers.add(classifier);

		// Logistic
		classifier = new Logistic();
		classifiers.add(classifier);

		// MultiLayerPerceptron
		classifier = new MultilayerPerceptron();
		classifiers.add(classifier);

		// DecisionStump
		classifier = new DecisionStump();
		classifiers.add(classifier);

		// PART
		classifier = new PART();
		classifiers.add(classifier);

		// RandomForest
		classifier = new RandomForest();
		classifiers.add(classifier);

		// LMT
		classifier = new LMT();
		classifiers.add(classifier);

		// SimpleCart
		classifier = new SimpleCart();
		classifiers.add(classifier);

		// BFTree
		classifier = new BFTree();
		classifiers.add(classifier);

		// RBFNetwork
		classifier = new RBFNetwork();
		classifiers.add(classifier);

		// J48
		classifier = new J48();
		((J48) classifier).setUnpruned(true);
		classifiers.add(classifier);

		// J48Graft
		classifier = new J48graft();
		((J48graft) classifier).setUnpruned(true);
		classifiers.add(classifier);

		// DTNB
		classifier = new DTNB();
		classifiers.add(classifier);

		// Jrip
		classifier = new JRip();
		classifiers.add(classifier);

		// Conjunction Rule
		classifier = new ConjunctiveRule();
		classifiers.add(classifier);

		// ZeroR
		classifier = new ZeroR();
		classifiers.add(classifier);

		// OneR
		classifier = new OneR();
		classifiers.add(classifier);

		// SMO
		classifier = new SMO();
		classifiers.add(classifier);

		return classifiers;

	}

	/**
	 * @param csvFilePath
	 * @return
	 * @throws Exception
	 */
	public Instances getDataFromCSV(String csvFilePath) throws Exception {
		DataSource source = new DataSource(csvFilePath);
		Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes() - 1);
		return data;
	}

}
