/*******************************************************************************
 * Copyright (c) 2013 alvarocarrera Grupo de Sistemas Inteligentes - Universidad Politécnica de Madrid. (GSI-UPM)
 * http://www.gsi.dit.upm.es/
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * 
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 * 
 * Contributors:
 *     alvarocarrera - initial API and implementation
 ******************************************************************************/
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
@SuppressWarnings("restriction")
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
