package steve6472.standalone.interactable.ex;

import org.json.JSONObject;
import steve6472.funnylib.json.codec.Codec;

/**
 * Created by steve6472
 * Date: 10/30/2022
 * Project: StevesFunnyLibrary <br>
 */
public class ExpressionCodec extends Codec<Expression>
{
	@Override
	public Expression fromJson(JSONObject json)
	{
		return Expressions.loadExpression(json.getJSONObject("exp"));
	}

	@Override
	public void toJson(Expression obj, JSONObject json)
	{
		json.put("exp", Expressions.saveExpression(obj));
	}
}
