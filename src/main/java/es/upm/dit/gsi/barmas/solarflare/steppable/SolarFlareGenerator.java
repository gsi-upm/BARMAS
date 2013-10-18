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
 * es.upm.dit.gsi.barmas.solarflare.SolarFlareGenerator.java
 */
package es.upm.dit.gsi.barmas.solarflare.steppable;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import sim.engine.SimState;
import sim.engine.Steppable;

import com.csvreader.CsvReader;

import es.upm.dit.gsi.barmas.solarflare.model.SolarFlare;
import es.upm.dit.gsi.barmas.solarflare.model.scenario.SolarFlareScenario;
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
import es.upm.dit.gsi.barmas.solarflare.simulation.SolarFlareClassificationSimulation;
import es.upm.dit.gsi.shanks.exception.ShanksException;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.solarflare.SolarFlareGenerator.java
 * 
 * Grupo de Sistemas Inteligentes
 * Departamento de Ingeniería de Sistemas Telemáticos
 * Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 02/10/2013
 * @version 0.1
 * 
 */
public class SolarFlareGenerator implements Steppable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -481268413730149934L;

	private CsvReader reader;
	private int counter;

	/**
	 * Constructor
	 * 
	 */
	public SolarFlareGenerator(String path) {
		this.counter = 0;
		Reader fr;
		try {
			fr = new FileReader(path);
			reader = new CsvReader(fr);
			reader.readHeaders();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sim.engine.Steppable#step(sim.engine.SimState)
	 */
	public void step(SimState simstate) {
		SolarFlareClassificationSimulation sim = (SolarFlareClassificationSimulation) simstate;
		SolarFlare flare = (SolarFlare) sim.getScenario().getNetworkElement(
				SolarFlareScenario.ORIGINALFLARE);

		try {
			if (!flare.getStatus().get(SolarFlare.READY)) {
				// if (!flare.getStatus().get(SolarFlare.READY) &&
				// this.counter==0) {

				if (reader.readRecord()) {
					String[] flareCase = reader.getValues();

					flare.changeProperty(LargestSpotSize.class.getSimpleName(),
							flareCase[0]);
					flare.changeProperty(
							SpotDistribution.class.getSimpleName(),
							flareCase[1]);
					flare.changeProperty(Activity.class.getSimpleName(),
							flareCase[2]);
					flare.changeProperty(Evolution.class.getSimpleName(),
							flareCase[3]);
					flare.changeProperty(
							PrevStatus24Hour.class.getSimpleName(),
							flareCase[4]);
					flare.changeProperty(HistComplex.class.getSimpleName(),
							flareCase[5]);
					flare.changeProperty(BecomeHist.class.getSimpleName(),
							flareCase[6]);
					flare.changeProperty(Area.class.getSimpleName(),
							flareCase[7]);
					flare.changeProperty(CNode.class.getSimpleName(),
							flareCase[8]);
					flare.changeProperty(MNode.class.getSimpleName(),
							flareCase[9]);
					flare.changeProperty(XNode.class.getSimpleName(),
							flareCase[10]);

					flare.changeProperty(SolarFlareType.class.getSimpleName(),
							flareCase[11]);

					flare.setCaseID(counter++);
					flare.setCurrentStatus(SolarFlare.READY, true);

					sim.getLogger().info(
							"New Solar Flare generated in step "
									+ sim.schedule.getSteps() + ". Case ID: "
									+ flare.getCaseID() + " Solar Flare Type: "
									+ flareCase[11]);
				} else {
					sim.getLogger().info(
							"Finishing simulation. No more test cases.");
					sim.finish();
				}
			}
		} catch (ShanksException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
