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
 * es.upm.dit.gsi.barmas.agent.capability.argumentation.DatasetGenerator.java
 */
package es.upm.dit.gsi.barmas.solarflare.dataset.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.agent.capability.argumentation.DatasetGenerator.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 09/10/2013
 * @version 0.2
 * 
 */
public class DatasetGenerator {

	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		 DatasetGenerator.twoArgAgentsAndTestDataset();
		DatasetGenerator.twoArgAgentsAndTestDatasetWithAllStates();

	}

	/**
	 * Dataset for Experiment 1
	 */
	public static void twoArgAgentsAndTestDataset() {
		String path = "src/main/resources/dataset/solarflare-global.csv";
		String testingPath = "src/main/resources/exp1/dataset/testdataset.csv";
		String dataSet1 = "src/main/resources/exp1/dataset/agentdataset-1.csv";
		String dataSet2 = "src/main/resources/exp1/dataset/agentdataset-2.csv";
		String dataSetCentral = "src/main/resources/exp1/dataset/agentdataset-central.csv";
		Reader fr;
		CsvReader reader;
		try {
			fr = new FileReader(path);
			reader = new CsvReader(fr);
			reader.readHeaders();
			String[] headers = reader.getHeaders();

			CsvWriter testWriter = new CsvWriter(new FileWriter(testingPath),
					',');
			CsvWriter data1Writer = new CsvWriter(new FileWriter(dataSet1), ',');
			CsvWriter data2Writer = new CsvWriter(new FileWriter(dataSet2), ',');
			CsvWriter dataCentralWriter = new CsvWriter(new FileWriter(
					dataSetCentral), ',');

			testWriter.writeRecord(headers);
			testWriter.flush();
			data1Writer.writeRecord(headers);
			data1Writer.flush();
			data2Writer.writeRecord(headers);
			data2Writer.flush();
			dataCentralWriter.writeRecord(headers);
			dataCentralWriter.flush();

			int i = 0;
			int[] counters = new int[3];
			while (reader.readRecord()) {
				String[] values = reader.getValues();
				switch (i) {
				case 0:
					testWriter.writeRecord(values);
					counters[i]++;
					i++;
					break;
				case 1:
					data1Writer.writeRecord(values);
					dataCentralWriter.writeRecord(values);
					counters[i]++;
					i++;
					break;
				case 2:
					data2Writer.writeRecord(values);
					dataCentralWriter.writeRecord(values);
					counters[i]++;
					i = 0;
					break;
				default:
					logger.warning("WARNING!!! Error writing datasets! i=" + i);
					break;
				}
			}

			testWriter.close();
			logger.info("Closing testing dataset. Entries: " + counters[0]);
			data1Writer.close();
			logger.info("Closing agent 1 dataset. Entries: " + counters[1]);
			data2Writer.close();
			logger.info("Closing agent 2 dataset. Entries: " + counters[2]);
			dataCentralWriter.close();

			logger.info("--> THE END");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Dataset for Experiment 2
	 */
	public static void twoArgAgentsAndTestDatasetWithAllStates() {
		String path = "src/main/resources/dataset/solarflare-global.csv";
		String testingPath = "src/main/resources/exp2/dataset/testdataset.csv";
		String dataSet1 = "src/main/resources/exp2/dataset/agentdataset-1.csv";
		String dataSet2 = "src/main/resources/exp2/dataset/agentdataset-2.csv";
		String dataSetCentral = "src/main/resources/exp2/dataset/agentdataset-central.csv";
		// Find essentials
		List<String[]> essentials = new ArrayList<String[]>();
		HashMap<String, List<String>> nodesAndStates = new HashMap<String, List<String>>();  
		try {
			// Look for all possible states
			Reader fr = new FileReader(path);
			CsvReader reader = new CsvReader(fr);
			reader.readHeaders();
			String[] headers = reader.getHeaders();
			for (String header : headers) {
				nodesAndStates.put(header, new ArrayList<String>());
			}
			String[] values;
			while(reader.readRecord()) {
				values = reader.getValues();
				for (int i = 0; i<values.length; i++) {
					if (!nodesAndStates.get(headers[i]).contains(values[i])) {
						nodesAndStates.get(headers[i]).add(values[i]);
						if (!essentials.contains(values)) {
							essentials.add(values);
						}
					}
				}
			}
			
			logger.info("Number of Essentials: " + essentials.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		Reader fr;
		CsvReader reader;
		try {
			fr = new FileReader(path);
			reader = new CsvReader(fr);
			reader.readHeaders();
			String[] headers = reader.getHeaders();

			CsvWriter testWriter = new CsvWriter(new FileWriter(testingPath),
					',');
			CsvWriter data1Writer = new CsvWriter(new FileWriter(dataSet1), ',');
			CsvWriter data2Writer = new CsvWriter(new FileWriter(dataSet2), ',');
			CsvWriter dataCentralWriter = new CsvWriter(new FileWriter(
					dataSetCentral), ',');

			testWriter.writeRecord(headers);
			testWriter.flush();
			data1Writer.writeRecord(headers);
			data1Writer.flush();
			data2Writer.writeRecord(headers);
			data2Writer.flush();
			dataCentralWriter.writeRecord(headers);
			dataCentralWriter.flush();

			int i = 0;
			int[] counters = new int[3];
			while (reader.readRecord()) {
				String[] values = reader.getValues();
				switch (i) {
				case 0:
					testWriter.writeRecord(values);
					counters[i]++;
					i++;
					break;
				case 1:
					data1Writer.writeRecord(values);
					dataCentralWriter.writeRecord(values);
					counters[i]++;
					i++;
					break;
				case 2:
					data2Writer.writeRecord(values);
					dataCentralWriter.writeRecord(values);
					counters[i]++;
					i = 0;
					break;
				default:
					logger.warning("WARNING!!! Error writing datasets! i=" + i);
					break;
				}
			}
			
			for (String[] values : essentials) {
				data1Writer.writeRecord(values);
				counters[1]++;
				data2Writer.writeRecord(values);
				counters[2]++;
				dataCentralWriter.writeRecord(values);
			}

			testWriter.close();
			logger.info("Closing testing dataset. Entries: " + counters[0]);
			data1Writer.close();
			logger.info("Closing agent 1 dataset. Entries: " + counters[1]);
			data2Writer.close();
			logger.info("Closing agent 2 dataset. Entries: " + counters[2]);
			dataCentralWriter.close();
			int aux = counters[1]+counters[2];
			logger.info("Closing agent Bayes central dataset. Entries: " + aux);

			logger.info("--> THE END");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
