package de.kittybot.backend.objects.module;

import java.util.Set;

public abstract class Module{

	protected Modules modules;

	public Module init(Modules modules){
		this.modules = modules;
		return this;
	}

	public Set<Class<? extends Module>> getDependencies(){
		return null;
	}

	protected void onEnable(){}

	protected void onDisable(){}
}
