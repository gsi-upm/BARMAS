/**
 * es.upm.dit.gsi.barmas.solarflare.agent.SolarFlareBayesCentralAgent.java
 */
package es.upm.dit.gsi.barmas.solarflare.agent;

import java.util.HashMap;
import java.util.Map.Entry;

import unbbayes.prs.bn.ProbabilisticNetwork;
import es.upm.dit.gsi.barmas.solarflare.model.SolarFlare;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.Activity;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.Area;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.BecomeHist;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.CNode;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.Evolution;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.HistComplex;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.LargestSpotSize;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.MNode;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.PrevStatus24Hour;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.SolarFlareType;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.SpotDistribution;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.XNode;
import es.upm.dit.gsi.barmas.solarflare.simulation.SolarFlareClassificationSimulation;
import es.upm.dit.gsi.shanks.ShanksSimulation;
import es.upm.dit.gsi.shanks.agent.SimpleShanksAgent;
import es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.BayesianReasonerShanksAgent;
import es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.ShanksAgentBayesianReasoningCapability;
import es.upm.dit.gsi.shanks.exception.ShanksException;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.solarflare.agent.SolarFlareBayesCentralAgent.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 03/10/2013
 * @version 0.1
 * 
 */
public class SolarFlareBayesCentralAgent extends SimpleShanksAgent implements
		BayesianReasonerShanksAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6542107693613585524L;
	private String bnPath;
	private ProbabilisticNetwork bn;

	/**
	 * Constructor
	 * 
	 * @param id
	 */
	public SolarFlareBayesCentralAgent(String id, String bnPath) {
		super(id);
		this.bnPath = bnPath;
		try {
			ShanksAgentBayesianReasoningCapability.loadNetwork(this);
		} catch (ShanksException e) {
			e.printStackTrace();
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
	public ProbabilisticNetwork getBayesianNetwork() {
		return this.bn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.
	 * BayesianReasonerShanksAgent
	 * #setBayesianNetwork(unbbayes.prs.bn.ProbabilisticNetwork)
	 */
	public void setBayesianNetwork(ProbabilisticNetwork bn) {
		this.bn = bn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.
	 * BayesianReasonerShanksAgent#getBayesianNetworkFilePath()
	 */
	public String getBayesianNetworkFilePath() {
		return this.bnPath;
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

		SolarFlare origflare = (SolarFlare) sim.getScenario()
				.getNetworkElement("OriginalSolarFlare");

		SolarFlare bayesflare = (SolarFlare) sim.getScenario()
				.getNetworkElement("CentralConclusion");

		// Check if it is time to check the solar flare
		if (origflare.getStatus().get(SolarFlare.READY)
				&& !bayesflare.getStatus().get(SolarFlare.READY)) {

			bayesflare.clean();
			bayesflare.setCaseID(origflare.getCaseID());
			
			HashMap<String, String> evidences = new HashMap<String, String>();

			String key = LargestSpotSize.class.getSimpleName();
			String evidence = (String) origflare.getProperty(key);
			evidences.put(key, evidence);

			key = SpotDistribution.class.getSimpleName();
			evidence = (String) origflare.getProperty(key);
			evidences.put(key, evidence);

			key = Activity.class.getSimpleName();
			evidence = (String) origflare.getProperty(key);
			evidences.put(key, evidence);

			key = Evolution.class.getSimpleName();
			evidence = (String) origflare.getProperty(key);
			evidences.put(key, evidence);

			key = PrevStatus24Hour.class.getSimpleName();
			evidence = (String) origflare.getProperty(key);
			evidences.put(key, evidence);

			key = HistComplex.class.getSimpleName();
			evidence = (String) origflare.getProperty(key);
			evidences.put(key, evidence);

			key = BecomeHist.class.getSimpleName();
			evidence = (String) origflare.getProperty(key);
			evidences.put(key, evidence);

			key = Area.class.getSimpleName();
			evidence = (String) origflare.getProperty(key);
			evidences.put(key, evidence);

			key = CNode.class.getSimpleName();
			evidence = (String) origflare.getProperty(key);
			evidences.put(key, evidence);

			key = MNode.class.getSimpleName();
			evidence = (String) origflare.getProperty(key);
			evidences.put(key, evidence);

			key = XNode.class.getSimpleName();
			evidence = (String) origflare.getProperty(key);
			evidences.put(key, evidence);

			try {
				ShanksAgentBayesianReasoningCapability.addEvidences(bn,
						evidences);

				// Get hypothesis
				HashMap<String, Float> hyps = ShanksAgentBayesianReasoningCapability
						.getNodeStatesHypotheses(this,
								SolarFlareType.class.getSimpleName());

				// Update the bayes central solar flare "device"

				for (Entry<String, String> ev : evidences.entrySet()) {
					bayesflare.changeProperty(ev.getKey(), ev.getValue());
				}

				String conclusion = "Unknown";
				float max = (float) 0.0;
				for (Entry<String, Float> hyp : hyps.entrySet()) {
					if (hyp.getValue() > max) {
						conclusion = hyp.getKey();
					}
				}

				bayesflare.changeProperty(SolarFlareType.class.getSimpleName(),
						conclusion);

				bayesflare.setCurrentStatus(SolarFlare.READY, true);

			} catch (ShanksException e) {
				e.printStackTrace();
			}
		}
	}
}
