/**
 * es.upm.dit.gsi.barmas.solarflare.model.portrayal.SolarFlare3DPortrayal.java
 */
package es.upm.dit.gsi.barmas.solarflare.model.portrayal;

import java.awt.Color;

import javax.media.j3d.TransformGroup;

import sim.portrayal3d.simple.SpherePortrayal3D;

/**
 * Project: barmas
 * File: es.upm.dit.gsi.barmas.solarflare.model.portrayal.SolarFlare3DPortrayal.java
 * 
 * Grupo de Sistemas Inteligentes
 * Departamento de Ingeniería de Sistemas Telemáticos
 * Universidad Politécnica de Madrid (UPM)
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
//		SolarFlare flare = (SolarFlare) object;
		int	size = 50;

		setAppearance(
				j3dModel,
				appearanceForColors(Color.green, null, Color.green, null, 1.0D, 1.0D));

		setScale(j3dModel, size * diameter);

		return super.getModel(object, j3dModel);
	}

}
