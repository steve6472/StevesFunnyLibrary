package steve6472.funnylib.util.generators;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.block.data.type.Wall;
import org.bukkit.material.MaterialData;
import steve6472.funnylib.util.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by steve6472
 * Date: 9/15/2022
 * Project: StevesFunnyLibrary <br>
 */
public class BlockStateCodeGenerator
{
	private static final String FACES = """
			public static Faces Faces()
			{
				return new Faces();
			}
			
			public static class Faces
			{
				HashMap<BlockFace, Boolean> faces = new HashMap<>();
				
				public Faces setFace(BlockFace face, boolean has)
				{
					faces.put(face, has);
					return this;
				}
			}
			
			
			
			public static WallFaces WallFaces()
		 	{
		 		return new WallFaces();
		 	}
		 
		 	public static class WallFaces
		 	{
		 		HashMap<BlockFace, Wall.Height> faces = new HashMap<>();
		 
		 		public WallFaces setFace(BlockFace face, Wall.Height height)
		 		{
		 			faces.put(face, height);
		 			return this;
		 		}
		 	}
		   
		   
		   
		   	public static RedFaces RedFaces()
		   	{
		   		return new RedFaces();
		   	}
		   
		   	public static class RedFaces
		   	{
		   		HashMap<BlockFace, RedstoneWire.Connection> faces = new HashMap<>();
		   
		   		public RedFaces setFace(BlockFace face, RedstoneWire.Connection connection)
		   		{
		   			faces.put(face, connection);
		   			return this;
		   		}
		   	}
		   	
		   	
		   	
		""";

	private static final String FACES_SET = """
				face.faces.forEach((f, b) -> {
					if (!data.getAllowedFaces().contains(f))
					{
						throw new RuntimeException("Tried to set non-allowed face!");
					}
					
					data.setFace(f, b);
				});
		""";

	private static final String REDSTONE_SET = """
				face.faces.forEach((f, b) -> {
					if (!data.getAllowedFaces().contains(f))
					{
						throw new RuntimeException("Tried to set non-allowed face!");
					}
					
					data.setFace(f, b);
				});
		""";

	private static final String BREWING_STAND = """
			public static BlockData BrewingStand(boolean bottle0, boolean bottle1, boolean bottle2)
			{
				BrewingStand data = (BrewingStand) Material.BREWING_STAND.createBlockData();
				data.setBottle(0, bottle0);
				data.setBottle(1, bottle1);
				data.setBottle(2, bottle2);
				return data;
			}
		""";

	private static final String CHISELED_BOOKSHELF = """
			/**
			 * @param occupied - mask for slots, left to right
			 */
			public static BlockData ChiseledBookshelf(int occupied, BlockFace facing)
		 	{
		 		ChiseledBookshelf data = (ChiseledBookshelf) Material.CHISELED_BOOKSHELF.createBlockData();
		 		for (int i = 0; i < 6; i++)
		 		{
		 		    data.setSlotOccupied(i, ((occupied >> (5 - i)) & 1) == 1);
		 		}
		 		data.setFacing(facing);
		 		return data;
		 	}
		""";

