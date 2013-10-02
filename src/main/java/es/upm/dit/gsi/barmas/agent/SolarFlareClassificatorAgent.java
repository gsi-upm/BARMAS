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
 * es.upm.dit.gsi.barmas.agent.BarmasAgent.java
 */
package es.upm.dit.gsi.barmas.agent;

import jason.asSemantics.Message;

import java.util.List;
import java.util.Set;

import unbbayes.prs.bn.ProbabilisticNetwork;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.ArgumentativeAgent;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argument;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.Argumentation;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.ArgumentationManagerAgent;
import es.upm.dit.gsi.shanks.ShanksSimulation;
import es.upm.dit.gsi.shanks.agent.SimpleShanksAgent;
import es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.BayesianReasonerShanksAgent;
import es.upm.dit.gsi.shanks.exception.ShanksException;

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
public class SolarFlareClassificatorAgent extends SimpleShanksAgent implements BayesianReasonerShanksAgent, ArgumentationManagerAgent, ArgumentativeAgent {

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

	public String getProponentName() {
		// TODO Auto-generated method stub
		return null;
	}

	public ArgumentativeAgent getProponent() {
		// TODO Auto-generated method stub
		return null;
	}

	public ArgumentationManagerAgent getArgumentationManager() {
		// TODO Auto-generated method stub
		return null;
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

	public void send(Message m) {
		// TODO Auto-generated method stub
		
	}

	public Argumentation getCurrentArgumentation() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Argumentation> getArgumentations() {
		// TODO Auto-generated method stub
		return null;
	}

	public void processNewArgument(Argumentation arg) {
		// TODO Auto-generated method stub
		
	}
}
