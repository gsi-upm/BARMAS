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
package es.upm.dit.gsi.barmas.solarflare.agent.advancedWithAssumptions;

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
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Given;
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
public class AdvancedWAClassificatorAgent extends SimpleShanksAgent implements
		BayesianReasonerShanksAgent, ArgumentativeAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8582918551821278046L;

	private ArgumentativeAgent manager;
	private String bnFilePath;
	private ProbabilisticNetwork bn;
	private List<String> sensors;

	private List<ArgumentativeAgent> argumentationGroup;

	private HashMap<String, String> evidences;

	private ArrayList<Argument> pendingArguments;

	private Argumentation argumentation;

	private HashMap<Integer, Argument> mySentArguments;

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
			String bnPath, List<String> sensors) {
		super(id);
		this.bnFilePath = bnPath;
		this.sensors = sensors;
		this.setArgumentationManager(manager);
		this.pendingArguments = new ArrayList<Argument>();
		this.evidences = new HashMap<String, String>();
		this.mySentArguments = new HashMap<Integer, Argument>();
		this.argumentationGroup = new ArrayList<ArgumentativeAgent>();
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

		this.pendingArguments.clear();
	}

	/**
	 * @param sim
	 */
	private void evaluateNextAction(SolarFlareClassificationSimulation sim) {
		// Check graph and/or argumentation and try to generate arguments
		boolean usefulArgument = this.evaluatePossibleArguments();
		if (usefulArgument) {
			this.goToArgumenting(sim);
			this.goToWaiting();
		} else {
			this.goToWaiting();
		}
	}

	private boolean evaluatePossibleArguments() {
		// Get all available givens
		boolean newEvidences = this.updateEvidences(this.pendingArguments);

		if (newEvidences) {
			return true;
		} else {
			return false;
		}

		// TODO check other possibilities

	}

	/**
	 * Process all pending arguments
	 */
	private void processPendingArguments(
			SolarFlareClassificationSimulation simulation) {

		// Process incoming messages
		for (Argument arg : this.pendingArguments) {
			simulation.getLogger().finer(
					"Agent: " + this.getID() + " -> Received arguments from: "
							+ arg.getProponent().getProponentName());
			this.argumentation.addArgument(arg);
		}

	}

	/**
	 * Check if new givens are found in the pending arguments and update
	 * evidences if any.
	 * 
	 * @param collection
	 * @return
	 */
	private boolean updateEvidences(List<Argument> pendingArguments) {
		// Update current evidences with new givens received in the incoming
		// argument
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		boolean newInfo = false;
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
					newInfo = true;
					this.evidences.put(given.getNode(), given.getValue());
					logger.finer("Agent: " + this.getID()
							+ " -> Adding evidence received from "
							+ arg.getProponent().getProponentName()
							+ " Evidence: " + given.getNode() + " - "
							+ given.getValue());

				}
			}
		}
		return newInfo;
	}

	/**
	 * @param sim
	 */
	private void generateArguments(SolarFlareClassificationSimulation sim) {

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
					sim.getLogger().fine(
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

			sim.logger.finer("Agent: " + this.getID()
					+ " has hypothesis for case SolarFlareID: "
					+ this.argumentation.getId() + " "
					+ SolarFlareType.class.getSimpleName() + " - " + hyp
					+ " -> Confidence: " + maxValue);

			Argument arg = AgentArgumentativeCapability.createArgument(this,
					SolarFlareType.class.getSimpleName(), hyp, maxValue,
					evidences, sim.schedule.getSteps(),
					System.currentTimeMillis());
			AgentArgumentativeCapability.sendArgument(this, arg);

			sim.getLogger().fine("Argument sent by agent: " + this.getID());
		} catch (ShanksException e) {
			e.printStackTrace();
		}
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

	public void setArgumentationManager(ArgumentativeAgent manager) {
		this.manager = manager;
	}

	public void finishArgumenation() {
		this.evidences.clear();
		this.pendingArguments.clear();
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
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
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
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).fine(
				this.getID() + " going to status: IDLE");
		this.IDLE = true;
		this.ARGUMENTING = false;
		this.PROCESSING = false;
		this.WAITING = false;
	}

	/**
	 * Go to status ARGUMENTING
	 */
	private void goToArgumenting(SolarFlareClassificationSimulation sim) {
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).fine(
				this.getID() + " going to status: ARGUMENTING");
		this.IDLE = false;
		this.ARGUMENTING = true;
		this.PROCESSING = false;
		this.WAITING = false;
		this.generateArguments(sim);
		this.goToWaiting();
	}

	/**
	 * Go to status PROCESSING
	 * 
	 * @param sim
	 */
	private void goToProcessing(SolarFlareClassificationSimulation sim) {
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).fine(
				this.getID() + " going to status: PROCESSING");
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
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).fine(
				this.getID() + " going to status: WAITING");
		this.IDLE = false;
		this.ARGUMENTING = false;
		this.PROCESSING = false;
		this.WAITING = true;
	}
}
