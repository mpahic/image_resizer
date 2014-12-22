package com.cloudcog.images.resize.config;

import java.util.TimerTask;

public class ConfigChecker extends TimerTask {

	@Override
	public void run() {
		Config.updateConfigs();

	}
}
