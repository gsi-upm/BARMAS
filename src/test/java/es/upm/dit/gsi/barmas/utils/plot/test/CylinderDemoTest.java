/*
 * 
 */
package es.upm.dit.gsi.barmas.utils.plot.test;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.Settings;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Cylinder;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * .CylinderDemo.java
 */

/**
 * Project: barmas File: .CylinderDemo.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@upm.es
 * @twitter @alvarocarrera
 * @date 04/11/2013
 * @version 0.1
 * 
 */
public class CylinderDemoTest {

	public static void main(String[] args) {

		AWTChartComponentFactory factory = new AWTChartComponentFactory();
		Chart chart = new Chart(factory, Quality.Nicest, SurfaceDemoTest.DEFAULT_CANVAS_TYPE,
				Settings.getInstance().getGLCapabilities());

		Cylinder cylinder = new Cylinder();
		cylinder.setData(new Coord3d(0, 0, 0), 5, 1, 20, 0, new Color(1, 1, 1, 0.75f));

		cylinder.setFaceDisplayed(true);
		// cylinder.setWireframeDisplayed(true);
		cylinder.setColorMapper(new ColorMapper(new ColorMapRainbow(), cylinder.getBounds()
				.getZmin(), cylinder.getBounds().getZmax(), new Color(1, 1, 1, 0.75f)));

		chart.getScene().getGraph().add(cylinder);

		cylinder = new Cylinder();
		cylinder.setData(new Coord3d(-10, -10, -10), 10, 3, 20, 0, new Color(-10, -10, -10, 0.75f));

		cylinder.setFaceDisplayed(true);
		cylinder.setWireframeDisplayed(true);
		cylinder.setColorMapper(new ColorMapper(new ColorMapRainbow(), cylinder.getBounds()
				.getZmin(), cylinder.getBounds().getZmax(), new Color(10, 10, 10, 0.75f)));

		chart.getScene().getGraph().add(cylinder);

		ChartLauncher.openChart(chart);
	}
}
