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
import es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.ShanksAgentBayesianReasoningCapability;
import es.upm.dit.gsi.shanks.exception.ShanksException;

/**
 * Project: barmas File: es.upm.dit.gsi.barmas.agent.capability.learning.bayes.
 * AgentBayesLearningCapability.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
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
	public synchronized static void learnBNWithBayesianSearch(BayesLearningAgent agent,
			int iterations, String classificationTarget) {

		String datasetFile = agent.getDatasetFile();
		DataSet dataset = new DataSet();
		dataset.readFile(datasetFile);

		HashMap<Network, Double> results = new HashMap<Network, Double>();
		for (int i = 0; i < iterations; i++) {
			Network bn = AgentBayesLearningCapability.learnBN(dataset);
			double validation = AgentBayesLearningCapability.validateBN(bn,
					dataset, classificationTarget, agent);
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

		AgentBayesLearningCapability.writeBNFile(betterBN, agent);
		agent.getLogger()
				.info("BN learnt in: " + agent.getBNOutputFile() + " from: "
						+ datasetFile + " with accuracy " + max + " for node " + classificationTarget + " after " + iterations + " iterations.");
		AgentBayesLearningCapability.testBNInUnbbayes(betterBN, agent);
	}

	/**
	 * @param bn
	 * @param dataset
	 * @param classificationTarget
	 * @return
	 */
	private synchronized static double validateBN(Network bn, DataSet dataset,
			String classificationTarget, BayesLearningAgent agent) {
		DataMatch[] matching = dataset.matchNetwork(bn);
		Validator validator = new Validator(bn, dataset, matching);
		validator.addClassNode(classificationTarget);
		validator.test();
		String[] states = bn.getOutcomeIds(classificationTarget);
		int[][] confusionMatrix = validator
				.getConfusionMatrix(classificationTarget);
		double[] accuracies = new double[states.length];
		int[] totalCasesInDatasetPerState = new int[states.length];
		int[] successCasesInDatasetPerState = new int[states.length];
		double globalAccuracy = 0;
		for (int i = 0; i < states.length; i++) {
			accuracies[i] = validator.getAccuracy(classificationTarget,
					states[i]);
			int totalCasesForThisState = 0;
			int succesCasesForThisState = 0;
			for (int j = 0; j < states.length; j++) {
				totalCasesForThisState = totalCasesForThisState
						+ confusionMatrix[i][j];
				if (i == j) {
					succesCasesForThisState = confusionMatrix[i][j];
				}
			}
			totalCasesInDatasetPerState[i] = totalCasesForThisState;
			successCasesInDatasetPerState[i] = succesCasesForThisState;
		}

		int successTotal = 0;
		int totalCases = 0;
		for (int i = 0; i < states.length; i++) {
			totalCases = totalCases + totalCasesInDatasetPerState[i];
			successTotal = successTotal + successCasesInDatasetPerState[i];
		}
		globalAccuracy = (double) successTotal / (double) totalCases;
		agent.getLogger().finest(
				"BN accuracy for node: " + classificationTarget + " equals to "
						+ globalAccuracy);

		return globalAccuracy;

	}

	/**
	 * @param bn
	 * @param agent
	 */
	private synchronized static void testBNInUnbbayes(Network bn, BayesLearningAgent agent) {
		try {
			ShanksAgentBayesianReasoningCapability.loadNetwork(agent
					.getBNOutputFile());
			agent.getLogger().info(
					"BN learnt in: " + agent.getBNOutputFile() + " from: "
							+ agent.getDatasetFile()
							+ " is compatible with Unbbayes.");
		} catch (ShanksException e) {
			agent.getLogger().fine(
					"BN is disconnected. Looking for disconnected nodes.");
			// If there is an exception, the net is disconnected.
			// So, new connections (arcs) are created.
			int[] allNodes = bn.getAllNodes();
			for (int node : allNodes) {
				int[] parents = bn.getParents(node);
				int[] children = bn.getChildren(node);
				if (parents.length == 0 && children.length == 0) {
					agent.getLogger().fine(
							"Disconnected Node Found: " + bn.getNodeId(node));
					if (node == allNodes[0]) {
						bn.addArc(allNodes[1], node);
					} else {
						bn.addArc(allNodes[0], node);
					}
					agent.getLogger().fine(
							"Node " + bn.getNodeId(node)
									+ " is already connected in "
									+ agent.getBNOutputFile());
				}
			}
			bn.writeFile(agent.getBNOutputFile());
			File checkFile = new File(agent.getBNOutputFile());
			while (!checkFile.exists()) {
				// Wait a bit...
			}
			try {
				ShanksAgentBayesianReasoningCapability.loadNetwork(agent
						.getBNOutputFile());
			} catch (Exception e1) {
				if (e1.getMessage().contains("cicle")) {
					agent.getLogger().fine(
							"--> Cicle found. Trying again...");
				} else {
					agent.getLogger()
							.fine(
									"Learnt net is not compatible with Unbbayes. Trying again...");
				}
			}
		}
	}

	/**
	 * @param bn
	 * @param agent
	 */
	private synchronized static void writeBNFile(Network bn, BayesLearningAgent agent) {
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
	private synchronized static Network learnBN(DataSet dataset) {
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
		return bs.learn(dataset);
	}

}
