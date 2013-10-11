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
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.model.element.device.SolarFlare.java
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
public class SolarFlare extends Device {

	public static final String READY = "READY";

	private int caseID;

	/**
	 * Constructor
	 * 
	 */
	public SolarFlare(String id) {
		super(id, READY, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.shanks.model.element.NetworkElement#fillIntialProperties()
	 */
	@Override
	public void fillIntialProperties() {
		this.addProperty(Activity.class.getSimpleName(), "");
		this.addProperty(Area.class.getSimpleName(), "");
		this.addProperty(BecomeHist.class.getSimpleName(), "");
		this.addProperty(CNode.class.getSimpleName(), "");
		this.addProperty(Evolution.class.getSimpleName(), "");
		this.addProperty(HistComplex.class.getSimpleName(), "");
		this.addProperty(LargestSpotSize.class.getSimpleName(), "");
		this.addProperty(MNode.class.getSimpleName(), "");
		this.addProperty(PrevStatus24Hour.class.getSimpleName(), "");
		this.addProperty(SpotDistribution.class.getSimpleName(), "");
		this.addProperty(XNode.class.getSimpleName(), "");

		this.addProperty(SolarFlareType.class.getSimpleName(), "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.shanks.model.element.NetworkElement#checkProperties()
	 */
	@Override
	public void checkProperties() throws ShanksException {
		// Nothing to do. A steppable is in charge of this.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upm.dit.gsi.shanks.model.element.NetworkElement#checkStatus()
	 */
	@Override
	public void checkStatus() throws ShanksException {
		// Nothing to do. A steppable is in charge of this.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.shanks.model.element.NetworkElement#setPossibleStates()
	 */
	@Override
	public void setPossibleStates() {
		this.addPossibleStatus(READY);
	}

	/**
	 * @return the caseID
	 */
	public int getCaseID() {
		return caseID;
	}

	/**
	 * @param caseID
	 *            the caseID to set
	 */
	public void setCaseID(int caseID) {
		this.caseID = caseID;
	}

	/**
	 * Delete all data of the solar flare.
	 */
	public void clean() {
		try {
			this.changeProperty(Activity.class.getSimpleName(), "");
			this.changeProperty(Area.class.getSimpleName(), "");
			this.changeProperty(BecomeHist.class.getSimpleName(), "");
			this.changeProperty(CNode.class.getSimpleName(), "");
			this.changeProperty(Evolution.class.getSimpleName(), "");
			this.changeProperty(HistComplex.class.getSimpleName(), "");
			this.changeProperty(LargestSpotSize.class.getSimpleName(), "");
			this.changeProperty(MNode.class.getSimpleName(), "");
			this.changeProperty(PrevStatus24Hour.class.getSimpleName(), "");
			this.changeProperty(SpotDistribution.class.getSimpleName(), "");
			this.changeProperty(XNode.class.getSimpleName(), "");
			this.changeProperty(SolarFlareType.class.getSimpleName(), "");
			this.setCaseID(-1);
		} catch (ShanksException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public void reset() {
		try {
			this.clean();
			this.setCurrentStatus(SolarFlare.READY, false);
		} catch (ShanksException e) {
			e.printStackTrace();
		}
	}

}
