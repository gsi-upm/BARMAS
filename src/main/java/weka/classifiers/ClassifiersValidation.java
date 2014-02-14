/**
 * weka.classifiers.ClassifierJ48.java
 */
package weka.classifiers;

import java.io.FileNotFoundException;
import java.io.IOException;

import smile.learning.NaiveBayes;

import weka.classifiers.functions.RBFNetwork;
import weka.classifiers.rules.DTNB;
import weka.classifiers.rules.JRip;
import weka.classifiers.trees.BFTree;
import weka.classifiers.trees.J48graft;
import weka.classifiers.trees.LMT;
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
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 14/02/2014
 * @version 0.1
 * 
 */
public class ClassifiersValidation {

	/**
	 * Constructor
	 * 
	 */
	public ClassifiersValidation() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			DataSource source = new DataSource(
					"../experiments/solarflare-simulation/input/0.1testRatio/iteration-2/bayes-central-dataset.csv");
			Instances data = source.getDataSet();
			data.setClassIndex(data.numAttributes() - 1);

			source = new DataSource(
					"../experiments/solarflare-simulation/input/0.1testRatio/iteration-2/test-dataset.csv");
			Instances test = source.getDataSet();
			test.setClassIndex(test.numAttributes() - 1);

			Classifier cls;

			// LADTree
			// cls = new LADTree();

			// REPTree
			// cls = new REPTree();

			// NBTree
			// cls = new NBTree();

			// SimpleLogistic
			// cls = new SimpleLogistic();

			// Logistic
			// cls = new Logistic();

			// MultiLayerPerceptron
			// cls = new MultilayerPerceptron();

			// DecisionStump
			// cls = new DecisionStump();

			// PART
			// cls = new PART();

			// RandomForest
			// cls = new RandomForest();

			// LMT
			// cls = new LMT();

			// SimpleCart
			// cls = new SimpleCart();

			// BFTree
			// cls = new BFTree();

			// RBFNetwork
			cls = new RBFNetwork();

			// J48
			// cls = new J48();
			// ((J48) cls).setUnpruned(true);

			// J48Graft
			// cls = new J48graft();
			// ((J48graft) cls).setUnpruned(true);

			// DTNB
			// cls = new DTNB();

			// Jrip
			// cls = new JRip();

			// Conjunction Rule
			// cls = new ConjunctiveRule();

			// ZeroR
			// cls = new ZeroR();

			// OneR
			// cls = new OneR();

			// SMO
			// cls = new SMO();

			cls.buildClassifier(data);

			Evaluation eval = new Evaluation(data);
			eval.evaluateModel(cls, test);
			System.out.println("Correct %: " + eval.pctCorrect());
			System.out.println("Incorrect %: " + eval.pctIncorrect());
			System.out.println("TOTAL %: " + (eval.pctIncorrect() + eval.pctCorrect()));

			// System.out.println(eval.toSummaryString("\nResults\n======\n",
			// false));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
