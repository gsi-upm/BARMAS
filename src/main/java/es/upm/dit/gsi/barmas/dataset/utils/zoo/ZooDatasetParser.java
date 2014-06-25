/*******************************************************************************
 * Copyright  (C) 2014 Álvaro Carrera Barroso
 * Grupo de Sistemas Inteligentes - Universidad Politecnica de Madrid
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
/**
 * es.upm.dit.gsi.barmas.dataset.utils.zoo.ZooDatasetParser.java
 */
package es.upm.dit.gsi.barmas.dataset.utils.zoo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import es.upm.dit.gsi.barmas.dataset.utils.poker.PokerDatasetParser;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.dataset.utils.zoo.ZooDatasetParser.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 13/11/2013
 * @version 0.1
 * 
 */
public class ZooDatasetParser {

	private Logger logger;

	/**
	 * Constructor
	 * 
	 */
	public ZooDatasetParser(Logger logger) {
		this.logger = logger;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logger logger = Logger.getLogger(PokerDatasetParser.class.getSimpleName());
		ZooDatasetParser parser = new ZooDatasetParser(logger);
		parser.parse("src/main/resources/dataset/zoo-orig.csv",
				"src/main/resources/dataset/zoo.csv");
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

			int legsPos = -1;
			for (int i = 0; i < headers.length; i++) {
				if (headers[i].equals("Legs")) {
					legsPos = i;
				}
			}

			int counter = 0;
			while (reader.readRecord()) {
				String[] row = reader.getValues();
				String[] outputRow = new String[row.length];
				for (int i = 0; i < row.length; i++) {
					if (i == legsPos || i == headers.length - 1) {
						outputRow[i] = this.getLegsTranslation(row[i]);
					} else {
						outputRow[i] = this.getTranslationBoolean(row[i]);
					}
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

	private String getTranslationBoolean(String original) {
		if (original.equals("1")) {
			return "True";
		} else if (original.equals("0")) {
			return "False";
		} else {
			return "Unknown";
		}
	}

	private String getLegsTranslation(String original) {
		if (original.equals("1")) {
			return "One";
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
			return "Ten";
		} else if (original.equals("11")) {
			return "Eleven";
		} else if (original.equals("12")) {
			return "Twelve";
		} else {
			return "Unknown";
		}
	}

}
