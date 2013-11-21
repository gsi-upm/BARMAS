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
/**
 * es.upm.dit.gsi.barmas.model.Argument.java
 */
package es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes;

import java.util.HashSet;
import java.util.Set;

import es.upm.dit.gsi.barmas.agent.capability.argumentation.AbstractArgument;

/**
 * Project: barmas File: es.upm.dit.gsi.barmas.model.Argument.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 22/07/2013
 * @version 0.1
 * 
 */
public class Argument extends AbstractArgument {

	private int id;
	private Set<Given> givens;
	private Set<Assumption> assumptions;
	private Set<Proposal> proposals;
	private ArgumentativeAgent proponent;
	private long step;
	private long timestamp;

	/**
	 * Constructor
	 * 
	 * @param givens
	 *            All givens
	 * @param proposals
	 *            All proposals
	 */
	public Argument(ArgumentativeAgent proponent, Set<Given> givens, Set<Assumption> assumptions,
			Set<Proposal> proposals, long step, long timestamp) {
		this.proponent = proponent;
		this.givens = givens;
		this.assumptions = assumptions;
		this.proposals = proposals;
		this.step = step;
		this.timestamp = timestamp;
	}

	/**
	 * Constructor of empty argument with known proponent
	 * 
	 */
	public Argument(ArgumentativeAgent proponent, long step, long timestamp) {
		this.proponent = proponent;
		this.givens = new HashSet<Given>();
		this.assumptions = new HashSet<Assumption>();
		this.proposals = new HashSet<Proposal>();
		this.step = step;
		this.timestamp = timestamp;
	}

	/**
	 * Constructor of empty argument with unknow proponent
	 * 
	 */
	public Argument() {
		this.givens = new HashSet<Given>();
		this.assumptions = new HashSet<Assumption>();
		this.proposals = new HashSet<Proposal>();
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param given
	 */
	public void addGiven(Given given) {
		this.givens.add(given);
	}

	/**
	 * @param assumption
	 */
	public void addAssumption(Assumption assumption) {
		this.assumptions.add(assumption);
	}

	/**
	 * @param proposal
	 */
	public void addProposal(Proposal proposal) {
		this.proposals.add(proposal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.barmas.model.AbstractArgument#getGivens()
	 */
	@Override
	public Set<Given> getGivens() {
		return this.givens;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.barmas.model.AbstractArgument#getProposals()
	 */
	@Override
	public Set<Proposal> getProposals() {
		return this.proposals;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.agent.capability.argumentation.AbstractArgument
	 * #getAssumptions()
	 */
	@Override
	public Set<Assumption> getAssumptions() {
		return this.assumptions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.agent.capability.argumentation.AbstractArgument
	 * #getProponent()
	 */
	public ArgumentativeAgent getProponent() {
		return this.proponent;
	}

	/**
	 * @param prop
	 */
	public void setProponent(ArgumentativeAgent prop) {
		this.proponent = prop;
	}

	/**
	 * @return the step
	 */
	public long getStep() {
		return step;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.agent.capability.argumentation.AbstractArgument
	 * #getProponentName()
	 */
	@Override
	public String getProponentName() {
		return this.getProponent().getProponentName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.barmas.agent.capability.argumentation.AbstractArgument
	 * #clone()
	 */
	@Override
	public AbstractArgument clone() {
		Argument clone = new Argument(this.proponent, this.givens, this.assumptions,
				this.proposals, this.step, this.timestamp);
		clone.setId(this.getId());
		return clone;
	}

}
