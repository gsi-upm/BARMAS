/**
 * es.upm.dit.gsi.barmas.agent.utils.plot.test.PlotterTest.java
 */
package es.upm.dit.gsi.barmas.utils.plot.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Cylinder;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

import es.upm.dit.gsi.barmas.agent.capability.learning.bayes.test.AgentBayesLearningCapabilityTest;
import es.upm.dit.gsi.barmas.launcher.utils.plot.Plotter;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.agent.utils.plot.test.PlotterTest.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 04/11/2013
 * @version 0.1
 * 
 */
public class PlotterTest {

	private Logger logger = Logger
			.getLogger(AgentBayesLearningCapabilityTest.class.getName());

	private List<Coord3d> coordinates;
	private String outputFile;
	private String[] axisLabels;

	/**
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		LogManager lm = LogManager.getLogManager();
		File configFile = new File("src/test/resources/logging.properties");
		lm.readConfiguration(new FileInputStream(configFile));
	}

	/**
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {

		this.axisLabels = new String[3];
		axisLabels[0] = "X";
		axisLabels[1] = "Y";
		axisLabels[2] = "Z";

		this.coordinates = new ArrayList<Coord3d>();
		coordinates.add(new Coord3d(0, 0, 0));
		coordinates.add(new Coord3d(0, 1, 3));
		coordinates.add(new Coord3d(0, 2, 3));
		coordinates.add(new Coord3d(0, 3, 3));
		coordinates.add(new Coord3d(0, 4, 3));
		coordinates.add(new Coord3d(0, 5, 3));
		coordinates.add(new Coord3d(0, 7, 3));
		coordinates.add(new Coord3d(0, 6, 3));
		coordinates.add(new Coord3d(1, 0, 3));
		coordinates.add(new Coord3d(1, 2, 0));
		coordinates.add(new Coord3d(1, 3, 1));
		coordinates.add(new Coord3d(1, 4, 2));
		coordinates.add(new Coord3d(1, 5, 3));
		coordinates.add(new Coord3d(1, 6, 4));
		coordinates.add(new Coord3d(1, 7, 5));

		this.outputFile = "src/test/resources/screenshots/test.png";
	}

	/**
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		File f = new File(this.outputFile);
		f.delete();
		this.coordinates = null;
	}

	@Test
	public void saveDelaunayChartScreenshotFreeView() {
		Plotter plotter = new Plotter(logger);
		plotter.saveDelaunaySurface3DChart(outputFile, axisLabels, coordinates,
				null, null);
		File f = new File(outputFile);
		Assert.assertTrue(f.exists());
	}

	@Test
	public void saveDelaunayChartScreenshotTopView() {
		Plotter plotter = new Plotter(logger);
		plotter.saveDelaunaySurface3DChart(outputFile, axisLabels, coordinates,
				ViewPositionMode.TOP, null);
		File f = new File(outputFile);
		Assert.assertTrue(f.exists());
	}

	@Test
	public void saveDelaunayChartScreenshotProfileView() {
		Plotter plotter = new Plotter(logger);
		plotter.saveDelaunaySurface3DChart(outputFile, axisLabels, coordinates,
				ViewPositionMode.PROFILE, null);
		File f = new File(outputFile);
		Assert.assertTrue(f.exists());
	}

	@Test
	public void saveDelaunayChartScreenshotFreeExplicitView() {
		Plotter plotter = new Plotter(logger);
		plotter.saveDelaunaySurface3DChart(outputFile, axisLabels, coordinates,
				ViewPositionMode.FREE, null);
		File f = new File(outputFile);
		Assert.assertTrue(f.exists());
	}

	@Test
	public void saveScatterChartScreenshotFreeView() {
		Plotter plotter = new Plotter(logger);
		Random r = new Random();
		r.setSeed(0);
		int size = 1000;
		List<Coord3d> coordinates = new ArrayList<Coord3d>();
		for (int i = 0; i < size; i++) {
			float x = r.nextFloat() - 0.5f;
			float y = r.nextFloat() - 0.5f;
			float z = r.nextFloat() - 0.5f;
			Coord3d coord = new Coord3d(x, y, z);
			coordinates.add(coord);
		}
		plotter.saveScatter3DChart(outputFile, axisLabels, coordinates, 2,
				null, null);
		File f = new File(outputFile);
		Assert.assertTrue(f.exists());
	}

	@Test
	public void saveScatterChartScreenshotTopView() {
		Plotter plotter = new Plotter(logger);
		Random r = new Random();
		r.setSeed(0);
		int size = 1000;
		List<Coord3d> coordinates = new ArrayList<Coord3d>();
		for (int i = 0; i < size; i++) {
			float x = r.nextFloat() - 0.5f;
			float y = r.nextFloat() - 0.5f;
			float z = r.nextFloat() - 0.5f;
			Coord3d coord = new Coord3d(x, y, z);
			coordinates.add(coord);
		}
		plotter.saveScatter3DChart(outputFile, axisLabels, coordinates, 2,
				ViewPositionMode.TOP, null);
		File f = new File(outputFile);
		Assert.assertTrue(f.exists());
	}

	@Test
	public void saveScatterChartScreenshotProfileView() {
		Plotter plotter = new Plotter(logger);
		Random r = new Random();
		r.setSeed(0);
		int size = 1000;
		List<Coord3d> coordinates = new ArrayList<Coord3d>();
		for (int i = 0; i < size; i++) {
			float x = r.nextFloat() - 0.5f;
			float y = r.nextFloat() - 0.5f;
			float z = r.nextFloat() - 0.5f;
			Coord3d coord = new Coord3d(x, y, z);
			coordinates.add(coord);
		}
		plotter.saveScatter3DChart(outputFile, axisLabels, coordinates, 2,
				ViewPositionMode.PROFILE, null);
		File f = new File(outputFile);
		Assert.assertTrue(f.exists());
	}

	@Test
	public void saveScatterChartScreenshotFreeExplicitView() {
		Plotter plotter = new Plotter(logger);
		Random r = new Random();
		r.setSeed(0);
		int size = 1000;
		List<Coord3d> coordinates = new ArrayList<Coord3d>();
		for (int i = 0; i < size; i++) {
			float x = r.nextFloat() - 0.5f;
			float y = r.nextFloat() - 0.5f;
			float z = r.nextFloat() - 0.5f;
			Coord3d coord = new Coord3d(x, y, z);
			coordinates.add(coord);
		}
		plotter.saveScatter3DChart(outputFile, axisLabels, coordinates, 2,
				ViewPositionMode.FREE, null);
		File f = new File(outputFile);
		Assert.assertTrue(f.exists());
	}

	@Test
	public void saveCylinderChartScreenshotFreeExplicitView() {
		Plotter plotter = new Plotter(logger);
		Random r = new Random();
		r.setSeed(0);
		int size = 1000;
		List<Cylinder> cylinders = new ArrayList<Cylinder>();
		for (int i = 0; i < size; i++) {
			float x = r.nextFloat() - 0.5f;
			float y = r.nextFloat() - 0.5f;
			float z = r.nextFloat() - 0.5f;
			Coord3d baseCenter = new Coord3d(x, y, z);
			float height = r.nextFloat() - 0.5f;
			cylinders.add(plotter.getCylinder(baseCenter, height, 0.1f));
		}
		plotter.saveCylinder3DChart(outputFile, axisLabels, cylinders,
				ViewPositionMode.FREE, null);
		File f = new File(outputFile);
		Assert.assertTrue(f.exists());
	}

}
