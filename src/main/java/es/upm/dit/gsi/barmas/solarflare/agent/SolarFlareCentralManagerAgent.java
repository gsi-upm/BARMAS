/**
 * es.upm.dit.gsi.barmas.agent.ArgumentationManagerAgent.java
 */
package es.upm.dit.gsi.barmas.solarflare.agent;

import jason.asSemantics.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.csvreader.CsvWriter;

import es.upm.dit.gsi.barmas.agent.capability.argumentation.ArgumentativeAgent;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argument;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Given;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Proposal;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.Argumentation;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.ArgumentationManagerAgent;
import es.upm.dit.gsi.barmas.solarflare.model.SolarFlare;
import es.upm.dit.gsi.barmas.solarflare.model.scenario.SolarFlareScenario;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.SolarFlareType;
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
		ArgumentationManagerAgent, ArgumentativeAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4553845190556382890L;

	private List<ArgumentativeAgent> suscribers;
	private List<Argumentation> argumentations;

	private boolean idle;
	private int idleSteps;

	private List<Argument> pendingArguments;

	private String outputDir;
	private String argumentationDir;

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param outputDir
	 */
	public SolarFlareCentralManagerAgent(String id, String outputDir) {
		super(id);
		this.suscribers = new ArrayList<ArgumentativeAgent>();
		this.argumentations = new ArrayList<Argumentation>();
		this.idle = true;
		this.pendingArguments = new ArrayList<Argument>();
		this.idleSteps = 0;
		this.outputDir = outputDir;
		this.argumentationDir = this.outputDir + File.separator
				+ "argumentation";

		// Create folders
		File f = new File(argumentationDir);
		if (!f.isDirectory()) {
			boolean made = f.mkdir();
			if (!made) {
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(
						"Impossible to create argumentation directory");
			}
		}
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
			if (this.getCurrentArgumentation() == null) {
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
			simulation.getLogger().info(
					"Argumentation Manager: Finishing argumentation...");
			for (ArgumentativeAgent s : this.suscribers) {
				s.finishArgumenation();
			}
			this.updateSolarFlare(a, simulation);
		} else {
			this.idle();
			simulation.getLogger().fine(
					"Argumentation Manager: Nothing to do. IDLE STEPS: "
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
					Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
							"Argumentative agents concludes that " + node
									+ " - " + state + " with confidence: "
									+ max);
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

		// Add undefeated arguments
		List<Argument> conclusions = argumentation.getConclusions();
		for (Entry<Argument, Boolean> e : undefeated.entrySet()) {
			if (e.getValue()) {
				conclusions.add(e.getKey());
			}
		}

		if (conclusions.size() == 0) {
			// If no conclusions...
			this.getHigherHypothesis(argumentation);
		} else {
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
					"Argumentation Manager--> Found undefeated arguments. Count: "
							+ conclusions.size());
		}

		this.argumentation2File(this.getCurrentArgumentation());
		argumentation.setFinished(true);
	}

	/**
	 * @param currentArgumentation
	 */
	private void argumentation2File(Argumentation currentArgumentation) {
		

//		File f = new File(argumentationDir + File.separator
//				+ "argumentation" + argumentation.getId());
//		if (!f.isDirectory()) {
//			boolean made = f.mkdir();
//			if (!made) {
//				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(
//						"Impossible to create argumentation directory. -> Argumentation: "
//								+ argumentation.getId());
//			}
//		}
//		f = new File(argumentationDir + File.separator
//				+ "argumentation" + argumentation.getId()
//				+ File.separator + "arguments");
//		if (!f.isDirectory()) {
//			boolean made = f.mkdir();
//			if (!made) {
//				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(
//						"Impossible to create arguments directory. -> Argumentation: "
//								+ argumentation.getId());
//			}
//		}
		
		
		
		// Write argumentation general info in csv
		try {
			List<String> headers = new ArrayList<String>();

			headers.add("ArgumentationID");
			headers.add("ArgumentID");
			headers.add("ProponentID");
			headers.add("Step");
			headers.add("Timestamp");
			int size = headers.size();
			String[] newHeaders = new String[size];
			int i = 0;
			for (String header : headers) {
				newHeaders[i++] = header;
			}

			File f = new File(this.outputDir + File.separator
					+ "argumentations.csv");
			CsvWriter writer = null;
			if (!f.exists()) {
				writer = new CsvWriter(new FileWriter(this.outputDir
						+ File.separator + "argumentations.csv"), ',');
				writer.writeRecord(newHeaders);
				writer.flush();
			} else {
				writer = new CsvWriter(new FileWriter(this.outputDir
						+ File.separator + "argumentations.csv", true), ',');
			}

			for (Argument arg : currentArgumentation.getArgumentsWithSteps()
					.keySet()) {
				String[] data = new String[newHeaders.length];
				data[0] = Integer.toString(currentArgumentation.getId());
				data[1] = Integer.toString(arg.getId());
				data[2] = arg.getProponent().getProponentName();
				data[3] = Long.toString(currentArgumentation
						.getArgumentsWithSteps().get(arg));
				data[4] = Long.toString(currentArgumentation
						.getArgumentsWithTimestamps().get(arg));
				writer.writeRecord(data);
				writer.flush();
			}

			writer.close();

//			for (Argument argument : currentArgumentation
//					.getArgumentsWithSteps().keySet()) {
//				FileWriter fw = new FileWriter(this.argumentationDir
//						+ File.separator + "argumentation"
//						+ currentArgumentation.getId() + File.separator
//						+ "arguments" + File.separator + "argument"
//						+ argument.getId() + ".info");
//				fw.write("Argumentation: " + currentArgumentation.getId()
//						+ " - Argument: " + argument.getId() + " - Proponent: "
//						+ argument.getProponent().getProponentName());
//				fw.close();
//			}
			
			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// TODO write graph
		// TODO write argumentation info
		// TODO write all arguments 2 file
	}

	/**
	 * Resolution conflicts method
	 * 
	 * @param argumentation
	 */
	private void getHigherHypothesis(Argumentation argumentation) {

		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		int maxEvidences = 0;
		for (Argument arg : argumentation.getArgumentsWithSteps().keySet()) {
			int evCardinal = arg.getGivens().size();
			if (evCardinal > maxEvidences) {
				maxEvidences = evCardinal;
			}
		}
		// Pick possible arguments
		String hyp = "";
		double max = 0;
		for (Argument arg : argumentation.getArgumentsWithSteps().keySet()) {
			if (arg.getGivens().size() == maxEvidences) {
				for (Proposal p : arg.getProposals()) {
					for (Entry<String, Double> e : p.getValuesWithConfidence()
							.entrySet()) {
						if (e.getValue() >= max) {
							max = e.getValue();
							hyp = e.getKey();
						}
					}
				}
			}
		}

		logger.fine("Argumentation Manager --> Higher hypothesis found: " + hyp
				+ " - " + max);

		Argument a = new Argument();
		for (Argument arg : argumentation.getArgumentsWithSteps().keySet()) {
			if (arg.getGivens().size() == maxEvidences) {
				for (Given g : arg.getGivens()) {
					a.addGiven(g);
				}
				break;
			}
		}
		HashMap<String, Double> beliefs = new HashMap<String, Double>();
		beliefs.put(hyp, max);
		Proposal proposal = new Proposal(SolarFlareType.class.getSimpleName(),
				beliefs);
		a.addProposal(proposal);

		argumentation.getConclusions().add(a);
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
		agent.addArgumentationGroupMember(this);
		for (ArgumentativeAgent ag : this.getSubscribers()) {
			ag.addArgumentationGroupMember(agent);
			agent.addArgumentationGroupMember(ag);
		}
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
		for (ArgumentativeAgent ag : this.getSubscribers()) {
			ag.removeArgumentationGroupMember(agent);
		}
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

	public String getProponentName() {
		return this.getID();
	}

	public ArgumentativeAgent getProponent() {
		// TODO Auto-generated method stub
		return null;
	}

	public ArgumentationManagerAgent getArgumentationManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setArgumentationManager(ArgumentationManagerAgent manager) {
		// TODO Auto-generated method stub

	}

	public String getArgumentationManagerName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Argument> getCurrentArguments() throws ShanksException {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateBeliefsWithNewArguments(Set<Argument> args)
			throws ShanksException {
		// TODO Auto-generated method stub

	}

	public void sendArgument(Argument arg) {
		// TODO Auto-generated method stub

	}

	public void finishArgumenation() {
		// TODO Auto-generated method stub

	}

	public void addArgumentationGroupMember(ArgumentativeAgent agent) {
		// TODO Auto-generated method stub

	}

	public void removeArgumentationGroupMember(ArgumentativeAgent agent) {
		// TODO Auto-generated method stub

	}

}
