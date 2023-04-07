package steve6472.funnylib.json;

import org.json.JSONObject;

/**
 * Created by steve6472
 * Date: 2/19/2023
 * Project: StevesFunnyLibrary <br>
 */
public interface IJsonConfig
{
	void save(JSONObject json);
	void load(JSONObject json);
}
