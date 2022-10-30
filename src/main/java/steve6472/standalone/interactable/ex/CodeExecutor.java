package steve6472.standalone.interactable.ex;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import steve6472.funnylib.json.codec.ann.Save;

/**
 * Created by steve6472
 * Date: 10/24/2022
 * Project: StevesFunnyLibrary <br>
 */
public class CodeExecutor
{
	@Save(ExpressionCodec.class)    public Expression expression;
	@Save                           public ExpContext context;

	public CodeExecutor()
	{

	}

	public CodeExecutor(Expression expression, ExpContext context)
	{
		this.expression = expression;
		this.context = context;
	}

	/**
	 *
	 * @return true if code finished executing
	 */
	public boolean executeTick()
	{
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
			Bukkit.broadcastMessage(ChatColor.YELLOW + "Result: " + result);
			if (expression instanceof CodeBlockExp cb)
			{
				cb.reset();
			}
			return true;
		}

		return false;
	}
}
