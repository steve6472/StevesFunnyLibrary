package steve6472.funnylib.coroutine;

import steve6472.funnylib.util.Procedure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 11/8/2022
 * Project: StevesFunnyLibrary <br>
 * Welcome to generic hell
 */
public abstract class Coroutine<T extends Coroutine<?, ?>, P extends Coroutine<?, ?>>
{
	private static final Map<String, Root<?>> CACHE = new HashMap<>();
	private static final Map<String, CoroutineExecutor> EXECUTOR_CACHE = new HashMap<>();

	public static Root<?> create(String id)
	{
		return CACHE.computeIfAbsent(id, k -> new Root<>(null, id));
	}

	public static CoroutineExecutor getExecutor(String id)
	{
		return EXECUTOR_CACHE.get(id);
	}

	public static void remove(String id)
	{
		CACHE.remove(id);
		EXECUTOR_CACHE.remove(id);
	}


	protected final P parent;
	protected final CoEx lines = new CoEx();
	private boolean finished = false;
	private int lineRetriever = 0;

	private Coroutine(P parent)
	{
		this.parent = parent;
	}

	public CoroutineExecutor finish()
	{
		if (this instanceof Root<?> root)
		{
			finished = true;
			return EXECUTOR_CACHE.computeIfAbsent(root.id, k -> new CoroutineExecutor(root));
		} else
		{
			throw new RuntimeException("Called finish() on non-root expression");
		}
	}

	protected abstract boolean execute(CoroutineExecutor executor);

	/*
	 *
	 */

	public P End()
	{
		// Do not return null
		if (parent == null)
			throw new RuntimeException("Minimum depth reached!");

		// reset the line retriever
		lineRetriever = 0;

		// add itself into parents execution queue
		parent.lines.add(endAdds());

		// return parent
		return parent;
	}

	protected Coroutine<?, ?> get()
	{
		Coroutine<?, ?> coroutine = lines.lines.get(lineRetriever);
		lineRetriever++;
		return coroutine;
	}

	public T Wait(int ticks)
	{
		lines.add(new Wait(this, ticks));
		return getThis();
	}

	public T WaitUntilTrue(Supplier<Boolean> condition)
	{
		lines.add(new WaitUntil(this, condition, 0));
		return getThis();
	}

	public T WaitUntilTrue(int timeout, Supplier<Boolean> condition)
	{
		lines.add(new WaitUntil(this, condition, timeout));
		return getThis();
	}

	public If<P> If(Supplier<Boolean> condition)
	{
		return new If<>(getAsParent(), condition);
	}

	public T Do(Procedure procedure)
	{
		lines.add(new Do(this, procedure));
		return getThis();
	}

	public Loop<P> Loop(int start, Function<Integer, Boolean> condition, int incrementBy)
	{
		return new Loop<>(getAsParent(), start, condition, incrementBy);
	}

	public T Loop(int start, Function<Integer, Boolean> condition, int incrementBy, Function<LoopBody<P>, Coroutine<?, ?>> body)
	{
		lines.add(new LoopBody<>(getAsParent(), start, condition, incrementBy, body));
		return getThis();
	}

	/**
	 * TODO: delete
	 * @return result of {@link #Do(Procedure)}
	 */
	public T Nop()
	{
		return Do(() -> {});
	}

	public T Say(String text)
	{
		return Do(() -> System.out.println(text));
	}

