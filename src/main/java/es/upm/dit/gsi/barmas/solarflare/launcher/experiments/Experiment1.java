/**
 * es.upm.dit.gsi.barmas.solarflare.launcher.experiments.Experiment1.java
 */
package es.upm.dit.gsi.barmas.solarflare.launcher.experiments;

import es.upm.dit.gsi.barmas.solarflare.launcher.SolarFlareNoGUILauncher;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.solarflare.launcher.experiments.Experiment1.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 16/10/2013
 * @version 0.1
 * 
 */
public class Experiment1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String summaryFile = "src/main/resources/exp1/output/global-summary.csv";
		long seed = 0;

		SolarFlareNoGUILauncher.launchSimulationBasic2Agents(seed, summaryFile);

		SolarFlareNoGUILauncher.launchSimulationAdvanced2Agents(seed,
				summaryFile);
	}

}
