package steve6472.funnylib.coroutine;

import steve6472.funnylib.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 11/9/2022
 * Project: StevesFunnyLibrary <br>
 */
public class DepthTest
{
	private int maxDepth;
	private int depth;
	private final List<Pair<Integer, String>> tree = new ArrayList<>();

	public DepthTest()
	{

	}

	public void depth(int mod)
	{
		maxDepth = Math.max(depth + mod, depth);
		depth += mod;
	}

	public void add(Object object)
	{
//		add(object.getClass().getSimpleName() + " @" + Integer.toHexString(object.hashCode()));
		add(object.getClass().getSimpleName());
	}

	public void add(Class<?> clazz)
	{
		add(clazz.getSimpleName());
	}

	public void add(String text)
	{
		tree.add(new Pair<>(depth, text));
	}

	private void print(Pair<Integer, String> pair)
	{
		System.out.println("\t".repeat(pair.a() - maxDepth + 1) + pair.b());
	}

	public void print()
	{
		tree.forEach(this::print);
	}
}
