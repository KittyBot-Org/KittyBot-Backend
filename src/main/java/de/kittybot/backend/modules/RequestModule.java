package de.kittybot.backend.modules;

import de.kittybot.backend.objects.module.Module;
import de.kittybot.backend.utils.Config;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.Method;
import net.dv8tion.jda.internal.requests.Requester;
import net.dv8tion.jda.internal.requests.Route;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@SuppressWarnings("unused")
public class RequestModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(RequestModule.class);
	public static final Route GATEWAY_BOT = Route.custom(Method.GET, "gateway/bot");

	public DataObject getGatewayBot(){
		try(var request = call(GATEWAY_BOT.compile(), null).execute()){
			var body = request.body();
			if(body == null){
				return null;
			}
			return DataObject.fromJson(body.byteStream());
		}
		catch(IOException e){
			LOG.error("Error while getting Gateway infos", e);
		}
		return null;
	}

	private Call call(Route.CompiledRoute route, RequestBody body){
		return this.modules.getHttpClient().newCall(newBuilder(route).method(route.getMethod().name(), body).build());
	}

	private Request.Builder newBuilder(Route.CompiledRoute route){
		return new Request.Builder()
			       .url(Requester.DISCORD_API_PREFIX + route.getCompiledRoute())
			       .addHeader("Authorization", "Bot " + Config.BOT_TOKEN);
	}

}
