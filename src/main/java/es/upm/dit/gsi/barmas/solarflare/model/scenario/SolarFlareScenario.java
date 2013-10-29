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
 * es.upm.dit.gsi.barmas.scenario.SolarFlareScenario.java
 */
package es.upm.dit.gsi.barmas.solarflare.model.scenario;

import java.util.HashMap;
import java.util.Properties;

import es.upm.dit.gsi.barmas.solarflare.model.SolarFlare;
import es.upm.dit.gsi.shanks.exception.ShanksException;
import es.upm.dit.gsi.shanks.model.event.failiure.Failure;
import es.upm.dit.gsi.shanks.model.scenario.Scenario;
import es.upm.dit.gsi.shanks.model.scenario.portrayal.Scenario2DPortrayal;
import es.upm.dit.gsi.shanks.model.scenario.portrayal.Scenario3DPortrayal;

/**
 * Project: barmas
 * File: es.upm.dit.gsi.barmas.scenario.SolarFlareScenario.java
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
public class SolarFlareScenario extends Scenario {
	
	public static final String ORIGINALFLARE ="OriginalSolarFlare";
	public static final String ARGUMENTATIONCONCLUSION = "ArgumentationConclusion";
	public static final String CENTRALCONCLUSION = "CentralConclusion";

	/**
	 * Constructor
	 *
	 * @param id
	 * @param initialState
	 * @param properties
	 * @throws ShanksException
	 */
	public SolarFlareScenario(String id, String initialState,
			Properties properties) throws ShanksException {
		super(id, initialState, properties);
	}
	
	public static final String NORMALSTATE= "NORMAL";

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.scenario.Scenario#createScenario2DPortrayal()
	 */
	@Override
	public Scenario2DPortrayal createScenario2DPortrayal()
			throws ShanksException {
        return null;
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.scenario.Scenario#createScenario3DPortrayal()
	 */
	@Override
	public Scenario3DPortrayal createScenario3DPortrayal()
			throws ShanksException {
        return null;
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.scenario.Scenario#setPossibleStates()
	 */
	@Override
	public void setPossibleStates() {
        this.addPossibleStatus(NORMALSTATE);
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.scenario.Scenario#addNetworkElements()
	 */
	@Override
	public void addNetworkElements() throws ShanksException {
		
		SolarFlare flare = new SolarFlare(ORIGINALFLARE);
		flare.setCurrentStatus(SolarFlare.READY, false);
		this.addNetworkElement(flare);
		
		SolarFlare classified = new SolarFlare(ARGUMENTATIONCONCLUSION);
		classified.setCurrentStatus(SolarFlare.READY, false);
		this.addNetworkElement(classified);
		
		SolarFlare bayesCentral = new SolarFlare(CENTRALCONCLUSION);
		bayesCentral.setCurrentStatus(SolarFlare.READY, false);
		this.addNetworkElement(bayesCentral);
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.scenario.Scenario#addPossibleFailures()
	 */
	@Override
	public void addPossibleFailures() {
		// NONE One steppable is in charge of this
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.scenario.Scenario#addPossibleEvents()
	 */
	@Override
	public void addPossibleEvents() {
		// NONE One steppable is in charge of this
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.scenario.Scenario#getPenaltiesInStatus(java.lang.String)
	 */
	@Override
	public HashMap<Class<? extends Failure>, Double> getPenaltiesInStatus(
			String status) throws ShanksException {
		return null;
	}

}
