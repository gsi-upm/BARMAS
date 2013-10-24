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
 * es.upm.dit.gsi.barmas.solarflare.launcher.ExperimentExecutor.java
 */
package es.upm.dit.gsi.barmas.solarflare.launcher;

import java.util.ArrayList;
import java.util.List;

import es.upm.dit.gsi.barmas.solarflare.launcher.experiments.Experiment1;
import es.upm.dit.gsi.barmas.solarflare.launcher.experiments.Experiment2;
import es.upm.dit.gsi.barmas.solarflare.launcher.experiments.Experiment3;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.solarflare.launcher.ExperimentExecutor.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 17/10/2013
 * @version 0.1
 * 
 */
public class ExperimentExecutor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		List<Runnable> experiments = new ArrayList<Runnable>();
		Experiment1 exp1 = new Experiment1();
		experiments.add(exp1);

		String summaryFile = "src/main/resources/exp2/output/global-summary.csv";
		long seed = 0;
		double threshold = 1;
		double beliefThreshold = 1;

		while (threshold > 0.01) {
			while (beliefThreshold > 0.01) {
				Experiment2 exp2 = new Experiment2(summaryFile, seed,
						threshold, beliefThreshold);
				experiments.add(exp2);
				beliefThreshold = beliefThreshold - 0.05;
			}
			beliefThreshold = 1;
			threshold = threshold - 0.05;
		}

		Experiment3 exp3 = new Experiment3();
		experiments.add(exp3);


		int maxThreads = 5;
		ExperimentExecutor executor = new ExperimentExecutor();
		executor.executeExperiments(experiments, maxThreads);

	}

	public void executeExperiments(List<Runnable> experiments, int maxThreads) {

		List<Thread> threads = new ArrayList<Thread>();
		for (Runnable experiment : experiments) {
			if (threads.size() >= maxThreads) {
				try {
					while (threads.size() >= maxThreads) {
						Thread.sleep(5000);
						List<Thread> threads2Remove = new ArrayList<Thread>();
						for (Thread thread : threads) {
							if (!thread.isAlive()) {
								threads2Remove.add(thread);
							}
						}
						if (!threads2Remove.isEmpty()) {
							for (Thread t : threads) {
								threads.remove(t);
							}
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			} else {
				Thread t = new Thread(experiment);
				threads.add(t);
				t.start();
			}
		}

	}

}
