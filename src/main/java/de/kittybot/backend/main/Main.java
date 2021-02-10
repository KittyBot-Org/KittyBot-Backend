package de.kittybot.backend.main;

import de.kittybot.backend.objects.exceptions.MissingConfigValuesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main{

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	public static void main(String... args){
		try{
			LOG.info("Starting KittyBot-Backend...");
			LOG.info("  _  ___ _   _         ____        _          ____             _                  _ \n" +
					" | |/ (_) | | |       |  _ \\      | |        |  _ \\           | |                | |\n" +
					" | ' / _| |_| |_ _   _| |_) | ___ | |_ ______| |_) | __ _  ___| | _____ _ __   __| |\n" +
					" |  < | | __| __| | | |  _ < / _ \\| __|______|  _ < / _` |/ __| |/ / _ \\ '_ \\ / _` |\n" +
					" | . \\| | |_| |_| |_| | |_) | (_) | |_       | |_) | (_| | (__|   <  __/ | | | (_| |\n" +
					" |_|\\_\\_|\\__|\\__|\\__, |____/ \\___/ \\__|      |____/ \\__,_|\\___|_|\\_\\___|_| |_|\\__,_|\n" +
					"                  __/ |                                                             \n" +
					"                 |___/                                                              \n" +
					"\n" +
					"                https://github.com/KittyBot-Org/KittyBot-Backend\\n"
			);
			new Backend();
		}
		catch(IOException | MissingConfigValuesException e){
			LOG.error("Error while starting KittyBot-Backend", e);
			System.exit(-1);
		}
	}

}
