/**
 * es.upm.dit.gsi.barmas.model.Given.java
 */
package es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes;

import es.upm.dit.gsi.barmas.agent.capability.argumentation.AbstractGiven;

/**
 * Project: barmas
 * File: es.upm.dit.gsi.barmas.model.Given.java
 * 
 * Grupo de Sistemas Inteligentes
 * Departamento de Ingeniería de Sistemas Telemáticos
 * Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 22/07/2013
 * @version 0.1
 * 
 */
public class Given extends AbstractGiven {

	private String node;
	private String value;

	/**
	 * Constructor
	 *
	 */
	public Given(String node, String value) {
		this.node = node;
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.barmas.model.AbstractGiven#getNode()
	 */
	@Override
	public String getNode() {
		return this.node;
	}

	/* (non-Javadoc)
	 * @see es.upm.dit.gsi.barmas.model.AbstractGiven#getValue()
	 */
	@Override
	public String getValue() {
		return this.value;
	}

}
