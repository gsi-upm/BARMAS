/**
 * es.upm.dit.gsi.barmas.simulation.SolarFlareNoGUILauncher.java
 */
package es.upm.dit.gsi.barmas.solarflare.launcher;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.upm.dit.gsi.barmas.solarflare.launcher.logging.LogConfigurator;
import es.upm.dit.gsi.barmas.solarflare.model.scenario.SolarFlareScenario;
import es.upm.dit.gsi.barmas.solarflare.simulation.SolarFlareClassificationSimulation;
import es.upm.dit.gsi.shanks.exception.ShanksException;
import es.upm.dit.gsi.shanks.model.scenario.Scenario;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.simulation.SolarFlareNoGUILauncher.java
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
public class SolarFlareNoGUILauncher {

	public static void main(String[] args) {

		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		Level level = Level.ALL;
//		long date = System.currentTimeMillis();
//		String name = "NoGUI-Basic2Agents-"+Long.toString(date);
		String name = "NoGUI-Basic2Agents";
		LogConfigurator.log2File(logger, name, level);
		
		long totalSteps = 101;
		
		
		 logger.info("--> Starting simulation...");
		 
		Properties scenarioProperties = new Properties();
		scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.NO_GUI);

		SolarFlareClassificationSimulation sim;
		try {
			sim = new SolarFlareClassificationSimulation(
					System.currentTimeMillis(), SolarFlareScenario.class,
					"SolarFlareClassificatorScenario",
					SolarFlareScenario.NORMALSTATE, scenarioProperties);

			sim.start();
			do
				if (!sim.schedule.step(sim)) {
					break;
				}
			while (sim.schedule.getSteps() < totalSteps);
			sim.finish();
		} catch (ShanksException e) {
			e.printStackTrace();
		}
	}
}
