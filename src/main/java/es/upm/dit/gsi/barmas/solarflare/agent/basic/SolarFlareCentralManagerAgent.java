/**
 * es.upm.dit.gsi.barmas.agent.ArgumentationManagerAgent.java
 */
package es.upm.dit.gsi.barmas.solarflare.agent.basic;

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
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Assumption;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Given;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Proposal;
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
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).finer(
						"Creating new argumentation - ID: "
								+ this.argumentations.size());
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
		Map<Argument, HashMap<Argument, Integer>> graph = argumentation
				.getGraph();
		Set<Argument> args = graph.keySet();
		Map<Argument, Boolean> undefeated = new HashMap<Argument, Boolean>();
		for (Argument a : args) {
			undefeated.put(a, true);
		}
		for (Entry<Argument, HashMap<Argument, Integer>> e : graph.entrySet()) {
			HashMap<Argument, Integer> attacks = e.getValue();
			for (Argument a : attacks.keySet()) {
				if (attacks.get(a) != 0) {
					undefeated.put(a, false);
				}
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

		File f = new File(argumentationDir + File.separator + "argumentation"
				+ currentArgumentation.getId());
		if (!f.isDirectory()) {
			boolean made = f.mkdir();
			if (!made) {
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(
						"Impossible to create argumentation directory. -> Argumentation: "
								+ currentArgumentation.getId());
			}
		}
		// f = new File(argumentationDir + File.separator + "argumentation"
		// + currentArgumentation.getId() + File.separator + "arguments");
		// if (!f.isDirectory()) {
		// boolean made = f.mkdir();
		// if (!made) {
		// Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(
		// "Impossible to create arguments directory. -> Argumentation: "
		// + currentArgumentation.getId());
		// }
		// }

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

			String generalArgumentationsFile = this.outputDir + File.separator
					+ "allArgumentations.csv";
			f = new File(generalArgumentationsFile);
			CsvWriter generalWriter = null;
			if (!f.exists()) {
				generalWriter = new CsvWriter(new FileWriter(
						generalArgumentationsFile), ',');
				generalWriter.writeRecord(newHeaders);
				generalWriter.flush();
			} else {
				generalWriter = new CsvWriter(new FileWriter(
						generalArgumentationsFile, true), ',');
			}

			CsvWriter concreteWriter = null;

			String concreteArgumentationFile = argumentationDir
					+ File.separator + "argumentation"
					+ currentArgumentation.getId() + File.separator
					+ "argumentation-" + currentArgumentation.getId() + ".csv";
			f = new File(concreteArgumentationFile);
			if (!f.exists()) {
				concreteWriter = new CsvWriter(new FileWriter(
						concreteArgumentationFile), ',');
				concreteWriter.writeRecord(newHeaders);
				concreteWriter.flush();
			} else {
				concreteWriter = new CsvWriter(new FileWriter(
						concreteArgumentationFile, true), ',');
			}

			int argNums = currentArgumentation.getArgumentsWithID().size();
			for (int argNum = 0; argNum < argNums; argNum++) {
				Argument arg = currentArgumentation.getArgumentsWithID().get(
						argNum);
				String[] data = new String[newHeaders.length];
				data[0] = Integer.toString(currentArgumentation.getId());
				data[1] = Integer.toString(arg.getId());
				data[2] = arg.getProponent().getProponentName();
				data[3] = Long.toString(arg.getStep());
				data[4] = Long.toString(arg.getTimestamp());
				generalWriter.writeRecord(data);
				generalWriter.flush();
				concreteWriter.writeRecord(data);
				concreteWriter.flush();
			}

			generalWriter.close();
			concreteWriter.close();

			// Write the graph
			HashMap<Argument, HashMap<Argument, Integer>> graph = currentArgumentation
					.getGraph();
			String graphFile = argumentationDir + File.separator
					+ "argumentation" + currentArgumentation.getId()
					+ File.separator + "graph-argumentation-"
					+ currentArgumentation.getId() + ".csv";
			CsvWriter graphWriter = null;
			String[] graphHeaders = new String[currentArgumentation
					.getArgumentsWithID().size() + 2];
			graphHeaders[0] = "ProponentID";
			graphHeaders[1] = "ArgID";
			for (int aux = 2; aux < graphHeaders.length; aux++) {
				graphHeaders[aux] = "Arg" + (aux - 2);
			}
			f = new File(graphFile);
			if (!f.exists()) {
				graphWriter = new CsvWriter(new FileWriter(graphFile), ',');
				graphWriter.writeRecord(graphHeaders);
				graphWriter.flush();
			} else {
				graphWriter = new CsvWriter(new FileWriter(graphFile, true),
						',');
			}

			for (int aux = 2; aux < graphHeaders.length; aux++) {
				String[] graphData = new String[graphHeaders.length];
				graphData[0] = currentArgumentation.getArgumentsWithID().get(
						aux - 2).getProponent().getProponentName();
				graphData[1] = Integer.toString(aux - 2);
				Argument arg = currentArgumentation.getArgumentsWithID().get(
						aux - 2);
				HashMap<Argument, Integer> attacks = graph.get(arg);
				for (int aux2 = 0; aux2 < graphHeaders.length - 2; aux2++) {
					Argument arg2 = currentArgumentation.getArgumentsWithID()
							.get(aux2);
					graphData[aux2 + 2] = attacks.get(arg2).toString();
				}
				graphWriter.writeRecord(graphData);
				graphWriter.flush();
			}

			graphWriter.close();

			// Arguments

			for (int aux = 0; aux < currentArgumentation.getArgumentsWithID()
					.size(); aux++) {
				Argument argument = currentArgumentation.getArgumentsWithID()
						.get(aux);
				FileWriter fw = new FileWriter(this.argumentationDir
						+ File.separator + "argumentation"
						+ currentArgumentation.getId() + File.separator
						+ "arguments-argumentation-"
						+ currentArgumentation.getId() + ".info", true);
				// FileWriter fw = new FileWriter(this.argumentationDir
				// + File.separator + "argumentation"
				// + currentArgumentation.getId() + File.separator
				// + "arguments" + File.separator + "argument"
				// + argument.getId() + ".info");
				fw.write("Argumentation: "
						+ currentArgumentation.getId()
						+ " - Argument: "
						+ argument.getId()
						+ "\nProponent: "
						+ argument.getProponent().getProponentName()
						+ "\n\nStep: "
						+ argument.getStep()
						+ " - Timestamp: "
						+ argument.getTimestamp() + "\n\n");
				fw.flush();
				fw.write("\tGivens:\n\tQuantity:" + argument.getGivens().size()
						+ "\n");
				fw.flush();
				for (Given given : argument.getGivens()) {
					fw.write("\t\tNode: " + given.getNode() + "\n\t\t\tValue: "
							+ given.getValue() + "\n");
					fw.flush();
				}
				fw.write("\n\tAssumptions:\n\tQuantity:"
						+ argument.getAssumptions().size() + "\n");
				fw.flush();
				for (Assumption assump : argument.getAssumptions()) {
					fw.write("\t\tNode: " + assump.getNode() + "\n");
					fw.flush();
					for (Entry<String, Double> entry : assump
							.getValuesWithConfidence().entrySet()) {
						fw.write("\t\t\tValue: " + entry.getKey()
								+ " - Confidene: " + entry.getValue() + "\n");
						fw.flush();
					}
				}
				fw.write("\n\tProposal:\n\tQuantity:"
						+ argument.getProposals().size() + "\n");
				fw.flush();
				for (Proposal proposal : argument.getProposals()) {
					fw.write("\t\tNode: " + proposal.getNode() + "\n");
					fw.flush();
					for (Entry<String, Double> entry : proposal
							.getValuesWithConfidence().entrySet()) {
						fw.write("\t\t\tValue: " + entry.getKey()
								+ " - Confidene: " + entry.getValue() + "\n");
						fw.flush();
					}
				}
				fw.write("\n\n");
				fw.flush();
				fw.close();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Resolution conflicts method
	 * 
	 * @param argumentation
	 */
	private void getHigherHypothesis(Argumentation argumentation) {
		
		HashMap<Argument, HashMap<Argument, Integer>> graph = argumentation.getGraph();
		List<Argument> possibleConclusions = new ArrayList<Argument>();
		possibleConclusions.addAll(argumentation.getArguments());
		for (Argument arg : argumentation.getArguments()) {
			HashMap<Argument, Integer> attacks = graph.get(arg);
			for (Argument attacked : attacks.keySet()) {
				int attackType = attacks.get(attacked);
				if (attackType==1) {
					possibleConclusions.remove(attacked);
				}
			}
		}

		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		int maxEvidences = 0;
		for (Argument arg : argumentation.getArguments()) {
			int evCardinal = arg.getGivens().size();
			if (evCardinal > maxEvidences) {
				maxEvidences = evCardinal;
			}
		}
		// Pick possible arguments
		String hyp = "";
		double max = 0;
		Argument argumentConclusion = null;
		for (Argument arg : possibleConclusions) {
			if (arg.getGivens().size() == maxEvidences) {
				for (Proposal p : arg.getProposals()) {
					if (p.getMaxValue()>max) {
						max=p.getMaxValue();
						hyp=p.getMaxState();
						argumentConclusion = arg;
					}
				}
			}
		}

		logger.fine("Argumentation Manager --> Higher hypothesis found: " + hyp
				+ " - " + max);
		
		argumentation.getConclusions().add(argumentConclusion);
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
		argumentation.addArgument(arg);
		this.updateArgumentationGraph(arg, argumentation);
	}

	/**
	 * @param arg
	 * @param argumentation
	 */
	private void updateArgumentationGraph(Argument arg,
			Argumentation argumentation) {
		for (Argument a : argumentation.getArguments()) {
			int attack = this.getAttackType(arg, a);
			if (attack == -1) {
				Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
				logger.severe("INCOHERENCE IN EVIDENCES IN ARGUMENTATION "
						+ argumentation.getId());
				logger.severe("Incoherence between arguments " + arg.getId()
						+ " and argument: " + a.getId());
			}
			argumentation.getGraph().get(arg).put(a, attack);
			attack = this.getAttackType(a, arg);
			if (attack == -1) {
				Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
				logger.severe("INCOHERENCE IN EVIDENCES IN ARGUMENTATION "
						+ argumentation.getId());
				logger.severe("Incoherence between arguments " + arg.getId()
						+ " and argument: " + a.getId());
			}
			argumentation.getGraph().get(a).put(arg, attack);
		}
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

	/**
	 * Return the type of the attack (from a to b) using the following rules:
	 * 
	 * 0 - a does not attack b
	 * 
	 * 1 - a is defeater of b if Claim(A) implies not all Support(B)
	 * 
	 * 2 - a is a direct defeater of b if there is phi in Support(B) such that
	 * Claim(A) implies not phi
	 * 
	 * 3 - a is a undercut of b if there is Phi subset of Support(B) such that
	 * Claim(A) is exactly not all Phi
	 * 
	 * 4 - a is a direct undercut of b if there is phi in Support(B) such that
	 * Claim(A) is exactly not phi
	 * 
	 * 5 - a is a canonical undercut of b if Claim(A) is exactly not Support(B)
	 * 
	 * 6 - a is a rebuttal of b if Claim(A) is exactly not Claim(B)
	 * 
	 * 7 - a is a defeating rebuttal of b if Claim(A) implies not Claim(B)
	 * 
	 * @param a
	 * @param b
	 * @return 0-7 attack type
	 */
	private int getAttackType(Argument a, Argument b) {

		// In the code (Claim = Givens + Proposal) and (Support = Givens +
		// Assumptions)
		// Check if argument a attacks b:

		if (!b.equals(a)) {
			Set<Given> agivens = a.getGivens();
			Set<Given> bgivens = b.getGivens();
			Set<Assumption> aassumptions = a.getAssumptions();
			Set<Assumption> bassumptions = b.getAssumptions();
			Set<Proposal> aproposals = a.getProposals();
			Set<Proposal> bproposals = b.getProposals();

			// Type -1 if evidences are not coherent
			for (Given bgiven : bgivens) {
				for (Given agiven : agivens) {
					if (agiven.getNode().equals(bgiven.getNode())) {
						if (!agiven.getValue().equals(bgiven.getValue())) {
							Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(
									"Incoherent evidence in arguments");
							return -1; // This has no sense!!
						}
					}
				}
			}

			// Check type 1 - a is defeater of b if Claim(A) implies not all
			// Support(B)
			if (agivens.size() > bgivens.size()) {
				return 1;
			}

			// Check type 2 - a is a direct defeater of b if there is phi in
			// Support(B) such that Claim(A) implies not phi

			// Check type 3 - a is a undercut of b if there is Phi subset of
			// Support(B) such that Claim(A) is exactly not all Phi

			// Check type 4 - a is a direct undercut of b if there is phi in
			// Support(B) such that Claim(A) is exactly not phi

			// Check type 5 - a is a canonical undercut of b if Claim(A) is
			// exactly
			// not Support(B)

			// Check types 6 and 7
			int aux = 0;
			for (Proposal bp : bproposals) {
				for (Proposal ap : aproposals) {
					String anode = bp.getNode();
					String node = ap.getNode();
					// If the proposed node are equals...
					if (node.equals(anode)) {
						String astate = ap.getMaxState();
						String bstate = bp.getMaxState();
						// if they don't aggree with the state
						if (!astate.equals(bstate)) {
							aux++;
						}
					}
				}
			}
			if (aux == bproposals.size()) {
				// Check type 6 - a is a rebuttal of b if Claim(A) is exactly
				// not
				// Claim(B)
				return 6;
			} else if (aux > 0) {
				// Check type 7 - a is a defeating rebuttal of b if Claim(A)
				// implies not
				// Claim(B)
				return 7;
			}

		}

		// If not... a does not attack b (Type 0)
		return 0;
	}
}
