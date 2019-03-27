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
 * es.upm.dit.gsi.barmas.launcher.utils.ConsoleOutputDisabler.java
 */
package es.upm.dit.gsi.barmas.launcher.utils;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.launcher.utils.ConsoleOutputDisabler.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@upm.es
 * @twitter @alvarocarrera
 * @date 31/10/2013
 * @version 0.1
 * 
 */
public class ConsoleOutputDisabler {

	/**
	 * 
	 */
	public static void disableConsoleOutput() {
		System.setOut(new PrintStream(new OutputStream() {
			public void write(int b) {
				// DO NOTHING
			}
		}));
	}
}
