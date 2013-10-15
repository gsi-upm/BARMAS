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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argument;

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

	private int id;
	private List<Argument> conclusions;
	private HashMap<Argument,HashMap<Argument,Integer>> graph;
	private HashMap<Integer, Argument> ids;
	private boolean finished;

	/**
	 * Constructor
	 * 
	 */
	public Argumentation(int id) {
		this.id = id;
		this.setFinished(false);
		this.ids = new HashMap<Integer, Argument>();
		this.conclusions = new ArrayList<Argument>();
		this.graph = new HashMap<Argument, HashMap<Argument, Integer>>();
	}

	/**
	 * @param arg
	 * @param simulation
	 */
	public void addArgument(Argument arg) {
		arg.setId(ids.keySet().size());
		this.ids.put(ids.keySet().size(), arg);
		this.graph.put(arg, new HashMap<Argument, Integer>());
	}

	/**
	 * @return
	 */
	public List<Argument> getSortedArguments() {
		List<Argument> sortedList = new ArrayList<Argument>();
		for (int i = 0; i<this.ids.keySet().size(); i++) {
			sortedList.add(this.ids.get(i));
		}
		return sortedList;
	}
	
	/**
	 * @return
	 */
	public Collection<Argument> getArguments() {
		return this.ids.values();
	}

	/**
	 * @return
	 */
	public List<Argument> getConclusions() {
		return this.conclusions;
	}

	/**
	 * @return
	 */
	public HashMap<Argument, HashMap<Argument, Integer>> getGraph() {
		return graph;
	}

	/**
	 * @param graph
	 */
	public void setGraph(HashMap<Argument, HashMap<Argument, Integer>> graph) {
		this.graph = graph;
	}

	/**
	 * @param conclusions
	 */
	public void setConclusions(List<Argument> conclusions) {
		this.conclusions = conclusions;
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
	 * @param finished
	 *            the finished to set
	 */
	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public HashMap<Integer, Argument> getArgumentsWithID() {
		return this.ids;
	}

}
