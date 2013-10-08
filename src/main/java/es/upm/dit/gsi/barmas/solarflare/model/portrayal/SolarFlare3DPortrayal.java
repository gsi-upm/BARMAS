/**
 * es.upm.dit.gsi.barmas.solarflare.model.portrayal.SolarFlare3DPortrayal.java
 */
package es.upm.dit.gsi.barmas.solarflare.model.portrayal;

import java.awt.Color;
import java.util.HashMap;

import javax.media.j3d.TransformGroup;

import sim.portrayal3d.simple.SpherePortrayal3D;
import es.upm.dit.gsi.barmas.solarflare.model.SolarFlare;

/**
 * Project: barmas File:
 * es.upm.dit.gsi.barmas.solarflare.model.portrayal.SolarFlare3DPortrayal.java
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
public class SolarFlare3DPortrayal extends SpherePortrayal3D {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6983973368891562557L;

	// Sphere diameter
	private int diameter = 3;

	public TransformGroup getModel(Object object, TransformGroup j3dModel) {
		SolarFlare flare = (SolarFlare) object;
		HashMap<String, Boolean> status = flare.getStatus();
		boolean ready = status.get(SolarFlare.READY);
		int size = 50;

		if (ready) {

			setAppearance(
					j3dModel,
					appearanceForColors(Color.green, null, Color.green, null,
							1.0D, 1.0D));

		} else {

			setAppearance(
					j3dModel,
					appearanceForColors(Color.red, null, Color.red, null, 1.0D,
							1.0D));

		}
		setScale(j3dModel, size * diameter);

		return super.getModel(object, j3dModel);
	}

}
