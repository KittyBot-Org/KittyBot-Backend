package de.kittybot.backend.modules;

import com.amihaiemil.docker.Container;
import com.amihaiemil.docker.Docker;
import com.amihaiemil.docker.UnexpectedResponseException;
import com.amihaiemil.docker.UnixDocker;
import de.kittybot.backend.objects.module.Module;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class DockerModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(DockerModule.class);
	private static final String KITTYBOT_IMAGE = "topisenpai/kittybot:latest";

	private Docker docker;
	private Map<Integer, Container> shards;
	private int totalShards;

	@Override
	protected void onEnable(){
		this.docker = new UnixDocker(new File("/var/run/docker.sock"));
		this.shards = new HashMap<>();
		loadTotalShards();
		startShards();
	}

	@Override
	protected void onDisable(){
		for(var shard : this.shards.values()){
			try{
				shard.stop();
				shard.remove();
			}
			catch(IOException e){
				LOG.error("Failed stopping/removing container");
			}
		}
	}

	private void loadTotalShards(){
		var json = this.modules.get(RequestModule.class).getGatewayBot();
		if(json == null){
			this.totalShards = 1;
			return;
		}
		this.totalShards = json.getInt("shards");
	}

	private void initSwarm(){
		try{
			var token = this.docker.swarm().init("eth0");
			LOG.info("Successfully initialized new swarm\nUse this command to join other nodes: docker swarm join --token {}", token);
		}
		catch(IOException e){
			LOG.error("Error while init swarm", e);
		}
	}

	private void startShards(){
		try{
			var info = this.docker.swarm().inspect();
		}
		catch(IOException e){
			LOG.error("Unexpected error while getting swarm info", e);
			return;
		}
		catch(UnexpectedResponseException e){
			if(e.actualStatus() == 503){
				initSwarm();
			}
			else{
				LOG.error("Unexpected response while getting swarm info", e);
				return;
			}
		}

		for(var shardId = 0; shardId < this.totalShards; shardId++){
			try{
				var shard = this.docker.containers().create("kittybot_shard_" + shardId, KITTYBOT_IMAGE);
				this.shards.put(shardId, shard);
			}
			catch(IOException e){
				LOG.error("Unexpected error while creating shard {}", shardId, e);
			}
		}
	}

}
