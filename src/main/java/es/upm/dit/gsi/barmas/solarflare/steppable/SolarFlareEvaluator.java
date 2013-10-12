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
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
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
	private String[] headers;
	private String classResultsFile;

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
			this.headers = newHeaders;
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
				String[] data = new String[this.headers.length];
				for (int i = 0; i < this.headers.length - 3; i++) {
					data[i] = (String) origflare.getProperty(headers[i]);
				}
				data[headers.length - 3] = origClass;
				data[headers.length - 2] = centralClass;
				data[headers.length - 1] = argClass;
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
