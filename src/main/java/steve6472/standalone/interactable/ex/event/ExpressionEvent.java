package steve6472.standalone.interactable.ex.event;

import steve6472.funnylib.json.INBT;
import steve6472.funnylib.menu.Click;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.serialize.PdcNBT;
import steve6472.funnylib.util.NMS;
import steve6472.funnylib.util.Pair;
import steve6472.standalone.interactable.ex.ExpContext;
import steve6472.standalone.interactable.ex.Expression;
import steve6472.standalone.interactable.ex.Expressions;
import steve6472.standalone.interactable.ex.elements.IElementType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 6/18/2023
 * Project: StevesFunnyLibrary <br>
 */
public abstract class ExpressionEvent implements INBT
{
	private Expression expression;
	private final List<Pair<String, InputType<?>>> outputs = new ArrayList<>();

	public ExpressionEvent()
	{
		populateOutputs(outputs);
	}

	public void setExpression(Expression expression)
	{
		this.expression = expression;
	}

	public Expression getExpression()
	{
		return expression;
	}

	public Expression cloneExpression()
	{
		if (expression == null)
			return null;
		// Serialize expression and load it to create a copy
		return Expressions.loadExpression(Expressions.saveExpression(PdcNBT.fromPDC(NMS.newCraftContainer()), expression), null, null);
	}

	public abstract void createEvents(ExpContext executingContext, List<ExpressionEventData> eventsToQueue);

	protected abstract void populateOutputs(List<Pair<String, InputType<?>>> outputs);

	public abstract Response action(IElementType type, Click click);

	public abstract IElementType[] getTypes();

	public List<Pair<String, InputType<?>>> getOutputs()
	{
		return outputs;
	}

	@Override
	public void toNBT(NBT compound)
	{
		if (expression != null)
		{
			NBT expCompound = compound.createCompound();
			Expressions.saveExpression(expCompound, expression);
			compound.setCompound("_expression", expCompound);
		}
	}

	@Override
	public void fromNBT(NBT compound)
	{
		if (compound.hasCompound("_expression"))
		{
			expression = Expressions.loadExpression(compound.getCompound("_expression"), null, null);
		}
	}

	protected ExpressionEventData event()
	{
		return new ExpressionEventData();
	}
}
