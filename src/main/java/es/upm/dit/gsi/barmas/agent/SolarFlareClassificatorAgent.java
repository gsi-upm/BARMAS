/**
 * es.upm.dit.gsi.barmas.agent.BarmasAgent.java
 */
package es.upm.dit.gsi.barmas.agent;

import unbbayes.prs.bn.ProbabilisticNetwork;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.ArgumentativeAgent;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.ArgumentationManagerAgent;
import es.upm.dit.gsi.shanks.ShanksSimulation;
import es.upm.dit.gsi.shanks.agent.SimpleShanksAgent;
import es.upm.dit.gsi.shanks.agent.capability.movement.Location;
import es.upm.dit.gsi.shanks.agent.capability.perception.PercipientShanksAgent;
import es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.BayesianReasonerShanksAgent;

/**
 * Project: barmas
 * File: es.upm.dit.gsi.barmas.agent.BarmasAgent.java
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
public class SolarFlareClassificatorAgent extends SimpleShanksAgent implements BayesianReasonerShanksAgent, ArgumentationManagerAgent, ArgumentativeAgent, PercipientShanksAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8582918551821278046L;

	/**
	 * Constructor
	 *
	 * @param id
	 */
	public SolarFlareClassificatorAgent(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.agent.ShanksAgent#checkMail()
	 */
	public void checkMail() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.agent.SimpleShanksAgent#executeReasoningCycle(es.upm.dit.gsi.shanks.ShanksSimulation)
	 */
	@Override
	public void executeReasoningCycle(ShanksSimulation simulation) {
		// TODO Auto-generated method stub

	}

	public ProbabilisticNetwork getBayesianNetwork() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setBayesianNetwork(ProbabilisticNetwork bn) {
		// TODO Auto-generated method stub
		
	}

	public String getBayesianNetworkFilePath() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getPerceptionRange() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Location getCurrentLocation() {
		// TODO Auto-generated method stub
		return null;
	}

}
