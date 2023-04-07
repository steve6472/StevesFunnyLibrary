package steve6472.standalone.interactable.ex;

import org.json.JSONArray;
import org.json.JSONObject;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.util.MiscUtil;
import steve6472.standalone.interactable.ex.Expression.Type;
import steve6472.standalone.interactable.ex.impl.BiInputExp;
import steve6472.standalone.interactable.ex.impl.bool.AnyPlayerInArea;
import steve6472.standalone.interactable.ex.impl.bool.EqualityExp;
import steve6472.standalone.interactable.ex.impl.IfExp;
import steve6472.standalone.interactable.ex.impl.bool.LogicExpr;
import steve6472.standalone.interactable.ex.impl.constants.ConstantNumberExp;
import steve6472.standalone.interactable.ex.impl.func.DebugHereExp;
import steve6472.standalone.interactable.ex.impl.func.DelayExp;

import java.util.*;
import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 10/9/2022
 * Project: StevesFunnyLibrary <br>
 */
public class Expressions
{
	public record ExpressionEntry(CustomItem icon, String id, Function<JSONObject, Expression> constructor) {}

	private static final Map<Type, List<ExpressionEntry>> EXPRESSIONS = new HashMap<>();
	private static final Map<Class<? extends Expression>, ExpressionEntry> ENTRY_BY_CLASS = new HashMap<>();
	private static final Map<String, Class<? extends Expression>> CLASS_BY_ID = new HashMap<>();

	static
	{
		register(Type.HIDDEN, ExpItems.ADD_EXPRESSION, "code_block", CodeBlockExp::loadJson);

		register(Type.CONTROL, ExpItems.IF_ICON, "if", json -> new IfExp(loadExpression(json.optJSONObject("condition")), loadExpression(json.optJSONObject("body"))));
		register(Type.BOOL, ExpItems.LOGIC_AND_ICON, "and", json -> new LogicExpr(json.optEnum(LogicExpr.Operator.class, "operator", LogicExpr.Operator.AND), loadExpression(json.optJSONObject("left")), loadExpression(json.optJSONObject("right"))));
		register(Type.BOOL, ExpItems.LOGIC_OR_ICON, "or", json -> new LogicExpr(json.optEnum(LogicExpr.Operator.class, "operator", LogicExpr.Operator.OR), loadExpression(json.optJSONObject("left")), loadExpression(json.optJSONObject("right"))));
		register(Type.BOOL, ExpItems.EQUALITY_EQUALS_ICON, "equals", json -> BiInputExp.load(json, EqualityExp.Operator.EQUALS, EqualityExp::new));
		register(Type.BOOL, ExpItems.EQUALITY_LESS_ICON, "less", json -> BiInputExp.load(json, EqualityExp.Operator.SMALLER, EqualityExp::new));
		register(Type.BOOL, ExpItems.EQUALITY_LESS_EQUAL_ICON, "less_equal", json -> BiInputExp.load(json, EqualityExp.Operator.SMALLER_EQUAL, EqualityExp::new));
		register(Type.BOOL, ExpItems.EQUALITY_GREATER_ICON, "greater", json -> BiInputExp.load(json, EqualityExp.Operator.BIGGER, EqualityExp::new));
		register(Type.BOOL, ExpItems.EQUALITY_GREATER_EQUAL_ICON, "greater_equal", json -> BiInputExp.load(json, EqualityExp.Operator.BIGGER_EQUAL, EqualityExp::new));
		register(Type.BOOL, ExpItems.PLAYER_IN_AREA, "player_in_area", json -> new AnyPlayerInArea(MiscUtil.deserializeItemStack(json.optJSONObject("area"))));
		register(Type.CONTROL, ExpItems.DEBUG_HERE_ICON, "debug_here", json -> new DebugHereExp(json.optInt("id")));
		register(Type.CONTROL, ExpItems.DELAY_ICON, "delay", json -> new DelayExp(json.optLong("ticks")));
		register(Type.INT, ExpItems.CONSTANT_NUMBER, "const_int", json -> new ConstantNumberExp(json.optInt("number")));
	}

	public static List<ExpressionEntry> getExpressions(Type type)
	{
		return EXPRESSIONS.computeIfAbsent(type, k -> new ArrayList<>());
	}

	public static void register(Type type, CustomItem icon, String id, Function<JSONObject, Expression> jsonLoad)
	{
		ExpressionEntry entry = new ExpressionEntry(icon, id, jsonLoad);
		EXPRESSIONS.computeIfAbsent(type, k -> new ArrayList<>()).add(entry);
		Class<? extends Expression> clazz = entry.constructor.apply(new JSONObject()).getClass();
		ENTRY_BY_CLASS.put(clazz, entry);
		CLASS_BY_ID.put(id, clazz);
	}

	/*
	 * Serialization
	 */

	public static JSONObject saveExpression(Expression expression)
	{
		if (expression == null)
			return new JSONObject();

		if (expression instanceof CodeBlockExp block)
		{
			if (!block.isBody())
			{
				if (block.getExpressions().isEmpty())
					return new JSONObject();

				return saveExpression(block.getExpressions().get(0));
			}
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("expressionId", ENTRY_BY_CLASS.get(expression.getClass()).id());
		expression.save(jsonObject);

		return jsonObject;
	}

	public static Expression loadExpression(JSONObject json)
	{
		if (json == null || json.isEmpty())
			return null;

		String expressionId = json.getString("expressionId");
		ExpressionEntry expressionEntry = ENTRY_BY_CLASS.get(CLASS_BY_ID.get(expressionId));
		return expressionEntry.constructor.apply(json);
	}

	public static JSONArray saveExpressions(Collection<Expression> expressions)
	{
		JSONArray array = new JSONArray();
		for (Expression expression : expressions)
		{
			array.put(saveExpression(expression));
		}

		return array;
	}

	public static List<Expression> loadExpressions(JSONArray array)
	{
		List<Expression> expressions = new ArrayList<>();

		for (int i = 0; i < array.length(); i++)
		{
			JSONObject jsonObject = array.getJSONObject(i);
			expressions.add(loadExpression(jsonObject));
		}

		return expressions;
	}
}
