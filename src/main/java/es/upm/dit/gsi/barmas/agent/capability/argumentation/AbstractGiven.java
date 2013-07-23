/**
 * es.upm.dit.gsi.barmas.model.AbstractGiven.java
 */
package es.upm.dit.gsi.barmas.agent.capability.argumentation;


/**
 * Project: barmas
 * File: es.upm.dit.gsi.barmas.model.AbstractGiven.java
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
public abstract class AbstractGiven {
	
	/**
	 * @return The name of the evidenced node.
	 */
	public abstract String getNode();
	
	/**
	 * @return The value of the given node.
	 */
	public abstract String getValue();

}
