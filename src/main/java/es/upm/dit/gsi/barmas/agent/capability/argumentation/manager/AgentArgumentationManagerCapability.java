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
 * es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.AgentArgumentationManagerCapability.java
 */
package es.upm.dit.gsi.barmas.agent.capability.argumentation.manager;

import jason.asSemantics.Message;

import java.util.List;

import es.upm.dit.gsi.barmas.agent.capability.argumentation.ArgumentativeAgent;
import es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes.Argument;
import es.upm.dit.gsi.shanks.agent.SimpleShanksAgent;

/**
 * Project: barmas
 * File: es.upm.dit.gsi.barmas.agent.capability.argumentation.manager.AgentArgumentationManagerCapability.java
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
public class AgentArgumentationManagerCapability {

	/**
	 * @param agent
	 * @param arg
	 */
	public static void broadcastArgument(ArgumentationManagerAgent manager, Argument arg) {
		
		List<ArgumentativeAgent> subs = manager.getSubscribers();
		ArgumentativeAgent proponent = arg.getProponent();
		for (ArgumentativeAgent subscriber : subs) {
			if (!subscriber.equals(proponent)) {
				SimpleShanksAgent sender = (SimpleShanksAgent) manager;
				SimpleShanksAgent receiver = (SimpleShanksAgent) subscriber;
				Message message = new Message();
				message.setReceiver(receiver.getID());
				message.setPropCont(arg);
				sender.sendMsg(message);
			}
		}
		
	}

	/**
	 * 
	 */
	public static void finishCurrentArgumentation(ArgumentationManagerAgent manager) {
		// TODO Auto-generated method stub
		
	}
	
}
