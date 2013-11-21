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
package es.upm.dit.gsi.barmas.agent;

import jason.asSemantics.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import smile.Network;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.AgentArgumentativeCapability;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argument;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argumentation;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.ArgumentativeAgent;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Assumption;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Given;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Proposal;
import es.upm.dit.gsi.barmas.agent.capability.learning.bayes.AgentBayesLearningCapability;
import es.upm.dit.gsi.barmas.agent.capability.learning.bayes.BayesLearningAgent;
import es.upm.dit.gsi.barmas.agent.capability.learning.bayes.ValidationMetricsStore;
import es.upm.dit.gsi.barmas.model.DiagnosisCase;
import es.upm.dit.gsi.barmas.model.scenario.DiagnosisScenario;
import es.upm.dit.gsi.barmas.simulation.DiagnosisSimulation;
import es.upm.dit.gsi.shanks.ShanksSimulation;
import es.upm.dit.gsi.shanks.agent.SimpleShanksAgent;
import es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.smile.BayesianReasonerShanksAgent;
import es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.smile.ShanksAgentBayesianReasoningCapability;
import es.upm.dit.gsi.shanks.exception.ShanksException;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.agent.BarmasClassificatorAgent.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 01/11/2013
 * @version 0.1
 * 
 */
public class BarmasClassificatorAgent extends SimpleShanksAgent implements
		BayesianReasonerShanksAgent, BayesLearningAgent, ArgumentativeAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8582918551821278046L;

	private ArgumentativeAgent manager;
	private String bnFile;
	private Network bn;
	private List<String> sensors;

	private List<ArgumentativeAgent> argumentationGroup;

	private HashMap<String, String> evidences;
	private HashMap<String, String> evidencesToUpdateScores;

	private ArrayList<Argument> pendingArguments;

	private Argumentation argumentation;

	private HashMap<Integer, Argument> mySentArguments;
	private List<Argument> attackedByMeArguments;
	private List<Argument> acceptedByMeArguments;

	private HashMap<String, HashMap<String, Double>> updatedBeliefs;
	private HashMap<String, ArgumentativeAgent> sourceOfData;
	private HashMap<Assumption, Argument> assumptionsToImprove;
	private boolean newEvidences;
	private boolean newBeliefs;

	private double diffThreshold;
	private double beliefThreshold;
	private double trustThreshold;
	private String classificationTarget;
	private String datasetFile;

	private boolean reputationMode;
	private ValidationMetricsStore scores;

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
	 * @param bnFile
	 */
	public BarmasClassificatorAgent(String id, ArgumentativeAgent manager,
			String classificationTarget, String bnFile, String datasetFile,
			List<String> sensors, double diffThreshold, double beliefThreshold,
			double trustThreshold, Logger logger) {
		super(id, logger);
		this.bnFile = bnFile;
		this.sensors = sensors;
		this.diffThreshold = diffThreshold;
		this.beliefThreshold = beliefThreshold;
		this.trustThreshold = trustThreshold;
		this.reputationMode = (trustThreshold <= 1);
		this.datasetFile = datasetFile;
		this.classificationTarget = classificationTarget;
		this.newEvidences = false;
		this.newBeliefs = false;
		this.setArgumentationManager(manager);
		this.pendingArguments = new ArrayList<Argument>();
		this.evidences = new HashMap<String, String>();
		this.mySentArguments = new HashMap<Integer, Argument>();
		this.attackedByMeArguments = new ArrayList<Argument>();
		this.acceptedByMeArguments = new ArrayList<Argument>();
		this.argumentationGroup = new ArrayList<ArgumentativeAgent>();
		this.updatedBeliefs = new HashMap<String, HashMap<String, Double>>();
		this.sourceOfData = new HashMap<String, ArgumentativeAgent>();
		this.assumptionsToImprove = new HashMap<Assumption, Argument>();
		while (this.bn == null) {
			try {
				ShanksAgentBayesianReasoningCapability.loadNetwork(this);
				logger.info("Bayesian network loaded successfully by agent "
						+ this.getID());
			} catch (Exception e) {
				try {
					int learningIterations = 3;
					AgentBayesLearningCapability.learnBNWithBayesianSearch(
							this, learningIterations, classificationTarget);
				} catch (Exception ex) {
					this.getLogger().severe(
							"Problem learning BN. Exception: "
									+ ex.getMessage());
					ex.printStackTrace();
					System.exit(1);
				}
			}
		}

		// THIS BLOCK IS ONLY FOR REPUTATION MODE
		if (reputationMode) {
			logger.info("Reputation mode ON in Agent: " + this.getID());
			this.evidencesToUpdateScores = new HashMap<String, String>();
			// Update scores based on background knowledge
			this.scores = AgentArgumentativeCapability
					.getFScoreStoreBasedOnBackgroundKnowledge(this,
							this.getBayesianNetworkFilePath());
		}
		// END OF REPUTATION BLOCK

		this.getLogger().info("Sensors for " + this.getID());
		for (String sensor : sensors) {
			this.getLogger().info(sensor);
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
		DiagnosisSimulation sim = (DiagnosisSimulation) simulation;
		DiagnosisCase orig = (DiagnosisCase) sim.getScenario()
				.getNetworkElement(DiagnosisScenario.ORIGINALDIAGNOSIS);
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
	private void evaluateNextAction(DiagnosisSimulation sim) {
		// Check graph and/or argumentation and try to generate arguments
		if (this.isThereNewInfo() || this.isThereAssumptionsToImprove(sim)) {
			this.getLogger()
					.fine("Useful information found by " + this.getID());
			this.goToArgumenting(sim);
			this.goToWaiting();
		} else {
			this.getLogger().fine(
					"No useful information found by " + this.getID());
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
	private void processPendingArguments(DiagnosisSimulation simulation) {

		// Process incoming messages
		for (Argument arg : this.pendingArguments) {
			this.getLogger().finer(
					"Agent: " + this.getID() + " -> Received arguments from: "
							+ arg.getProponent().getProponentName());
			this.argumentation.addArgument((Argument) arg.clone(), this);
		}

		// Update evidences and add them to the BN
		this.updateEvidences(this.pendingArguments);
		// Update beliefs to add to the BN as soft-evidences
		this.updateBeliefs(this.pendingArguments);
	}

	/**
	 * Check possible beliefs to update once no new evidences are found
	 * 
	 * @param args
	 */
	private void updateBeliefs(List<Argument> args) {

		if (this.getMyLastArgument() != null
				&& this.getMyLastArgument().getGivens().size() == this.evidences
						.size()) {
			// check if it is valid
			List<Integer> attackTypes = new ArrayList<Integer>();
			attackTypes.add(AgentArgumentativeCapability.UNDERCUT);
			attackTypes.add(AgentArgumentativeCapability.DIRECTUNDERCUT);
			attackTypes.add(AgentArgumentativeCapability.CANONICALUNDERCUT);
			// attackTypes.add(AgentArgumentativeCapability.DEFEATER);
			// attackTypes.add(AgentArgumentativeCapability.DIRECTDEFEATER);
			List<Argument> unattacked = AgentArgumentativeCapability
					.getUnattackedArguments(args, this.argumentation,
							attackTypes);
			unattacked.removeAll(this.acceptedByMeArguments);
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
						// Only strong beliefs are proposed (sent as
						// proposals), so no more constrains must be added.
						// Anyway, you must check if you have other
						// different strong belief in that node.
						Proposal auxp = new Proposal(p.getNode(), ownBelief);
						double maxDiff = p.getMaxValue() - auxp.getMaxValue();
						boolean otherIsBetter;
						boolean moreTrust;
						boolean enoughDistance = this
								.areDistributionsFarEnough(receivedBelief,
										ownBelief);
						if (reputationMode) {
							// moreTrust = p.getTrustScoreValue()
							// - this.getTrustScoreValueForCurrentBelief(p
							// .getNode()) >= this.trustThreshold;
							moreTrust = (p.getTrustScoreValue()
									* p.getMaxValue() * 10)
									- (this.getTrustScoreValueForCurrentBelief(p
											.getNode())
											* auxp.getMaxValue() * 10) >= this.trustThreshold;
							otherIsBetter = moreTrust
									&& (maxDiff >= this.beliefThreshold);
						} else {
							otherIsBetter = (maxDiff >= this.beliefThreshold);
						}
						if (enoughDistance && otherIsBetter) {
							try {
								ShanksAgentBayesianReasoningCapability
										.addSoftEvidence(
												this.getBayesianNetwork(),
												p.getNode(), receivedBelief);
								this.updatedBeliefs.put(p.getNode(),
										receivedBelief);
								this.acceptedByMeArguments.add(arg);
								this.updateSourceOfData(p.getNode(),
										p.getSource());
								this.getLogger().fine(
										"Belief " + p.getNode()
												+ " updated for agent: "
												+ this.getID());
								if (reputationMode) {
									this.getLogger().fine(
											"New belief with a trust strength equals to: "
													+ p.getTrustScoreValue());
								}
								this.newBeliefs = true;
							} catch (ShanksException e) {
								this.getLogger()
										.warning(
												this.getID()
														+ " -> Problems updating beliefs.");
								this.getLogger().warning(e.getMessage());
								e.printStackTrace();
								System.exit(1);
							}
						} else {
							this.getLogger()
									.fine(this.getID()
											+ " -> Belief discarded because it is not strong enough. Node: "
											+ p.getNode() + " proposed by "
											+ arg.getProponentName());
						}
					}
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

		List<String> evidencesToOffer = new ArrayList<String>();
		for (String evidence : this.evidences.keySet()) {
			evidencesToOffer.add(evidence);
		}
		for (Argument arg : pendingArguments) {
			Set<Given> givens = arg.getGivens();

			// Check if the argument contains new evidences to add to local
			// ones.
			for (Given given : givens) {
				if (evidencesToOffer.contains(given.getNode())) {
					evidencesToOffer.remove(given.getNode());
					if (!this.evidences.get(given.getNode()).equals(
							given.getValue())) {
						this.getLogger()
								.warning(
										"INCOHERENCE! -> No sense! Different evidences from different agents.");
						this.getLogger().warning(
								"Agent: " + this.getID() + " Evidence: "
										+ given.getNode() + " - "
										+ evidences.get(given.getValue()));
						this.getLogger().warning(
								"Agent: "
										+ arg.getProponent().getProponentName()
										+ " Evidence: " + given.getNode()
										+ " - " + given.getValue());
						System.exit(1);
					}
				}
				if (evidences.keySet().contains(given.getNode())) {
					if (!evidences.get(given.getNode())
							.equals(given.getValue())) {
						this.getLogger()
								.warning(
										"INCOHERENCE! -> No sense! Different evidences from different agents.");
						this.getLogger().warning(
								"Agent: " + this.getID() + " Evidence: "
										+ given.getNode() + " - "
										+ evidences.get(given.getValue()));
						this.getLogger().warning(
								"Agent: "
										+ arg.getProponent().getProponentName()
										+ " Evidence: " + given.getNode()
										+ " - " + given.getValue());
						System.exit(1);
					}
				} else {
					this.evidences.put(given.getNode(), given.getValue());
					this.updateSourceOfData(given.getNode(), given.getSource());
					this.newEvidences = true;
				}
			}
		}

		if (evidencesToOffer.size() > 0) {
			this.newEvidences = true;
		}

		this.addEvidencesToBN();
	}

	/**
	 * 
	 */
	private void addEvidencesToBN() {
		// Add evidences
		this.getLogger().finer(
				"Agent: " + this.getID() + " -> Number of evidences: "
						+ this.evidences.size());
		try {
			ShanksAgentBayesianReasoningCapability.addEvidences(this,
					this.evidences);
		} catch (Exception e) {
			this.getLogger().fine(
					"Agent: " + this.getID()
							+ " -> Problems updating evidences: "
							+ e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * @param sim
	 */
	private void generateArguments(DiagnosisSimulation sim) {

		// If the agent is starting the argumentation or
		// if it is the first argument from this agent in this argumentation
		// if new evidences can be added or new belief has been updated
		if (this.argumentation.getArguments().isEmpty()
				|| this.mySentArguments.isEmpty() || this.isThereNewInfo()) {
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
	private boolean isThereAssumptionsToImprove(DiagnosisSimulation sim) {
		HashMap<String, HashMap<String, Float>> ownHypotheses = this
				.getAllMyHypotheses();

		List<Integer> attackTypes = new ArrayList<Integer>();
		attackTypes.add(AgentArgumentativeCapability.UNDERCUT);
		attackTypes.add(AgentArgumentativeCapability.DIRECTUNDERCUT);
		attackTypes.add(AgentArgumentativeCapability.CANONICALUNDERCUT);
		// attackTypes.add(AgentArgumentativeCapability.DEFEATER);
		// attackTypes.add(AgentArgumentativeCapability.DIRECTDEFEATER);
		List<Argument> unattacked = AgentArgumentativeCapability
				.getUnattackedArguments(this.argumentation, attackTypes);
		unattacked.removeAll(this.attackedByMeArguments);
		for (Argument arg : unattacked) {
			for (Assumption assum : arg.getAssumptions()) {
				try {
					HashMap<String, Double> receivedBelief = (HashMap<String, Double>) assum
							.getValuesWithConfidence();
					HashMap<String, Double> ownBelief = AgentArgumentativeCapability
							.convertToDoubleValues(ownHypotheses.get(assum
									.getNode()));
					Proposal auxp = new Proposal(assum.getNode(), ownBelief);

					double maxDiff = auxp.getMaxValue() - assum.getMaxValue();
					boolean myBeliefIsBetter;
					boolean moreTrust;
					boolean enoughDistance = this.areDistributionsFarEnough(
							receivedBelief, ownBelief);
					if (reputationMode) {
						// moreTrust = this
						// .getTrustScoreValueForCurrentBelief(assum
						// .getNode())
						// - assum.getTrustScoreValue() >= this.trustThreshold;
						moreTrust = (this
								.getTrustScoreValueForCurrentBelief(assum
										.getNode())
								* auxp.getMaxValue() * 10)
								- (assum.getTrustScoreValue()
										* assum.getMaxValue() * 10) >= this.trustThreshold;
						myBeliefIsBetter = moreTrust
								&& (maxDiff > this.beliefThreshold);
					} else {
						myBeliefIsBetter = (maxDiff > this.beliefThreshold);
					}

					if (enoughDistance && myBeliefIsBetter
							&& this.getSourceOfData(assum.getNode()) == this) {
						this.assumptionsToImprove.put(assum, arg);
					}
				} catch (Exception e) {
					this.getLogger().warning(
							"Problems getting local belief for: "
									+ assum.getNode());
					this.getLogger().warning(e.getMessage());
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
		return !this.assumptionsToImprove.isEmpty();
	}

	/**
	 * @param assumptionsToImprove
	 */
	private void sendCounterArgument(
			HashMap<Assumption, Argument> assumptionsToImprove,
			ShanksSimulation sim) {
		this.addEvidencesToBN();
		HashMap<String, HashMap<String, Float>> ownHypotheses = this
				.getAllMyHypotheses();
		for (Assumption assum : assumptionsToImprove.keySet()) {
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

			this.getLogger().fine(
					this.getID() + " -> Counter argument sent for belief: "
							+ assum.getNode());
			Argument arg = AgentArgumentativeCapability.createArgument(this,
					proposals, assumptions, evidences, sim.schedule.getSteps(),
					System.currentTimeMillis());
			this.sendArgument(arg);
			this.attackedByMeArguments.add(assumptionsToImprove.get(assum));
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
			ownBeliefs = ShanksAgentBayesianReasoningCapability
					.getAllHypotheses(this);
			List<String> beliefsToRemove = new ArrayList<String>();
			for (Entry<String, HashMap<String, Float>> entry : ownBeliefs
					.entrySet()) {
				if (entry.getKey().startsWith("softEvidenceNode")) {
					beliefsToRemove.add(entry.getKey());
				}
			}
			for (String b : beliefsToRemove) {
				ownBeliefs.remove(b);
			}

		} catch (ShanksException e) {
			this.getLogger().warning("Problems getting hypotheses...");
			this.getLogger().warning(e.getMessage());
			e.printStackTrace();
			System.exit(1);
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
	private boolean getSensorsUpdateData(DiagnosisSimulation sim) {
		DiagnosisCase orig = (DiagnosisCase) sim.getScenario()
				.getNetworkElement(DiagnosisScenario.ORIGINALDIAGNOSIS);
		boolean newEvent = orig.getStatus().get(DiagnosisCase.READY);
		if (newEvent) {
			for (String sensor : this.sensors) {
				String value = (String) orig.getProperty(sensor);
				evidences.remove(sensor);
				evidences.put(sensor, value);
				this.updateSourceOfData(sensor, this);
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

	public Network getBayesianNetwork() {
		return this.bn;
	}

	public void setBayesianNetwork(Network bn) {
		this.bn = bn;
	}

	public String getBayesianNetworkFilePath() {
		return this.bnFile;
	}

	public void setArgumentationManager(ArgumentativeAgent manager) {
		this.manager = manager;
	}

	public void finishArgumenation() {
		this.evidencesToUpdateScores = new HashMap<String, String>();
		if (reputationMode) {
			for (Entry<String, String> evidence : this.evidences.entrySet()) {
				this.evidencesToUpdateScores.put(evidence.getKey(),
						evidence.getValue());
			}
		}
		this.evidences.clear();
		this.pendingArguments.clear();
		this.sourceOfData.clear();
		this.mySentArguments.clear();
		this.updatedBeliefs.clear();
		this.argumentation = null;
		try {
			ShanksAgentBayesianReasoningCapability.clearEvidences(this);
		} catch (ShanksException e) {
			e.printStackTrace();
			System.exit(1);
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
		this.argumentation.addArgument((Argument) arg.clone(), this);
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
			this.getLogger()
					.fine("Agent: "
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
		this.getLogger().fine(this.getID() + " going to status: IDLE");
		this.IDLE = true;
		this.ARGUMENTING = false;
		this.PROCESSING = false;
		this.WAITING = false;
	}

	/**
	 * Go to status ARGUMENTING
	 */
	private void goToArgumenting(DiagnosisSimulation sim) {
		this.getLogger().fine(this.getID() + " going to status: ARGUMENTING");
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
	private void goToProcessing(DiagnosisSimulation sim) {
		this.getLogger().fine(this.getID() + " going to status: PROCESSING");
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
		this.getLogger().fine(this.getID() + " going to status: WAITING");
		this.IDLE = false;
		this.ARGUMENTING = false;
		this.PROCESSING = false;
		this.WAITING = true;
		this.pendingArguments.clear();
		this.newEvidences = false;
		this.newBeliefs = false;
		this.assumptionsToImprove.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.ArgumentativeAgent
	 * #areDistributionsFarEnough(java.util.Map, java.util.Map)
	 */
	@Override
	public boolean areDistributionsFarEnough(Map<String, Double> a,
			Map<String, Double> b) {
		if (AgentArgumentativeCapability.getNormalisedHellingerDistance(
				(HashMap<String, Double>) a, (HashMap<String, Double>) b) >= diffThreshold) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.agent.capability.learning.bayes.BayesLearningAgent
	 * #getDatasetFile()
	 */
	@Override
	public String getDatasetFile() {
		return this.datasetFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.agent.capability.learning.bayes.BayesLearningAgent
	 * #getBNOutputFile()
	 */
	@Override
	public String getBNOutputFile() {
		return this.bnFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.ArgumentativeAgent
	 * #updateTrustScores(es.upm.dit.gsi.barmas.model.DiagnosisCase)
	 */
	@Override
	public void updateFScoreStore(DiagnosisCase diagnosisCase) {
		try {
			this.getLogger().finest(
					"Updating scores for agent: " + this.getID()
							+ " after diagnosis case id: "
							+ diagnosisCase.getCaseID());
			ShanksAgentBayesianReasoningCapability.addEvidences(this,
					this.evidencesToUpdateScores);
			HashMap<String, HashMap<String, Float>> hyps = ShanksAgentBayesianReasoningCapability
					.getAllHypotheses(this);

			for (Entry<String, Object> property : diagnosisCase.getProperties()
					.entrySet()) {
				HashMap<String, Float> belief = hyps.get(property.getKey());
				String state = (String) property.getValue();
				this.checkBeliefAndUpdateScores(property.getKey(), state,
						belief);
			}
		} catch (ShanksException e) {
			this.getLogger().severe(
					"Problem updating scores in reputation mode. Exception: "
							+ e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * @param state
	 * @param belief
	 */
	private void checkBeliefAndUpdateScores(String node, String state,
			HashMap<String, Float> belief) {
		float max = 0;
		String maxState = "";
		for (Entry<String, Float> entry : belief.entrySet()) {
			if (entry.getValue() > max) {
				max = entry.getValue();
				maxState = entry.getKey();
			}
		}
		scores.updateConfusionMatrixWithNewCase(node, state, maxState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.ArgumentativeAgent
	 * #getSourceOfData()
	 */
	@Override
	public HashMap<String, ArgumentativeAgent> getSourceOfData() {
		return this.sourceOfData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.ArgumentativeAgent
	 * #updateSourceOfData(java.lang.String,
	 * es.upm.dit.gsi.barmas.agent.capability
	 * .argumentation.bayes.ArgumentativeAgent)
	 */
	@Override
	public void updateSourceOfData(String variable, ArgumentativeAgent source) {
		this.sourceOfData.put(variable, source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.ArgumentativeAgent
	 * #getSourceOfData(java.lang.String)
	 */
	@Override
	public ArgumentativeAgent getSourceOfData(String variable) {
		if (this.sourceOfData.containsKey(variable)) {
			return this.sourceOfData.get(variable);
		} else {
			return this;
		}
	}

	/**
	 * @param variable
	 * @return
	 */
	public double getTrustScoreValueForCurrentBelief(String variable) {
		HashMap<String, Float> hyps;
		try {
			hyps = ShanksAgentBayesianReasoningCapability
					.getNodeStatesHypotheses(this, variable);
			float max = 0;
			String stateMax = "";
			for (Entry<String, Float> entry : hyps.entrySet()) {
				if (entry.getValue() > max) {
					max = entry.getValue();
					stateMax = entry.getKey();
				}
			}
			ArgumentativeAgent source = this.getSourceOfData(variable);
			return source.getTrustScore(variable, stateMax);

		} catch (ShanksException e) {
			this.getLogger().severe(
					"Problem getting beliefs from BN for node: " + variable);
			System.exit(1);
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.ArgumentativeAgent
	 * #getTrustScore(java.lang.String, java.lang.String)
	 */
	@Override
	public double getTrustScore(String node, String state) {
		return this.scores.getMCC(node, state);
	}
}
