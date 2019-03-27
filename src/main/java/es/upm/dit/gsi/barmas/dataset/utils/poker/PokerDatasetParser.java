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
 * @email a.carrera@upm.es
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
		Logger logger = Logger.getLogger(PokerDatasetParser.class.getSimpleName());
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
			for (int i = 0; i < headers.length; i++) {
				if (headers[i].startsWith("S")) {
					headers[i] = "Suit" + headers[i].substring(1);
				} else if (headers[i].startsWith("C")) {
					headers[i] = "Card" + headers[i].substring(1);
				}
			}
			headers[headers.length - 1] = "PokerHand";
			writer.writeRecord(headers);

			int counter = 0;
			while (reader.readRecord()) {
				String[] row = reader.getValues();
				String[] outputRow = new String[row.length];
				for (int i = 0; i < row.length; i++) {
					if (i == headers.length - 1) {
						outputRow[i] = this.getHandTranslation(row[i]);
					} else if (i % 2 == 0) {
						outputRow[i] = this.getSuitTranslation(row[i]);
					} else {
						outputRow[i] = this.getCardTranslation(row[i]);
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

	private String getSuitTranslation(String original) {
		if (original.equals("1")) {
			return "Spades";
		} else if (original.equals("2")) {
			return "Hearts";
		} else if (original.equals("3")) {
			return "Diamonds";
		} else if (original.equals("4")) {
			return "Clubs";
		} else {
			return "Unknown";
		}
	}

	private String getCardTranslation(String original) {
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
		} else if (original.equals("13")) {
			return "Joker";
		} else {
			return "Unknown";
		}
	}

	private String getHandTranslation(String original) {
		if (original.equals("0")) {
			return "HighCard";
		} else if (original.equals("1")) {
			return "OnePair";
		} else if (original.equals("2")) {
			return "TwoPair";
		} else if (original.equals("3")) {
			return "ThreeOfAKind";
		} else if (original.equals("4")) {
			return "Straight";
		} else if (original.equals("5")) {
			return "Flush";
		} else if (original.equals("6")) {
			return "FullHouse";
		} else if (original.equals("7")) {
			return "FourOfAKind";
		} else if (original.equals("8")) {
			return "StraightFlush";
		} else if (original.equals("9")) {
			return "RoyalFlush";
		} else {
			return "Unknown";
		}
	}

}
