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
 * es.upm.dit.gsi.barmas.solarflare.simulation.SolarFlareClassificationSimulation3DGUI.java
 */
package es.upm.dit.gsi.barmas.solarflare.simulation;

import java.util.HashMap;

import javax.swing.JFrame;

import es.upm.dit.gsi.shanks.ShanksSimulation;
import es.upm.dit.gsi.shanks.ShanksSimulation3DGUI;
import es.upm.dit.gsi.shanks.exception.ShanksException;
import es.upm.dit.gsi.shanks.model.scenario.portrayal.Scenario2DPortrayal;
import es.upm.dit.gsi.shanks.model.scenario.portrayal.Scenario3DPortrayal;

/**
 * Project: barmas
 * File: es.upm.dit.gsi.barmas.solarflare.simulation.SolarFlareClassificationSimulation3DGUI.java
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
public class SolarFlareClassificationSimulation3DGUI extends
		ShanksSimulation3DGUI {

	/**
	 * Constructor
	 *
	 * @param sim
	 */
	public SolarFlareClassificationSimulation3DGUI(ShanksSimulation sim) {
		super(sim);
	}

    /**
     * @return
     */
    public static String getName() {
        return "Solar Flare Classification with BARMAS";
    }

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.ShanksSimulation3DGUI#addDisplays(es.upm.dit.gsi.shanks.model.scenario.portrayal.Scenario3DPortrayal)
	 */
	@Override
	public void addDisplays(Scenario3DPortrayal scenarioPortrayal) {
		// Nothing to do
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.ShanksSimulation3DGUI#addCharts(es.upm.dit.gsi.shanks.model.scenario.portrayal.Scenario3DPortrayal)
	 */
	@Override
	public void addCharts(Scenario3DPortrayal scenarioPortrayal)
			throws ShanksException {
		this.addTimeChart(
                "Histogram",
                "Time / Steps", "Resolved failures");
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.ShanksSimulation3DGUI#locateFrames(es.upm.dit.gsi.shanks.model.scenario.portrayal.Scenario3DPortrayal)
	 */
	@Override
	public void locateFrames(Scenario3DPortrayal scenarioPortrayal) {
		HashMap<String, JFrame> frames = scenarioPortrayal.getFrameList();
        JFrame mainFrame = frames.get(Scenario2DPortrayal.MAIN_DISPLAY_ID);
        JFrame chartFrame = frames
                .get("Histogram");

        mainFrame.setLocation(100, 100);
        chartFrame.setLocation(300, 200);
	}

}
