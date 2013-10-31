/**
 * es.upm.dit.gsi.barmas.solarflare.launcher.logging.LogConfigurator.java
 */
package es.upm.dit.gsi.barmas.launcher.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.solarflare.launcher.logging.LogConfigurator.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingenier�a de Sistemas
 * Telem�ticos Universidad Polit�cnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 10/10/2013
 * @version 0.1
 * 
 */
public class LogConfigurator {

	/**
	 * Constructor
	 * 
	 */
	public static void log2File(Logger logger, String name, Level level,
			String dir) {
		try {
			File aux = new File(".");
			String parent = aux.getCanonicalPath();
			File f = new File(parent + File.separator + dir);
			if (!f.isDirectory()) {
				boolean made = f.mkdir();
				if (!made) {
					logger.warning("Impossible to create log directory");
				}
			}
			f = new File(parent + File.separator + dir + File.separator
					+ "logs");
			if (!f.isDirectory()) {
				boolean made = f.mkdir();
				if (!made) {
					logger.warning("Impossible to create log directory");
				}
			}
			logger.setLevel(Level.ALL);
			String path = parent + File.separator + dir + File.separator
					+ "logs" + File.separator + name + "%u.log";
			FileHandler fh = new FileHandler(path);
			fh.setFormatter(new SimpleFormatter());
			fh.setEncoding("UTF-8");
			fh.setLevel(level);
			logger.addHandler(fh);
		} catch (IOException e) {
			logger.warning("Error configuring the log file.");
			e.printStackTrace();
		}
	}

}
