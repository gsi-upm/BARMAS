/**
 * es.upm.dit.gsi.barmas.agent.ArgumentationManagerAgent.java
 */
package es.upm.dit.gsi.barmas.solarflare.agent.assumptions;

import jason.asSemantics.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.csvreader.CsvWriter;

import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.AgentArgumentativeCapability;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argument;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argumentation;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.ArgumentativeAgent;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Assumption;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Given;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Proposal;
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
public class AdvancedCentralManagerAgent extends SimpleShanksAgent implements
		ArgumentativeAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4553845190556382890L;

	private List<ArgumentativeAgent> suscribers;
	private List<Argumentation> argumentations;

	private List<Argument> pendingArguments;

	private String outputDir;
	private String argumentationDir;

	// STATES
	private boolean IDLE;
	private boolean PROCESSING;
	private boolean WAITING;
	private boolean FINISHING;

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param outputDir
	 */
	public AdvancedCentralManagerAgent(String id, String outputDir) {
		super(id);
		this.suscribers = new ArrayList<ArgumentativeAgent>();
		this.argumentations = new ArrayList<Argumentation>();
		this.pendingArguments = new ArrayList<Argument>();
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

		this.goToIdle();

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
			this.goToProcessing();
			this.goToWaiting();
		} else if (this.WAITING) {
			Argumentation a = this.getCurrentArgumentation();
			this.goToFinishing();
			this.updateSolarFlare(a, simulation);
		}

		this.pendingArguments.clear();
	}

	/**
	 * 
	 */
	private void processPendingArguments() {

		if (this.getCurrentArgumentation() == null) {
			Argumentation argumentation = new Argumentation(
					this.argumentations.size());
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).finer(
					"Creating new argumentation - ID: "
							+ this.argumentations.size());
			this.argumentations.add(argumentation);
		}

		for (Argument arg : pendingArguments) {
			this.registerNewArgument(arg);
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
	private void finishCurrentArgumentation() {
		Argumentation argumentation = this.getCurrentArgumentation();
		AgentArgumentativeCapability.addConclusionHigherHypothesis(argumentation);
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

		try {
			// Write argumentation general info in csv
			this.writeArgumentationInCSVFile(currentArgumentation);
			// Write the graph
			this.writeGraphInCSVFile(currentArgumentation);
			// Write arguments
			this.writeArgumentsInFriendlyFormat(currentArgumentation);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param currentArgumentation
	 * @throws IOException
	 */
	private void writeArgumentsInFriendlyFormat(
			Argumentation currentArgumentation) throws IOException {

		for (int aux = 0; aux < currentArgumentation.getArgumentsWithID()
				.size(); aux++) {
			Argument argument = currentArgumentation.getArgumentsWithID().get(
					aux);
			FileWriter fw = new FileWriter(this.argumentationDir
					+ File.separator + "argumentation"
					+ currentArgumentation.getId() + File.separator
					+ "arguments-argumentation-" + currentArgumentation.getId()
					+ ".info", true);
			fw.write("Argumentation: " + currentArgumentation.getId()
					+ " - Argument: " + argument.getId() + "\nProponent: "
					+ argument.getProponent().getProponentName() + "\n\nStep: "
					+ argument.getStep() + " - Timestamp: "
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

	}

	/**
	 * @param currentArgumentation
	 * @throws IOException
	 */
	private void writeGraphInCSVFile(Argumentation currentArgumentation)
			throws IOException {
		HashMap<Argument, HashMap<Argument, Integer>> graph = currentArgumentation
				.getGraph();
		String graphFile = argumentationDir + File.separator + "argumentation"
				+ currentArgumentation.getId() + File.separator
				+ "graph-argumentation-" + currentArgumentation.getId()
				+ ".csv";
		CsvWriter graphWriter = null;
		String[] graphHeaders = new String[currentArgumentation
				.getArgumentsWithID().size() + 2];
		graphHeaders[0] = "ProponentID";
		graphHeaders[1] = "ArgID";
		for (int aux = 2; aux < graphHeaders.length; aux++) {
			graphHeaders[aux] = "Arg" + (aux - 2);
		}
		File f = new File(graphFile);
		if (!f.exists()) {
			graphWriter = new CsvWriter(new FileWriter(graphFile), ',');
			graphWriter.writeRecord(graphHeaders);
			graphWriter.flush();
		} else {
			graphWriter = new CsvWriter(new FileWriter(graphFile, true), ',');
		}

		for (int aux = 2; aux < graphHeaders.length; aux++) {
			String[] graphData = new String[graphHeaders.length];
			graphData[0] = currentArgumentation.getArgumentsWithID()
					.get(aux - 2).getProponent().getProponentName();
			graphData[1] = Integer.toString(aux - 2);
			Argument arg = currentArgumentation.getArgumentsWithID().get(
					aux - 2);
			HashMap<Argument, Integer> attacks = graph.get(arg);
			for (int aux2 = 0; aux2 < graphHeaders.length - 2; aux2++) {
				Argument arg2 = currentArgumentation.getArgumentsWithID().get(
						aux2);
				graphData[aux2 + 2] = attacks.get(arg2).toString();
			}
			graphWriter.writeRecord(graphData);
			graphWriter.flush();
		}

		graphWriter.close();
	}

	/**
	 * Wrtie the argumentation info in general CSV file and individual CSV file.
	 * 
	 * @param currentArgumentation
	 * @throws IOException
	 */
	private void writeArgumentationInCSVFile(Argumentation currentArgumentation)
			throws IOException {
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
		File f = new File(generalArgumentationsFile);
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

		String concreteArgumentationFile = argumentationDir + File.separator
				+ "argumentation" + currentArgumentation.getId()
				+ File.separator + "argumentation-"
				+ currentArgumentation.getId() + ".csv";
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
			Argument arg = currentArgumentation.getArgumentsWithID()
					.get(argNum);
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

	/**
	 * @param arg
	 */
	private void registerNewArgument(Argument arg) {
		Argumentation argumentation = this.getCurrentArgumentation();
		argumentation.addArgument(arg);
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

	public ArgumentativeAgent getArgumentationManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setArgumentationManager(ArgumentativeAgent manager) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.ArgumentativeAgent
	 * #finishArgumenation()
	 */
	public void finishArgumenation() {
		this.finishCurrentArgumentation();
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(
				"Argumentation Manager: Finishing argumentation...");
		for (ArgumentativeAgent s : this.suscribers) {
			s.finishArgumenation();
		}
	}

	public void addArgumentationGroupMember(ArgumentativeAgent agent) {
		this.suscribers.add(agent);
		agent.addArgumentationGroupMember(this);
		for (ArgumentativeAgent ag : this.getSubscribers()) {
			ag.addArgumentationGroupMember(agent);
			agent.addArgumentationGroupMember(ag);
		}
	}

	public void removeArgumentationGroupMember(ArgumentativeAgent agent) {
		this.suscribers.remove(agent);
		for (ArgumentativeAgent ag : this.getSubscribers()) {
			ag.removeArgumentationGroupMember(agent);
		}
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
	 * @return the fINISHING
	 */
	public boolean isFINISHING() {
		return FINISHING;
	}

	/**
	 * @param fINISHING
	 *            the fINISHING to set
	 */
	public void setFINISHING(boolean fINISHING) {
		FINISHING = fINISHING;
	}

	/**
	 * Go to status IDLE
	 */
	private void goToIdle() {
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).fine(
				this.getID() + " going to status: IDLE");
		this.IDLE = true;
		this.FINISHING = false;
		this.PROCESSING = false;
		this.WAITING = false;
	}

	/**
	 * Go to status PROCESSING
	 * 
	 * @param sim
	 */
	private void goToProcessing() {
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).fine(
				this.getID() + " going to status: PROCESSING");
		this.IDLE = false;
		this.FINISHING = false;
		this.PROCESSING = true;
		this.WAITING = false;
		this.processPendingArguments();
	}

	/**
	 * Go to status WAITING
	 */
	private void goToWaiting() {
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).fine(
				this.getID() + " going to status: WAITING");
		this.IDLE = false;
		this.FINISHING = false;
		this.PROCESSING = false;
		this.WAITING = true;
	}

	/**
	 * Go to status FINISHING
	 */
	private void goToFinishing() {
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).fine(
				this.getID() + " going to status: FINISHING");
		this.IDLE = false;
		this.FINISHING = true;
		this.PROCESSING = false;
		this.WAITING = false;
		this.finishArgumenation();
	}

}
