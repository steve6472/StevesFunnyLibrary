package steve6472.standalone.interactable.script;

import org.bukkit.Bukkit;
import oshi.util.platform.unix.openbsd.FstatUtil;
import steve6472.scriptit.Result;
import steve6472.scriptit.Script;
import steve6472.scriptit.expressions.Function;
import steve6472.scriptit.expressions.FunctionParameters;
import steve6472.scriptit.libraries.Library;
import steve6472.scriptit.transformer.JavaTransformer;
import steve6472.scriptit.type.PrimitiveTypes;
import steve6472.scriptit.value.Value;

/**********************
 * Created by steve6472
 * On date: 6/9/2022
 * Project: AkmaEventPlugin
 *
 ***********************/
public class MinecraftLibrary extends Library
{
	public MinecraftLibrary()
	{
		super("Minecraft");
		init();
	}

	public void init()
	{
		addFunction("broadcast", (text) ->
		{
			Bukkit.broadcastMessage("" + text.toString());

			return Value.NULL;
		}, PrimitiveTypes.ANY_TYPE);

		addFunction(FunctionParameters.function("fullDebug").build(), new Function()
		{
			@Override
			public Result apply(Script script)
			{
				System.out.println("Variables:");
				script.getMemory().variables.forEach((k, v) -> System.out.println(k + " -> " + v));
				System.out.println("\n\nTypes:");
				script.getMemory().types.forEach((k, v) ->
				{
					if (!v.isArray())
					{
						System.out.println("\n" + k + " -> " + v);
						System.out.println("\tFunctions:");
						v.functions.forEach((p, f) -> System.out.println("\t" + p + " -> " + f));
						System.out.println("\tConstructors:");
						v.constructors.forEach((p, f) -> System.out.println("\t" + p + " -> " + f));
						System.out.println("\tUnary:");
						v.unary.forEach((p, f) -> System.out.println("\t" + p + " -> " + f));
						System.out.println("\tBinary:");
						v.binary.forEach((p, f) ->
						{
							System.out.println("\t" + p + " -> ");
							f.forEach((a, b) -> System.out.println("\t\t" + a + " -> " + b));
						});
					}
				});
				System.out.println("\n\nLibraries:");
				script.getMemory().libraries.forEach((k, v) -> System.out.println(k + " -> " + v));
				System.out.println("\n\nFunctions:");
				script.getMemory().functions.forEach((k, v) ->
				{
					if (v.name == null)
					{
						System.out.println(k + " -> " + v);
					} else if (!v.name.endsWith("[]"))
					{
						System.out.println(k + " -> " + v);
					}
				});

				return Result.pass();
			}
		});

		addFunction(FunctionParameters.function("getPlayer").addType(PrimitiveTypes.STRING).build(), new Function()
		{
			@Override
			public Result apply(Script script)
			{
				try
				{
					return Result.value(JavaTransformer.transformObject(Bukkit.getPlayer(arguments[0].asPrimitive().getString()), script));
				} catch (IllegalAccessException e)
				{
					throw new RuntimeException(e);
				}
			}
		});

		addFunction(FunctionParameters.function("getWorld").addType(PrimitiveTypes.STRING).build(), new Function()
		{
			@Override
			public Result apply(Script script)
			{
				try
				{
					return Result.value(JavaTransformer.transformObject(Bukkit.getWorld(arguments[0].asPrimitive().getString()), script));
				} catch (IllegalAccessException e)
				{
					throw new RuntimeException(e);
				}
			}
		});

//		addFunction("getPlayer", (playerName) ->
//		{
//			Player player = Bukkit.getPlayer(playerName.asPrimitive().getString());
//			if (player == null)
//			{
//				return Value.NULL;
//			}
//
//			try
//			{
//				return JavaTransformer.generateType();
//			} catch (IllegalAccessException e)
//			{
//				throw new RuntimeException(e);
//			}
//		}, PrimitiveTypes.STRING);
/*
		addFunction("getWorld", (worldName) ->
		{
			World world = Bukkit.getWorld(worldName.asPrimitive().getString());
			if (world == null)
			{
				return Value.NULL;
			}

			try
			{
				return ClassTransformer.transformObject(world);
			} catch (IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
		}, PrimitiveTypes.STRING);

		addFunction("material", (materialName) ->
		{
			Material material;
			try
			{
				material = Material.valueOf(materialName.asPrimitive().getString().toUpperCase());
			} catch (IllegalArgumentException ex)
			{
				material = Material.AIR;
			}

			try
			{
				return ClassTransformer.transformObject(material);
			} catch (IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
		}, PrimitiveTypes.STRING);

		addFunction("itemStack", (materialName) ->
		{
			Material material;
			try
			{
				material = Material.valueOf(materialName.asPrimitive().getString().toUpperCase());
			} catch (IllegalArgumentException ex)
			{
				material = Material.AIR;
			}

			ItemStack itemStack = new ItemStack(material);

			try
			{
				return ClassTransformer.transformObject(itemStack);
			} catch (IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
		}, PrimitiveTypes.STRING);*/
	}
}
