/**
 * es.upm.dit.gsi.barmas.launcher.utils.ConsoleOutputDisabler.java
 */
package es.upm.dit.gsi.barmas.launcher.utils;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.launcher.utils.ConsoleOutputDisabler.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 31/10/2013
 * @version 0.1
 * 
 */
public class ConsoleOutputDisabler {

	/**
	 * 
	 */
	public static void disableConsoleOutput() {
		System.setOut(new PrintStream(new OutputStream() {
			public void write(int b) {
				// DO NOTHING
			}
		}));
	}
}
