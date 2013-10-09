/**
 * es.upm.dit.gsi.barmas.agent.capability.argumentation.DatasetGenerator.java
 */
package es.upm.dit.gsi.barmas.solarflare.dataset.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
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
 * @version 0.1
 * 
 */
public class DatasetGenerator {

	/**
	 * Constructor
	 * 
	 */
	public DatasetGenerator() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Logger logger = Logger.getLogger(DatasetGenerator.class.getName());
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
			CsvWriter dataCentralWriter = new CsvWriter(new FileWriter(dataSetCentral), ',');

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
					logger.warning("WARNING!!! Error writing datasets! i="+i);
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
}
