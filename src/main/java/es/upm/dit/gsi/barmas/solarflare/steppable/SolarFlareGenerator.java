/**
 * es.upm.dit.gsi.barmas.solarflare.SolarFlareGenerator.java
 */
package es.upm.dit.gsi.barmas.solarflare.steppable;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import sim.engine.SimState;
import sim.engine.Steppable;

import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;

import es.upm.dit.gsi.barmas.solarflare.model.SolarFlare;
import es.upm.dit.gsi.barmas.solarflare.simulation.SolarFlareClassificationSimulation;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.solarflare.SolarFlareGenerator.java
 * 
 * Grupo de Sistemas Inteligentes Departamento de Ingeniería de Sistemas
 * Telemáticos Universidad Politécnica de Madrid (UPM)
 * 
 * @author alvarocarrera
 * @email a.carrera@gsi.dit.upm.es
 * @twitter @alvarocarrera
 * @date 02/10/2013
 * @version 0.1
 * 
 */
public class SolarFlareGenerator implements Steppable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -481268413730149934L;

	private CSVReader<String[]> data;
	
	/**
	 * Constructor
	 * @throws IOException 
	 * 
	 */
	public SolarFlareGenerator(String path) throws IOException {
		Reader reader = new FileReader(path);
		this.data = CSVReaderBuilder
				.newDefaultReader(reader);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sim.engine.Steppable#step(sim.engine.SimState)
	 */
	public void step(SimState simstate) {
		SolarFlareClassificationSimulation sim = (SolarFlareClassificationSimulation) simstate;
		SolarFlare flare = (SolarFlare) sim.getScenario().getNetworkElement("SolarFlare");

		if (flare.getStatus().get(SolarFlare.READY)) {
			String[] flareCase = this.data.readNext(); 
			
			flare.setCurrentStatus(desiredStatus, SolarFlare.NOT_READY);
		}
	}

}
