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
 * es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.Argumentation.java
 */
package es.upm.dit.gsi.barmas.agent.capability.argumentation.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argument;
import es.upm.dit.gsi.shanks.ShanksSimulation;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.agent.capability.argumentation.manager
 * .Argumentation.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 01/10/2013
 * @version 0.1
 * 
 */
public class Argumentation {

	private Map<Argument, Long> timestamps;
	private Map<Argument, Long> steps;
	private int id;
	private List<Argument> conclusions;
	private boolean finished;

	/**
	 * Constructor
	 * 
	 */
	public Argumentation(int id) {
		this.id = id;
		this.setFinished(false);
		this.timestamps = new HashMap<Argument, Long>();
		this.steps = new HashMap<Argument, Long>();
		this.conclusions = new ArrayList<Argument>();
	}

	/**
	 * @param arg
	 * @param simulation
	 */
	public void addArgument(Argument arg, ShanksSimulation simulation) {
		long timestamp = System.currentTimeMillis();
		long step = simulation.schedule.getSteps();
		this.timestamps.put(arg, timestamp);
		this.steps.put(arg, step);
	}

	/**
	 * @return
	 */
	public Map<Argument, Long> getArgumentsWithSteps() {
		return this.steps;
	}
	
	/**
	 * @return
	 */
	public Map<Argument, Long> getArgumentsWithTimestamps() {
		return this.timestamps;
	}
	
	/**
	 * @return
	 */
	public List<Argument> getSortedArguments() {
		// TODO implement this method
		return null;
	}
	
	public List<Argument> getConclusions () {
		return this.conclusions;
	}
	
	/**
	 * @param arg
	 */
	public void addConclusion(Argument arg) {
		this.conclusions.add(arg);
	}

	/**
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the finished
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * @param finished the finished to set
	 */
	public void setFinished(boolean finished) {
		this.finished = finished;
	}

}
