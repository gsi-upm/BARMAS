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
 * es.upm.dit.gsi.barmas.model.AbstractArgument.java
 */
package es.upm.dit.gsi.barmas.agent.capability.argumentation;

import java.util.Set;

import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.ArgumentativeAgent;

/**
 * Project: barmas
 * File: es.upm.dit.gsi.barmas.model.AbstractArgument.java
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
public abstract class AbstractArgument {
	
	/**
	 * @return The givens/evidences of the argument
	 */
	public abstract Set<? extends AbstractGiven> getGivens();
	
	/**
	 * @return The assumptions of the argument
	 */
	public abstract Set<? extends AbstractAssumption> getAssumptions();
	
	/**
	 * @return The proposals/beliefs of the argument
	 */
	public abstract Set<? extends AbstractProposal> getProposals();
	
	/**
	 * @return The agent that proposes the argument
	 */
	public abstract ArgumentativeAgent getProponent();
	
	/**
	 * @return The name of the agent that proposes the argument
	 */
	public abstract String getProponentName();

}
