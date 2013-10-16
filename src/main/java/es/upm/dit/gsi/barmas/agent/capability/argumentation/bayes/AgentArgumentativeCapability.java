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
 * es.upm.dit.gsi.barmas.agent.capability.argumentation.AgentArgumentativeCapability.java
 */
package es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import unbbayes.prs.bn.ProbabilisticNetwork;
import unbbayes.prs.bn.ProbabilisticNode;
import es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.BayesianReasonerShanksAgent;
import es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.ShanksAgentBayesianReasoningCapability;
import es.upm.dit.gsi.shanks.exception.ShanksException;

/**
 * Project: barmas File: es.upm.dit.gsi.barmas.agent.capability.argumentation.
 * AgentArgumentativeCapability.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 23/07/2013
 * @version 0.1
 * 
 */
public class AgentArgumentativeCapability {

	/**
	 * 
	 * @param proponent
	 * @param node
	 * @param value
	 * @param conf
	 * @param evidences
	 * @return an argument with the provided info.
	 */
	public static Argument createArgument(ArgumentativeAgent proponent,
			String node, String value, double conf,
			HashMap<String, String> evidences, long step, long timestamp) {

		Argument arg = new Argument(proponent, step, timestamp);
		for (Entry<String, String> entry : evidences.entrySet()) {
			Given given = new Given(entry.getKey(), entry.getValue());
			arg.addGiven(given);
		}
		Proposal proposal = new Proposal(node);
		proposal.addValueWithConfidence(value, conf);
		arg.addProposal(proposal);

		return arg;
	}

	/**
	 * 
	 * @param proponent
	 * @param bn
	 * @return all arguments for a given Bayesian network
	 * @throws ShanksException
	 */
	public static Set<Argument> createArguments(ArgumentativeAgent proponent,
			ProbabilisticNetwork bn, long step, long timestamp) throws ShanksException {
		Set<Argument> args = new HashSet<Argument>();
		HashMap<String, String> evidences = (HashMap<String, String>) ShanksAgentBayesianReasoningCapability
				.getEvidences(bn);
		HashMap<String, HashMap<String, Float>> hypotheses = ShanksAgentBayesianReasoningCapability
				.getAllHypotheses(bn);
		for (Entry<String, HashMap<String, Float>> hyp : hypotheses.entrySet()) {
			ProbabilisticNode node = (ProbabilisticNode) bn.getNode(hyp
					.getKey());
			if (!node.hasEvidence()) {
				HashMap<String, Float> states = hyp.getValue();
				for (Entry<String, Float> state : states.entrySet()) {
					Argument arg = AgentArgumentativeCapability.createArgument(
							proponent, hyp.getKey(), state.getKey(),
							state.getValue(), evidences, step, timestamp);
					args.add(arg);
				}
			}
		}
		return args;
	}

	/**
	 * 
	 * @param agent
	 * @return all arguments for a given agent
	 * @throws ShanksException
	 */
	public static Set<Argument> createArguments(
			BayesianReasonerShanksAgent agent, long step, long timestamp) throws ShanksException {
		return AgentArgumentativeCapability.createArguments(
				(ArgumentativeAgent) agent, agent.getBayesianNetwork(), step, timestamp);
	}

	/**
	 * Update the BN to update all beliefs included in the given arguments
	 * 
	 * @param args
	 * @param bn
	 * @throws ShanksException
	 */
	public static void updateBeliefs(Set<Argument> args, ProbabilisticNetwork bn)
			throws ShanksException {
		HashMap<String, HashMap<String, Double>> beliefs = new HashMap<String, HashMap<String, Double>>();
		for (Argument arg : args) {
			for (Proposal p : arg.getProposals()) {
				String node = p.getNode();
				if (!beliefs.containsKey(node)) {
					beliefs.put(node, new HashMap<String, Double>());
				}
				Map<String, Double> values = p.getValuesWithConfidence();
				for (Entry<String, Double> value : values.entrySet()) {
					if (!beliefs.get(node).containsKey(value.getKey())) {
						beliefs.get(node).put(value.getKey(), value.getValue());
					} else {
						throw new ShanksException(
								"Duplicated belief in the argument: " + node
										+ "-" + value.getKey());
					}
				}
			}
		}
		ShanksAgentBayesianReasoningCapability.addSoftEvidences(bn, beliefs);
	}

	/**
	 * Update the agent BN to update all beliefs included in the given arguments
	 * 
	 * @param args
	 * @param agent
	 * @throws ShanksException
	 */
	public static void updateBeliefs(Set<Argument> args,
			BayesianReasonerShanksAgent agent) throws ShanksException {
		ProbabilisticNetwork bn = agent.getBayesianNetwork();
		AgentArgumentativeCapability.updateBeliefs(args, bn);
	}

	/**
	 * Send the arguments to the arguementation manager
	 * 
	 * 
	 * @param proponent
	 * @param args
	 */
	public static void sendArguments(ArgumentativeAgent proponent,
			Set<Argument> args) {
		for (Argument arg : args) {
			proponent.sendArgument(arg);
		}
	}

	/**
	 * Send one argument to the argumentation manager
	 * 
	 * @param proponent
	 * @param arg
	 */
	public static void sendArgument(ArgumentativeAgent proponent, Argument arg) {
		proponent.sendArgument(arg);
	}


