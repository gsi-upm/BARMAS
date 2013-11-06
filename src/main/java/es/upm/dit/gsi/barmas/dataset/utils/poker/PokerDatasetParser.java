/**
 * es.upm.dit.gsi.barmas.dataset.utils.poker.PokerDatasetParser.java
 */
package es.upm.dit.gsi.barmas.dataset.utils.poker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.dataset.utils.poker.PokerDatasetParser.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 06/11/2013
 * @version 0.1
 * 
 */
public class PokerDatasetParser {

	private Logger logger;

	/**
	 * Constructor
	 * 
	 */
	public PokerDatasetParser(Logger logger) {
		this.logger = logger;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logger logger = Logger.getLogger(PokerDatasetParser.class
				.getSimpleName());
		PokerDatasetParser parser = new PokerDatasetParser(logger);
		parser.parse("src/main/resources/dataset/poker-orig.csv",
				"src/main/resources/dataset/poker.csv");
	}

	/**
	 * @param origninalFile
	 * @param outputFile
	 */
	private void parse(String origninalFile, String outputFile) {
		
		try {
			CsvReader reader = new CsvReader(origninalFile);
			CsvWriter writer = new CsvWriter(outputFile);
			reader.readHeaders();
			String[] headers = reader.getHeaders();
			writer.writeRecord(headers);
			
			int counter = 0;
			while (reader.readRecord()) {
				String[] row = reader.getValues();
				String[] outputRow = new String[row.length];
				for (int i = 0; i<row.length; i++) {
					outputRow[i] = this.getTranslation(row[i]);
				}
				writer.writeRecord(outputRow);
				counter++;
			}
			
			writer.flush();
			writer.close();
			reader.close();	
			logger.info("Poker dataset parsed. Written Rows: " + counter);
			
		} catch (FileNotFoundException e) {
			logger.severe("Expection: " + e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			logger.severe("Expection: " + e.getMessage());
			System.exit(1);
		}

	}

	private String getTranslation(String original) {
		if (original.equals("1")) {
			return "Ace";
		} else if (original.equals("2")) {
			return "Two";
		} else if (original.equals("3")) {
			return "Three";
		} else if (original.equals("4")) {
			return "Four";
		} else if (original.equals("5")) {
			return "Five";
		} else if (original.equals("6")) {
			return "Six";
		} else if (original.equals("7")) {
			return "Seven";
		} else if (original.equals("8")) {
			return "Eight";
		} else if (original.equals("9")) {
			return "Nine";
		} else if (original.equals("10")) {
			return "Jack";
		} else if (original.equals("11")) {
			return "Queen";
		} else if (original.equals("12")) {
			return "King";
		} else {
			return "Unknown";
		}
	}

}
