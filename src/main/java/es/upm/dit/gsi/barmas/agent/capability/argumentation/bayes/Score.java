/**
 * es.upm.dit.gsi.barmas.agent.capability.argumentation.Score.java
 */
package es.upm.dit.gsi.barmas.agent.capability.argumentation.bayes;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.agent.capability.argumentation.Score.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 06/11/2013
 * @version 0.1
 * 
 */
public class Score {

	private String node;
	private String state;
	private int successCount;
	private int totalCount;

	/**
	 * @param successCount the successCount to set
	 */
	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

	/**
	 * @param totalCount the totalCount to set
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * Constructor
	 * 
	 */
	public Score(String node, String state) {
		this.node = node;
		this.state = state;
		this.successCount = 0;
		this.totalCount = 0;
	}

	/**
	 * @param success
	 */
	public void scoreUpdate(boolean success) {
		if (success) {
			this.successCount++;
		}
		this.totalCount++;
	}

	/**
	 * @return
	 */
	public double getRatio() {
		if (totalCount == 0) {
			return 0;
		} else {
			double doubleSC = successCount;
			double doubleTC = totalCount;
			return doubleSC / doubleTC;
		}
	}

	/**
	 * @return the node
	 */
	public String getNode() {
		return node;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @return the successCount
	 */
	public int getSuccessCount() {
		return successCount;
	}

	/**
	 * @return the totalCount
	 */
	public int getTotalCount() {
		return totalCount;
	}
	
}
