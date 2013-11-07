/**
 * es.upm.dit.gsi.barmas.agent.capability.argumentation.Score.java
 */
package es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes;

import java.util.HashMap;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.agent.capability.argumentation.Score.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 06/11/2013
 * @version 0.1
 * 
 */
public class FScoreStore {

	private HashMap<String, HashMap<String, Integer>> states;
	private HashMap<String, int[][]> matrices; // matrix[i] realValures
												// matrix[X][j] classifiedValue

	public FScoreStore() {
		this.states = new HashMap<String, HashMap<String, Integer>>();
		this.matrices = new HashMap<String, int[][]>();
	}

	public void addNode(String node) {
		this.states.put(node, new HashMap<String, Integer>());
	}

	public void addState(String node, String state, int pos) {
		this.states.get(node).put(state, pos);
	}

	public void addMatrix(String node, int[][] matrix) {
		// // **********************
		// // THIS BLOCK IS ONLY FOR TESTING
		// Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
		// "Confusion matrix for node: " + node);
		// for (Entry<String, Integer> state : states.get(node).entrySet()) {
		// Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
		// "State: " + state.getKey() + " Pos: " + state.getValue());
		// }
		// for (int i = 0; i < matrix.length; i++) {
		// String output = "";
		// for (int j = 0; j < matrix[i].length; j++) {
		// output = output + " - " + matrix[i][j];
		// }
		// Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
		// "Row for real cases: " + i + ": -> " + output);
		// }
		// // **********************
		this.matrices.put(node, matrix);
	}

	public double getFScore(String node, String state) {
		double precision = this.getPrecision(node, state);
		double recall = this.getRecall(node, state);
		if (precision == 0 && recall == 0) {
			return 0;
		}
		double fscore = 2 * (precision * recall) / (precision + recall);
		return fscore;
	}

	public double getPrecision(String node, String state) {
		double tp = this.getTruePositive(node, state);
		double fp = this.getFalsePositive(node, state);
		if (tp == 0 && fp == 0) {
			return 0;
		}
		return tp / (tp + fp);
	}

	public double getRecall(String node, String state) {
		double tp = this.getTruePositive(node, state);
		double fn = this.getFalseNegative(node, state);
		if (tp == 0 && fn == 0) {
			return 0;
		}
		return tp / (tp + fn);
	}

	public double getSpecifity(String node, String state) {
		double tn = this.getTrueNegative(node, state);
		double fp = this.getFalsePositive(node, state);
		if (tn == 0 && fp == 0) {
			return 0;
		}
		return tn / (tn + fp);
	}

	public double getAccuracy(String node, String state) {
		double tp = this.getTruePositive(node, state);
		double tn = this.getTrueNegative(node, state);
		double fp = this.getFalsePositive(node, state);
		double fn = this.getFalseNegative(node, state);
		if (tp == 0 && fp == 0 && fn == 0 && tn == 0) {
			return 0;
		}
		return (tp + tn) / (tp + tn + fp + fn);
	}

	public double getTruePositive(String node, String state) {
		int pos = this.states.get(node).get(state);
		return this.matrices.get(node)[pos][pos];
	}

	public double getFalsePositive(String node, String state) {
		int total = 0;
		int[][] matrix = this.matrices.get(node);
		int pos = this.states.get(node).get(state);
		for (int i = 0; i < matrix.length; i++) {
			if (i != pos) {
				total = total + matrix[i][pos];
			}
		}
		return total;
	}

	public double getFalseNegative(String node, String state) {
		int total = 0;
		int[][] matrix = this.matrices.get(node);
		int pos = this.states.get(node).get(state);
		for (int j = 0; j < matrix.length; j++) {
			if (j != pos) {
				total = total + matrix[pos][j];
			}
		}
		return total;
	}

	public double getTrueNegative(String node, String state) {
		int total = 0;
		int[][] matrix = this.matrices.get(node);
		int pos = this.states.get(node).get(state);
		for (int i = 0; i < matrix.length; i++) {
			if (i != pos) {
				for (int j = 0; j < matrix[i].length; j++) {
					if (j != pos) {
						total = total + matrix[i][j];
					}
				}
			}
		}
		return total;
	}

	public void updateConfusionMatrixWithNewCase(String node, String trueValue,
			String predictedValue) {
		int[][] matrix = this.matrices.get(node);
		int i = this.states.get(node).get(trueValue);
		int j = this.states.get(node).get(predictedValue);
		matrix[i][j] = matrix[i][j] + 1;
	}
}
