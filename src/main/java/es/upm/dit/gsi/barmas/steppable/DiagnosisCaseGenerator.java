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
package es.upm.dit.gsi.barmas.steppable;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Handler;

import sim.engine.SimState;
import sim.engine.Steppable;

import com.csvreader.CsvReader;

import es.upm.dit.gsi.barmas.model.DiagnosisCase;
import es.upm.dit.gsi.barmas.model.scenario.DiagnosisScenario;
import es.upm.dit.gsi.barmas.simulation.DiagnosisSimulation;
import es.upm.dit.gsi.shanks.exception.ShanksException;

/**
 * Project: barmas
 * File: es.upm.dit.gsi.barmas.steppable.DiagnosisCaseGenerator.java
 * 
 * Grupo de Sistemas Inteligentes
 * Departamento de Ingeniería de Sistemas Telemáticos
 * Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 31/10/2013
 * @version 0.1
 * 
 */
public class DiagnosisCaseGenerator implements Steppable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -481268413730149934L;

	private CsvReader reader;
	private String[] headers;
	private int counter;

	/**
	 * Constructor
	 * 
	 */
	public DiagnosisCaseGenerator(String path) {
		this.counter = 0;
		Reader fr;
		try {
			fr = new FileReader(path);
			this.reader = new CsvReader(fr);
			this.reader.readHeaders();
			this.headers = reader.getHeaders();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sim.engine.Steppable#step(sim.engine.SimState)
	 */
	public void step(SimState simstate) {
		DiagnosisSimulation sim = (DiagnosisSimulation) simstate;
		DiagnosisCase diagnosisCase = (DiagnosisCase) sim.getScenario()
				.getNetworkElement(DiagnosisScenario.ORIGINALDIAGNOSIS);

		try {
			if (!diagnosisCase.getStatus().get(DiagnosisCase.READY)) {

				if (reader.readRecord()) {
					String[] diagnosisCaseValues = reader.getValues();

					for (int i = 0; i < headers.length; i++) {
						diagnosisCase.changeProperty(headers[i], diagnosisCaseValues[i]);
					}

					diagnosisCase.setCaseID(counter++);
					diagnosisCase.setCurrentStatus(DiagnosisCase.READY, true);

					sim.getLogger().info(
							"New Diagnosis Case generated in step "
									+ sim.schedule.getSteps() + ". Case ID: "
									+ diagnosisCase.getCaseID()
									+ " Classification Target: " + diagnosisCaseValues[headers.length-1]);
				} else {
					sim.getLogger().info(
							"Finishing simulation. No more test cases.");
					for (Handler h : sim.getLogger().getHandlers()) {
						h.close();
					}
					sim.finish();
				}
			}
		} catch (ShanksException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
}
