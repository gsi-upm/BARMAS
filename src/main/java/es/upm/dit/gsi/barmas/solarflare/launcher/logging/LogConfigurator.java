/**
 * es.upm.dit.gsi.barmas.solarflare.launcher.logging.LogConfigurator.java
 */
package es.upm.dit.gsi.barmas.solarflare.launcher.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Project: barmas
 * File: es.upm.dit.gsi.barmas.solarflare.launcher.logging.LogConfigurator.java
 * 
 * Grupo de Sistemas Inteligentes
 * Departamento de Ingeniería de Sistemas Telemáticos
 * Universidad Politécnica de Madrid (UPM)
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
	public static void log2File (Logger logger, String name, Level level) {
		 try {
	            File aux = new File(".");
	            String parent = aux.getCanonicalPath();
	            File f = new File(parent + File.separator + "log");
	            if (!f.isDirectory()) {
	                boolean made = f.mkdir();
	                if (!made) {
	                    logger.warning("Impossible to create log directory");
	                }
	            }
	            String path = parent + File.separator + "log" + File.separator
	                    + name + "%u.log";
	            FileHandler fh = new FileHandler(path);
	            fh.setFormatter(new SimpleFormatter());
	            fh.setEncoding("UTF-8");
	            logger.addHandler(fh);
	            logger.setLevel(level);
	        } catch (IOException e) {
	            logger.warning("Error configuring the log file.");
	            e.printStackTrace();
	        }
	}

}
