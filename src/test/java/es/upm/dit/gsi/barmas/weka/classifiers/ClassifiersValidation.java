/*******************************************************************************
 * Copyright  (C) 2014 Álvaro Carrera Barroso
 * Grupo de Sistemas Inteligentes - Universidad Politecnica de Madrid
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
/**
 * weka.classifiers.ClassifierJ48.java
 */
package es.upm.dit.gsi.barmas.weka.classifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
 * Project: barmas File: weka.classifiers.ClassifierJ48.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@upm.es
 * @twitter @alvarocarrera
 * @date 14/02/2014
 * @version 0.1
 * 
 */
public class ClassifiersValidation {

	private static Logger logger = Logger.getLogger(ClassifiersValidation.class.getSimpleName());

	/**
	 * Constructor
	 * 
	 */
	public ClassifiersValidation() {
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
	 * @param cls
	 * @param data
	 * @param folds
	 * @return [0] = pctCorrect, [1] = pctIncorrect
	 * @throws Exception
	 */
	public double[] getCrossValidation(Classifier cls, Instances data, int folds) throws Exception {

		cls.buildClassifier(data);

		Classifier copy = Classifier.makeCopy(cls);
		double[] results = new double[2];
		for (int n = 0; n < folds; n++) {
			Instances train = data.trainCV(folds, n);
			Instances test = data.testCV(folds, n);

			// CSVSaver saver = new CSVSaver();
			// saver.setInstances(train);
			// saver.setFile(new File("../data.csv"));
			// saver.writeBatch();

			cls.buildClassifier(train);
			Evaluation eval = new Evaluation(data);
			eval.evaluateModel(cls, test);
			results[0] = results[0] + (eval.pctCorrect() / 100);
			results[1] = results[1] + (eval.pctIncorrect() / 100);
		}

		cls = copy;
		results[0] = results[0] / folds;
		results[1] = results[1] / folds;
		return results;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ClassifiersValidation cv = new ClassifiersValidation();

		int iterations = 10;

		Classifier cls;
		List<Classifier> clss = new ArrayList<Classifier>();

		// LADTree
		cls = new LADTree();
		clss.add(cls);

		// REPTree
		cls = new REPTree();
		clss.add(cls);

		// NBTree
		cls = new NBTree();
		clss.add(cls);

		// SimpleLogistic
		cls = new SimpleLogistic();
		clss.add(cls);

		// Logistic
		cls = new Logistic();
		clss.add(cls);

		// MultiLayerPerceptron
		cls = new MultilayerPerceptron();
		clss.add(cls);

		// DecisionStump
		cls = new DecisionStump();
		clss.add(cls);

		// PART
		cls = new PART();
		clss.add(cls);

		// RandomForest
		cls = new RandomForest();
		clss.add(cls);

		// LMT
		cls = new LMT();
		clss.add(cls);

		// SimpleCart
		cls = new SimpleCart();
		clss.add(cls);

		// BFTree
		cls = new BFTree();
		clss.add(cls);

		// RBFNetwork
		cls = new RBFNetwork();
		clss.add(cls);

		// J48
		cls = new J48();
		((J48) cls).setUnpruned(true);
		clss.add(cls);

		// J48Graft
		cls = new J48graft();
		((J48graft) cls).setUnpruned(true);
		clss.add(cls);

		// DTNB
		cls = new DTNB();
		clss.add(cls);

		// Jrip
		cls = new JRip();
		clss.add(cls);

		// Conjunction Rule
		cls = new ConjunctiveRule();
		clss.add(cls);

		// ZeroR
		cls = new ZeroR();
		clss.add(cls);

		// OneR
		cls = new OneR();
		clss.add(cls);

		// SMO
		cls = new SMO();
		clss.add(cls);

		for (int j = 0; j < clss.size(); j++) {
			logger.info("Starting cross-validation evaluation for "
					+ clss.get(j).getClass().getSimpleName());

			try {

				Instances trainingData = cv
						.getDataFromCSV("src/main/resources/dataset/solarflare.csv");
				Classifier classifier = clss.get(j);
				double[] results = cv.getCrossValidation(classifier, trainingData, 10);
				logger.finer("-> " + classifier.getClass().getSimpleName());
				logger.info("Success Rate / Error Rate -> "
						+ clss.get(j).getClass().getSimpleName() + " -> " + results[0] + " / "
						+ results[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		double[] errors = new double[clss.size()];
		double[] success = new double[clss.size()];

		for (int j = 0; j < clss.size(); j++) {
			logger.info("Starting evaluation for " + clss.get(j).getClass().getSimpleName());
			for (int i = 0; i < iterations; i++) {
				String folder = "../experiments/solarflare-simulation/input/0.1testRatio/iteration-"
						+ i + "/";
				try {
					Instances trainingData = cv
							.getDataFromCSV(folder + "bayes-central-dataset.csv");
					Instances testData = cv.getDataFromCSV(folder + "test-dataset.csv");

					testData.deleteAttributeAt(0);
					testData.deleteAttributeAt(1);
					testData.deleteAttributeAt(2);

					Classifier classifier = clss.get(j);
					double[] results = cv.getValidation(classifier, trainingData, testData);
					logger.finer("-> " + classifier.getClass().getSimpleName());
					double errorRates = errors[j];
					errors[j] = errorRates + results[1];
					double successRates = success[j];
					success[j] = successRates + results[0];
					logger.finer("ER for iteration " + i + ": " + results[1]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			logger.info("Success Rate / Error Rate -> " + clss.get(j).getClass().getSimpleName()
					+ " -> " + success[j] / iterations + " / " + errors[j] / iterations);
		}
	}
}
