/**
 * es.upm.dit.gsi.barmas.agent.ArgumentationManagerAgent.java
 */
package es.upm.dit.gsi.barmas.solarflare.agent;

import jason.asSemantics.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import es.upm.dit.gsi.barmas.agent.capability.argumentation.ArgumentativeAgent;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argument;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Given;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Proposal;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.AgentArgumentationManagerCapability;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.Argumentation;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.ArgumentationManagerAgent;
import es.upm.dit.gsi.barmas.solarflare.model.SolarFlare;
import es.upm.dit.gsi.barmas.solarflare.model.scenario.SolarFlareScenario;
import es.upm.dit.gsi.shanks.ShanksSimulation;
import es.upm.dit.gsi.shanks.agent.SimpleShanksAgent;
import es.upm.dit.gsi.shanks.exception.ShanksException;

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
public class SolarFlareCentralManagerAgent extends SimpleShanksAgent implements
		ArgumentationManagerAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4553845190556382890L;

	private List<ArgumentativeAgent> suscribers;
	private List<Argumentation> argumentations;

	private boolean idle;
	private int idleSteps;

	private List<Argument> pendingArguments;

	/**
	 * Constructor
	 * 
	 * @param id
	 */
	public SolarFlareCentralManagerAgent(String id) {
		super(id);
		this.suscribers = new ArrayList<ArgumentativeAgent>();
		this.argumentations = new ArrayList<Argumentation>();
		this.idle = true;
		this.pendingArguments = new ArrayList<Argument>();
		this.idleSteps = 0;
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
			if (this.getCurrentArgumentation()==null) {
				Argumentation argumentation = new Argumentation(
						this.argumentations.size());
				this.argumentations.add(argumentation);
			}

			this.pendingArguments = new ArrayList<Argument>();
			for (Message msg : inbox) {
				Argument arg = (Argument) msg.getPropCont();
				this.pendingArguments.add(arg);
			}
		} else {
			// If no message is received, the argumentation finish
		}

		inbox.clear();
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
			this.busy();
			for (Argument arg : pendingArguments) {
				this.processNewArgument(arg, simulation);
			}
			this.pendingArguments.clear();
		} else if (this.idle && this.getCurrentArgumentation() != null
				&& this.getCurrentArgumentation().isFinished() == false) {
			this.busy();
			Argumentation a = this.getCurrentArgumentation();
			this.finishCurrentArgumentation();
			simulation.getLogger()
					.info("Argumentation Manager: Finishing argumentation...");
			for (ArgumentativeAgent s : this.suscribers) {
				s.finishArgumenation();
			}
			this.updateSolarFlare(a, simulation);
		} else {
			this.idle();
			simulation.getLogger()
					.fine("Argumentation Manager: Nothing to do. IDLE STEPS: "
							+ this.idleSteps);
		}

	}

	/**
	 * @param a
	 * @param simulation
	 */
	private void updateSolarFlare(Argumentation a, ShanksSimulation simulation) {
		SolarFlare argflare = (SolarFlare) simulation.getScenario()
				.getNetworkElement(SolarFlareScenario.ARGUMENTATIONCONCLUSION);
		try {
			for (Argument arg : a.getConclusions()) {
				for (Given g : arg.getGivens()) {
					argflare.changeProperty(g.getNode(), g.getValue());
				}
				for (Proposal p : arg.getProposals()) {
					String node = p.getNode();
					String state = "";
					double max = 0;
					for (Entry<String, Double> e : p.getValuesWithConfidence()
							.entrySet()) {
						if (e.getValue() > max) {
							max = e.getValue();
							state = e.getKey();
						}
					}
					argflare.changeProperty(node, state);
				}
			}
			argflare.setCurrentStatus(SolarFlare.READY, true);
		} catch (ShanksException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private void busy() {
		this.idle = false;
		this.idleSteps = 0;
	}

	/**
	 * 
	 */
	private void idle() {
		this.idle = true;
		this.idleSteps++;
	}

	/**
	 * 
	 */
	private void finishCurrentArgumentation() {
		Argumentation argumentation = this.getCurrentArgumentation();
		Map<Argument, List<Argument>> graph = argumentation.getGraph();
		Set<Argument> args = graph.keySet();
		Map<Argument, Boolean> undefeated = new HashMap<Argument, Boolean>();
		for (Argument a : args) {
			undefeated.put(a, true);
		}
		for (Entry<Argument, List<Argument>> e : graph.entrySet()) {
			List<Argument> attacks = e.getValue();
			for (Argument a : attacks) {
				undefeated.put(a, false);
			}
		}

		List<Argument> conclusions = argumentation.getConclusions();
		for (Entry<Argument, Boolean> e : undefeated.entrySet()) {
			if (e.getValue()) {
				conclusions.add(e.getKey());
			}
		}

		argumentation.setFinished(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.
	 * ArgumentationManagerAgent#getCurrentArgumentation()
	 */
	public Argumentation getCurrentArgumentation() {
		int last = this.argumentations.size();
		if (last > 0) {
			Argumentation arg = this.argumentations.get(last - 1);
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
		return this.argumentations;
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
		for (Argumentation arg : this.argumentations) {
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
