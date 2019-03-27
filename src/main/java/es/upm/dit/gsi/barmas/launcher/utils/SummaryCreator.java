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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.launcher.utils.SummaryCreator.java
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
public class SummaryCreator {

	public static void makeNumbers(String simulationName, String origPath, String outputPath) {
		Logger logger = Logger.getLogger(simulationName);
		File origFile = new File(origPath);
		File outputFile = new File(outputPath);
		File outputDetailedFile = new File(outputPath.replaceAll(".csv", "-detailed.csv"));
		// Calculate comparative data
		CsvWriter writer = null;
		CsvWriter writer2 = null;
		int columnsWriter = 9;
		int columnsWriter2 = 22;
		try {
			if (!outputDetailedFile.exists()) {
				writer = new CsvWriter(new FileWriter(outputDetailedFile), ',');
				String[] headers = new String[columnsWriter];
				headers[0] = "SimulationID";
				headers[1] = "Type";
				headers[2] = "Cases";
				headers[3] = "BayesCentralOK";
				headers[4] = "ArgumentationOK";
				headers[5] = "BayesCentralBetter";
				headers[6] = "ArgumentationBetter";
				headers[7] = "BothOK";
				headers[8] = "BothWrong";
				writer.writeRecord(headers);
			} else {
				writer = new CsvWriter(new FileWriter(outputDetailedFile, true), ',');
			}

			writer.flush();
			if (!outputFile.exists()) {
				writer2 = new CsvWriter(new FileWriter(outputFile), ',');
				String[] headers = new String[columnsWriter2];
				headers[0] = "SimulationID";
				headers[1] = "Type";
				headers[2] = "Cases";
				headers[3] = "BayesCentralOK";
				headers[4] = "ArgumentationOK";
				headers[5] = "BayesCentralBetter";
				headers[6] = "ArgumentationBetter";
				headers[7] = "BothOK";
				headers[8] = "BothWrong";
				headers[9] = "GlobalImprovementWithArgumentation";
				headers[10] = "Draw";
				headers[11] = "DifferenceProbDistThreshold";
				headers[12] = "BeliefThreshold";
				headers[13] = "LostEvidencesByAgents";
				headers[14] = "TrustThreshold";
				headers[15] = "Iteration";
				headers[16] = "ArgAgents";
				headers[17] = "TestRatio";
				headers[18] = "Seed";
				headers[19] = "Timestamp";
				headers[20] = "Dataset";
				headers[21] = "MaxArgumentationRounds";
				writer2.writeRecord(headers);
			} else {
				writer2 = new CsvWriter(new FileWriter(outputFile, true), ',');
			}
			writer2.flush();

			CsvReader reader = new CsvReader(new FileReader(origFile));
			reader.readHeaders();
			HashMap<String, HashMap<String, Integer>> counters = new HashMap<String, HashMap<String, Integer>>();
			while (reader.readRecord()) {
				String[] origdata = reader.getValues();
				if (!counters.keySet().contains(origdata[0])) {
					HashMap<String, Integer> cs = new HashMap<String, Integer>();
					cs.put("Cases", 0);
					cs.put("BayesCentralOK", 0);
					cs.put("ArgumentationOK", 0);
					cs.put("BayesCentralBetter", 0);
					cs.put("ArgumentationBetter", 0);
					cs.put("BothOK", 0);
					cs.put("BothWrong", 0);
					counters.put(origdata[0], cs);
				}
				HashMap<String, Integer> counter = counters.get(origdata[0]);
				int aux = counter.get("Cases");
				counter.put("Cases", aux + 1);
				if (origdata[2].equals("1")) {
					aux = counter.get("BayesCentralOK");
					counter.put("BayesCentralOK", aux + 1);
				}
				if (origdata[3].equals("1")) {
					aux = counter.get("ArgumentationOK");
					counter.put("ArgumentationOK", aux + 1);
				}
				if (origdata[4].equals("1")) {
					aux = counter.get("BayesCentralBetter");
					counter.put("BayesCentralBetter", aux + 1);
				}
				if (origdata[5].equals("1")) {
					aux = counter.get("ArgumentationBetter");
					counter.put("ArgumentationBetter", aux + 1);
				}
				if (origdata[6].equals("1")) {
					aux = counter.get("BothOK");
					counter.put("BothOK", aux + 1);
				}
				if (origdata[7].equals("1")) {
					aux = counter.get("BothWrong");
					counter.put("BothWrong", aux + 1);

				}
			}
			reader.close();

			// Write all types & TOTAL row
			int[] total = new int[7];
			for (Entry<String, HashMap<String, Integer>> entry : counters.entrySet()) {
				String[] data = new String[columnsWriter];
				data[0] = simulationName;
				data[1] = entry.getKey();
				HashMap<String, Integer> summaries = entry.getValue();
				data[2] = Integer.toString(summaries.get("Cases"));
				data[3] = Integer.toString(summaries.get("BayesCentralOK"));
				data[4] = Integer.toString(summaries.get("ArgumentationOK"));
				data[5] = Integer.toString(summaries.get("BayesCentralBetter"));
				data[6] = Integer.toString(summaries.get("ArgumentationBetter"));
				data[7] = Integer.toString(summaries.get("BothOK"));
				data[8] = Integer.toString(summaries.get("BothWrong"));

				writer.writeRecord(data);
				writer.flush();
				logger.info("New row in " + outputPath);
				String info = "";
				for (String s : data) {
					info = info + " - " + s;
				}
				logger.info(info);

				total[0] = total[0] + summaries.get("Cases");
				total[1] = total[1] + summaries.get("BayesCentralOK");
				total[2] = total[2] + summaries.get("ArgumentationOK");
				total[3] = total[3] + summaries.get("BayesCentralBetter");
				total[4] = total[4] + summaries.get("ArgumentationBetter");
				total[5] = total[5] + summaries.get("BothOK");
				total[6] = total[6] + summaries.get("BothWrong");
			}
			String[] totalData = new String[columnsWriter];
			totalData[0] = simulationName;
			totalData[1] = "TOTAL";
			for (int i = 0; i < total.length; i++) {
				totalData[i + 2] = Integer.toString(total[i]);
			}
			writer.writeRecord(totalData);
			writer.flush();
			logger.info("TOTAL row in " + outputPath);
			String info = "";
			for (String s : totalData) {
				info = info + " - " + s;
			}
			logger.info(info);

			String[] totalRatio = new String[columnsWriter];
			String[] totalRatio2 = new String[columnsWriter2];
			totalRatio[0] = simulationName;
			totalRatio[1] = "RATIO-TOTAL";
			totalRatio2[0] = simulationName;
			totalRatio2[1] = "RATIO-TOTAL";
			for (int i = 0; i < total.length; i++) {
				double aux = new Double(total[i]) / total[0];
				totalRatio[i + 2] = Double.toString(aux);
				totalRatio2[i + 2] = Double.toString(aux);
			}
			double totalBayesCentralOK = new Double(totalRatio[3]);
			double totalArgumentationOK = new Double(totalRatio[4]);
			totalRatio2[9] = Double.toString(totalArgumentationOK - totalBayesCentralOK);
			double totalBothOK = new Double(totalRatio[7]);
			double totalBothWrong = new Double(totalRatio[8]);
			totalRatio2[10] = Double.toString(totalBothOK + totalBothWrong);

			String[] nameSplits = simulationName.split("-");
			for (int i = 0; i < nameSplits.length; i++) {
				String split = nameSplits[i];
				if (i == 1) {
					totalRatio2[20] = nameSplits[i];
				} else if (split.equals("DTH")) {
					totalRatio2[11] = nameSplits[++i];
				} else if (split.equals("BTH")) {
					totalRatio2[12] = nameSplits[++i];
				} else if (split.equals("LEBA")) {
					totalRatio2[13] = nameSplits[++i];
				} else if (split.equals("TTH")) {
					totalRatio2[14] = nameSplits[++i];
				} else if (split.equals("IT")) {
					totalRatio2[15] = nameSplits[++i];
				} else if (split.equals("TESTRATIO")) {
					totalRatio2[17] = nameSplits[++i];
				} else if (split.contains("agents")) {
					totalRatio2[16] = split.replaceAll("agents", "");
//				} else if (split.contains("Agent")) {
//					totalRatio2[16] = "1";
				} else if (split.equals("seed")) {
					totalRatio2[18] = nameSplits[++i];
				} else if (split.equals("timestamp")) {
					totalRatio2[19] = nameSplits[++i];
				} else if (split.equals("MAXARGSROUNDS")) {
					totalRatio2[21] = nameSplits[++i];
				}
			}

			writer.writeRecord(totalRatio);
			writer.flush();
			writer2.writeRecord(totalRatio2);
			writer2.flush();
			logger.info("RATIO-TOTAL row in " + outputPath);
			info = "";
			for (String s : totalRatio) {
				info = info + " - " + s;
			}
			logger.info(info);

			writer.close();
			writer2.close();

		} catch (IOException e) {
			logger.warning("Impossible to create summary file for simulation: " + simulationName
					+ " in file: " + outputPath);
			logger.warning(e.getMessage());
			System.exit(1);
		}

	}

}
