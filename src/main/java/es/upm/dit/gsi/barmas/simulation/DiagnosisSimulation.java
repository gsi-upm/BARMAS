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
package es.upm.dit.gsi.barmas.simulation;

import java.util.List;
import java.util.Properties;

import sim.engine.Schedule;
import sim.engine.Steppable;
import es.upm.dit.gsi.barmas.launcher.utils.SimulationConfiguration;
import es.upm.dit.gsi.barmas.steppable.DiagnosisCaseEvaluator;
import es.upm.dit.gsi.barmas.steppable.DiagnosisCaseGenerator;
import es.upm.dit.gsi.shanks.ShanksSimulation;
import es.upm.dit.gsi.shanks.agent.ShanksAgent;
import es.upm.dit.gsi.shanks.exception.ShanksException;
import es.upm.dit.gsi.shanks.model.scenario.Scenario;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.simulation.DiagnosisSimulation.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 31/10/2013
 * @version 0.1
 * 
 */
public class DiagnosisSimulation extends ShanksSimulation {

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
	public DiagnosisSimulation(long seed, Class<? extends Scenario> scenarioClass,
			String scenarioID, String initialState, Properties properties) throws ShanksException {
		super(seed, scenarioClass, scenarioID, initialState, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.shanks.ShanksSimulation#addSteppables()
	 */
	@Override
	public void addSteppables() {
		Steppable manager = (Steppable) this.getScenario().getProperties().get("ManagerAgent");
		try {
			this.registerShanksAgent((ShanksAgent) manager);
		} catch (ShanksException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
		schedule.scheduleRepeating(Schedule.EPOCH, 3, manager, 1);
		Steppable generator = new DiagnosisCaseGenerator(this.getScenario().getProperties()
				.getProperty(SimulationConfiguration.TESTDATASET));
		schedule.scheduleRepeating(Schedule.EPOCH, 6, generator, 1);
		boolean repMode = new Boolean(this.getScenario().getProperties()
				.getProperty(SimulationConfiguration.REPUTATIONMODE));
		Steppable evaluator = new DiagnosisCaseEvaluator(this.getScenario().getProperties()
				.getProperty(SimulationConfiguration.CLASSIFICATIONTARGET), this.getScenario()
				.getProperties().getProperty(SimulationConfiguration.EXPOUTPUT), this.getScenario()
				.getProperties().getProperty(SimulationConfiguration.TESTDATASET), repMode);
		schedule.scheduleRepeating(Schedule.EPOCH, 5, evaluator, 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.shanks.ShanksSimulation#registerShanksAgents()
	 */
	@Override
	public void registerShanksAgents() throws ShanksException {

		@SuppressWarnings("unchecked")
		List<ShanksAgent> agents = (List<ShanksAgent>) this.getScenario().getProperties()
				.get("AGENTS");
		for (ShanksAgent agent : agents) {
			this.registerShanksAgent(agent);
		}
	}

}
