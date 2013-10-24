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
 * es.upm.dit.gsi.barmas.solarflare.launcher.AllExperiments.java
 */
package es.upm.dit.gsi.barmas.solarflare.launcher;

import es.upm.dit.gsi.barmas.solarflare.launcher.experiments.Experiment1;
import es.upm.dit.gsi.barmas.solarflare.launcher.experiments.Experiment2;
import es.upm.dit.gsi.barmas.solarflare.launcher.experiments.Experiment3;

/**
 * Project: barmas
 * File: es.upm.dit.gsi.barmas.solarflare.launcher.AllExperiments.java
 * 
 * Grupo de Sistemas Inteligentes
 * Departamento de Ingeniería de Sistemas Telemáticos
 * Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 17/10/2013
 * @version 0.1
 * 
 */
public class AllExperiments {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Thread t;
		Experiment1 exp1 = new Experiment1();
		t=new Thread(exp1);
		t.start();
		
		
		
		String summaryFile = "src/main/resources/exp2/output/global-summary.csv";
		long seed = 0;
		double threshold = 1;
		double beliefThreshold = 1;

		while (threshold > 0.01) {
			while (beliefThreshold > 0.01) {
				Experiment2 exp2 = new Experiment2(summaryFile,
						seed, threshold, beliefThreshold);
				t = new Thread(exp2);
				t.start();
				beliefThreshold = beliefThreshold - 0.05;
			}
			beliefThreshold = 1;
			threshold = threshold - 0.05;
		}
		
		
		Experiment3 exp3 = new Experiment3();
		t = new Thread(exp3);
		t.start();
	}

}
