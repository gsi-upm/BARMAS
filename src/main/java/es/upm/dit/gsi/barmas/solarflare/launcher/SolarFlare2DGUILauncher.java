/**
 * es.upm.dit.gsi.barmas.solarflare.simulation.SolarFlare2DGUILauncher.java
 */
package es.upm.dit.gsi.barmas.solarflare.launcher;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.upm.dit.gsi.barmas.solarflare.launcher.logging.LogConfigurator;
import es.upm.dit.gsi.barmas.solarflare.model.scenario.SolarFlareScenario;
import es.upm.dit.gsi.barmas.solarflare.simulation.SolarFlareClassificationSimulation;
import es.upm.dit.gsi.barmas.solarflare.simulation.SolarFlareClassificationSimulation2DGUI;
import es.upm.dit.gsi.shanks.model.scenario.Scenario;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.solarflare.simulation.SolarFlare2DGUILauncher.java
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
public class SolarFlare2DGUILauncher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {


		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		Level level = Level.FINE;
		long date = System.currentTimeMillis();
		String name = "NoGUI-Basic2Agents-"+Long.toString(date);
		LogConfigurator.log2File(logger, name, level);
		
		Properties scenarioProperties = new Properties();
		scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.SIMULATION_2D);

		SolarFlareClassificationSimulation sim;
		try {
			sim = new SolarFlareClassificationSimulation(
					System.currentTimeMillis(), SolarFlareScenario.class,
					"SolarFlareClassificatorScenario",
					SolarFlareScenario.NORMALSTATE, scenarioProperties);
			SolarFlareClassificationSimulation2DGUI gui = new SolarFlareClassificationSimulation2DGUI(
					sim);
			gui.start();
//			do {
//				if (!gui.getSimulation().schedule.step(sim))
//					break;
//			} while (gui.getSimulation().schedule.getSteps() < 2001);
//			gui.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