	public static void main(String[] args)
	{
		StringBuilder generator = new StringBuilder();
		StringBuilder headerGenerator = new StringBuilder();
		headerGenerator.append("package steve6472.funnylib.util.generated;\n\n");

		Set<String> toBeImported = new HashSet<>();
		toBeImported.add("org.bukkit.block.data.*");
		toBeImported.add("org.bukkit.block.data.type.*");
		toBeImported.add("org.bukkit.Material");
		toBeImported.add("java.util.HashMap");

		generator.append("public class BlockGen\n");
		generator.append("{");
		generator.append("\n");

		generator.append(FACES);
		generator.append("\n");


		Material[] values = Material.values();
		for (int i = 0; i < values.length; i++)
		{
			Material value = values[i];

			if (!value.isBlock()) continue;
			if (value.isLegacy()) continue;
			if (value.data.isAssignableFrom(MaterialData.class)) continue;

			// Special for brewing stand 'cause im lazy
			if (value == Material.BREWING_STAND)
			{
				generator.append(BREWING_STAND).append("\n");
				toBeImported.add("org.bukkit.block.data.type.BrewingStand");
				continue;
			}

			if (value == Material.CHISELED_BOOKSHELF)
			{
				generator.append(CHISELED_BOOKSHELF).append("\n");
				continue;
			}



			generator.append("\tpublic static BlockData ").append(transformEnumName(value, true));
			generator.append("(");
			List<Pair<Method, String>> setters = new ArrayList<>();
			boolean hasFaces = false;
			boolean isWall = false;
			boolean isRedstone = false;
			m: for (Method method : value.data.getMethods())
			{
				for (Method blockDataMethod : Arrays.stream(BlockData.class.getMethods()).sorted(Comparator.comparing(m -> m.getName() + m.getReturnType().getSimpleName() + Arrays.toString(m.getParameterTypes()))).toList())
				{
					if (method.getName().equals(blockDataMethod.getName()))
						continue m;
				}

				if (method.isAnnotationPresent(Deprecated.class))
					continue;

				if (MultipleFacing.class.isAssignableFrom(value.data))
				{
					if (method.getName().equals("setFace")
						&& method.getParameters().length == 2
						&& method.getParameters()[0].getType().isAssignableFrom(BlockFace.class)
						&& method.getParameters()[1].getType().isAssignableFrom(boolean.class))
					{
						generator.append("Faces face, ");
						hasFaces = true;
						continue;
					}
				}

				if (Wall.class.isAssignableFrom(value.data))
				{
					if (method.getName().equals("setHeight")
						&& method.getParameters().length == 2
						&& method.getParameters()[0].getType().isAssignableFrom(BlockFace.class)
						&& method.getParameters()[1].getType().isAssignableFrom(Wall.Height.class))
					{
						generator.append("WallFaces face, ");
						isWall = true;
						continue;
					}
				}

				if (RedstoneWire.class.isAssignableFrom(value.data))
				{
					if (method.getName().equals("setFace")
						&& method.getParameters().length == 2
						&& method.getParameters()[0].getType().isAssignableFrom(BlockFace.class)
						&& method.getParameters()[1].getType().isAssignableFrom(RedstoneWire.Connection.class))
					{
						generator.append("RedFaces face, ");
						isRedstone = true;
						continue;
					}
				}

				String name = method.getName();
				if (name.startsWith("set"))
				{
					name = ("" + name.charAt(3)).toLowerCase() + name.substring(4);
					Parameter parameter = method.getParameters()[0];
					if (!parameter.getType().isPrimitive())
					{
						if (parameter.getType().getEnclosingClass() != null)
						{
							toBeImported.add(parameter.getType().getEnclosingClass().getCanonicalName());
						} else
						{
							toBeImported.add(parameter.getType().getCanonicalName());
						}
					}
					String parameterName = parameter.getType().getSimpleName();
					if (parameter.getType().getEnclosingClass() != null)
					{
						parameterName = parameter.getType().getEnclosingClass().getSimpleName() + "." + parameterName;
					}
					name = name.replace("short", "_short");
					generator.append(parameterName).append(" ").append(name).append(", ");
					setters.add(new Pair<>(method, name));
				}
			}
			if (generator.charAt(generator.length() - 2) == ',')
			{
				generator.setLength(generator.length() - 2);
			}

			generator.append(")");
			generator.append("\n\t{\n");
			generator.append("\t\t").append(value.data.getSimpleName()).append(" data = (").append(value.data.getSimpleName()).append(") Material.").append(value.name()).append(".createBlockData();\n");

			for (Pair<Method, String> pair : setters)
			{
				Method method = pair.a();
				String name = pair.b();

				generator.append("\t\tdata.").append(method.getName()).append("(").append(name).append(");\n");
			}

			if (hasFaces)
			{
				generator.append(FACES_SET);
			}

			if (isRedstone)
			{
				generator.append(REDSTONE_SET);
			}

			if (isWall)
			{
				generator.append("face.faces.forEach(data::setHeight);\n");
			}

			generator.append("\t\treturn data;\n");
			generator.append("\t}\n\n");
		}

		generator.append("}");

		for (String s : toBeImported)
		{
			headerGenerator.append("import ").append(s).append(";\n");
		}

		headerGenerator.append("/**\n");
		headerGenerator.append(" * Generated by BlockStateCodeGenerator\n");
		headerGenerator.append(" */\n\n");
		headerGenerator.append("@SuppressWarnings(\"unused\")\n");

		String output = headerGenerator.toString() + generator.toString();

//		System.out.println(output);

		File f = new File("src/main/java/steve6472/funnylib/util/generated/BlockGen.java");
		try
		{
			boolean newFile = f.createNewFile();
			if (!newFile)
			{
				if (f.delete())
				{
					System.out.println("Deleted existing file");
					if (!f.createNewFile())
					{
						throw new IOException("Error creating new file");
					}
				}

//				throw new IOException("Probably already exists or whatever");
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			writer.write(output);
			writer.close();

		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static String transformEnumName(Material material, boolean capitalizeFirst)
	{
		StringBuilder sb = new StringBuilder();

		boolean capitalize = false;

		for (char c : material.name().toLowerCase().toCharArray())
		{
			if (c == '_')
			{
				capitalize = true;
				continue;
			}

			if (capitalize)
			{
				capitalize = false;
				sb.append(("" + c).toUpperCase());
				continue;
			}

			if (capitalizeFirst)
			{
				capitalizeFirst = false;
				sb.append(("" + c).toUpperCase());
				continue;
			}

			sb.append(c);
		}

		return sb.toString();
	}
}
