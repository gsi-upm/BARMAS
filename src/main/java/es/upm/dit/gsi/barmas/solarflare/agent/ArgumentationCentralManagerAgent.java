/**
 * es.upm.dit.gsi.barmas.agent.ArgumentationManagerAgent.java
 */
package es.upm.dit.gsi.barmas.solarflare.agent;

import jason.asSemantics.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import es.upm.dit.gsi.barmas.agent.capability.argumentation.ArgumentativeAgent;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argument;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.AgentArgumentationManagerCapability;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.Argumentation;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.ArgumentationManagerAgent;
import es.upm.dit.gsi.shanks.ShanksSimulation;
import es.upm.dit.gsi.shanks.agent.SimpleShanksAgent;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.agent.ArgumentationManagerAgent.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 02/10/2013
 * @version 0.1
 * 
 */
public class ArgumentationCentralManagerAgent extends SimpleShanksAgent
		implements ArgumentationManagerAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4553845190556382890L;

	private List<ArgumentativeAgent> suscribers;
	private List<Argumentation> argumenations;

	private boolean idle;

	private List<Argument> pendingArguments;

	/**
	 * Constructor
	 * 
	 * @param id
	 */
	public ArgumentationCentralManagerAgent(String id) {
		super(id);
		this.suscribers = new ArrayList<ArgumentativeAgent>();
		this.argumenations = new ArrayList<Argumentation>();
		this.idle = true;
		this.pendingArguments = new ArrayList<Argument>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.shanks.agent.ShanksAgent#checkMail()
	 */
	public void checkMail() {
		// Check incoming new argumentation
		List<Message> inbox = this.getInbox();
		if (inbox.size() > 0) {
			if (this.idle) {
				this.idle = false;
				Argumentation argumentation = new Argumentation(
						this.argumenations.size());
				this.argumenations.add(argumentation);
			}

			this.pendingArguments = new ArrayList<Argument>();
			for (Message msg : inbox) {
				HashSet<Argument> args = (HashSet<Argument>) msg.getPropCont();
				for (Argument arg : args) {
					this.pendingArguments.add(arg);	
				}
			}
		} else {
			// If no message is received, the argumentation finish
			this.idle = true;
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
		if (this.pendingArguments.size() > 0) {
			for (Argument arg : pendingArguments) {
				this.processNewArgument(arg, simulation);
			}
		} else if (this.idle
				&& this.getCurrentArgumentation()!=null && this.getCurrentArgumentation().isFinished() == false) {
			AgentArgumentationManagerCapability
					.finishCurrentArgumentation(this);
		} else {
			simulation.getScenarioManager().logger
					.info("Argumentation Manager: Nothing to do.");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.
	 * ArgumentationManagerAgent#getCurrentArgumentation()
	 */
	public Argumentation getCurrentArgumentation() {
		int last = this.argumenations.size();
		if (last > 0) {
			Argumentation arg = this.argumenations.get(last - 1);
			if (!arg.isFinished()) {
				return arg;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.
	 * ArgumentationManagerAgent#getArgumentations()
	 */
	public List<Argumentation> getArgumentations() {
		return this.argumenations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.
	 * ArgumentationManagerAgent
	 * #processNewArgument(es.upm.dit.gsi.barmas.agent.capability
	 * .argumentation.bayes.Argument)
	 */
	public void processNewArgument(Argument arg, ShanksSimulation simulation) {
		Argumentation argumentation = this.getCurrentArgumentation();
		argumentation.addArgument(arg, simulation);
		AgentArgumentationManagerCapability.broadcastArgument(this, arg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.
	 * ArgumentationManagerAgent#getArgumentation(int)
	 */
	public Argumentation getArgumentation(int id) {
		for (Argumentation arg : this.argumenations) {
			if (arg.getId() == id) {
				return arg;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.
	 * ArgumentationManagerAgent
	 * #addSubscriber(es.upm.dit.gsi.barmas.agent.capability
	 * .argumentation.ArgumentativeAgent)
	 */
	public void addSubscriber(ArgumentativeAgent agent) {
		this.suscribers.add(agent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.
	 * ArgumentationManagerAgent
	 * #removeSubscriber(es.upm.dit.gsi.barmas.agent.capability
	 * .argumentation.ArgumentativeAgent)
	 */
	public void removeSubscriber(ArgumentativeAgent agent) {
		this.suscribers.remove(agent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.
	 * ArgumentationManagerAgent#getSubscribers()
	 */
	public List<ArgumentativeAgent> getSubscribers() {
		return this.suscribers;
	}

}
