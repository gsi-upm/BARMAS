/**
 * es.upm.dit.gsi.barmas.model.Argument.java
 */
package es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes;

import java.util.HashSet;
import java.util.Set;

import es.upm.dit.gsi.barmas.agent.capability.argumentation.AbstractArgument;

/**
 * Project: barmas
 * File: es.upm.dit.gsi.barmas.model.Argument.java
 * 
 * Grupo de Sistemas Inteligentes
 * Departamento de Ingeniería de Sistemas Telemáticos
 * Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 22/07/2013
 * @version 0.1
 * 
 */
public class Argument extends AbstractArgument {

	private Set<Given> givens;
	private Set<Proposal> proposals;

	/**
	 * Constructor
	 *
	 * @param givens All givens
	 * @param proposals All proposals
	 */
	public Argument(Set<Given> givens, Set<Proposal> proposals) {
		this.givens = givens;
		this.proposals = proposals;
	}
	
	/**
	 * Constructor of empty argument
	 *
	 */
	public Argument() {
		this.givens = new HashSet<Given>();
		this.proposals = new HashSet<Proposal>();
	}
	
	/**
	 * @param given
	 */
	public void addGiven(Given given) {
		this.givens.add(given);
	}
	
	/**
	 * @param proposal
	 */
	public void addProposal(Proposal proposal) {
		this.proposals.add(proposal);
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.barmas.model.AbstractArgument#getGivens()
	 */
	@Override
	public Set<Given> getGivens() {
		return this.givens;
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.barmas.model.AbstractArgument#getProposals()
	 */
	@Override
	public Set<Proposal> getProposals() {
		return this.proposals;
	}

}
