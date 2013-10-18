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
 * es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Assumption.java
 */
package es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import es.upm.dit.gsi.barmas.agent.capability.argumentation.AbstractAssumption;

/**
 * Project: barmas
 * File: es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Assumption.java
 * 
 * Grupo de Sistemas Inteligentes
 * Departamento de Ingeniería de Sistemas Telemáticos
 * Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 01/10/2013
 * @version 0.1
 * 
 */
public class Assumption extends AbstractAssumption {

	private String node;
	private Map<String, Double> beliefs;

	/**
	 * Constructor
	 *
	 * @param node The name of the proposed node
	 * @param beliefs The map with value-confidence. Consistency of this probability distribution is not checked in this class.
	 */
	public Assumption (String node, Map<String,Double> beliefs) {
		this.node = node;
		this.beliefs = beliefs;
	}
	
	/**
	 * Constructor
	 *
	 * @param node
	 */
	public Assumption (String node) {
		this.node = node;
		this.beliefs = new HashMap<String, Double>();
	}
	
	/**
	 * @param value A possible value of the node
	 * @param confidence Its confidence. Consistency of this probability distribution is not checked in this class.
	 */
	public void addValueWithConfidence(String value, double confidence) {
		this.beliefs.put(value, confidence);
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.barmas.model.AbstractProposal#getNode()
	 */
	@Override
	public String getNode() {
		return this.node;
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.barmas.model.AbstractProposal#getValues()
	 */
	@Override
	public Set<String> getValues() {
		return this.beliefs.keySet();
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.barmas.model.AbstractProposal#getValuesWithConfidence()
	 */
	@Override
	public Map<String, Double> getValuesWithConfidence() {
		return this.beliefs;
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.barmas.model.AbstractProposal#getConfidenceForValue(java.lang.String)
	 */
	@Override
	public double getConfidenceForValue(String value) {
		double confidence = this.beliefs.get(value);
		return confidence;
	}

}
