package de.kittybot.backend.objects.module;

import de.kittybot.backend.main.Backend;
import de.kittybot.backend.objects.exceptions.ModuleNotFoundException;
import de.kittybot.backend.utils.ThreadFactoryHelper;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Modules{

	private static final String MODULE_PACKAGE = "de.kittybot.backend.modules";
	private static final Logger LOG = LoggerFactory.getLogger(Modules.class);

	private final Backend main;
	private final OkHttpClient httpClient;
	private final ScheduledExecutorService scheduler;
	private final List<Module> modules;

	public Modules(Backend main){
		this.main = main;
		this.httpClient = new OkHttpClient();
		this.scheduler = new ScheduledThreadPoolExecutor(2, new ThreadFactoryHelper());
		this.modules = new LinkedList<>();
		loadModules();
	}

	private void loadModules(){
		LOG.info("Loading modules...");
		try(var result = new ClassGraph().acceptPackages(MODULE_PACKAGE).scan()){
			var queue = result.getSubclasses(Module.class.getName()).stream()
			                  .map(ClassInfo::loadClass)
			                  .filter(Module.class::isAssignableFrom)
			                  .map(clazz -> {
				                  try{
					                  return ((Module) clazz.getDeclaredConstructor().newInstance()).init(this);
				                  }
				                  catch(Exception e){
					                  LOG.info("WTF?!?!?!?! Horsti what did u do to me", e);
				                  }
				                  return null;
			                  })
			                  .filter(Objects::nonNull)
			                  .collect(Collectors.toCollection(LinkedList::new));

			while(!queue.isEmpty()){
				var instance = queue.remove();
				var dependencies = instance.getDependencies();
				if(dependencies != null && !dependencies.stream().allMatch(mod -> this.modules.stream().anyMatch(module -> mod == module.getClass()))){
					queue.add(instance);
					LOG.info("Added '{}' back to the queue. Dependencies: {} (Dependency circle jerk incoming!)", instance.getClass().getSimpleName(), dependencies.toString());
					continue;
				}
				instance.onEnable();
				this.modules.add(instance);
			}
		}
		LOG.info("Finished loading {} modules", this.modules.size());
	}

	@SuppressWarnings("unchecked")
	public <T extends Module> T get(Class<T> clazz){
		var module = this.modules.stream().filter(mod -> mod.getClass().equals(clazz)).findFirst();
		if(module.isEmpty()){
			throw new ModuleNotFoundException(clazz);
		}
		return (T) module.get();
	}

	public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initDelay, long delay, TimeUnit timeUnit){
		return this.scheduler.scheduleAtFixedRate(() -> {
			try{
				runnable.run();
			}
			catch(Exception e){
				LOG.error("Unexpected error in scheduler", e);
			}
		}, initDelay, delay, timeUnit);
	}

	public ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit timeUnit){
		return this.scheduler.schedule(() -> {
			try{
				runnable.run();
			}
			catch(Exception e){
				LOG.error("Unexpected error in scheduler", e);
			}
		}, delay, timeUnit);
	}

	public OkHttpClient getHttpClient(){
		return this.httpClient;
	}

}
