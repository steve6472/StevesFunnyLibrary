package steve6472.standalone;

/**
 * Created by steve6472
 * Date: 5/27/2023
 * Project: StevesFunnyLibrary <br>
 */
public class Skins
{
	/*
	 * Blocks
	 */
	public static final class Blocks
	{
		public static final String LIGHT_HEAVY_MACHINE_BLOCK = "35cf035528a7fba6d84be615fd6ed3c93042926d54dbdbbec82055084d45a2ed";
	}

	/*
	 * Items
	 */
	public static final class Items
	{
		public static final String HEAVY_MACHINE_BLOCK = "ecca6839f22e037944ad31fdc835947a7f51a94e278772ad11bd22b0619f8b1";
		public static final String LIGHT_MACHINE_BLOCK = "4740bbe8808bd85e92fb018de26c95af9ebbac7ad08d0dc67c78e2786dd88a2";
	}

	public static String toUrl(String key)
	{
		return "http://textures.minecraft.net/texture/" + key;
	}
}
