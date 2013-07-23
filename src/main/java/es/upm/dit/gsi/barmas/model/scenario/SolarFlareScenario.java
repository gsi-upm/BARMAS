/**
 * es.upm.dit.gsi.barmas.scenario.SolarFlareScenario.java
 */
package es.upm.dit.gsi.barmas.model.scenario;

import java.util.HashMap;
import java.util.Properties;

import es.upm.dit.gsi.barmas.model.SolarFlare;
import es.upm.dit.gsi.barmas.model.scenario.portrayal.SolarFlareScenario2DPortrayal;
import es.upm.dit.gsi.barmas.model.scenario.portrayal.SolarFlareScenario3DPortrayal;
import es.upm.dit.gsi.shanks.exception.ShanksException;
import es.upm.dit.gsi.shanks.model.event.failiure.Failure;
import es.upm.dit.gsi.shanks.model.scenario.Scenario;
import es.upm.dit.gsi.shanks.model.scenario.portrayal.Scenario2DPortrayal;
import es.upm.dit.gsi.shanks.model.scenario.portrayal.Scenario3DPortrayal;

/**
 * Project: barmas
 * File: es.upm.dit.gsi.barmas.scenario.SolarFlareScenario.java
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
public class SolarFlareScenario extends Scenario {

	/**
	 * Constructor
	 *
	 * @param id
	 * @param initialState
	 * @param properties
	 * @throws ShanksException
	 */
	public SolarFlareScenario(String id, String initialState,
			Properties properties) throws ShanksException {
		super(id, initialState, properties);
	}
	
	public static final String NORMALSTATE= "NORMAL";

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.scenario.Scenario#createScenario2DPortrayal()
	 */
	@Override
	public Scenario2DPortrayal createScenario2DPortrayal()
			throws ShanksException {
        return new SolarFlareScenario2DPortrayal(this, 100, 100);
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.scenario.Scenario#createScenario3DPortrayal()
	 */
	@Override
	public Scenario3DPortrayal createScenario3DPortrayal()
			throws ShanksException {
        return new SolarFlareScenario3DPortrayal(this, 100, 100, 100);
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.scenario.Scenario#setPossibleStates()
	 */
	@Override
	public void setPossibleStates() {
        this.addPossibleStatus(NORMALSTATE);
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.scenario.Scenario#addNetworkElements()
	 */
	@Override
	public void addNetworkElements() throws ShanksException {
		SolarFlare s = new SolarFlare();
		this.addNetworkElement(s);
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.scenario.Scenario#addPossibleFailures()
	 */
	@Override
	public void addPossibleFailures() {
		// NONE One steppable is in charge of this
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.scenario.Scenario#addPossibleEvents()
	 */
	@Override
	public void addPossibleEvents() {
		// NONE One steppable is in charge of this
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.shanks.model.scenario.Scenario#getPenaltiesInStatus(java.lang.String)
	 */
	@Override
	public HashMap<Class<? extends Failure>, Double> getPenaltiesInStatus(
			String status) throws ShanksException {
		return null;
	}

}
