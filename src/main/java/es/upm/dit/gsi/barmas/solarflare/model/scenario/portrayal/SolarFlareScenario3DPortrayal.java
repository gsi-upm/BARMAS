/*******************************************************************************
 * Copyright (c) 2013 alvarocarrera Grupo de Sistemas Inteligentes - Universidad Polit�cnica de Madrid. (GSI-UPM)
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
 * es.upm.dit.gsi.barmas.model.scenario.portrayal.SolarFlareScenario3DPortrayal.java
 */
package es.upm.dit.gsi.barmas.solarflare.model.scenario.portrayal;

import es.upm.dit.gsi.shanks.exception.ShanksException;
import es.upm.dit.gsi.shanks.model.scenario.Scenario;
import es.upm.dit.gsi.shanks.model.scenario.portrayal.Scenario3DPortrayal;

/**
 * Project: barmas
 * File: es.upm.dit.gsi.barmas.model.scenario.portrayal.SolarFlareScenario3DPortrayal.java
 * 
 * Grupo de Sistemas Inteligentes
 * Departamento de Ingenier�a de Sistemas Telem�ticos
 * Universidad Polit�cnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 23/07/2013
 * @version 0.1
 * 
 */
public class SolarFlareScenario3DPortrayal extends Scenario3DPortrayal {

	/**
	 * Constructor
	 *
	 * @param scenario
	 * @param width
	 * @param height
	 * @param length
	 * @throws ShanksException
	 */
	public SolarFlareScenario3DPortrayal(Scenario scenario, long width,
			long height, long length) throws ShanksException {
		super(scenario, width, height, length);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.scenario.portrayal.Scenario3DPortrayal#addPortrayals()
	 */
	@Override
	public void addPortrayals() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.scenario.portrayal.Scenario3DPortrayal#placeElements()
	 */
	@Override
	public void placeElements() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.scenario.portrayal.ScenarioPortrayal#setupPortrayals()
	 */
	@Override
	public void setupPortrayals() {
		// TODO Auto-generated method stub

	}

}