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
package es.upm.dit.gsi.barmas.agent;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import smile.Network;
import es.upm.dit.gsi.barmas.agent.capability.learning.bayes.AgentBayesLearningCapability;
import es.upm.dit.gsi.barmas.agent.capability.learning.bayes.BayesLearningAgent;
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
 * es.upm.dit.gsi.barmas.agent.BarmasBayesCentralAgent.java
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
public class BarmasBayesCentralAgent extends SimpleShanksAgent implements
		BayesianReasonerShanksAgent, BayesLearningAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6542107693613585524L;
	private String bnFile;
	private String datasetFile;
	private Network bn;
	private List<String> sensors;
	private String classificationTarget;

	/**
	 * Constructor
	 * 
	 * @param id
	 */
	public BarmasBayesCentralAgent(String id, String classificationTarget,
			String bnFile, String datasetFile, List<String> sensors,
			Logger logger) {
		super(id, logger);
		this.classificationTarget = classificationTarget;
		this.datasetFile = datasetFile;
		this.sensors = sensors;
		this.bnFile = bnFile;
		while (this.bn == null) {
			try {
				ShanksAgentBayesianReasoningCapability.loadNetwork(this);
				logger.info("Bayesian network loaded successfully by agent "
						+ this.getID());
			} catch (Exception e) {
				try {
					int learningIterations = 2;
					AgentBayesLearningCapability.learnBNWithBayesianSearch(
							this, learningIterations, classificationTarget);
				} catch (Exception ex) {
					ex.printStackTrace();
					System.exit(1);
				}
			}
		}
		this.getLogger().info("Sensors for " + this.getID());
		for (String sensor : sensors) {
			this.getLogger().info(sensor);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.shanks.agent.ShanksAgent#checkMail()
	 */
	public void checkMail() {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.
	 * BayesianReasonerShanksAgent#getBayesianNetwork()
	 */
	public Network getBayesianNetwork() {
		return this.bn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.smile.
	 * BayesianReasonerShanksAgent#setBayesianNetwork(smile.Network)
	 */
	public void setBayesianNetwork(Network bn) {
		this.bn = bn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.
	 * BayesianReasonerShanksAgent#getBayesianNetworkFilePath()
	 */
	public String getBayesianNetworkFilePath() {
		return this.bnFile;
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

		DiagnosisCase origDiagnosis = (DiagnosisCase) sim.getScenario()
				.getNetworkElement(DiagnosisScenario.ORIGINALDIAGNOSIS);

		DiagnosisCase bayesDiagnosis = (DiagnosisCase) sim.getScenario()
				.getNetworkElement(DiagnosisScenario.CENTRALCONCLUSION);

		// Check if it is time to check the diagnosis case
		if (origDiagnosis.getStatus().get(DiagnosisCase.READY)
				&& !bayesDiagnosis.getStatus().get(DiagnosisCase.READY)) {

			bayesDiagnosis.clean();
			bayesDiagnosis.setCaseID(origDiagnosis.getCaseID());

			HashMap<String, String> evidences = new HashMap<String, String>();

			for (String key : this.sensors) {
				String evidence = (String) origDiagnosis.getProperty(key);
				evidences.put(key, evidence);
			}

			try {
				for (Entry<String, String> entry : evidences.entrySet()) {
					try {
						ShanksAgentBayesianReasoningCapability.addEvidence(
								this, entry.getKey(), entry.getValue());
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
						.getNodeStatesHypotheses(this, classificationTarget);

				// Update the bayes central "device"

				for (Entry<String, String> ev : evidences.entrySet()) {
					bayesDiagnosis.changeProperty(ev.getKey(), ev.getValue());
				}

				String conclusion = "Unknown";
				float max = (float) 0.0;
				for (Entry<String, Float> hyp : hyps.entrySet()) {
					if (hyp.getValue() > max) {
						max = hyp.getValue();
						conclusion = hyp.getKey();
					}
				}

				bayesDiagnosis.changeProperty(classificationTarget, conclusion);

				bayesDiagnosis.setCurrentStatus(DiagnosisCase.READY, true);

				sim.getLogger().info(
						"Hypothesis by Central Bayesian Agent. Diagnosis Case ID: "
								+ bayesDiagnosis.getCaseID() + ": "
								+ classificationTarget + " - " + conclusion
								+ " -> Confidence: " + max);

				ShanksAgentBayesianReasoningCapability.clearEvidences(this);

			} catch (ShanksException e) {
				e.printStackTrace();
				System.exit(1);
			}
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
}