	@SuppressWarnings("unchecked")
	protected T getThis()
	{
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	protected P getAsParent()
	{
		return (P) this;
	}

	/**
	 *
	 * @return a coroutine instance to be added to the list of coroutines <br>
	 * to be executed
	 */
	protected Coroutine<?, ?> endAdds()
	{
		return this;
	}

	protected abstract void debug(DepthTest test);

	public boolean goToNextOnSleep()
	{
		return true;
	}

	/*
	 * Impl
	 */

	public static class LoopBody<P extends Coroutine<?, ?>> extends Coroutine<LoopBody<P>, P>
	{
		private final Function<Integer, Boolean> condition;
		private final Function<LoopBody<P>, Coroutine<?, ?>> body;
		private final int start;
		private final int incrementBy;
		private int index;
		private boolean waited = false;
		private boolean executing = false;

		private LoopBody(P parent, int start, Function<Integer, Boolean> condition, int incrementBy, Function<LoopBody<P>, Coroutine<?, ?>> body)
		{
			super(parent);
			this.start = start;
			this.condition = condition;
			this.incrementBy = incrementBy;
			this.body = body;
		}

		@Override
		protected boolean execute(CoroutineExecutor executor)
		{
			if (executing)
			{
				return lines.execute(executor);
			}

			if (!waited)
			{
				index = start;
			}

			for (; condition.apply(index); index += incrementBy)
			{
				Coroutine<?, ?> apply = body.apply(this);
				executing = true;
				if (apply.execute(executor))
				{
					waited = true;
					executing = false;
					lines.lines.clear();
					return true;
				}
				waited = false;
				executing = false;
				lines.lines.clear();
			}
			return false;
		}

		@Override
		public boolean goToNextOnSleep()
		{
			return !waited;
		}

		public int i()
		{
			return index;
		}

		@Override
		protected void debug(DepthTest test)
		{
			test.add(this);
			test.depth(1);
			lines.forEach(l -> l.debug(test));
			test.depth(-1);
			test.add("End");
		}
	}

	public static class Loop<P extends Coroutine<?, ?>> extends Coroutine<Loop<P>, P>
	{
		private final Function<Integer, Boolean> condition;
		private final int start;
		private final int incrementBy;
		private int index;
		private boolean waited = false;

		private Loop(P parent, int start, Function<Integer, Boolean> condition, int incrementBy)
		{
			super(parent);
			this.start = start;
			this.condition = condition;
			this.incrementBy = incrementBy;
		}

		@Override
		protected boolean execute(CoroutineExecutor executor)
		{
			for (index = waited ? index : start; condition.apply(index); index += incrementBy)
			{
				if (lines.execute(executor))
				{
					waited = true;
					return true;
				}
			}
			return false;
		}

		@Override
		protected void debug(DepthTest test)
		{
			test.add(this);
			test.depth(1);
			lines.forEach(l -> l.debug(test));
			test.depth(-1);
			test.add("End");
		}
	}

	private static class Wait extends Coroutine<Coroutine<?, ?>, Coroutine<?, ?>>
	{
		int time;

		private Wait(Coroutine<?, ?> parent, int time)
		{
			super(parent);
			this.time = time;
		}

		@Override
		protected boolean execute(CoroutineExecutor executor)
		{
			executor.waitFor(time - 1);
			return time > 0;
		}

		@Override
		protected void debug(DepthTest test)
		{
			test.add("Wait: " + time);
		}
	}

	private static class WaitUntil extends Coroutine<Coroutine<?, ?>, Coroutine<?, ?>>
	{
		Supplier<Boolean> condition;
		private final int tryTimeout;
		private int timeoutCounter;

		private WaitUntil(Coroutine<?, ?> parent, Supplier<Boolean> condition, int tryTimeout)
		{
			super(parent);
			this.condition = condition;
			this.tryTimeout = tryTimeout;
		}

		@Override
		protected boolean execute(CoroutineExecutor executor)
		{
			if (tryTimeout > 0 && timeoutCounter == 0)
			{
				timeoutCounter = tryTimeout;

				if (!condition.get())
					return true;
			}
			return !condition.get();
		}

		@Override
		protected void debug(DepthTest test)
		{
			test.add("WaitUntil");
		}

		@Override
		public boolean goToNextOnSleep()
		{
			return false;
		}
	}

	private static class Do extends Coroutine<Coroutine<?, ?>, Coroutine<?, ?>>
	{
		Procedure procedure;

		private Do(Coroutine<?, ?> parent, Procedure procedure)
		{
			super(parent);
			this.procedure = procedure;
		}

		@Override
		protected boolean execute(CoroutineExecutor executor)
		{
			procedure.apply();
			return false;
		}

		@Override
		protected void debug(DepthTest test)
		{
			test.add(this);
		}
	}

	public static class Root<P extends Coroutine<?, ?>> extends Coroutine<Root<P>, P>
	{
		private final String id;

		private Root(P parent, String id)
		{
			super(parent);
			this.id = id;
		}

		@Override
		protected boolean execute(CoroutineExecutor executor)
		{
//			System.out.println("Root -> " + lines);
			return lines.execute(executor);
		}

		@Override
		protected void debug(DepthTest test)
		{
			lines.forEach(l -> l.debug(test));
		}

		@Override
		public String toString()
		{
			return "Root{" + "id='" + id + '\'' + '}';
		}
	}

	public static class Else extends Coroutine<Else, Coroutine<?, ?>>
	{
		private final If<?> ifParent;

		private Else(If<?> ifParent, Coroutine<?, ?> parent)
		{
			super(parent);
			this.ifParent = ifParent;
		}

		@Override
		protected boolean execute(CoroutineExecutor executor)
		{
			return lines.execute(executor);
		}

		@Override
		protected Coroutine<?, ?> endAdds()
		{
			return ifParent;
		}

		@Override
		protected void debug(DepthTest test)
		{
			test.add(this);
			test.depth(1);
			lines.forEach(l -> l.debug(test));
			test.depth(-1);
		}
	}

	public static class ElseIf extends Coroutine<If<?>, Coroutine<?, ?>>
	{
		private final If<?> ifParent;
		private final Supplier<Boolean> condition;

		private ElseIf(If<?> ifParent, Coroutine<?, ?> parent, Supplier<Boolean> condition)
		{
			super(parent);
			this.ifParent = ifParent;
			this.condition = condition;
		}

		@Override
		protected boolean execute(CoroutineExecutor executor)
		{
			if (condition.get())
				return lines.execute(executor);
			return false;
		}

		@Override
		protected Coroutine<?, ?> endAdds()
		{
			return ifParent;
		}

		@Override
		protected If<?> getThis()
		{
			return ifParent;
		}

		@Override
		protected void debug(DepthTest test)
		{
			test.add(this);
			test.depth(1);
			lines.forEach(l -> l.debug(test));
			test.depth(-1);
		}
	}

	public static class If<P extends Coroutine<?, ?>> extends Coroutine<If<P>, P>
	{
		private final Supplier<Boolean> condition;
		private List<ElseIf> elseIfs;
		private Else lastElse;

		private If(P parent, Supplier<Boolean> condition)
		{
			super(parent);
			this.condition = condition;
		}

		@Override
		protected boolean execute(CoroutineExecutor executor)
		{
			if (!condition.get())
			{
				if (elseIfs != null)
				{
					for (ElseIf elseIf : elseIfs)
					{
						if (elseIf.condition.get())
						{
							return elseIf.execute(executor);
						}
					}
				}

				if (lastElse != null)
				{
					return lastElse.execute(executor);
				}
			} else
			{
				return lines.execute(executor);
			}
			return false;
		}

		@Override
		protected void debug(DepthTest test)
		{
			test.add(this);
			test.depth(1);
			lines.forEach(l -> l.debug(test));
			test.depth(-1);
			if (elseIfs != null)
			{
				elseIfs.forEach(l -> l.debug(test));
			}
			if (lastElse != null)
			{
				lastElse.debug(test);
			}
			test.add("End");
		}

		public ElseIf ElseIf(Supplier<Boolean> condition)
		{
			ElseIf elseIf = new ElseIf(this, parent.getAsParent(), condition);
			if (elseIfs == null)
			{
				elseIfs = new ArrayList<>();
			}
			elseIfs.add(elseIf);
			return elseIf;
		}

		public Else Else()
		{
			if (lastElse != null)
				throw new RuntimeException("Else already exists for this if! (how did you get here btw ???)");

			Else anElse = new Else(this, parent.getAsParent());
			lastElse = anElse;
			return anElse;
		}
	}
}
