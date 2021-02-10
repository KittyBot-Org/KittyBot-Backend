package de.kittybot.backend.main;

import de.kittybot.backend.objects.exceptions.MissingConfigValuesException;
import de.kittybot.backend.objects.module.Module;
import de.kittybot.backend.objects.module.Modules;
import de.kittybot.backend.utils.Config;
import de.kittybot.backend.utils.ThreadFactoryHelper;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Backend{

	private final Modules modules;

	public Backend() throws IOException, MissingConfigValuesException{
		Config.init("./config.json");
		this.modules = new Modules(this);
	}

}
