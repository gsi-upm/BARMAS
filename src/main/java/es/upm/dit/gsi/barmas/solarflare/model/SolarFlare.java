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
 * es.upm.dit.gsi.barmas.model.element.device.SolarFlare.java
 */
package es.upm.dit.gsi.barmas.solarflare.model;

import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.Activity;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.Area;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.BecomeHist;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.CNode;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.Evolution;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.HistComplex;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.LargestSpotSize;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.MNode;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.PrevStatus24Hour;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.SolarFlareType;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.SpotDistribution;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.XNode;
import es.upm.dit.gsi.shanks.exception.ShanksException;
import es.upm.dit.gsi.shanks.model.element.device.Device;

/**
 * Project: barmas
 * File: es.upm.dit.gsi.barmas.model.element.device.SolarFlare.java
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
public class SolarFlare extends Device {

	public static final String READY = "READY";
	
	/**
	 * Constructor
	 *
	 */
	public SolarFlare(String id) {
		super(id, READY, false);
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.element.NetworkElement#fillIntialProperties()
	 */
	@Override
	public void fillIntialProperties() {
		this.addProperty(Activity.class.getSimpleName(), Activity.Status1.toString());
		this.addProperty(Area.class.getSimpleName(), Area.Status1.toString());
		this.addProperty(BecomeHist.class.getSimpleName(), BecomeHist.Status1.toString());
		this.addProperty(CNode.class.getSimpleName(), CNode.Status0.toString());
		this.addProperty(Evolution.class.getSimpleName(), Evolution.Status1.toString());
		this.addProperty(HistComplex.class.getSimpleName(), HistComplex.Status1.toString());
		this.addProperty(LargestSpotSize.class.getSimpleName(), LargestSpotSize.A.toString());
		this.addProperty(MNode.class.getSimpleName(), MNode.Status0.toString());
		this.addProperty(PrevStatus24Hour.class.getSimpleName(), PrevStatus24Hour.Status1.toString());
		this.addProperty(SpotDistribution.class.getSimpleName(), SpotDistribution.C.toString());
		this.addProperty(XNode.class.getSimpleName(), XNode.Status0.toString());
		
		this.addProperty(SolarFlareType.class.getSimpleName(), SolarFlareType.B.toString());
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.element.NetworkElement#checkProperties()
	 */
	@Override
	public void checkProperties() throws ShanksException {
		// Nothing to do. A steppable is in charge of this.
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.element.NetworkElement#checkStatus()
	 */
	@Override
	public void checkStatus() throws ShanksException {
		// Nothing to do. A steppable is in charge of this.
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.element.NetworkElement#setPossibleStates()
	 */
	@Override
	public void setPossibleStates() {
		this.addPossibleStatus(READY);
	}

}
