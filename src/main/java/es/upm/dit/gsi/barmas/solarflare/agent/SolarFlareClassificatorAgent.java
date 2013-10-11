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
 * es.upm.dit.gsi.barmas.agent.BarmasAgent.java
 */
package es.upm.dit.gsi.barmas.solarflare.agent;

import jason.asSemantics.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import unbbayes.prs.bn.ProbabilisticNetwork;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.AgentArgumentativeCapability;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.ArgumentativeAgent;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argument;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Given;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Proposal;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.ArgumentationManagerAgent;
import es.upm.dit.gsi.barmas.solarflare.model.SolarFlare;
import es.upm.dit.gsi.barmas.solarflare.model.scenario.SolarFlareScenario;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.SolarFlareType;
import es.upm.dit.gsi.barmas.solarflare.simulation.SolarFlareClassificationSimulation;
import es.upm.dit.gsi.shanks.ShanksSimulation;
import es.upm.dit.gsi.shanks.agent.SimpleShanksAgent;
import es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.BayesianReasonerShanksAgent;
import es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.ShanksAgentBayesianReasoningCapability;
import es.upm.dit.gsi.shanks.exception.ShanksException;

/**
 * Project: barmas File: es.upm.dit.gsi.barmas.agent.BarmasAgent.java
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
public class SolarFlareClassificatorAgent extends SimpleShanksAgent implements
		BayesianReasonerShanksAgent, ArgumentativeAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8582918551821278046L;

	private ArgumentationManagerAgent manager;
	private String bnFilePath;
	private ProbabilisticNetwork bn;
	private List<String> sensors;

	private boolean argumenting;

	private HashMap<String, String> evidences;

	private ArrayList<Argument> pendingArguments;

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param manager
	 * @param bnPath
	 */
	public SolarFlareClassificatorAgent(String id,
			ArgumentationManagerAgent manager, String bnPath,
			List<String> sensors) {
		super(id);
		this.bnFilePath = bnPath;
		this.sensors = sensors;
		this.setArgumentationManager(manager);
		this.argumenting = false;
		this.pendingArguments = new ArrayList<Argument>();
		this.evidences = new HashMap<String, String>();
		try {
			ShanksAgentBayesianReasoningCapability.loadNetwork(this);
		} catch (ShanksException e) {
			e.printStackTrace();
		}

		// Register in manager
		this.getArgumentationManager().addSubscriber(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.shanks.agent.ShanksAgent#checkMail()
	 */
	public void checkMail() {
		// Check inbox
		List<Message> inbox = this.getInbox();
		if (inbox.size() > 0) {

			this.pendingArguments = new ArrayList<Argument>();
			for (Message msg : inbox) {
				Argument arg = (Argument) msg.getPropCont();
				this.pendingArguments.add(arg);
			}

			inbox.clear();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.shanks.agent.SimpleShanksAgent#executeReasoningCycle(es
	 * .upm.dit.gsi.shanks.ShanksSimulation)
	 */
	@Override
	public void executeReasoningCycle(ShanksSimulation simulation) {
		SolarFlareClassificationSimulation sim = (SolarFlareClassificationSimulation) simulation;
		// Check sensors
		this.getSensorsUpdateData(sim);
		
		// Decide what to do: start a new argumentation or process incoming arguments
		if (this.pendingArguments.size() > 0) {
			if (!argumenting) {
				this.argumenting = true;
			}
			// Process incoming messages
			for (Argument arg : this.pendingArguments) {
				simulation.getLogger().finer(
						"Agent: " + this.getID()
								+ " -> Received arguments from: "
								+ arg.getProponent().getProponentName());
				this.processNewArgument(arg, sim);
			}
			this.pendingArguments.clear();
		} else {
			SolarFlare orig = (SolarFlare) sim.getScenario().getNetworkElement(
					SolarFlareScenario.ORIGINALFLARE);

			if (!argumenting && orig.getStatus().get(SolarFlare.READY)
					&& this.sensors.size() > 0) {
				this.startClassification(sim);
			}
		}
	}

	private void processNewArgument(Argument arg,
			SolarFlareClassificationSimulation sim) {
		
		// Update current evidences with new givens received in the incoming argument
		Set<Given> givens = arg.getGivens();
		for (Given given : givens) {
			if (evidences.keySet().contains(given.getNode())) {
				if (!evidences.get(given.getNode()).equals(given.getValue())) {
					sim.logger
							.warning("No sense! Different evidences from different agents.");
					sim.logger.finest("Agent: " + this.getID() + " Evidence: "
							+ given.getNode() + " - "
							+ evidences.get(given.getValue()));
					sim.logger.finest("Agent: "
							+ arg.getProponent().getProponentName()
							+ " Evidence: " + given.getNode() + " - "
							+ given.getValue());
				}
			} else {
				this.evidences.put(given.getNode(), given.getValue());
				try {
					//********
					// TODO check if this is required
					// Maybe it could be done only once per argumentation, at finishing steps
					ShanksAgentBayesianReasoningCapability.clearEvidences(this
							.getBayesianNetwork());
					//*********
					
					for (Entry<String, String> entry : evidences.entrySet()) {
						sim.getLogger().finer(
								"Agent: " + this.getID() + " adding evidence: "
										+ entry.getKey() + " - "
										+ entry.getValue());
						ShanksAgentBayesianReasoningCapability.addEvidence(
								this, entry.getKey(), entry.getValue());
					}
				} catch (ShanksException e) {
					// sim.getLogger().warning(
					// "Agent: " + this.getID()
					// + " -> Unknown state for node: "
					// + entry.getKey() + " -> State: "
					// + entry.getValue());
					sim.getLogger().warning(
							"Given received from agent: "
									+ arg.getProponent().getProponentName());
				}
			}
		}

		// Get hypothesis
		HashMap<String, Float> hyps = null;
		try {
			hyps = ShanksAgentBayesianReasoningCapability
					.getNodeStatesHypotheses(this,
							SolarFlareType.class.getSimpleName());
		} catch (ShanksException e) {
			e.printStackTrace();
		}
		String hyp = "";
		float maxValue = 0;
		for (Entry<String, Float> entry : hyps.entrySet()) {
			if (entry.getValue() > maxValue) {
				maxValue = entry.getValue();
				hyp = entry.getKey();
			}
		}
		sim.logger.finer("Agent: " + this.getID() + " has hypothesis of: "
				+ SolarFlareType.class.getSimpleName() + " - " + hyp);

		Set<Proposal> proposals = arg.getProposals();
		String phyp = "";
		double pmaxValue = 0;
		for (Proposal p : proposals) {
			if (p.getNode().equals(SolarFlareType.class.getSimpleName())) {
				HashMap<String, Double> values = (HashMap<String, Double>) p
						.getValuesWithConfidence();
				for (Entry<String, Double> entry : values.entrySet()) {
					if (entry.getValue() > pmaxValue) {
						pmaxValue = entry.getValue();
						phyp = entry.getKey();
					}
				}
			}
		}

		if (!hyp.equals(phyp)) {
			sim.getLogger().finer(
					"Agent: " + this.getID() + " disagrees with "
							+ arg.getProponent().getProponentName());
			// TODO decide what to do here
			sim.getLogger().severe("Agent: " + this.getID() + " at critical point!!");

			// Create and send initial argument
			Argument counterArg = AgentArgumentativeCapability.createArgument(
					this, SolarFlareType.class.getSimpleName(), hyp, maxValue,
					evidences);
			AgentArgumentativeCapability.sendArgument(this, counterArg);

			sim.getLogger().finer(
					"Counter argument sent by agent: " + this.getID());
		} else {
			sim.getLogger().finer(
					"Agent: " + this.getID() + " agrees with "
							+ arg.getProponent().getProponentName());
		}
	}

	private void startClassification(SolarFlareClassificationSimulation sim) {

		try {
			for (Entry<String, String> entry : evidences.entrySet()) {
				try {
					sim.getLogger()
							.finer("Agent: " + this.getID()
									+ " adding evidence: " + entry.getKey()
									+ " - " + entry.getValue());
					ShanksAgentBayesianReasoningCapability.addEvidence(this,
							entry.getKey(), entry.getValue());
				} catch (Exception e) {
					sim.getLogger().warning(
							"Agent: " + this.getID()
									+ " -> Unknown state for node: "
									+ entry.getKey() + " -> State: "
									+ entry.getValue());
				}

			}
			// Get hypothesis
			HashMap<String, Float> hyps = ShanksAgentBayesianReasoningCapability
					.getNodeStatesHypotheses(this,
							SolarFlareType.class.getSimpleName());
			String hyp = "";
			float maxValue = 0;
			for (Entry<String, Float> entry : hyps.entrySet()) {
				if (entry.getValue() > maxValue) {
					maxValue = entry.getValue();
					hyp = entry.getKey();
				}
			}

			sim.logger.finer("Agent: " + this.getID() + " has hypothesis of: "
					+ SolarFlareType.class.getSimpleName() + " - " + hyp);

			// Create and send initial argument
			Argument arg = AgentArgumentativeCapability.createArgument(this,
					SolarFlareType.class.getSimpleName(), hyp, maxValue,
					evidences);
			AgentArgumentativeCapability.sendArgument(this, arg);

			this.argumenting = true;
			sim.getLogger().fine(
					"Initial argument sent by agent: " + this.getID());
		} catch (ShanksException e) {
			e.printStackTrace();
		}
	}

	private void getSensorsUpdateData(SolarFlareClassificationSimulation sim) {
		SolarFlare orig = (SolarFlare) sim.getScenario().getNetworkElement(
				SolarFlareScenario.ORIGINALFLARE);
		for (String sensor : this.sensors) {
			String value = (String) orig.getProperty(sensor);
			evidences.put(sensor, value);
		}
	}

	public String getProponentName() {
		return this.getID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.agent.capability.argumentation.ArgumentativeAgent
	 * #getProponent()
	 */
	public ArgumentativeAgent getProponent() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.agent.capability.argumentation.ArgumentativeAgent
	 * #getArgumentationManager()
	 */
	public ArgumentationManagerAgent getArgumentationManager() {
		return this.manager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.agent.capability.argumentation.ArgumentativeAgent
	 * #getArgumentationManagerName()
	 */
	public String getArgumentationManagerName() {
		SimpleShanksAgent ag = (SimpleShanksAgent) this.manager;
		return ag.getID();
	}

	public Set<Argument> getCurrentArguments() throws ShanksException {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateBeliefsWithNewArguments(Set<Argument> args)
			throws ShanksException {
		// TODO Auto-generated method stub

	}

	public ProbabilisticNetwork getBayesianNetwork() {
		return this.bn;
	}

	public void setBayesianNetwork(ProbabilisticNetwork bn) {
		this.bn = bn;
	}

	public String getBayesianNetworkFilePath() {
		return this.bnFilePath;
	}

	public void setArgumentationManager(ArgumentationManagerAgent manager) {
		this.manager = manager;
	}

	public void finishArgumenation() {
		this.argumenting = false;
		this.evidences.clear();
		this.pendingArguments.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.agent.capability.argumentation.ArgumentativeAgent
	 * #sendArgument
	 * (es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argument)
	 */
	public void sendArgument(Argument arg) {
		Message m = new Message();
		m.setPropCont(arg);
		m.setReceiver(this.getArgumentationManagerName());
		super.sendMsg(m);
	}
}
