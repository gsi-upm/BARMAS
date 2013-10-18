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
 * es.upm.dit.gsi.barmas.solarflare.steppable.SolarFlareEvaluator.java
 */
package es.upm.dit.gsi.barmas.solarflare.steppable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sim.engine.SimState;
import sim.engine.Steppable;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import es.upm.dit.gsi.barmas.solarflare.model.SolarFlare;
import es.upm.dit.gsi.barmas.solarflare.model.scenario.SolarFlareScenario;
import es.upm.dit.gsi.barmas.solarflare.model.vocabulary.SolarFlareType;
import es.upm.dit.gsi.barmas.solarflare.simulation.SolarFlareClassificationSimulation;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.solarflare.steppable.SolarFlareEvaluator.java
 * 
 * Grupo de Sistemas Inteligentes
 * Departamento de Ingeniería de Sistemas Telemáticos
 * Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 02/10/2013
 * @version 0.1
 * 
 */
public class SolarFlareEvaluator implements Steppable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6678249143484534051L;

	private String outputPath;
	private String originalPath;
	private String[] classResultsHeaders;
	private String[] summaryHeaders;
	private String classResultsFile;
	private String summaryPerClassFile;

	public SolarFlareEvaluator(String output, String originalTestCases) {
		this.outputPath = output;
		this.originalPath = originalTestCases;

		// Output classification results file
		// Writing csv headers
		this.classResultsFile = outputPath + File.separator
				+ "classification-results.csv";
		try {

			Reader fr = new FileReader(originalPath);
			CsvReader reader = new CsvReader(fr);
			reader.readHeaders();
			String[] headers = reader.getHeaders();
			List<String> resultsHeaders = new ArrayList<String>();
			resultsHeaders.add("caseID");
			resultsHeaders.addAll(Arrays.asList(headers));
			resultsHeaders.add("BayesCentralClassifiedAs");
			resultsHeaders.add("ArgumentationClassifiedAs");
			int size = resultsHeaders.size();
			String[] newHeaders = new String[size];
			int i = 0;
			for (String header : resultsHeaders) {
				newHeaders[i++] = header;
			}
			CsvWriter writer = new CsvWriter(new FileWriter(classResultsFile),
					',');
			this.classResultsHeaders = newHeaders;
			writer.writeRecord(newHeaders);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Output classification results file
		// Writing csv headers
		this.summaryPerClassFile = outputPath + File.separator + "summary.csv";

		try {
			List<String> summaryHeaders = new ArrayList<String>();
			summaryHeaders.add("Type");
			summaryHeaders.add("Original");
			summaryHeaders.add("BayesCentral");
			summaryHeaders.add("Argumentation");
			summaryHeaders.add("BetterBayesCentral");
			summaryHeaders.add("BetterArgumentation");
			summaryHeaders.add("BothOK");
			summaryHeaders.add("BothWrong");
			int size = summaryHeaders.size();
			String[] newHeaders = new String[size];
			int i = 0;
			for (String header : summaryHeaders) {
				newHeaders[i++] = header;
			}
			CsvWriter writer = new CsvWriter(
					new FileWriter(summaryPerClassFile), ',');
			this.summaryHeaders = newHeaders;
			writer.writeRecord(newHeaders);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sim.engine.Steppable#step(sim.engine.SimState)
	 */
	public void step(SimState simstate) {
		SolarFlareClassificationSimulation sim = (SolarFlareClassificationSimulation) simstate;
		SolarFlare argConclusion = (SolarFlare) sim.getScenario()
				.getNetworkElement(SolarFlareScenario.ARGUMENTATIONCONCLUSION);
		SolarFlare centralConclusion = (SolarFlare) sim.getScenario()
				.getNetworkElement(SolarFlareScenario.CENTRALCONCLUSION);
		SolarFlare origflare = (SolarFlare) sim.getScenario()
				.getNetworkElement(SolarFlareScenario.ORIGINALFLARE);

		if (argConclusion.getStatus().get(SolarFlare.READY)
				&& centralConclusion.getStatus().get(SolarFlare.READY)) {

			String argClass = (String) argConclusion
					.getProperty(SolarFlareType.class.getSimpleName());
			String centralClass = (String) centralConclusion
					.getProperty(SolarFlareType.class.getSimpleName());
			String origClass = (String) origflare
					.getProperty(SolarFlareType.class.getSimpleName());
			sim.getLogger().info("-----> Writing CSV files...");
			try {
				FileWriter fw = new FileWriter(classResultsFile, true); // append
				// content
				CsvWriter writer = new CsvWriter(fw, ',');
				String[] data = new String[this.classResultsHeaders.length];
				data[0] = Integer.toString(origflare.getCaseID());
				for (int i = 0; i < this.classResultsHeaders.length - 3; i++) {
					data[i + 1] = (String) origflare
							.getProperty(classResultsHeaders[i + 1]);
				}
				data[classResultsHeaders.length - 3] = origClass;
				data[classResultsHeaders.length - 2] = centralClass;
				data[classResultsHeaders.length - 1] = argClass;
				writer.writeRecord(data);
				writer.flush();
				writer.close();

				fw = new FileWriter(summaryPerClassFile, true);
				writer = new CsvWriter(fw, ',');
				data = new String[this.summaryHeaders.length];
				data[0] = origClass;
				data[1] = "1";
				if (centralClass.equals(origClass)) {
					data[2] = "1";
				} else {
					data[2] = "0";
				}
				if (argClass.equals(origClass)) {
					data[3] = "1";
				} else {
					data[3] = "0";
				}
				if (centralClass.equals(origClass)
						&& !argClass.equals(origClass)) {
					data[4] = "1";
				} else {
					data[4] = "0";
				}
				if (!centralClass.equals(origClass)
						&& argClass.equals(origClass)) {
					data[5] = "1";
				} else {
					data[5] = "0";
				}
				if (centralClass.equals(origClass) && argClass.equals(origClass)) {
					data[6] = "1";
				} else {
					data[6] = "0";
				}
				if (!centralClass.equals(origClass) && !argClass.equals(origClass)) {
					data[7] = "1";
				} else {
					data[7] = "0";
				}

				writer.writeRecord(data);
				writer.flush();
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			argConclusion.reset();
			centralConclusion.reset();
			origflare.reset();

			sim.getLogger().info("-----> EVALUATION finished --- RESULTS: ");
			sim.getLogger().info("-> Original Flare: " + origClass);
			sim.getLogger().info("-> Bayes Central: " + centralClass);
			sim.getLogger().info("-> Argumentation: " + argClass);

		}
	}

}
