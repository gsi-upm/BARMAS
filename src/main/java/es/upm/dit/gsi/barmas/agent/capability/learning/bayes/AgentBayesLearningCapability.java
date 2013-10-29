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
 * es.upm.dit.gsi.barmas.agent.capability.learning.bayes.AgentBayesLearningCapability.java
 */
package es.upm.dit.gsi.barmas.agent.capability.learning.bayes;

import smile.Network;
import smile.learning.BayesianSearch;
import smile.learning.DataSet;
import es.upm.dit.gsi.shanks.agent.capability.reasoning.bayes.ShanksAgentBayesianReasoningCapability;
import es.upm.dit.gsi.shanks.exception.ShanksException;

/**
 * Project: barmas File: es.upm.dit.gsi.barmas.agent.capability.learning.bayes.
 * AgentBayesLearningCapability.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 29/10/2013
 * @version 0.1
 * 
 */
public class AgentBayesLearningCapability {

	public static void learnBNWithBayesianSearch(BayesLearningAgent agent) {
		String datasetFile = agent.getDatasetFile();
		DataSet dataset = new DataSet();
		dataset.readFile(datasetFile);

		BayesianSearch bs = new BayesianSearch();
		Network bn = bs.learn(dataset);
		bn.writeFile(agent.getBNOutputFile());
		agent.getLogger().fine(
				"BN learnt in: " + agent.getBNOutputFile() + " from: "
						+ datasetFile);

		// Test network in Unbbayes
		try {
			ShanksAgentBayesianReasoningCapability.loadNetwork(agent
					.getBNOutputFile());
		} catch (ShanksException e) {
			agent.getLogger().warning("BN is disconnected. Looking for disconnected nodes.");
			// If there is an exception, the net is disconnected.
			// So, new connections (arcs) are created.
			int[] allNodes = bn.getAllNodes();
			for (int node : allNodes) {
				int[] parents = bn.getParents(node);
				int[] children = bn.getChildren(node);
				if (parents.length == 0 && children.length == 0) {
					agent.getLogger().warning("Disconnected Node Found: " + bn.getNodeId(node));
					bn.addArc(allNodes[0], node);
				}
			}
			bn.writeFile(agent.getBNOutputFile());
		}

	}

}
