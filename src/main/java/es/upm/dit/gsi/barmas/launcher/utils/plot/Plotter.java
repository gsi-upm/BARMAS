/*******************************************************************************
 * Copyright (c) 2013 alvarocarrera Grupo de Sistemas Inteligentes - Universidad Politécnica de Madrid. (GSI-UPM)
 * http://www.gsi.dit.upm.es/
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * 
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 * 
 * Contributors:
 *     alvarocarrera - initial API and implementation
 ******************************************************************************/
/**
 * es.upm.dit.gsi.barmas.launcher.utils.Plotter.java
 */
package es.upm.dit.gsi.barmas.launcher.utils.plot;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.media.opengl.GLException;

import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.Settings;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.primitives.Cylinder;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

import com.jogamp.opengl.util.texture.TextureIO;

/**
 * Project: barmas File: es.upm.dit.gsi.barmas.launcher.utils.Plotter.java
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
public class Plotter {

	private Logger logger;
	private AWTChartComponentFactory factory;

	public Plotter(Logger logger) {
		this.logger = logger;
		this.factory = new AWTChartComponentFactory();
		Settings.getInstance().setHardwareAccelerated(true);
	}

	/**
	 * 
	 */
	private void saveScreenshot(String outputFile, Chart chart, ViewPositionMode viewPosMode) {
		Rectangle window = new Rectangle(200, 200, 600, 600);
	
		FrameAWT frame = (FrameAWT) chart.getFactory().newFrame(chart, window,
				"Chart Frame");
	
		if (viewPosMode != null) {
			chart.setViewMode(viewPosMode);
		}
		try {
			File output = new File(outputFile);
			if (!output.getParentFile().exists()) {
				output.getParentFile().mkdirs();
			}
			TextureIO.write(chart.getCanvas().screenshot(), output);
			frame.remove((java.awt.Component) chart.getCanvas());
			chart.dispose();
			frame.dispose();
		} catch (GLException e) {
			logger.severe("Problem with screenshot: " + e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			logger.severe("Problem with screenshot: " + e.getMessage());
			System.exit(1);
		}
	}

	public void saveDelaunaySurface3DChart(String outputFile,
			List<Coord3d> coordinates, ViewPositionMode viewPosMode) {
		Chart chart = this.getDelaunayChart(coordinates);
		this.saveScreenshot(outputFile, chart, viewPosMode);
	}

	public void saveScatter3DChart(String outputFile,
			List<Coord3d> coordinates, float width, ViewPositionMode viewPosMode) {
		Chart chart = this.getScatterChart(coordinates, width);
		this.saveScreenshot(outputFile, chart, viewPosMode);	
	}

	public void saveCylinder3DChart(String outputFile,
			List<Cylinder> cylinders, ViewPositionMode viewPosMode) {
		Chart chart = this.getCylinder3DChart(cylinders);
		this.saveScreenshot(outputFile, chart, viewPosMode);	
	}

	/**
	 * @param coordinates
	 */
	public void openDelaunayChart(List<Coord3d> coordinates) {
		ChartLauncher.openChart(this.getDelaunayChart(coordinates));
	}

	/**
	 * @param coordinates
	 */
	public void openScatterChart(List<Coord3d> coordinates, float width) {
		ChartLauncher.openChart(this.getScatterChart(coordinates, width));
	}
	
	public void openCylinderChart(List<Cylinder> cylinders) {
		ChartLauncher.openChart(this.getCylinder3DChart(cylinders));
	}

	/**
	 * @param coordinates
	 * @return
	 */
	public Chart getDelaunayChart(List<Coord3d> coordinates) {

		// Create the object to represent the function over the given range.
		Shape surface = Builder.buildDelaunay(coordinates);

		surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface
				.getBounds().getZmin(), surface.getBounds().getZmax(),
				new Color(1, 1, 1, 0.75f)));
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(true);

		// Create a chart
		Chart chart = new Chart(this.factory, Quality.Nicest, "awt", Settings
				.getInstance().getGLCapabilities());
		chart.setAxeDisplayed(true);
		chart.getScene().getGraph().add(surface);

		return chart;
	}

	/**
	 * @param coordinates
	 * @return
	 */
	public Chart getScatterChart(List<Coord3d> coordinates, float width) {

		Coord3d[] points = new Coord3d[coordinates.size()];
		Color[] colors = new Color[coordinates.size()];

		int i = 0;
		for (Coord3d coord : coordinates) {
			points[i] = coord;
			colors[i] = new Color(coord.x, coord.y, coord.z, 0.75f);
			i++;
		}

		Scatter scatter;
		if (width == 0) {
			scatter = new Scatter(points, colors);
		} else {
			scatter = new Scatter(points, colors, width);
		}

		Chart chart = new Chart(this.factory, Quality.Nicest, "awt", Settings
				.getInstance().getGLCapabilities());
		chart.getScene().add(scatter);

		return chart;

	}

	/**
	 * @return
	 */
	public Chart getCylinder3DChart(List<Cylinder> cylinders) {

		Chart chart = new Chart(this.factory, Quality.Nicest, "awt", Settings
				.getInstance().getGLCapabilities());
		for (Cylinder cylinder : cylinders) {
			chart.getScene().add(cylinder);
		}

		return chart;
	}

	/**
	 * @param coord
	 * @param height
	 * @param radius
	 * @return
	 */
	public Cylinder getCylinder(Coord3d baseCenter, float height, float radius) {
		Cylinder cylinder = new Cylinder();
		cylinder.setData(baseCenter, height, radius, 20, 0,
				new Color(1, 1, 1, 0.75f));

		cylinder.setFaceDisplayed(true);
		cylinder.setColorMapper(new ColorMapper(new ColorMapRainbow(), cylinder
				.getBounds().getZmin(), cylinder.getBounds().getZmax(),
				new Color(1, 1, 1, 0.75f)));

		return cylinder;
	}
}
