/**
 * es.upm.dit.gsi.barmas.agent.capability.argumentation.ArgumentativeAgent.java
 */
package es.upm.dit.gsi.barmas.agent.capability.argumentation;

import jason.asSemantics.Message;

import java.util.Set;

import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argument;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.ArgumentationManagerAgent;
import es.upm.dit.gsi.shanks.exception.ShanksException;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.agent.capability.argumentation.ArgumentativeAgent.java
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
public interface ArgumentativeAgent {

	/**
	 * @return the name of the
	 */
	public String getProponentName();

	/**
	 * @return the agent itself
	 */
	public ArgumentativeAgent getProponent();

	/**
	 * @return return the argumentation manager agent
	 */
	public ArgumentationManagerAgent getArgumentationManager();

	/**
	 * @return the name of the argumentation manager agent
	 */
	public String getArgumentationManagerName();

	/**
	 * @return all its arguments
	 */
	public Set<Argument> getCurrentArguments() throws ShanksException;

	/**
	 * @param args
	 */
	public void updateBeliefsWithNewArguments(Set<Argument> args) throws ShanksException;

	/**
	 * Send the message
	 * 
	 * @param m
	 */
	public void send(Message m);

}
