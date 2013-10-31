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
package es.upm.dit.gsi.barmas.solarflare.agent.basic;

import jason.asSemantics.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import unbbayes.prs.bn.ProbabilisticNetwork;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.AgentArgumentativeCapability;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argument;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.ArgumentativeAgent;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Given;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Proposal;
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
 * Grupo de Sistemas Inteligentes
 * Departamento de Ingeniería de Sistemas Telemáticos
 * Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 23/07/2013
 * @version 0.1
 * 
 */
public class BasicClassificatorAgent extends SimpleShanksAgent implements
		BayesianReasonerShanksAgent, ArgumentativeAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8582918551821278046L;

	private ArgumentativeAgent manager;
	private String bnFilePath;
	private ProbabilisticNetwork bn;
	private List<String> sensors;

	private boolean argumenting;

	private List<ArgumentativeAgent> argumentationGroup;

	private HashMap<String, String> evidences;

	private ArrayList<Argument> pendingArguments;

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param manager
	 * @param bnPath
	 */
	public BasicClassificatorAgent(String id,
			ArgumentativeAgent manager, String bnPath,
			List<String> sensors, Logger logger) {
		super(id, logger);
		this.bnFilePath = bnPath;
		this.sensors = sensors;
		this.setArgumentationManager(manager);
		this.argumenting = false;
		this.pendingArguments = new ArrayList<Argument>();
		this.evidences = new HashMap<String, String>();
		this.argumentationGroup = new ArrayList<ArgumentativeAgent>();
		try {
			ShanksAgentBayesianReasoningCapability.loadNetwork(this);
		} catch (ShanksException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Register in manager
		this.getArgumentationManager().addArgumentationGroupMember(this);
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
		SolarFlare orig = (SolarFlare) sim.getScenario().getNetworkElement(
				SolarFlareScenario.ORIGINALFLARE);
		if (orig.getStatus().get(SolarFlare.READY)) {
			this.getSensorsUpdateData(sim);
		}

		// Decide what to do: start a new argumentation or process incoming
		// arguments
		if (this.pendingArguments.size() > 0) {
			if (!argumenting) {
				this.argumenting = true;
			}
			// Get all available givens
			boolean newEvidences = this.updateEvidences(this.pendingArguments);

			// Process incoming messages
			for (Argument arg : this.pendingArguments) {
				simulation.getLogger().finer(
						"Agent: " + this.getID()
								+ " -> Received arguments from: "
								+ arg.getProponent().getProponentName());
				boolean response = this.processNewArgument(arg, newEvidences, sim);
				if (response) {
					break; // when assumptions exist, this is now valid -> there are no assumptions in this basic agent
				}
			}
			this.pendingArguments.clear();
		} else {
			if (!argumenting && orig.getStatus().get(SolarFlare.READY)) {
				this.startClassification(sim);
			}
		}
	}

	private boolean updateEvidences(List<Argument> pendingArguments) {
		// Update current evidences with new givens received in the incoming
		// argument
		boolean newInfo = false;
		for (Argument arg : pendingArguments) {
			Set<Given> givens = arg.getGivens();
			for (Given given : givens) {
				if (evidences.keySet().contains(given.getNode())) {
					if (!evidences.get(given.getNode())
							.equals(given.getValue())) {
						this.getLogger().warning("No sense! Different evidences from different agents.");
						this.getLogger().finest("Agent: " + this.getID() + " Evidence: "
								+ given.getNode() + " - "
								+ evidences.get(given.getValue()));
						this.getLogger().finest("Agent: "
								+ arg.getProponent().getProponentName()
								+ " Evidence: " + given.getNode() + " - "
								+ given.getValue());
					}
//					else {
//						this.getLogger().finer("Agent: " + this.getID()
//								+ " -> Old evidence received from "
//								+ arg.getProponent().getProponentName()
//								+ " Evidence: " + given.getNode() + " - "
//								+ given.getValue());
//					}
				} else {
					newInfo = true;
					this.evidences.put(given.getNode(), given.getValue());
					this.getLogger().finer("Agent: " + this.getID()
							+ " -> Adding evidence received from "
							+ arg.getProponent().getProponentName()
							+ " Evidence: " + given.getNode() + " - "
							+ given.getValue());

				}
			}
		}
		return newInfo;
	}

	private boolean processNewArgument(Argument arg, boolean newInfo,
			SolarFlareClassificationSimulation sim) {
		boolean sent = false;
		if (newInfo) {
			sim.getLogger().finer(
					"Agent: " + this.getID() + " -> Number of evidences: "
							+ this.evidences.size());
			for (Entry<String, String> entry : evidences.entrySet()) {
				sim.getLogger()
						.finest("Agent: " + this.getID()
								+ " adding evidence: " + entry.getKey()
								+ " - " + entry.getValue());
				try {

					ShanksAgentBayesianReasoningCapability.addEvidence(
							this, entry.getKey(), entry.getValue());
				} catch (ShanksException e) {
					sim.getLogger().fine(
							"Agent: " + this.getID()
									+ " -> Unknown state for node: "
									+ entry.getKey() + " -> State: "
									+ entry.getValue());

					sim.getLogger().warning(e.getMessage());
					System.exit(1);
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
			System.exit(1);
		}
		String hyp = "";
		float maxValue = 0;
		for (Entry<String, Float> entry : hyps.entrySet()) {
			if (entry.getValue() > maxValue) {
				maxValue = entry.getValue();
				hyp = entry.getKey();
			}
		}
		SolarFlare orig = (SolarFlare) sim.getScenario().getNetworkElement(
				SolarFlareScenario.ORIGINALFLARE);
		sim.logger.finer("Agent: " + this.getID()
				+ " has hypothesis for case SolarFlareID: " + orig.getCaseID()
				+ " " + SolarFlareType.class.getSimpleName() + " - " + hyp
				+ " -> Confidence: " + maxValue);

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
			if (newInfo) {
				// Create and send counter argument
				Argument counterArg = AgentArgumentativeCapability
						.createArgument(this,
								SolarFlareType.class.getSimpleName(), hyp,
								maxValue, evidences, sim.schedule.getSteps(),
								System.currentTimeMillis());
				this.sendArgument(counterArg);
				sent = true;
				// if no new evidences can be offered - add assumptions?? -> This is not done in this basic agent

				sim.getLogger().finer(
						"Counter argument sent by agent: " + this.getID());
			}
		} else if (evidences.size() > arg.getGivens().size()) {
			sim.getLogger()
					.finer("Agent: " + this.getID() + " agrees with "
							+ arg.getProponent().getProponentName()
							+ " and sends a support argument with more givens.");
			// Create and send defensive argument (to support)
			Argument supportArg = AgentArgumentativeCapability.createArgument(
					this, SolarFlareType.class.getSimpleName(), hyp, maxValue,
					evidences, sim.schedule.getSteps(),
					System.currentTimeMillis());
			this.sendArgument(supportArg);
			sent = true;
		} else {
			sim.getLogger().finer(
					"Agent: " + this.getID() + " agrees with "
							+ arg.getProponent().getProponentName());
		}
		return sent;
	}

	private void startClassification(SolarFlareClassificationSimulation sim) {

		try {

			sim.getLogger().finer(
					"Agent: " + this.getID() + " -> Number of evidences: "
							+ this.evidences.size());
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
					System.exit(1);
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
			SolarFlare orig = (SolarFlare) sim.getScenario().getNetworkElement(
					SolarFlareScenario.ORIGINALFLARE);
			sim.logger.finer("Agent: " + this.getID()
					+ " has hypothesis for case SolarFlareID: "
					+ orig.getCaseID() + " "
					+ SolarFlareType.class.getSimpleName() + " - " + hyp
					+ " -> Confidence: " + maxValue);

			// Create and send initial argument
			Argument arg = AgentArgumentativeCapability.createArgument(this,
					SolarFlareType.class.getSimpleName(), hyp, maxValue,
					evidences, sim.schedule.getSteps(),
					System.currentTimeMillis());
			this.sendArgument(arg);

			this.argumenting = true;
			sim.getLogger().fine(
					"Initial argument sent by agent: " + this.getID());
		} catch (ShanksException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void getSensorsUpdateData(SolarFlareClassificationSimulation sim) {
		SolarFlare orig = (SolarFlare) sim.getScenario().getNetworkElement(
				SolarFlareScenario.ORIGINALFLARE);
		for (String sensor : this.sensors) {
			String value = (String) orig.getProperty(sensor);
			evidences.remove(sensor);
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
	 * #getArgumentationManagerName()
	 */
	public String getArgumentationManagerName() {
		SimpleShanksAgent ag = (SimpleShanksAgent) this.manager;
		return ag.getID();
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

	public void setArgumentationManager(ArgumentativeAgent manager) {
		this.manager = manager;
	}

	public void finishArgumenation() {
		this.argumenting = false;
		this.evidences.clear();
		this.pendingArguments.clear();
		try {
			ShanksAgentBayesianReasoningCapability.clearEvidences(this);
		} catch (ShanksException e) {
			e.printStackTrace();
			System.exit(1);
		}
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
		for (ArgumentativeAgent agent : this.argumentationGroup) {
			Message m = new Message();
			m.setPropCont(arg);
			m.setReceiver(((SimpleShanksAgent) agent).getID());
			super.sendMsg(m);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.agent.capability.argumentation.ArgumentativeAgent
	 * #addArgumentationGroupMember
	 * (es.upm.dit.gsi.barmas.agent.capability.argumentation.ArgumentativeAgent)
	 */
	public void addArgumentationGroupMember(ArgumentativeAgent agent) {
		if (!agent.equals(this) && !this.argumentationGroup.contains(agent)) {
			this.argumentationGroup.add(agent);
			this.getLogger().fine("Agent: "
							+ this.getID()
							+ " has added a new agent as argumentation member -> New member: "
							+ agent.getProponentName());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.agent.capability.argumentation.ArgumentativeAgent
	 * #removeArgumentationGroupMember
	 * (es.upm.dit.gsi.barmas.agent.capability.argumentation.ArgumentativeAgent)
	 */
	public void removeArgumentationGroupMember(ArgumentativeAgent agent) {
		this.argumentationGroup.remove(agent);
	}

	public ArgumentativeAgent getArgumentationManager() {
		return this.manager;
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.ArgumentativeAgent#areDistributionsFarEnough(java.util.Map, java.util.Map)
	 */
	@Override
	public boolean areDistributionsFarEnough(Map<String, Double> a,
			Map<String, Double> b) {
		// Nothing to do
		return false;
	}
}
