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
 * es.upm.dit.gsi.barmas.model.test.ModelTest.java
 */
package es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.test;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argument;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Given;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Proposal;
import es.upm.dit.gsi.shanks.exception.ShanksException;

/**
 * Project: barmas File: es.upm.dit.gsi.barmas.model.test.ModelTest.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 22/07/2013
 * @version 0.1
 * 
 */
public class ModelTest {

	/**
	 * @throws ShanksException
	 */
	@Test
    public void checkArgument() {
    	// Setup
    	Argument arg  = new Argument();
    	Given g1 = new Given("MyNode", "Monday");
    	arg.addGiven(g1);
    	Given g2 = new Given("MySecondNode", "Green");
    	arg.addGiven(g2);
    	Proposal p1 = new Proposal("MyProp");
    	p1.addValueWithConfidence("Black", 0.6);
    	p1.addValueWithConfidence("Dog", 0.4);
    	arg.addProposal(p1);
    	
    	//Check
    	Set<Given> givens = arg.getGivens();
    	Assert.assertEquals(2, givens.size());
    	Set<Proposal> props = arg.getProposals();
    	Assert.assertEquals(1, props.size());
    	for (Given g : givens) {
    		if (g.getNode().equals("MyNode")) {
    			Assert.assertEquals("Monday", g.getValue());
    		} else {
    			Assert.assertEquals("MySecondNode", g.getNode());
    			Assert.assertEquals("Green", g.getValue());
    		}
    	}
    	Assert.assertEquals(1, props.size());
    	for (Proposal p : props) {
    		Assert.assertEquals("MyProp", p.getNode());
    		Assert.assertEquals(0.6, p.getConfidenceForValue("Black"));
    		Assert.assertEquals(0.4, p.getConfidenceForValue("Dog"));
    	}
    }
}
