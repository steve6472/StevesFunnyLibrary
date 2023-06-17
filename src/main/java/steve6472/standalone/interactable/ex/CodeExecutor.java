package steve6472.standalone.interactable.ex;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import steve6472.funnylib.json.INBT;
import steve6472.funnylib.serialize.NBT;

/**
 * Created by steve6472
 * Date: 10/24/2022
 * Project: StevesFunnyLibrary <br>
 */
public class CodeExecutor implements INBT
{
	public Expression expression;
	public ExpContext context;
	public boolean running;

	public CodeExecutor()
	{

	}

	public CodeExecutor(Expression expression, ExpContext context)
	{
		this.expression = expression;
		this.context = context;
	}

	@Override
	public void toNBT(NBT compound)
	{
		compound.setBoolean("running", running);

		NBT contextCompound = compound.createCompound();
		context.toNBT(contextCompound);
		compound.setCompound("context", contextCompound);

		compound.setCompound("expression", Expressions.saveExpression(compound.createCompound(), expression));
	}

	@Override
	public void fromNBT(NBT compound)
	{
		running = compound.getBoolean("running", false);

		context = new ExpContext();
		context.fromNBT(compound.getCompound("context"));

		expression = Expressions.loadExpression(compound.getCompound("expression"), null, null);
	}

	public void start()
	{
		running = true;
	}

	/**
	 *
	 * @return true if code finished executing
	 */
	public boolean executeTick()
	{
		if (!running)
			return true;

		ExpResult result;
		try
		{
			result = expression.execute(context);
		} catch (Exception exception)
		{
			exception.printStackTrace();
			return true;
		}

		if (result == ExpResult.DELAY)
		{
			context.passDelay();
			return false;
		}

		if (result == ExpResult.STOP)
		{
//			Bukkit.broadcastMessage(ChatColor.YELLOW + "Result: " + result);
			if (expression instanceof CodeBlockExp cb)
			{
				running = false;
				cb.reset();
			}
			return true;
		}

		return false;
	}
}
