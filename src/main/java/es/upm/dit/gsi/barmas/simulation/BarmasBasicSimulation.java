/**
 * es.upm.dit.gsi.barmas.simulation.BarmasBasicSimulation.java
 */
package es.upm.dit.gsi.barmas.simulation;

import java.util.Properties;

import es.upm.dit.gsi.shanks.ShanksSimulation;
import es.upm.dit.gsi.shanks.exception.ShanksException;
import es.upm.dit.gsi.shanks.model.scenario.Scenario;

/**
 * Project: barmas
 * File: es.upm.dit.gsi.barmas.simulation.BarmasBasicSimulation.java
 * 
 * Grupo de Sistemas Inteligentes
 * Departamento de Ingeniería de Sistemas Telemáticos
 * Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 23/07/2013
 * @version 0.1
 * 
 */
public class BarmasBasicSimulation extends ShanksSimulation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4766549890044944967L;

	/**
	 * Constructor
	 *
	 * @param seed
	 * @param scenarioClass
	 * @param scenarioID
	 * @param initialState
	 * @param properties
	 * @throws ShanksException
	 */
	public BarmasBasicSimulation(long seed,
			Class<? extends Scenario> scenarioClass, String scenarioID,
			String initialState, Properties properties) throws ShanksException {
		super(seed, scenarioClass, scenarioID, initialState, properties);
		// TODO Auto-generated constructor stub
	}

}
