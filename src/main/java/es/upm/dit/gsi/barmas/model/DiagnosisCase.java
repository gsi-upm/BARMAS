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
package es.upm.dit.gsi.barmas.model;

import java.util.logging.Logger;

import es.upm.dit.gsi.shanks.exception.ShanksException;
import es.upm.dit.gsi.shanks.model.element.device.Device;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.model.element.device.SolarFlare.java
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
public class DiagnosisCase extends Device {

	public static final String READY = "READY";

	private int caseID;
	private String[] variables;

	/**
	 * Constructor
	 * 
	 */
	public DiagnosisCase(String id, String[] variables, Logger logger) {
		super(id, READY, false, logger);
		this.variables = variables;
		for (String variable : variables) {
			this.addProperty(variable, "");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upm.dit.gsi.shanks.model.element.NetworkElement#fillIntialProperties()
	 */
	@Override
	public void fillIntialProperties() {
		//Nothing to do. This is done in the constructor.
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
			for (String variable : variables) {
				this.changeProperty(variable, "");
			}
			this.setCaseID(-1);
		} catch (ShanksException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * 
	 */
	public void reset() {
		try {
			this.clean();
			this.setCurrentStatus(DiagnosisCase.READY, false);
		} catch (ShanksException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public String[] getPossibleVariables() {
		return this.variables;
	}

}
