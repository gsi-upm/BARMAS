/**
 * es.upm.dit.gsi.barmas.simulation.SolarFlareNoGUILauncher.java
 */
package es.upm.dit.gsi.barmas.solarflare.launcher;

import java.io.File;
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

		SolarFlareNoGUILauncher.launchSimulationBasic();
	}
	
	public static void launchSimulationBasic() {
		// Simulation properties
				long seed = System.currentTimeMillis();
				String simulationName = "SolarFlareClassificatorScenario"
						+ "-2AgentsHigherResolution-" + seed;

				// Logging properties
				Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
				Level level = Level.ALL;
				String name = "NoGUI-" + simulationName;
				String experimentDatasetPath = "src" + File.separator + "main" + File.separator
						+ "resources" + File.separator + "exp1";
				String experimentOutputPath = experimentDatasetPath + File.separator
						+ "output" + File.separator + simulationName;
				LogConfigurator.log2File(logger, name, level, experimentOutputPath);

				logger.info("--> Starting simulation...");

				Properties scenarioProperties = new Properties();
				scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.NO_GUI);
				scenarioProperties.put(SolarFlareClassificationSimulation.EXPDATA, experimentDatasetPath);
				scenarioProperties.put(SolarFlareClassificationSimulation.EXPOUTPUT, experimentOutputPath);

				SolarFlareClassificationSimulation sim;
				try {
					sim = new SolarFlareClassificationSimulation(seed,
							SolarFlareScenario.class, simulationName,
							SolarFlareScenario.NORMALSTATE, scenarioProperties);

					sim.start();
					do
						if (!sim.schedule.step(sim)) {
							break;
						}
					while (true);
					// while (sim.schedule.getSteps() < totalSteps);
					// sim.finish();
				} catch (ShanksException e) {
					e.printStackTrace();
				}
	}
	
//	public static void launchSimulationBasic2() {
//		// Simulation properties
//				long seed = System.currentTimeMillis();
//				String simulationName = "SOther-" + seed;
//
//				// Logging properties
//				Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
//				Level level = Level.ALL;
//				String name = "NoGUI-" + simulationName;
//				String experimentDatasetPath = "src" + File.separator + "main" + File.separator
//						+ "resources" + File.separator + "exp1";
//				String experimentOutputPath = experimentDatasetPath + File.separator
//						+ "output" + File.separator + simulationName;
//				LogConfigurator.log2File(logger, name, level, experimentOutputPath);
//
//				logger.info("--> Starting simulation...");
//
//				Properties scenarioProperties = new Properties();
//				scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.NO_GUI);
//				scenarioProperties.put(SolarFlareClassificationSimulation.EXPDATA, experimentDatasetPath);
//				scenarioProperties.put(SolarFlareClassificationSimulation.EXPOUTPUT, experimentOutputPath);
//
//				SolarFlareClassificationSimulation sim;
//				try {
//					sim = new SolarFlareClassificationSimulation(seed,
//							SolarFlareScenario.class, simulationName,
//							SolarFlareScenario.NORMALSTATE, scenarioProperties);
//
//					sim.start();
//					do
//						if (!sim.schedule.step(sim)) {
//							break;
//						}
//					while (true);
//					// while (sim.schedule.getSteps() < totalSteps);
//					// sim.finish();
//				} catch (ShanksException e) {
//					e.printStackTrace();
//				}
//	}
}
