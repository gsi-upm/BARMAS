/*******************************************************************************
 * Copyright (c) 2013 alvarocarrera Grupo de Sistemas Inteligentes - Universidad Politécnica de Madrid. (GSI-UPM)
 * http://www.gsi.dit.upm.es/
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * 
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 * 
 * Contributors:
 *     alvarocarrera - initial API and implementation
 ******************************************************************************/
/**
 * es.upm.dit.gsi.barmas.agent.capability.learning.bayes.AgentBayesLearningCapability.java
 */
package es.upm.dit.gsi.barmas.agent.capability.learning.bayes;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import smile.Network;
import smile.learning.BayesianSearch;
import smile.learning.DataMatch;
import smile.learning.DataSet;
import smile.learning.Validator;

/**
 * Project: barmas File: es.upm.dit.gsi.barmas.agent.capability.learning.bayes.
 * AgentBayesLearningCapability.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@upm.es
 * @twitter @alvarocarrera
 * @date 29/10/2013
 * @version 0.1
 * 
 */
public class AgentBayesLearningCapability {

	/**
	 * @param agent
	 * @param iterations
	 * @param classificationTarget
	 */
	public static void learnBNWithBayesianSearch(BayesLearningAgent agent, int iterations,
			String classificationTarget) {

		String datasetFile = agent.getDatasetFile();
		DataSet dataset = new DataSet();
		dataset.readFile(datasetFile);

		HashMap<Network, Double> results = new HashMap<Network, Double>();
		for (int i = 0; i < iterations; i++) {
			Network bn = AgentBayesLearningCapability.learnBN(dataset);
			double validation = AgentBayesLearningCapability.validateBNWithMCC(bn, dataset, agent);
			results.put(bn, validation);
		}
		double max = 0;
		Network betterBN = null;
		for (Entry<Network, Double> entry : results.entrySet()) {
			if (entry.getValue() > max) {
				max = entry.getValue();
				betterBN = entry.getKey();
			}
		}

		if (betterBN == null) {
			agent.getLogger()
					.info("All learnt BNs has MCC equals to zero for all states... They are almost constant results for all variables.");

			agent.getLogger().info("Trying with accuracy as validation metric");
			for (Entry<Network, Double> result : results.entrySet()) {
				Network bn = result.getKey();
				double validation = AgentBayesLearningCapability.validateBN(bn, dataset,
						classificationTarget, agent);
				results.put(bn, validation);
			}
			max = -1;
			for (Entry<Network, Double> entry : results.entrySet()) {
				if (entry.getValue() > max || betterBN == null) {
					max = entry.getValue();
					betterBN = entry.getKey();
				}
			}

			AgentBayesLearningCapability.writeBNFile(betterBN, agent);
			agent.getLogger().info(
					"BN learnt in: " + agent.getBNOutputFile() + " from: " + datasetFile
							+ " with accuracy " + max + " for node " + classificationTarget
							+ " after " + iterations + " iterations.");
		} else {
			AgentBayesLearningCapability.writeBNFile(betterBN, agent);
			agent.getLogger().info(
					"BN learnt in: " + agent.getBNOutputFile() + " from: " + datasetFile
							+ " with average MCC " + max + " for node " + classificationTarget
							+ " after " + iterations + " iterations.");
		}
	}

	/**
	 * @param bn
	 * @param dataset
	 * @param agent
	 * @return
	 */
	private static double validateBNWithMCC(Network bn, DataSet dataset, BayesLearningAgent agent) {

		String datasetFile = agent.getDatasetFile();
		dataset.readFile(datasetFile);

		DataMatch[] matching = dataset.matchNetwork(bn);
		Validator validator = new Validator(bn, dataset, matching);
		for (String node : bn.getAllNodeIds()) {
			validator.addClassNode(node);
		}
		validator.test();

		ValidationMetricsStore scores = new ValidationMetricsStore();
		double avgMCC = 0;
		double statesCounter = 0;
		for (String node : bn.getAllNodeIds()) {
			scores.addNode(node);
			int statesCount = bn.getOutcomeCount(node);
			for (int i = 0; i < statesCount; i++) {
				scores.addState(node, bn.getOutcomeId(bn.getNode(node), i), i);
			}
			int[][] confusionMatrix = validator.getConfusionMatrix(node);
			scores.addMatrix(node, confusionMatrix);
		}
		for (String node : bn.getAllNodeIds()) {
			int statesCount = bn.getOutcomeCount(node);
			statesCounter = statesCounter + statesCount;
			for (int i = 0; i < statesCount; i++) {
				double mcc = scores.getMCC(node, scores.getState(node, i));
				avgMCC = (avgMCC + mcc);
			}
		}
		avgMCC = avgMCC / statesCounter;
		return avgMCC;

	}

	/**
	 * @param bn
	 * @param dataset
	 * @param classificationTarget
	 * @return
	 */
	private static double validateBN(Network bn, DataSet dataset, String classificationTarget,
			BayesLearningAgent agent) {
		String datasetFile = agent.getDatasetFile();
		dataset.readFile(datasetFile);

		DataMatch[] matching = dataset.matchNetwork(bn);
		Validator validator = new Validator(bn, dataset, matching);
		for (String node : bn.getAllNodeIds()) {
			validator.addClassNode(node);
		}
		validator.test();

		ValidationMetricsStore scores = new ValidationMetricsStore();
		double avgAccuracy = 0;
		for (String node : bn.getAllNodeIds()) {
			scores.addNode(node);
			int statesCount = bn.getOutcomeCount(node);
			for (int i = 0; i < statesCount; i++) {
				scores.addState(node, bn.getOutcomeId(bn.getNode(node), i), i);
			}
			int[][] confusionMatrix = validator.getConfusionMatrix(node);
			scores.addMatrix(node, confusionMatrix);

			for (int i = 0; i < statesCount; i++) {
				avgAccuracy = (avgAccuracy + scores.getAccuracy(node, scores.getState(node, i)))
						/ (i + 1);
			}
		}
		return avgAccuracy;

	}

	/**
	 * @param bn
	 * @param agent
	 */
	private static void writeBNFile(Network bn, BayesLearningAgent agent) {
		// Check if folder parent exists
		File f = new File(agent.getBNOutputFile());
		File parent = f.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}

		// Write BN file
		bn.writeFile(agent.getBNOutputFile());
		File checkFile = new File(agent.getBNOutputFile());
		while (!checkFile.exists()) {
			// Wait a bit...
		}
	}

	/**
	 * @param dataset
	 * @return
	 */
	private static Network learnBN(DataSet dataset) {

		// Learning algorithm configuration
		BayesianSearch bs = new BayesianSearch();
		bs.setRandSeed(0);
		bs.setIterationCount(20);
		bs.setLinkProbability(0.1);
		bs.setMaxParents(8);
		bs.setPriorSampleSize(50);
		bs.setPriorLinkProbability(0.01);
		bs.setMaxSearchTime(0);

		// Algorithm execution
		Network net = bs.learn(dataset);
		return net;
	}

}
