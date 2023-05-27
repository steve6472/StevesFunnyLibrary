package steve6472.standalone.interactable.ex;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import steve6472.funnylib.item.CustomItem;
import steve6472.funnylib.serialize.NBT;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 10/9/2022
 * Project: StevesFunnyLibrary <br>
 */
public class Expressions
{
	public record ExpressionEntry(CustomItem icon, String id, Supplier<Expression> constructor) {}

	private static final Map<Type, List<ExpressionEntry>> EXPRESSIONS = new HashMap<>();
	private static final Map<Class<? extends Expression>, ExpressionEntry> ENTRY_BY_CLASS = new HashMap<>();
	private static final Map<String, Class<? extends Expression>> CLASS_BY_ID = new HashMap<>();

	static
	{
		register(Type.HIDDEN, ExpItems.ADD_EXPRESSION, "code_block", CodeBlockExp.class, CodeBlockExp::new);

		register(Type.CONTROL, ExpItems.IF_ICON, "if", IfExp.class, IfExp::new);
		register(Type.BOOL, ExpItems.LOGIC_AND_ICON, "and", LogicExpr.class, () -> new LogicExpr(LogicExpr.Operator.AND));
		register(Type.BOOL, ExpItems.LOGIC_OR_ICON, "or", LogicExpr.class, () -> new LogicExpr(LogicExpr.Operator.OR));
		register(Type.BOOL, ExpItems.EQUALITY_EQUALS_ICON, "equals", EqualityExp.class, () -> new EqualityExp(EqualityExp.Operator.EQUALS));
		register(Type.BOOL, ExpItems.EQUALITY_LESS_ICON, "less", EqualityExp.class, () -> new EqualityExp(EqualityExp.Operator.SMALLER));
		register(Type.BOOL, ExpItems.EQUALITY_LESS_EQUAL_ICON, "less_equal", EqualityExp.class, () -> new EqualityExp(EqualityExp.Operator.SMALLER_EQUAL));
		register(Type.BOOL, ExpItems.EQUALITY_GREATER_ICON, "greater", EqualityExp.class, () -> new EqualityExp(EqualityExp.Operator.BIGGER));
		register(Type.BOOL, ExpItems.EQUALITY_GREATER_EQUAL_ICON, "greater_equal", EqualityExp.class, () -> new EqualityExp(EqualityExp.Operator.BIGGER_EQUAL));
		register(Type.BOOL, ExpItems.PLAYER_IN_AREA, "player_in_area", AnyPlayerInArea.class, AnyPlayerInArea::new);
		register(Type.CONTROL, ExpItems.DEBUG_HERE_ICON, "debug_here", DebugHereExp.class, DebugHereExp::new);
		register(Type.CONTROL, ExpItems.DELAY_ICON, "delay", DelayExp.class, DelayExp::new);
		register(Type.INT, ExpItems.CONSTANT_NUMBER, "const_int", ConstantNumberExp.class, ConstantNumberExp::new);
	}

	public static List<ExpressionEntry> getExpressions(Type type)
	{
		return EXPRESSIONS.computeIfAbsent(type, k -> new ArrayList<>());
	}

	public static void register(Type type, CustomItem icon, String id, Class<? extends Expression> clazz, Supplier<Expression> constructor)
	{
		ExpressionEntry entry = new ExpressionEntry(icon, id, constructor);
		EXPRESSIONS.computeIfAbsent(type, k -> new ArrayList<>()).add(entry);
		ENTRY_BY_CLASS.put(clazz, entry);
		CLASS_BY_ID.put(id, clazz);
	}

	/*
	 * Serialization
	 */

	public static NBT saveExpression(NBT nbt, Expression expression)
	{
		if (expression == null)
			return nbt;

//		if (expression instanceof CodeBlockExp block)
//		{
//			if (!block.isBody())
//			{
//				if (block.getExpressions().isEmpty())
//					return nbt;
//
//				return saveExpression(nbt, block.getExpressions().get(0));
//			}
//		}

		nbt.setString("expression_id", ENTRY_BY_CLASS.get(expression.getClass()).id());
		expression.toNBT(nbt);
		return nbt;
	}

	public static <T extends Expression> T loadExpression(NBT nbt, Class<T> expectedType)
	{
		if (nbt == null || nbt.isEmpty())
			return null;

		if (!nbt.hasString("expression_id"))
			return null;

		String expressionId = nbt.getString("expression_id");
		Class<? extends Expression> key = CLASS_BY_ID.get(expressionId);
		if (expectedType != null && !key.isAssignableFrom(expectedType))
			throw new RuntimeException("Expected " + expectedType.getSimpleName() + ", got " + key.getSimpleName() + " instead!");
		ExpressionEntry expressionEntry = ENTRY_BY_CLASS.get(key);
		Expression expression = expressionEntry.constructor.get();
		expression.fromNBT(nbt);
		return (T) expression;
	}

	@Deprecated
	public static Expression loadExpression(NBT nbt)
	{
		if (nbt == null || nbt.isEmpty())
			return null;

		if (!nbt.hasString("expression_id"))
			return null;

		String expressionId = nbt.getString("expression_id");
		Class<? extends Expression> key = CLASS_BY_ID.get(expressionId);
		ExpressionEntry expressionEntry = ENTRY_BY_CLASS.get(key);
		Expression expression = expressionEntry.constructor.get();
		expression.fromNBT(nbt);
		return expression;
	}

	public static NBT[] saveExpressions(NBT nbt, Collection<Expression> expressions)
	{
		NBT[] array = nbt.createCompoundArray(expressions.size());
		int i = 0;
		for (Expression expression : expressions)
		{
			saveExpression(array[i], expression);
			i++;
		}

		return array;
	}

	public static List<Expression> loadExpressions(NBT[] array)
	{
		List<Expression> expressions = new ArrayList<>();

		for (NBT nbt : array)
		{
			expressions.add(loadExpression(nbt, null));
		}

		return expressions;
	}
}
