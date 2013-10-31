/**
 * es.upm.dit.gsi.barmas.kowlancz.dataset.utils.DatasetBuilder.java
 */
package es.upm.dit.gsi.barmas.dataset.utils.kowlancz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.kowlancz.dataset.utils.DatasetBuilder.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 31/10/2013
 * @version 0.1
 * 
 */
public class DatasetBuilder {

	private Logger logger = Logger.getLogger(DatasetBuilder.class.getName());

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		DatasetBuilder builder = new DatasetBuilder();
		List<String> scenarios = new ArrayList<String>();
		scenarios.add("VpnExpressKomfort");
		scenarios.add("CZ01");
		scenarios.add("CZ02");
		scenarios.add("CZ03");
		scenarios.add("CZ05");

		String path = "src/main/resources/dataset/kowlancz/";

		for (String scenario : scenarios) {
			List<String> header = builder.getTableHeader(path, scenario);
			File file = new File(path + scenario + "/" + scenario
					+ "-dataset.csv");
			builder.createCSVFile(file, header);
			builder.fillDiagnosesForHeader(path, header, file, scenario);
			builder.checkValidDataset(file);
		}

	}

	/**
	 * @param file
	 * @throws IOException
	 */
	private void checkValidDataset(File file) throws IOException {
		CsvReader reader = new CsvReader(new FileReader(file));

		reader.readHeaders();
		int counter = 0;
		int valids = 0;
		while (reader.readRecord()) {
			String[] row = reader.getValues();
			boolean valid = this.checkValidRow(row);
			if (valid) {
				valids++;
			}
			counter++;
		}
		logger.info("Empty values counter for file " + file.getName() + ": "
				+ (counter - valids) + " Total: " + counter + " Valids: "
				+ valids);
	}

	/**
	 * @param row
	 * @return
	 */
	private boolean checkValidRow(String[] row) {
		for (String v : row) {
			if (v == null || v.equals("")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param scenario
	 * @return
	 */
	private List<String> getTableHeader(String path, String scenario) {
		List<String> header = new ArrayList<String>();
		try {

			File observationcsv = new File(path + scenario + "/observation.csv");
			CsvReader observations = new CsvReader(new FileReader(
					observationcsv));

			observations.readHeaders();

			while (observations.readRecord()) {
				String obstype = observations.get(1);
				if (!header.contains(obstype)) {
					header.add(obstype);
				}
			}
			observations.close();

			header.add("Diagnosis");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		logger.info("Scenario: " + scenario + " -> Total number of columns: "
				+ header.size());
		return header;
	}

	/**
	 * @param string
	 * @param header
	 * @return
	 */
	private void createCSVFile(File file, List<String> header) {
		try {
			CsvWriter csv = new CsvWriter(new FileWriter(file), ',');

			String[] finalHeaders = new String[header.size()];
			int pos = 0;
			for (String column : header) {
				finalHeaders[pos++] = column;
			}
			csv.writeRecord(finalHeaders);
			csv.close();

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * @param header
	 * @param file
	 */
	private void fillDiagnosesForHeader(String path, List<String> header,
			File file, String scenario) {
		try {
			CsvReader datasetFile = new CsvReader(new FileReader(file));

			datasetFile.readHeaders();
			String[] datasetHeaders = datasetFile.getHeaders();

			File operationscsv = new File(path + scenario + "/operations.csv");
			CsvReader operations = new CsvReader(new FileReader(operationscsv));

			HashMap<String, String[]> data = new HashMap<String, String[]>();

			operations = new CsvReader(new FileReader(operationscsv));
			operations.readHeaders();
			while (operations.readRecord()) {
				String[] row = new String[datasetHeaders.length];
				data.put(operations.get(0), row);
			}

			File observationcsv = new File(path + scenario + "/observation.csv");
			CsvReader observations = new CsvReader(new FileReader(
					observationcsv));

			observations.readHeaders();

			while (observations.readRecord()) {
				if (data.containsKey(observations.get(0))) {
					int column = this.lookPosFor(observations.get(1),
							datasetHeaders);
					String[] row = data.get(observations.get(0));
					String composedValue = observations.get(2);
					String value = composedValue.split("_")[1];
					row[column] = value;
				}
			}

			File beliefcsv = new File(path + scenario + "/belief.csv");
			CsvReader beliefs = new CsvReader(new FileReader(beliefcsv));

			beliefs.readHeaders();

			HashMap<String, HashMap<String, String>> beliefsPerOperationID = new HashMap<String, HashMap<String, String>>();

			while (beliefs.readRecord()) {
				if (data.containsKey(beliefs.get(0))) {
					if (!beliefsPerOperationID.containsKey(beliefs.get(0))) {
						beliefsPerOperationID.put(beliefs.get(0),
								new HashMap<String, String>());
					}
					beliefsPerOperationID.get(beliefs.get(0)).put(
							beliefs.get(1), beliefs.get(3));
				}
			}

			beliefs.close();

			for (Entry<String, HashMap<String, String>> bels : beliefsPerOperationID
					.entrySet()) {
				String higherHyp = this.getHigherHypothesis(bels.getValue());
				String[] row = data.get(bels.getKey());
				row[datasetHeaders.length - 1] = higherHyp;
			}

			CsvWriter datasetFileWriter = new CsvWriter(new FileWriter(file,
					true), ',');

			int valids = 0;
			int invalids = 0;
			for (Entry<String, String[]> datum : data.entrySet()) {
				if (this.checkValidRow(datum.getValue())) {
					datasetFileWriter.writeRecord(datum.getValue());
					valids++;
				} else {
					invalids++;
					logger.finer("Data row with empty values found for file "
							+ file.getName() + " -> Values: "
							+ datum.getValue());
				}
			}
			logger.info("File: " + file.getName() + " is ready!");
			logger.info("-> Number of valid diagnoses: " + valids);
			logger.info("-> Number of invalid diagnoses: " + invalids);
			logger.info("-> Total Number of diagnoses: " + (valids + invalids));

			datasetFileWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * @param value
	 * @return
	 */
	private String getHigherHypothesis(HashMap<String, String> value) {
		double max = 0;
		String higherHyp = "";
		for (Entry<String, String> e : value.entrySet()) {
			double conf = new Double(e.getValue());
			if (conf > max) {
				max = conf;
				higherHyp = e.getKey();
			}
		}

		return higherHyp;
	}

	private int lookPosFor(String column, String[] headers) {

		for (int i = 0; i < headers.length; i++) {
			if (column.equals(headers[i])) {
				return i;
			}
		}

		return -1;
	}

}
