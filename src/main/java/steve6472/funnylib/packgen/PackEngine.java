package steve6472.funnylib.packgen;

import org.bukkit.Bukkit;
import steve6472.funnylib.FunnyLib;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 2/12/2024
 * Project: StevesFunnyLibrary <br>
 */
public class PackEngine
{
	public final PackFiles files;
	public final PackZipper zipper;
	public final Models models;

	public PackEngine()
	{
		this.files = new PackFiles(FunnyLib.getPlugin());
		this.models = new Models(this);
		this.zipper = new PackZipper(this);
	}

	public void getPackAddress(Consumer<String> ipAddress)
	{
		CompletableFuture.runAsync(() ->
		{
			final String fileUUID = files.zippedFile.getName().split("\\.")[0];
			Bukkit.getScheduler().runTask(FunnyLib.getPlugin(), () ->
			{
				ipAddress.accept("http://%s:%s/%s.zip".formatted(getExternalAddress(), FunnyLib.getSettings().sparkPort, fileUUID));
			});
		}, Executors.newSingleThreadExecutor());
	}

	private static boolean isDev()
	{
		return new File("").getAbsolutePath().startsWith("C:\\storage\\server");
	}

	private String getExternalAddress()
	{
		if (isDev())
			return "localhost";

		try
		{
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://ifconfig.me/ip")).build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			return response.body();
		} catch (IOException | URISyntaxException | InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}
}
