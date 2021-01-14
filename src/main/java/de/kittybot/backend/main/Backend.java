package de.kittybot.backend.main;

import de.kittybot.backend.exceptions.MissingConfigValuesException;
import de.kittybot.backend.utils.Config;
import de.kittybot.backend.utils.ThreadFactoryHelper;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Backend{

	private final ScheduledExecutorService scheduler;
	private final OkHttpClient httpClient;

	public Backend() throws IOException, MissingConfigValuesException{
		Config.init("./config.json");
		this.scheduler = new ScheduledThreadPoolExecutor(2, new ThreadFactoryHelper());
		this.httpClient = new OkHttpClient();
	}

}
