package de.htw.berlin.polysun4diac.plugins;

import static de.htw.berlin.polysun4diac.CommonFunctionsAndConstants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.velasolaris.plugin.controller.spi.PluginControllerConfiguration;
import com.velasolaris.plugin.controller.spi.PluginControllerException;
import com.velasolaris.plugin.controller.spi.PluginControllerConfiguration.Sensor;

import de.htw.berlin.polysun4diac.forte.comm.CommLayerParams;
import de.htw.berlin.polysun4diac.forte.datatypes.ForteDataType;

/**
 * Plugin controller for sending the PV field's power to 4diac-RTE (FORTE).
 * @author Marc Jakobi</p>HTW Berlin</p>July 2017
 * @see de.htw.berlin.polysun4diac.forte.comm.IForteSocket
 * @see de.htw.berlin.polysun4diac.forte.comm.CommLayerParams
 * @see com.velasolaris.plugin.controller.spi.IPluginController
 */
public class PVSensorController extends AbstractSensorController {

	private static final String SENSOR1 = "PV power output AC";
	private static final String SENSOR2 = "Maximum grid feed-in";
	
	public PVSensorController() throws PluginControllerException {
		super();
	}

	@Override
	public String getName() {
		return "Photovoltaics Sensor";
	}
	
	@Override
	public String getDescription() {
		return "Sensor for sending the PV field's AC power output to 4diac-RTE (FORTE).";
	}

	@Override
	public PluginControllerConfiguration getConfiguration(Map<String, Object> parameters) {
		List<Sensor> sensors = new ArrayList<>();
		sensors.add(new Sensor(SENSOR1, "W", true, false, "The AC power output of the PV field."));
		sensors.add(new Sensor(SENSOR2, "", true, false, "The feed-in limit as set by the internal grid."));
		return new PluginControllerConfiguration(initialisePropertyList(), sensors, null, null, 0, 0, 0, getPluginIconResource(), null);
	}
	
	@Override
	protected void initialiseConnection(String host, int port) throws PluginControllerException {
		// Default service type of CommLayerParams is client.
		CommLayerParams params = new CommLayerParams(host, port);
		// Inputs to send to FORTE
		params.addInput(ForteDataType.LREAL); // Input 1: PV power
		params.addInput(ForteDataType.LREAL); // Input 2: Feed-in limit
		if (sendTimestamp()) {
			params.addInput(ForteDataType.DATE_AND_TIME);
		}
		makeIPSocket(params); // Create the socket and connect to FORTE
	}

	@Override
	protected void putSensors(float[] sensors) {
		getSocket().put((double) getSensor(SENSOR1, sensors));
		getSocket().put((double) getSensor(SENSOR2, sensors));
	}
}
