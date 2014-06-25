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
 * es.upm.dit.gsi.barmas.agent.capability.argumentation.Score.java
 */
package es.upm.dit.gsi.barmas.agent.capability.learning.bayes;

import java.util.HashMap;
import java.util.Map.Entry;

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
public class ValidationMetricsStore {

	private HashMap<String, HashMap<String, Integer>> states;
	private HashMap<String, int[][]> matrices; // matrix[i] realValures
												// matrix[X][j] classifiedValue

	private HashMap<String, Double> cache;

	public ValidationMetricsStore() {
		this.states = new HashMap<String, HashMap<String, Integer>>();
		this.matrices = new HashMap<String, int[][]>();
		this.cache = new HashMap<String, Double>();
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
		// **********************
		this.matrices.put(node, matrix);

	}

	public String getState(String node, int pos) {
		for (Entry<String, Integer> entry : states.get(node).entrySet()) {
			if (pos == entry.getValue()) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * @param node
	 * @param state
	 * @return Matthews Correlation Coefficient
	 */
	public double getMCC(String node, String state) {
		double tp = this.getTruePositive(node, state);
		double tn = this.getTrueNegative(node, state);
		double fp = this.getFalsePositive(node, state);
		double fn = this.getFalseNegative(node, state);

		// *********
		// Calculate MCC with classic formula
		double aux = (tp + fp) * (tp + fn) * (tn + fp) * (tn + fn);
		if (aux == 0) {
			return 0;
		}
		double mcc = ((tp * tn) - (fp * fn)) / Math.sqrt(aux);
		// *********

		// // *********
		// // Calculate MCC with other formula
		// double n = tn + tp + fn + fp;
		// double s = (tp + fn) / n;
		// double p = (tp + fp) / n;
		//
		// double aux = p * s * (1 - s) * (1 - p);
		// if (aux == 0) {
		// return 0;
		// }
		// double mcc = ((tp / n) - (s * p)) / Math.sqrt(aux);
		// // *********

		return mcc;
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

	public double getPositivePredictiveValue(String node, String state) {
		return this.getPrecision(node, state);
	}

	public double getRecall(String node, String state) {
		double tp = this.getTruePositive(node, state);
		double fn = this.getFalseNegative(node, state);
		if (tp == 0 && fn == 0) {
			return 0;
		}
		return tp / (tp + fn);
	}

	public double getSensitivity(String node, String state) {
		return this.getRecall(node, state);
	}

	public double getTruePositiveRate(String node, String state) {
		return this.getRecall(node, state);
	}

	public double getHitRate(String node, String state) {
		return this.getRecall(node, state);
	}

	public double getFalsePositiveRate(String node, String state) {
		double fp = this.getFalsePositive(node, state);
		double tn = this.getTrueNegative(node, state);
		if (tn == 0 && fp == 0) {
			return 0;
		}
		return fp / (fp + tn);
	}

	public double getFalseAlarmRate(String node, String state) {
		return this.getFalsePositiveRate(node, state);
	}

	public double getFallOut(String node, String state) {
		return this.getFalsePositiveRate(node, state);
	}

	public double getSpecifity(String node, String state) {
		double tn = this.getTrueNegative(node, state);
		double fp = this.getFalsePositive(node, state);
		if (tn == 0 && fp == 0) {
			return 0;
		}
		return tn / (tn + fp);
	}

	public double getTrueNegativeRate(String node, String state) {
		return this.getSpecifity(node, state);
	}

	public double getNegativePredictiveValue(String node, String state) {
		double tn = this.getTrueNegative(node, state);
		double fn = this.getFalseNegative(node, state);
		if (tn == 0 && fn == 0) {
			return 0;
		}
		return tn / (tn + fn);
	}

	public double getFalseDiscoveryRate(String node, String state) {
		double fp = this.getFalsePositive(node, state);
		double tp = this.getTruePositive(node, state);
		if (tp == 0 && fp == 0) {
			return 0;
		}
		return fp / (fp + tp);
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
		this.cache.clear();
	}

	/**
	 * @param node
	 * @param state
	 * @return
	 */
	public double getTrustScore(String node, String state) {
		String key = node + "-&-" + state;
		if (cache.containsKey(key)) {
			return cache.get(key);
		} else {
			double mcc = this.getMCC(node, state);
			this.cache.put(key, mcc);
			return mcc;
		}
	}
}
