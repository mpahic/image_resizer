package com.cloudcog.images.resize.config;

import java.util.TimerTask;

/**
 * 
 * @author mpahic
 *
 */
public class ConfigChecker extends TimerTask {

	@Override
	public void run() {
		Config.updateConfigs();

	}
}
