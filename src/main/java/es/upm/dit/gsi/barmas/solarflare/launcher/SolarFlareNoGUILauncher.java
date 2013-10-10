/**
 * es.upm.dit.gsi.barmas.simulation.SolarFlareNoGUILauncher.java
 */
package es.upm.dit.gsi.barmas.solarflare.launcher;

import java.util.Properties;

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

		Properties scenarioProperties = new Properties();
		// scenarioProperties.put(MyScenario.CLOUDY_PROB, "5");
		// scenarioProperties.put(Scenario.SIMULATION_GUI,
		// Scenario.SIMULATION_2D);
		// scenarioProperties.put(Scenario.SIMULATION_GUI,
		// Scenario.SIMULATION_3D);
		scenarioProperties.put(Scenario.SIMULATION_GUI, Scenario.NO_GUI);

		SolarFlareClassificationSimulation sim;
		try {
			sim = new SolarFlareClassificationSimulation(
					System.currentTimeMillis(), SolarFlareScenario.class,
					"SolarFlareClassificatorScenario",
					SolarFlareScenario.NORMALSTATE, scenarioProperties);
			// MyShanksSimulation2DGUI gui = new MyShanksSimulation2DGUI(sim);
			// MyShanksSimulation3DGUI gui = new MyShanksSimulation3DGUI(sim);
			// gui.start();
			sim.start();
			do
				if (!sim.schedule.step(sim)) {
					break;
				}
			while (sim.schedule.getSteps() < 201);
			sim.finish();
		} catch (ShanksException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