	/**
	 * Return the type of the attack (from a to b) using the following rules:
	 * 
	 * 0 - a does not attack b
	 * 
	 * 1 - a is defeater of b if Claim(A) implies not all Support(B)
	 * 
	 * 2 - a is a direct defeater of b if there is phi in Support(B) such that
	 * Claim(A) implies not phi
	 * 
	 * 3 - a is a undercut of b if there is Phi subset of Support(B) such that
	 * Claim(A) is exactly not all Phi
	 * 
	 * 4 - a is a direct undercut of b if there is phi in Support(B) such that
	 * Claim(A) is exactly not phi
	 * 
	 * 5 - a is a canonical undercut of b if Claim(A) is exactly not Support(B)
	 * 
	 * 6 - a is a rebuttal of b if Claim(A) is exactly not Claim(B)
	 * 
	 * 7 - a is a defeating rebuttal of b if Claim(A) implies not Claim(B)
	 * 
	 * @param a
	 * @param b
	 * @return 0-7 attack type
	 */
	public static int getAttackType(Argument a, Argument b) {

		// In the code (Claim = Givens + Proposal) and (Support = Givens +
		// Assumptions)
		// Check if argument a attacks b:

		if (!b.equals(a)) {
			Set<Given> agivens = a.getGivens();
			Set<Given> bgivens = b.getGivens();
			// Set<Assumption> aassumptions = a.getAssumptions();
			// Set<Assumption> bassumptions = b.getAssumptions();
			Set<Proposal> aproposals = a.getProposals();
			Set<Proposal> bproposals = b.getProposals();

			// Type -1 if evidences are not coherent
			for (Given bgiven : bgivens) {
				for (Given agiven : agivens) {
					if (agiven.getNode().equals(bgiven.getNode())) {
						if (!agiven.getValue().equals(bgiven.getValue())) {
							Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
									"Incoherent evidence in arguments");
							return -1; // This has no sense!!
						}
					}
				}
			}

			// Check type 1 - a is defeater of b if Claim(A) implies not all
			// Support(B)
			if (agivens.size() > bgivens.size()) {
				return 1;
			}

			// Check type 2 - a is a direct defeater of b if there is phi in
			// Support(B) such that Claim(A) implies not phi

			// Check type 3 - a is a undercut of b if there is Phi subset of
			// Support(B) such that Claim(A) is exactly not all Phi

			// Check type 4 - a is a direct undercut of b if there is phi in
			// Support(B) such that Claim(A) is exactly not phi

			// Check type 5 - a is a canonical undercut of b if Claim(A) is
			// exactly
			// not Support(B)

			// Check types 6 and 7
			int aux = 0;
			for (Proposal bp : bproposals) {
				for (Proposal ap : aproposals) {
					String anode = bp.getNode();
					String node = ap.getNode();
					// If the proposed node are equals...
					if (node.equals(anode)) {
						String astate = ap.getMaxState();
						String bstate = bp.getMaxState();
						// if they don't aggree with the state
						if (!astate.equals(bstate)) {
							aux++;
						}
					}
				}
			}
			if (aux == bproposals.size()) {
				// Check type 6 - a is a rebuttal of b if Claim(A) is exactly
				// not
				// Claim(B)
				return 6;
			} else if (aux > 0) {
				// Check type 7 - a is a defeating rebuttal of b if Claim(A)
				// implies not
				// Claim(B)
				return 7;
			}

		}

		// If not... a does not attack b (Type 0)
		return 0;
	}
	
	/**
	 * Update the graph of the argumentation
	 * 
	 * @param argument
	 * @param argumentation
	 */
	public static void updateAtacksGraph(Argument argument, Argumentation argumentation) {
		HashMap<Argument, HashMap<Argument, Integer>> graph = argumentation.getGraph();
		for (Argument arg : argumentation.getArguments()) {
			int attackType = AgentArgumentativeCapability.getAttackType(argument, arg);
			graph.get(argument).put(arg, attackType);
			attackType = AgentArgumentativeCapability.getAttackType(arg, argument);
			graph.get(arg).put(argument, attackType);
		}
	}
	


	/**
	 * Resolution conflicts method
	 * 
	 * @param argumentation
	 */
	public static void addConclusionHigherHypothesis(Argumentation argumentation) {

		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.fine("Getting the higher hypothesis...");
		HashMap<Argument, HashMap<Argument, Integer>> graph = argumentation
				.getGraph();
		logger.finest("Evaluating possible conclusions...");
		List<Argument> possibleConclusions = new ArrayList<Argument>();
		possibleConclusions.addAll(argumentation.getArguments());
		for (Argument arg : argumentation.getArguments()) {
			HashMap<Argument, Integer> attacks = graph.get(arg);
			for (Argument attacked : attacks.keySet()) {
				int attackType = attacks.get(attacked);
				if (attackType == 1) {
					possibleConclusions.remove(attacked);
					logger.finest("Argument " + attacked.getId() + " (Proponent: " + attacked.getProponent().getProponentName() +") removed because it is defeated by Argument " + arg.getId() + " (Proponent: " + attacked.getProponent().getProponentName() +")");
					
				}
			}
		}
		
		int maxEvidences = 0;
		for (Argument arg : argumentation.getArguments()) {
			int evCardinal = arg.getGivens().size();
			if (evCardinal > maxEvidences) {
				maxEvidences = evCardinal;
			}
		}
		// Pick possible arguments
		String hyp = "";
		double max = 0;
		Argument argumentConclusion = null;
		for (Argument arg : possibleConclusions) {
			if (arg.getGivens().size() == maxEvidences) {
				for (Proposal p : arg.getProposals()) {
					if (p.getMaxValue() > max) {
						max = p.getMaxValue();
						hyp = p.getMaxState();
						argumentConclusion = arg;
					}
				}
			}
		}

		logger.fine("Argumentation Manager --> Higher hypothesis found: " + hyp
				+ " - " + max + " from "
				+ argumentConclusion.getProponent().getProponentName()
				+ " - ArgumentID: " + argumentConclusion.getId());

		argumentation.getConclusions().add(argumentConclusion);
	}
}
