/**
 * es.upm.dit.gsi.barmas.solarflare.simulation.SolarFlare3DGUILauncher.java
 */
package es.upm.dit.gsi.barmas.solarflare.launcher;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.upm.dit.gsi.barmas.solarflare.launcher.logging.LogConfigurator;
import es.upm.dit.gsi.barmas.solarflare.model.scenario.SolarFlareScenario;
import es.upm.dit.gsi.barmas.solarflare.simulation.SolarFlareClassificationSimulation;
import es.upm.dit.gsi.barmas.solarflare.simulation.SolarFlareClassificationSimulation3DGUI;
import es.upm.dit.gsi.shanks.model.scenario.Scenario;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.solarflare.simulation.SolarFlare3DGUILauncher.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 02/10/2013
 * @version 0.1
 * 
 */
public class SolarFlare3DGUILauncher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Simulation properties
		long seed = System.currentTimeMillis();
		String simulationName = "SolarFlareClassificatorScenario"
				+ "-2AgentsHigherResolution-" + seed;

		// Logging properties
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		Level level = Level.ALL;
		// long date = System.currentTimeMillis();
		// String name = "NoGUI-Basic2Agents-"+Long.toString(date);
		String name = "NoGUI-" + simulationName;
		String dir = "src/main/resources/output" + simulationName;
		LogConfigurator.log2File(logger, name, level, dir);

		Properties scenarioProperties = new Properties();
		scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.SIMULATION_3D);

		SolarFlareClassificationSimulation sim;
		try {
			sim = new SolarFlareClassificationSimulation(seed,
					SolarFlareScenario.class, simulationName,
					SolarFlareScenario.NORMALSTATE, scenarioProperties);
			SolarFlareClassificationSimulation3DGUI gui = new SolarFlareClassificationSimulation3DGUI(
					sim);
			gui.start();
			// do {
			// if (!gui.getSimulation().schedule.step(sim))
			// break;
			// } while (gui.getSimulation().schedule.getSteps() < 2001);
			// gui.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
