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
package es.upm.dit.gsi.barmas.agent.capability.argumentation;

import jason.asSemantics.Message;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import unbbayes.prs.bn.ProbabilisticNetwork;
import unbbayes.prs.bn.ProbabilisticNode;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argument;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Given;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Proposal;
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
	 * @param node
	 * @param value
	 * @param conf
	 * @param evidences
	 * @return an argument with the provided info.
	 */
	public static Argument createArgument(String node, String value,
			double conf, HashMap<String, String> evidences) {

		Argument arg = new Argument();
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
	 * @param bn
	 * @return all arguments for a given Bayesian network
	 * @throws ShanksException
	 */
	public static Set<Argument> createArguments(ProbabilisticNetwork bn)
			throws ShanksException {
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
							hyp.getKey(), state.getKey(), state.getValue(),
							evidences);
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
			BayesianReasonerShanksAgent agent) throws ShanksException {
		return AgentArgumentativeCapability.createArguments(agent
				.getBayesianNetwork());
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
	 * @param manager
	 * @param args
	 */
	public static void sendArguments(ArgumentativeAgent proponent,
			Set<Argument> args) {
		Message m = new Message();
		m.setSender(proponent.getProponentName());
		m.setReceiver(proponent.getArgumentationManagerName());
		m.setPropCont(args);
		proponent.send(m);
	}

}
