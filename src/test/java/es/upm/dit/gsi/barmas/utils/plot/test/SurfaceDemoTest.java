package es.upm.dit.gsi.barmas.utils.plot.test;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.Settings;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.CompileableComposite;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axes.layout.AxeBoxLayout;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;

public class SurfaceDemoTest {

	protected static String DEFAULT_CANVAS_TYPE = "awt";
	public static void main(String[] args) throws Exception {
		SurfaceDemoTest example = new SurfaceDemoTest();
		// example.secondChance();
		example.screenshot();
	}

	// /**
	// *
	// */
	// private void secondChance() {
	// Settings.getInstance().setHardwareAccelerated(true);
	// Chart chart = this.getChart();
	//
	// Display display = new Display();
	// Shell shell = new Shell(display);
	// shell.setLayout(new FillLayout());
	// Bridge.adapt(shell, (Component) chart.getCanvas());
	//
	// shell.setText(this.getName());
	// shell.setSize(800, 600);
	// shell.open();
	//
	// while (!shell.isDisposed()) {
	// if (!display.readAndDispatch())
	// display.sleep();
	// }
	// display.dispose();
	// }

	/**
	 * 
	 */
	private void screenshot() {
		Settings.getInstance().setHardwareAccelerated(true);
		// Define a function to plot
		Mapper mapper = new Mapper() {
			public double f(double x, double y) {
				return Math.sin(x * y);
			}
		};

		// Define range and precision for the function to plot
		Range range = new Range(-2, 2);
		int steps = 100;

		// Create the object to represent the function over the given range.
		Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(
				range, steps, range, steps), mapper);
		surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface
				.getBounds().getZmin(), surface.getBounds().getZmax(),
				new Color(1, 1, 1, 0.75f)));
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(true);
		CompileableComposite surface2 = Builder.buildOrthonormalBig(new OrthonormalGrid(
				range, steps, range, steps), mapper);
		surface2.setFaceDisplayed(true);
		surface2.setWireframeDisplayed(true);
		surface2.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface
				.getBounds().getZmin(), surface.getBounds().getZmax(),
				new Color(1, 1, 1, 0.75f)));
		surface2.setLegend(new AWTColorbarLegend(surface2, new AxeBoxLayout()));
		Shape surface3 = Builder.buildRing(new OrthonormalGrid(
				range, steps, range, steps), mapper, 1f, 1.5f);
		surface3.setFaceDisplayed(true);
		surface3.setWireframeDisplayed(true);
		surface3.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface
				.getBounds().getZmin(), surface.getBounds().getZmax(),
				new Color(1, 1, 1, 0.75f)));
		
		

        int size = 10;
        float x;
        float y;
        float z;
        float a;
		Coord3d[] points = new Coord3d[size];
        Color[]   colors = new Color[size];
        
        Random r = new Random();
        r.setSeed(0);
        
        for(int i=0; i<size; i++){
            x = r.nextFloat() - 0.5f;
            y = r.nextFloat() - 0.5f;
            z = r.nextFloat() - 0.5f;
            points[i] = new Coord3d(x, y, z);
            a = 0.25f;
            colors[i] = new Color(x, y, z, a);
        }
		
		List<Coord3d> list = new ArrayList<Coord3d>();
		for (Coord3d coord : points) {
			list.add(coord);
		}
        
		Shape surface4 = Builder.buildDelaunay(list);
		surface4.setFaceDisplayed(true);
		surface4.setWireframeDisplayed(true);
		surface4.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface
				.getBounds().getZmin(), surface.getBounds().getZmax(),
				new Color(1, 1, 1, 0.75f)));

		// Create a chart
		AWTChartComponentFactory factory = new AWTChartComponentFactory();
		Chart chart = new Chart(factory, Quality.Nicest, SurfaceDemoTest.DEFAULT_CANVAS_TYPE,
				Settings.getInstance().getGLCapabilities());
		chart.setAxeDisplayed(true);
//		chart.getScene().getGraph().add(surface);
		chart.getScene().getGraph().add(surface2);
//		chart.getScene().getGraph().add(surface3);
//		chart.getScene().getGraph().add(surface4);
		
		// Rectangle window = new Rectangle(200, 200, 600,
		// 600);

		ChartLauncher.openChart(chart);

//		FrameAWT frame = (FrameAWT) chart.getFactory().newFrame(chart,
//				window, "My Frame");
//		try {
////			chart.setViewMode(ViewPositionMode.TOP);
//			chart.setViewMode(ViewPositionMode.PROFILE);
//			TextureIO.write(chart.getCanvas().screenshot(), new File(
//					"prueba3.png"));
//			frame.remove((java.awt.Component) chart.getCanvas());
//			chart.dispose();
//			frame.dispose();
//		} catch (GLException e) {
//			System.out.println("Exception: " + e.getMessage());
//		} catch (IOException e) {
//			System.out.println("Exception: " + e.getMessage());
//		}
	}

}