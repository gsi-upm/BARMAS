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
 * es.upm.dit.gsi.barmas.solarflare.launcher.logging.LogConfigurator.java
 */
package es.upm.dit.gsi.barmas.solarflare.launcher.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.solarflare.launcher.logging.LogConfigurator.java
 * 
 * Grupo de Sistemas Inteligentes
 * Departamento de Ingeniería de Sistemas Telemáticos
 * Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 10/10/2013
 * @version 0.1
 * 
 */
public class LogConfigurator {

	/**
	 * Constructor
	 * 
	 */
	public static void log2File(Logger logger, String name, Level level,
			String dir) {
		try {
			File aux = new File(".");
			String parent = aux.getCanonicalPath();
			File f = new File(parent + File.separator + dir);
			if (!f.isDirectory()) {
				boolean made = f.mkdir();
				if (!made) {
					logger.warning("Impossible to create log directory");
				}
			}
			f = new File(parent + File.separator + dir + File.separator
					+ "logs");
			if (!f.isDirectory()) {
				boolean made = f.mkdir();
				if (!made) {
					logger.warning("Impossible to create log directory");
				}
			}
			String path = parent + File.separator + dir + File.separator
					+ "logs" + File.separator + name + "%u.log";
			FileHandler fh = new FileHandler(path);
			fh.setFormatter(new SimpleFormatter());
			fh.setEncoding("UTF-8");
			logger.addHandler(fh);
			logger.setLevel(level);
		} catch (IOException e) {
			logger.warning("Error configuring the log file.");
			e.printStackTrace();
		}
	}

}
