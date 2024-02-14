package steve6472.funnylib;

import dev.dominion.ecs.api.Results;
import dev.dominion.ecs.engine.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import steve6472.funnylib.json.JsonPrettify;
import steve6472.funnylib.util.ArbitraryData;
import steve6472.funnylib.util.Checks;
import steve6472.funnylib.util.ParticleUtil;
import steve6472.standalone.interactable.ReflectionHacker;

/**
 * Created by steve6472
 * Date: 9/18/2022
 * Project: StevesFunnyLibrary <br>
 */
public class MavenSux
{
	public MavenSux()
	{
		new JSONObject();
		new JSONArray();
		new Checks();
		new ArbitraryData();
		new JSONString()
		{
			@Override
			public String toJSONString()
			{
				return "";
			}
		};
		new ParticleUtil();
		new ReflectionHacker();
		new JsonPrettify();
		Class<ResultSet.With1> with1Class = ResultSet.With1.class;
		Class<ResultSet.NextWith1> nextWith1Class = ResultSet.NextWith1.class;
	}
}
