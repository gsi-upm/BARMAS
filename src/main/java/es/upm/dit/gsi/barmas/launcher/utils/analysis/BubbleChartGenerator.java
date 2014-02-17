/**
 * es.upm.dit.gsi.barmas.launcher.utils.analysis.BubbleChartGenerator.java
 */
package es.upm.dit.gsi.barmas.launcher.utils.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Cylinder;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

import com.csvreader.CsvReader;

import es.upm.dit.gsi.barmas.launcher.logging.LogConfigurator;
import es.upm.dit.gsi.barmas.launcher.utils.ExperimentsAnalyser;
import es.upm.dit.gsi.barmas.launcher.utils.plot.Plotter;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.launcher.utils.analysis.BubbleChartGenerator.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 12/02/2014
 * @version 0.1
 * 
 */
public class BubbleChartGenerator {

	private Logger logger;
	private String summaryFile;
	private String outputFolder;
	private String outputFileName;

	/**
	 * Constructor
	 * 
	 */
	public BubbleChartGenerator(Logger logger, String summaryFile, String outputFolder,
			String outputFileName) {
		this.logger = logger;
		LogConfigurator.log2File(logger, ExperimentsAnalyser.class.getSimpleName(), Level.ALL,
				Level.INFO, outputFolder);
		this.summaryFile = summaryFile;
		this.outputFolder = outputFolder;
		this.outputFileName = outputFileName;
	}

	public void generateBubbleChart() {

		CsvReader reader;
		try {
			reader = new CsvReader(new FileReader(new File(this.summaryFile)));
			reader.readHeaders();
			// String[] headers = reader.getHeaders();

			Plotter plotter = new Plotter(this.logger);
			List<Cylinder> cylinders = new ArrayList<Cylinder>();

			while (reader.readRecord()) {

				String[] row = reader.getValues();

				// Parse data
				String lebaS = row[4];
				int leba = Integer.parseInt(lebaS);
				String agentsS = row[5];
				int agents = Integer.parseInt(agentsS);
				String impS = row[6];
				float imp = Float.parseFloat(impS);

				// Build cylinder
				float radius = (float) imp * 2;
				float floor = 0f;
				Coord3d baseCentre = new Coord3d(leba, agents, floor);
				float height = 1f;
				Cylinder cylinder = plotter.getCylinder(baseCentre, height, radius);
				cylinder.setColorMapper(new ColorMapper(new ColorMapRainbow(), new Color(height,
						height, height)));
				cylinders.add(cylinder);
			}
			reader.close();

			// Add new cylinder to avoid deformation (optional)
			float radius = (float) 0;
			float floor = 0f;
			Coord3d baseCentre = new Coord3d(10, 10, floor);
			float height = 1f;
			Cylinder cylinder = plotter.getCylinder(baseCentre, height, radius);
			cylinder.setColorMapper(new ColorMapper(new ColorMapRainbow(), new Color(height,
					height, height)));
			cylinders.add(cylinder);

			logger.info("Generating chart");
			String[] axisLabels = new String[3];
			axisLabels[0] = "LEBA";
			axisLabels[1] = "AGENTS";
			axisLabels[2] = "";
			// plotter.openCylinderChart(cylinders);
			plotter.saveCylinder3DChart(this.outputFolder + "/" + this.outputFileName, axisLabels,
					cylinders, ViewPositionMode.TOP, new Coord3d(0, 1, 0));

			logger.info("Bubble chart generated");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String dataset = "nursery";
		String file = "../experiments/results/results-" + dataset + ".csv";
		String outputFolder = "../experiments/results/output";
		String outputFileName = "bubbleChart-" + dataset + ".png";
		Logger logger = Logger.getLogger(ExperimentsAnalyser.class.getSimpleName());
		BubbleChartGenerator gen = new BubbleChartGenerator(logger, file, outputFolder,
				outputFileName);
		gen.generateBubbleChart();

	}

}
