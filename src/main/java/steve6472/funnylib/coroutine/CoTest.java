package steve6472.funnylib.coroutine;

import steve6472.standalone.interactable.ex.CodeExecutor;

import java.util.concurrent.TimeUnit;

/**
 * Created by steve6472
 * Date: 11/8/2022
 * Project: StevesFunnyLibrary <br>
 */
public class CoTest
{
	public static void main(String[] args)
	{
//		Coroutine.create("run").Do(() -> System.out.println("Hello World!")).Nop().finish().run();

		long startTime = System.nanoTime();
		boolean[] b = {false};

		CoroutineExecutor executor = Coroutine
			.create("loop")
			.Say("START")
//			.Loop(0, i -> i < 8, 1, (l) -> l
//				.If(() -> l.i() == 1)
//					.Say("\tI == 2")
//					.Wait(6)
//				.End()
//				.Say("\tIndex: " + l.i())
//			)
			.WaitUntilTrue(() -> System.nanoTime() - startTime >= TimeUnit.MILLISECONDS.toNanos(1000))
			.Say("END")
			.Do(() -> b[0] = true)
			.finish();

		while (!b[0])
		{
			executor.run();
		}

//		for (int i = 0; i < 10; i++)
//		{
//			executor.run();
//		}

//		Thread t = new Thread(r, "coroutine");
//		t.start();
//		while (true)
//		{
//			if (System.nanoTime() - startTime >= TimeUnit.MILLISECONDS.toNanos(50))
//			{
//				t.stop();
//				break;
//			}
//		}

		// TODO: implement the cache system

		if (true)
			return;

		Coroutine
			.create("test")
				.If(() -> true)
					.Nop()
					.Nop()
				.Else()
					.If(() -> true)
						.Nop()
					.Else()
						.Nop()
						.Nop()
					.End()
				.End()
			.finish()
			.run();

		Coroutine
			.create("best")
				.If(() -> true)
					.Nop()
					.Nop()
				.ElseIf(() -> false)
					.Nop()
				.Else()
					.Nop()
				.End()
			.finish()
			.run();

		Coroutine
			.create("if_elseif")
				.If(() -> true)
					.Nop()
				.ElseIf(() -> false)
					.Nop()
					.Nop()
				.End();

		System.out.println(Coroutine.create("empty"));
		System.out.println(Coroutine.create("if").If(() -> true).Nop().End());
		System.out.println(Coroutine.create("if_else").If(() -> true).Nop().Else().End());
		System.out.println(Coroutine.create("if_elseif").If(() -> true).Nop().ElseIf(() -> false).Nop().Nop().End());

		//		for (int i = 0; i < 256; i++)
//		{
//			run(i);
//		}
	}

	public static void run(final int tick)
	{
//		Coroutine.create("test")
//			.If(() -> tick < 20)
//				.Do(() -> System.out.println("Below 20 ticks"))
//			.orElse()
//				.Do(() -> System.out.println("Above 20 ticks"))
//			.waitUntil(() -> tick >= 20)
//			.run();
	}
}
