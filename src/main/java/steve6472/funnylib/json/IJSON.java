package steve6472.funnylib.json;

import org.json.JSONObject;

/**
 * Created by steve6472
 * Date: 4/22/2023
 * Project: StevesFunnyLibrary <br>
 */
public interface IJSON
{
	void toJSON(JSONObject json);

	void fromJSON(JSONObject json);
}
