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
 * es.upm.dit.gsi.barmas.simulation.BarmasBasicSimulation.java
 */
package es.upm.dit.gsi.barmas.solarflare.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import sim.engine.Schedule;
import sim.engine.Steppable;
import es.upm.dit.gsi.barmas.solarflare.agent.SolarFlareCentralManagerAgent;
import es.upm.dit.gsi.barmas.solarflare.agent.SolarFlareBayesCentralAgent;
import es.upm.dit.gsi.barmas.solarflare.agent.SolarFlareClassificatorAgent;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.Activity;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.Area;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.BecomeHist;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.CNode;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.Evolution;
import es.upm.dit.gsi.barmas.solarflare.steppable.SolarFlareEvaluator;
import es.upm.dit.gsi.barmas.solarflare.steppable.SolarFlareGenerator;
import es.upm.dit.gsi.shanks.ShanksSimulation;
import es.upm.dit.gsi.shanks.exception.ShanksException;
import es.upm.dit.gsi.shanks.model.scenario.Scenario;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.simulation.BarmasBasicSimulation.java
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
public class SolarFlareClassificationSimulation extends ShanksSimulation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4766549890044944967L;

	/**
	 * Constructor
	 * 
	 * @param seed
	 * @param scenarioClass
	 * @param scenarioID
	 * @param initialState
	 * @param properties
	 * @throws ShanksException
	 */
	public SolarFlareClassificationSimulation(long seed,
			Class<? extends Scenario> scenarioClass, String scenarioID,
			String initialState, Properties properties) throws ShanksException {
		super(seed, scenarioClass, scenarioID, initialState, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.shanks.ShanksSimulation#addSteppables()
	 */
	@Override
	public void addSteppables() {
		Steppable generator = new SolarFlareGenerator(
				"src/main/resources/exp1/dataset/testdataset.csv");
		schedule.scheduleRepeating(Schedule.EPOCH, 1, generator, 5);
//		Steppable evaluator = new SolarFlareEvaluator(
//				"src/main/resources/output/classification-results.csv",
//				"src/main/resources/exp1/dataset/testdataset.csv");
//		schedule.scheduleRepeating(Schedule.EPOCH, 3, evaluator, 5);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.shanks.ShanksSimulation#registerShanksAgents()
	 */
	@Override
	public void registerShanksAgents() throws ShanksException {

		SolarFlareBayesCentralAgent bayes = new SolarFlareBayesCentralAgent(
				"BayesCentral",
				"src/main/resources/exp1/bayes/agentdataset-central.net");
		this.registerShanksAgent(bayes);

		SolarFlareCentralManagerAgent manager = new SolarFlareCentralManagerAgent(
				"Manager");
		this.registerShanksAgent(manager);

		List<String> sensors1 = new ArrayList<String>();
		sensors1.add(Activity.class.getSimpleName());
		sensors1.add(Area.class.getSimpleName());
		sensors1.add(BecomeHist.class.getSimpleName());
		sensors1.add(CNode.class.getSimpleName());
//		sensors1.add(Evolution.class.getSimpleName());
		SolarFlareClassificatorAgent agent1 = new SolarFlareClassificatorAgent(
				"ArgAgent1", manager,
				"src/main/resources/exp1/bayes/agentdataset-1.net", sensors1);
		this.registerShanksAgent(agent1);


		List<String> sensors2 = new ArrayList<String>();
		SolarFlareClassificatorAgent agent2 = new SolarFlareClassificatorAgent(
				"ArgAgent2", manager,
				"src/main/resources/exp1/bayes/agentdataset-2.net", sensors2);
		 this.registerShanksAgent(agent2);

	}

}
