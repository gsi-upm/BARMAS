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
package es.upm.dit.gsi.barmas.solarflare.agent.advanced.assumptions.classic;

import jason.asSemantics.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import unbbayes.prs.bn.ProbabilisticNetwork;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.AgentArgumentativeCapability;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argument;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argumentation;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.ArgumentativeAgent;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Assumption;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Given;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Proposal;
import es.upm.dit.gsi.barmas.solarflare.model.SolarFlare;
import es.upm.dit.gsi.barmas.solarflare.model.scenario.SolarFlareScenario;
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
public class AdvancedWAClassificatorAgent extends SimpleShanksAgent implements
		BayesianReasonerShanksAgent, ArgumentativeAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8582918551821278046L;

	private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private ArgumentativeAgent manager;
	private String bnFilePath;
	private ProbabilisticNetwork bn;
	private List<String> sensors;

	private List<ArgumentativeAgent> argumentationGroup;

	private HashMap<String, String> evidences;

	private ArrayList<Argument> pendingArguments;

	private Argumentation argumentation;

	private HashMap<Integer, Argument> mySentArguments;

	private HashMap<String, HashMap<String, Double>> updatedBeliefs;
	private List<Assumption> assumptionsToImprove = new ArrayList<Assumption>();
	private boolean newEvidences;
	private boolean newBeliefs;

	private double threshold;
	private double beliefThreshold;
	private String classificationTarget;
	// private String datasetPath;

	// STATES
	private boolean IDLE;
	private boolean ARGUMENTING;
	private boolean PROCESSING;
	private boolean WAITING;

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param manager
	 * @param bnPath
	 */
	public AdvancedWAClassificatorAgent(String id, ArgumentativeAgent manager,
			String bnPath, String classificationTarget, String datasetPath,
			List<String> sensors, double threshold, double beliefThreshold) {
		super(id);
		this.bnFilePath = bnPath;
		this.sensors = sensors;
		this.threshold = threshold;
		this.beliefThreshold = beliefThreshold;
		// this.datasetPath = datasetPath;
		this.classificationTarget = classificationTarget;
		this.newEvidences = false;
		this.newBeliefs = false;
		this.setArgumentationManager(manager);
		this.pendingArguments = new ArrayList<Argument>();
		this.evidences = new HashMap<String, String>();
		this.mySentArguments = new HashMap<Integer, Argument>();
		this.argumentationGroup = new ArrayList<ArgumentativeAgent>();
		this.updatedBeliefs = new HashMap<String, HashMap<String, Double>>();
		try {
			ShanksAgentBayesianReasoningCapability.loadNetwork(this);
		} catch (ShanksException e) {
			e.printStackTrace();
		}
		this.goToIdle();

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
		SolarFlare orig = (SolarFlare) sim.getScenario().getNetworkElement(
				SolarFlareScenario.ORIGINALFLARE);
		// Check sensors
		boolean newEvent = false;
		if (this.IDLE) {
			newEvent = this.getSensorsUpdateData(sim);
		}

		// State machine
		if (this.pendingArguments.size() > 0) {
			if (this.IDLE) {
				this.argumentation = new Argumentation(orig.getCaseID());
			}
			if (this.IDLE || this.WAITING) {
				this.goToProcessing(sim);
				this.evaluateNextAction(sim);
			}
		} else if (this.IDLE && newEvent) {
			this.argumentation = new Argumentation(orig.getCaseID());
			this.goToArgumenting(sim);
			this.goToWaiting();
		}
	}

	/**
	 * @param sim
	 */
	private void evaluateNextAction(SolarFlareClassificationSimulation sim) {
		// Check graph and/or argumentation and try to generate arguments
		if (this.isThereNewInfo() || this.isThereAssumptionsToImprove(sim)) {
			logger.fine("Useful information found by " + this.getID());
			this.goToArgumenting(sim);
			this.goToWaiting();
		} else {
			logger.fine("No useful information found by " + this.getID());
			this.goToWaiting();
		}
	}

	/**
	 * @return if there is useful info to generate new arguments
	 */
	private boolean isThereNewInfo() {

		// If there are new info
		if (this.newEvidences || this.newBeliefs) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Process all pending arguments
	 */
	private void processPendingArguments(
			SolarFlareClassificationSimulation simulation) {

		// Process incoming messages
		for (Argument arg : this.pendingArguments) {
			logger.finer("Agent: " + this.getID()
					+ " -> Received arguments from: "
					+ arg.getProponent().getProponentName());
			this.argumentation.addArgument((Argument) arg.clone());
		}

		// Update evidences and add them to the BN
		this.updateEvidences(this.pendingArguments);
		// Update beliefs to add to the BN as soft-evidences
		this.updatedBeliefs(this.pendingArguments);
	}

	/**
	 * Check possible beliefs to update once no new evidences are found
	 * 
	 * @param args
	 */
	private void updatedBeliefs(List<Argument> args) {

		if (this.getMyLastArgument() != null
				&& this.getMyLastArgument().getGivens().size() == this.evidences
						.size()) {
			// check if it is valid
			List<Argument> unattacked = AgentArgumentativeCapability
					.getUnattackedArguments(args, this.argumentation,
							AgentArgumentativeCapability.UNDERCUT); // TODO
																	// check if
																	// more
																	// attacks
																	// must be
																	// checked
																	// here
			for (Argument arg : unattacked) {
				// check the proposals that are not the classification class
				for (Proposal p : arg.getProposals()) {
					if (!p.getNode().equals(this.classificationTarget)) {
						// Compare with local belief
						HashMap<String, Double> receivedBelief = (HashMap<String, Double>) p
								.getValuesWithConfidence();
						HashMap<String, Double> ownBelief = AgentArgumentativeCapability
								.convertToDoubleValues(this
										.getAllMyHypotheses().get(p.getNode()));
						double distance = AgentArgumentativeCapability
								.getNormalisedHellingerDistance(receivedBelief,
										ownBelief);
						logger.fine("Distance for belief: " + p.getNode()
								+ " is " + distance);
						// Only strong beliefs are proposed (sent as
						// proposals), so no more constrains must be added.
						// Anyway, you must check if you have other
						// different strong belief in that node.
						Proposal auxp = new Proposal(p.getNode(), ownBelief);
						double maxDiff = p.getMaxValue() - auxp.getMaxValue();
						if (distance >= this.threshold
								&& maxDiff >= beliefThreshold) {
							this.updatedBeliefs
									.put(p.getNode(), receivedBelief);
							this.newBeliefs = true;
						}
					}
				}
			}
			// Add beliefs to BN
			if (this.newBeliefs) {
				try {
					ShanksAgentBayesianReasoningCapability.addSoftEvidences(
							this, this.updatedBeliefs);
					logger.fine("Beliefs updated for agent: " + this.getID());
				} catch (ShanksException e) {
					logger.warning(this.getID()
							+ " -> Problems updating beliefs.");
					logger.warning(e.getMessage());
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * @param pendingArguments
	 */
	private void updateEvidences(List<Argument> pendingArguments) {
		// Update current evidences with new givens received in the incoming
		// argument
		for (Argument arg : pendingArguments) {
			Set<Given> givens = arg.getGivens();
			for (Given given : givens) {
				if (evidences.keySet().contains(given.getNode())) {
					if (!evidences.get(given.getNode())
							.equals(given.getValue())) {
						logger.warning("INCOHERENCE! -> No sense! Different evidences from different agents.");
						logger.finest("Agent: " + this.getID() + " Evidence: "
								+ given.getNode() + " - "
								+ evidences.get(given.getValue()));
						logger.finest("Agent: "
								+ arg.getProponent().getProponentName()
								+ " Evidence: " + given.getNode() + " - "
								+ given.getValue());
					}
				} else {
					this.evidences.put(given.getNode(), given.getValue());
					this.newEvidences = true;
					logger.finer("Agent: " + this.getID()
							+ " -> Adding evidence received from "
							+ arg.getProponent().getProponentName()
							+ " Evidence: " + given.getNode() + " - "
							+ given.getValue());

				}
			}
		}

		this.addEvidencesToBN();
	}

	/**
	 * 
	 */
	private void addEvidencesToBN() {
		// Add evidences
		logger.finer("Agent: " + this.getID() + " -> Number of evidences: "
				+ this.evidences.size());
		for (Entry<String, String> entry : evidences.entrySet()) {
			try {
				logger.finer("Agent: " + this.getID() + " adding evidence: "
						+ entry.getKey() + " - " + entry.getValue());
				ShanksAgentBayesianReasoningCapability.addEvidence(this,
						entry.getKey(), entry.getValue());
			} catch (Exception e) {
				logger.fine("Agent: " + this.getID()
						+ " -> Unknown state for node: " + entry.getKey()
						+ " -> State: " + entry.getValue());
			}

		}
	}

	/**
	 * @param sim
	 */
	private void generateArguments(SolarFlareClassificationSimulation sim) {

		// If the agent is starting the argumentation or
		// if it is the first argument from this agent in this argumentation
		// if new evidences can be added or new belief has been updated
		if (this.argumentation.getArguments().isEmpty()
				|| this.mySentArguments.isEmpty() || this.newEvidences) {
			// Full argument
			this.sendFullArgument(sim);
		} else {
			// Check assumptions and try to improve them
			this.sendCounterArgument(this.assumptionsToImprove, sim);

		}
	}

	/**
	 * @param sim
	 * @return
	 */
	private boolean isThereAssumptionsToImprove(
			SolarFlareClassificationSimulation sim) {
		HashMap<String, HashMap<String, Float>> ownHypotheses = this
				.getAllMyHypotheses();

		for (Argument arg : this.pendingArguments) {
			for (Assumption assum : arg.getAssumptions()) {
				try {
					HashMap<String, Double> receivedBelief = (HashMap<String, Double>) assum
							.getValuesWithConfidence();
					HashMap<String, Double> ownBelief = AgentArgumentativeCapability
							.convertToDoubleValues(ownHypotheses.get(assum
									.getNode()));
					double distance = AgentArgumentativeCapability
							.getNormalisedHellingerDistance(receivedBelief,
									ownBelief);
					logger.fine("Looking for assumptions to improve -> Distance for belief: "
							+ assum.getNode() + " is " + distance);
					if (distance > this.threshold) {
						Proposal auxp = new Proposal(assum.getNode(), ownBelief);
						boolean myBeliefIsBetter = (auxp.getMaxValue()
								- assum.getMaxValue() > this.beliefThreshold);
						if (myBeliefIsBetter) { // TODO this is only for
												// non-eficcient approach
												// because if it is
												// efficient, this belief
												// should be taken in the
												// updateBeliefs method as
												// relevant/interesting
							this.assumptionsToImprove.add(assum);
						}
					}
				} catch (Exception e) {
					logger.warning("Problems getting local belief for: "
							+ assum.getNode());
					logger.warning(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return !this.assumptionsToImprove.isEmpty();
	}

	/**
	 * @param assumptionsToImprove
	 */
	private void sendCounterArgument(List<Assumption> assumptionsToImprove,
			ShanksSimulation sim) {
		this.addEvidencesToBN();
		HashMap<String, HashMap<String, Float>> ownHypotheses = this
				.getAllMyHypotheses();
		for (Assumption assum : assumptionsToImprove) {
			// Get proposal
			HashMap<String, HashMap<String, Double>> proposals = new HashMap<String, HashMap<String, Double>>();
			HashMap<String, Float> hyps = ownHypotheses.get(assum.getNode());

			HashMap<String, Double> beliefs = new HashMap<String, Double>();
			for (Entry<String, Float> entry : hyps.entrySet()) {
				beliefs.put(entry.getKey(), new Double(entry.getValue()));
			}
			proposals.put(assum.getNode(), beliefs);

			HashMap<String, HashMap<String, Double>> assumptions = new HashMap<String, HashMap<String, Double>>();
			for (Entry<String, HashMap<String, Float>> entry : ownHypotheses
					.entrySet()) {
				String node = entry.getKey();
				if (!node.equals(this.classificationTarget)
						&& !node.equals(assum.getNode())
						&& !evidences.keySet().contains(node)) {
					beliefs = new HashMap<String, Double>();
					for (Entry<String, Float> values : entry.getValue()
							.entrySet()) {
						beliefs.put(values.getKey(),
								new Double(values.getValue()));
					}
					assumptions.put(node, beliefs);
				}
			}

			logger.fine(this.getID() + " -> Counter argument sent for belief: " + assum.getNode());
			Argument arg = AgentArgumentativeCapability.createArgument(this,
					proposals, assumptions, evidences, sim.schedule.getSteps(),
					System.currentTimeMillis());
			this.sendArgument(arg);

		}

	}

	private void sendFullArgument(ShanksSimulation sim) {
		this.addEvidencesToBN();

		// Get hypothesis
		HashMap<String, HashMap<String, Float>> hypotheses = this
				.getAllMyHypotheses();

		HashMap<String, HashMap<String, Double>> proposals = new HashMap<String, HashMap<String, Double>>();
		HashMap<String, Float> hyps = hypotheses.get(this.classificationTarget);

		HashMap<String, Double> beliefs = new HashMap<String, Double>();
		for (Entry<String, Float> entry : hyps.entrySet()) {
			beliefs.put(entry.getKey(), new Double(entry.getValue()));
		}
		proposals.put(this.classificationTarget, beliefs);

		HashMap<String, HashMap<String, Double>> assumptions = new HashMap<String, HashMap<String, Double>>();
		for (Entry<String, HashMap<String, Float>> entry : hypotheses
				.entrySet()) {
			String node = entry.getKey();
			if (!node.equals(this.classificationTarget)
					&& !evidences.keySet().contains(node)) {
				beliefs = new HashMap<String, Double>();
				for (Entry<String, Float> values : entry.getValue().entrySet()) {
					beliefs.put(values.getKey(), new Double(values.getValue()));
				}
				assumptions.put(node, beliefs);
			}
		}

		Argument arg = AgentArgumentativeCapability.createArgument(this,
				proposals, assumptions, evidences, sim.schedule.getSteps(),
				System.currentTimeMillis());
		this.sendArgument(arg);

		logger.fine("Argument sent by agent: " + this.getID());
	}

	/**
	 * Remove all "softEvidenceNode" beliefs (not relevant for classfication).
	 * These nodes are processed as auxiliary info to update beliefs 
	 * 
	 * @return
	 */
	private HashMap<String, HashMap<String, Float>> getAllMyHypotheses() {
		HashMap<String, HashMap<String, Float>> ownBeliefs = null;
		try {
			ownBeliefs = ShanksAgentBayesianReasoningCapability.getAllHypotheses(this);
			List<String> beliefsToRemove = new ArrayList<String>();
			for (Entry<String, HashMap<String, Float>> entry : ownBeliefs.entrySet()) {
				if (entry.getKey().startsWith("softEvidenceNode")) {
					beliefsToRemove.add(entry.getKey());
				}
			}
			for (String b : beliefsToRemove) {
				ownBeliefs.remove(b);
			}

		} catch (ShanksException e) {
			logger.warning("Problems getting hypotheses...");
			logger.warning(e.getMessage());
			e.printStackTrace();
		}
		return ownBeliefs;
	}

	/**
	 * @return
	 */
	private Argument getMyLastArgument() {
		int size = this.mySentArguments.size();
		return this.mySentArguments.get(size - 1);
	}

	/**
	 * @param sim
	 * @return
	 */
	private boolean getSensorsUpdateData(SolarFlareClassificationSimulation sim) {
		SolarFlare orig = (SolarFlare) sim.getScenario().getNetworkElement(
				SolarFlareScenario.ORIGINALFLARE);
		boolean newEvent = orig.getStatus().get(SolarFlare.READY);
		if (newEvent) {
			for (String sensor : this.sensors) {
				String value = (String) orig.getProperty(sensor);
				evidences.remove(sensor);
				evidences.put(sensor, value);
			}
		}
		return newEvent;
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
	public ArgumentativeAgent getArgumentationManager() {
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
		this.evidences.clear();
		this.pendingArguments.clear();
		this.mySentArguments.clear();
		this.updatedBeliefs.clear();
		this.argumentation = null;
		try {
			ShanksAgentBayesianReasoningCapability.clearEvidences(this);
		} catch (ShanksException e) {
			e.printStackTrace();
		}
		this.goToIdle();
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

		this.mySentArguments.put(mySentArguments.size(), arg);
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
			logger.fine("Agent: "
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

	/**
	 * @return the iDLE
	 */
	public boolean isIDLE() {
		return IDLE;
	}

	/**
	 * @param iDLE
	 *            the iDLE to set
	 */
	public void setIDLE(boolean iDLE) {
		IDLE = iDLE;
	}

	/**
	 * @return the aRGUMENTING
	 */
	public boolean isARGUMENTING() {
		return ARGUMENTING;
	}

	/**
	 * @param aRGUMENTING
	 *            the aRGUMENTING to set
	 */
	public void setARGUMENTING(boolean aRGUMENTING) {
		ARGUMENTING = aRGUMENTING;
	}

	/**
	 * @return the pROCESSING
	 */
	public boolean isPROCESSING() {
		return PROCESSING;
	}

	/**
	 * @param pROCESSING
	 *            the pROCESSING to set
	 */
	public void setPROCESSING(boolean pROCESSING) {
		PROCESSING = pROCESSING;
	}

	/**
	 * @return the wAITING
	 */
	public boolean isWAITING() {
		return WAITING;
	}

	/**
	 * @param wAITING
	 *            the wAITING to set
	 */
	public void setWAITING(boolean wAITING) {
		WAITING = wAITING;
	}

	/**
	 * Go to status IDLE
	 */
	private void goToIdle() {
		logger.fine(this.getID() + " going to status: IDLE");
		this.IDLE = true;
		this.ARGUMENTING = false;
		this.PROCESSING = false;
		this.WAITING = false;
	}

	/**
	 * Go to status ARGUMENTING
	 */
	private void goToArgumenting(SolarFlareClassificationSimulation sim) {
		logger.fine(this.getID() + " going to status: ARGUMENTING");
		this.IDLE = false;
		this.ARGUMENTING = true;
		this.PROCESSING = false;
		this.WAITING = false;
		this.generateArguments(sim);
	}

	/**
	 * Go to status PROCESSING
	 * 
	 * @param sim
	 */
	private void goToProcessing(SolarFlareClassificationSimulation sim) {
		logger.fine(this.getID() + " going to status: PROCESSING");
		this.IDLE = false;
		this.ARGUMENTING = false;
		this.PROCESSING = true;
		this.WAITING = false;
		this.processPendingArguments(sim);
	}

	/**
	 * Go to status WAITING
	 */
	private void goToWaiting() {
		logger.fine(this.getID() + " going to status: WAITING");
		this.IDLE = false;
		this.ARGUMENTING = false;
		this.PROCESSING = false;
		this.WAITING = true;
		this.pendingArguments.clear();
		this.newEvidences = false;
		this.newBeliefs = false;
		this.assumptionsToImprove.clear();
	}
}
