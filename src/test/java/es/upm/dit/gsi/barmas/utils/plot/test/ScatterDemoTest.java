package es.upm.dit.gsi.barmas.utils.plot.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import es.upm.dit.gsi.barmas.launcher.utils.plot.Plotter;

public class ScatterDemoTest extends AbstractAnalysis {
	public static void main(String[] args) throws Exception {
		// AnalysisLauncher.open(new ScatterDemo());
		Plotter plotter = new Plotter(Logger.getLogger("plotter"));

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

		plotter.openScatterChart(coordinates, 10);
	}

	public void init() {
		int size = 500000;
		float x;
		float y;
		float z;
		float a;

		Coord3d[] points = new Coord3d[size];
		Color[] colors = new Color[size];

		Random r = new Random();
		r.setSeed(0);

		for (int i = 0; i < size; i++) {
			x = r.nextFloat() - 0.5f;
			y = r.nextFloat() - 0.5f;
			z = r.nextFloat() - 0.5f;
			points[i] = new Coord3d(x, y, z);
			a = 0.25f;
			colors[i] = new Color(x, y, z, a);
		}

		Scatter scatter = new Scatter(points, colors);
		// scatter = new Scatter(points, colors, 5);
		chart = AWTChartComponentFactory.chart(Quality.Advanced, "newt");
		chart.getScene().add(scatter);
	}
}
