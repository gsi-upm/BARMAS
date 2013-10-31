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
package es.upm.dit.gsi.barmas.launcher.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

/**
 * Project: barmas
 * File: es.upm.dit.gsi.barmas.launcher.utils.SummaryRatioDataExtractor.java
 * 
 * Grupo de Sistemas Inteligentes
 * Departamento de Ingeniería de Sistemas Telemáticos
 * Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 31/10/2013
 * @version 0.1
 * 
 */
public class SummaryRatioDataExtractor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		CsvReader reader;
		CsvWriter writer;
		File input = new File("output/global-summary-onlyratios.csv");
		File output = new File("output/global-summary-onlyratios-analysed.csv");
		try {
			reader = new CsvReader(new FileReader(input));
			writer = new CsvWriter(new FileWriter(output), ',');

			reader.readHeaders();
			String[] oldHeaders = reader.getHeaders();
			String[] newHeaders = new String[oldHeaders.length + 3];
			for (int i = 0; i < oldHeaders.length; i++) {
				newHeaders[i] = oldHeaders[i];
			}
			newHeaders[oldHeaders.length] = "EvidencesSet";
			newHeaders[oldHeaders.length + 1] = "Threshold";
			newHeaders[oldHeaders.length + 2] = "BeliefThreshold";
			writer.writeRecord(newHeaders);

			while (reader.readRecord()) {
				String[] values = reader.getValues();
				String[] newValues = new String[values.length + 3];
				for (int i = 0; i < values.length; i++) {
					newValues[i] = values[i];
				}

				String simulationID = values[0];
				String[] splits = simulationID.split("-");
				String evidenceSet = splits[1];
				String th;
				String bth;
				if (evidenceSet.contains("3")) {
					th = "0";
					bth = "0";
				} else if (splits[3].contains("validation")){
					th = splits[4];
					bth = splits[6];
				} else {
					th = splits[3];
					bth = splits[5];
				}
				newValues[values.length] = evidenceSet;
				newValues[values.length + 1] = th;
				newValues[values.length + 2] = bth;
				writer.writeRecord(newValues);
				writer.flush();
			}

			reader.close();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("Analysis DONE");
	}

}
