/**
 * es.upm.dit.gsi.barmas.model.AbstractProposal.java
 */
package es.upm.dit.gsi.barmas.agent.capability.argumentation;

import java.util.Map;
import java.util.Set;

/**
 * Project: barmas
 * File: es.upm.dit.gsi.barmas.model.AbstractProposal.java
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
public abstract class AbstractProposal {
	
	/**
	 * @return The name of the proposed node.
	 */
	public abstract String getNode();
	
	/**
	 * @return The possible values of the node.
	 */
	public abstract Set<String> getValues();
	
	/**
	 * @return All values of the node with their confidences.
	 */
	public abstract Map<String,Double> getValuesWithConfidence();
	
	/**
	 * @param value
	 * @return The confidence of this value/state for the proposed node.
	 */
	public abstract double getConfidenceForValue(String value);	
	

}
