package steve6472.funnylib.json;

import org.bukkit.persistence.PersistentDataContainer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import steve6472.funnylib.serialize.NBT;
import steve6472.funnylib.serialize.PdcNBT;
import steve6472.funnylib.util.MiscUtil;
import steve6472.funnylib.util.NMS;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * Created by steve6472
 * Date: 5/20/2023
 * Project: StevesFunnyLibrary <br>
 */
public class JsonNBT
{
	private static final Pattern BYTE_REGEX = Pattern.compile("\\[B;(\\d+B(?:,\\d+B)*)\\]");
	private static final Pattern INT_REGEX = Pattern.compile("\\[I;(\\d+(?:,\\d+)*)\\]");
	private static final Pattern LONG_REGEX = Pattern.compile("\\[L;(\\d+L(?:,\\d+L)*)\\]");

	public static JSONObject NBTtoJSON(NBT nbt)
	{
		return containertoJSON(nbt.getContainer());
	}

	public static JSONObject containertoJSON(PersistentDataContainer container)
	{
		Map<String, Object> stringObjectMap = NMS.serializePDC(container);
		return new JSONObject(stringObjectMap);
	}

	private static NBT[] parseArray(NBT nbt, JSONArray array)
	{
		NBT[] compoundArray = nbt.createCompoundArray(array.length());

		if (array.length() == 0)
		{
			return compoundArray;
		}

		if (array.get(0) instanceof JSONObject)
		{
			for (int i = 0; i < array.length(); i++)
			{
				JSONObject jsonObject1 = array.getJSONObject(i);
				compoundArray[i] = PdcNBT.fromPDC(JSONtoNBT(jsonObject1));
			}
		} else if (array.get(0) instanceof JSONArray a)
		{
			for (int i = 0; i < a.length(); i++)
			{
				JSONArray jsonArray = array.getJSONArray(i);
				parseArray(compoundArray[i], jsonArray);
			}
		} else
		{
			throw new RuntimeException("Unexpected type in array " + array.get(0).getClass().getSimpleName());
		}

		return compoundArray;
	}

	public static PersistentDataContainer JSONtoNBT(JSONObject jsonObject)
	{
		PersistentDataContainer container = NMS.newCraftContainer();
		PdcNBT nbt = PdcNBT.fromPDC(container);

		for (String key : jsonObject.keySet())
		{
			Object o = jsonObject.get(key);
			if (o instanceof JSONObject nestedJson)
			{
				nbt.setCompound(key, JSONtoNBT(nestedJson));
				continue;
			}

			if (o instanceof JSONArray array)
			{
				nbt.setCompoundArray(key, parseArray(nbt, array));
				continue;
			}

			String string = jsonObject.getString(key);

			if (isNumeric(string, 'b')) nbt.setByte(key, toNumber(string).byteValue());
			else if (isNumeric(string, 's')) nbt.setShort(key, toNumber(string).shortValue());
			else if (isNumeric(string, 'i')) nbt.setInt(key, toNumber(string).intValue());
			else if (isNumeric(string, 'l')) nbt.setLong(key, toNumber(string).longValue());
			else if (isNumeric(string, 'f')) nbt.setFloat(key, toNumber(string).floatValue());
			else if (isNumeric(string, 'd')) nbt.setDouble(key, toNumber(string).doubleValue());
			else
			{
				byte[] byteArr = extractBytes(string);
				if (byteArr != null) { nbt.setByteArray(key, byteArr); continue; }
				int[] intArr = extractIntegers(string);
				if (intArr != null) { nbt.setIntArray(key, intArr); continue; }
				long[] longArr = extractLongs(string);
				if (longArr != null) { nbt.setLongArray(key, longArr); continue; }
				nbt.setString(key, string);
			}
		}
		return nbt.getContainer();
	}

	public static byte[] extractBytes(String input)
	{
		Matcher matcher = BYTE_REGEX.matcher(input);

		if (!matcher.matches())
			return null;

		String numbersString = matcher.group(1);
		String[] numberArr = numbersString.split(",");
		byte[] numbers = new byte[numberArr.length];

		for (int i = 0; i < numberArr.length; i++)
		{
			numbers[i] = Byte.parseByte(numberArr[i].replace("B", ""));
		}

		return numbers;

	}

	public static int[] extractIntegers(String input)
	{
		Matcher matcher = INT_REGEX.matcher(input);

		if (!matcher.matches())
			return null;

		String numbersString = matcher.group(1);
		String[] numberArr = numbersString.split(",");
		int[] numbers = new int[numberArr.length];

		for (int i = 0; i < numberArr.length; i++)
		{
			numbers[i] = Integer.parseInt(numberArr[i]);
		}

		return numbers;

	}

	public static long[] extractLongs(String input)
	{
		Matcher matcher = LONG_REGEX.matcher(input);

		if (!matcher.matches())
			return null;

		String numbersString = matcher.group(1);
		String[] numberArr = numbersString.split(",");
		long[] numbers = new long[numberArr.length];

		for (int i = 0; i < numberArr.length; i++)
		{
			numbers[i] = Long.parseLong(numberArr[i].replace("L", ""));
		}

		return numbers;

	}

	private static Number toNumber(String s)
	{
		String substring = s.substring(0, s.length() - 1);
		return stringToNumber(substring);
	}

	private static byte[] toByteArray(IntStream stream)
	{
		return stream
			.collect(ByteArrayOutputStream::new, (baos, i) -> baos.write((byte) i), (baos1, baos2) -> baos1.write(baos2.toByteArray(), 0, baos2.size()))
			.toByteArray();
	}

	private static boolean isNumeric(String text, char compare)
	{
		boolean b = text.toLowerCase().endsWith(Character.toString(Character.toLowerCase(compare)));
		if (!b) return false;
		String num = text.substring(0, text.length() - 1);
		return MiscUtil.IS_INTEGER.matcher(num).matches() || MiscUtil.IS_DECIMAL.matcher(num).matches();
	}

	/**
	 * Tests if the value should be tried as a decimal. It makes no test if there are actual digits.
	 *
	 * @param val value to test
	 * @return true if the string is "-0" or if it contains '.', 'e', or 'E', false otherwise.
	 */
	protected static boolean isDecimalNotation(final String val)
	{
		return val.indexOf('.') > -1 || val.indexOf('e') > -1 || val.indexOf('E') > -1 || "-0".equals(val);
	}

	/**
	 * Converts a string to a number using the narrowest possible type. Possible
	 * returns for this function are BigDecimal, Double, BigInteger, Long, and Integer.
	 * When a Double is returned, it should always be a valid Double and not NaN or +-infinity.
	 *
	 * @param val value to convert
	 * @return Number representation of the value.
	 * @throws NumberFormatException thrown if the value is not a valid number. A public
	 *                               caller should catch this and wrap it in a {@link JSONException} if applicable.
	 */
	protected static Number stringToNumber(final String val) throws NumberFormatException
	{
		char initial = val.charAt(0);
		if ((initial >= '0' && initial <= '9') || initial == '-')
		{
			// decimal representation
			if (isDecimalNotation(val))
			{
				// Use a BigDecimal all the time so we keep the original
				// representation. BigDecimal doesn't support -0.0, ensure we
				// keep that by forcing a decimal.
				try
				{
					BigDecimal bd = new BigDecimal(val);
					if (initial == '-' && BigDecimal.ZERO.compareTo(bd) == 0)
					{
						return -0.0;
					}
					return bd;
				} catch (NumberFormatException retryAsDouble)
				{
					// this is to support "Hex Floats" like this: 0x1.0P-1074
					try
					{
						Double d = Double.valueOf(val);
						if (d.isNaN() || d.isInfinite())
						{
							throw new NumberFormatException("val [" + val + "] is not a valid number.");
						}
						return d;
					} catch (NumberFormatException ignore)
					{
						throw new NumberFormatException("val [" + val + "] is not a valid number.");
					}
				}
			}
			// block items like 00 01 etc. Java number parsers treat these as Octal.
			if (initial == '0' && val.length() > 1)
			{
				char at1 = val.charAt(1);
				if (at1 >= '0' && at1 <= '9')
				{
					throw new NumberFormatException("val [" + val + "] is not a valid number.");
				}
			} else if (initial == '-' && val.length() > 2)
			{
				char at1 = val.charAt(1);
				char at2 = val.charAt(2);
				if (at1 == '0' && at2 >= '0' && at2 <= '9')
				{
					throw new NumberFormatException("val [" + val + "] is not a valid number.");
				}
			}
			// integer representation.
			// This will narrow any values to the smallest reasonable Object representation
			// (Integer, Long, or BigInteger)

			// BigInteger down conversion: We use a similar bitLength compare as
			// BigInteger#intValueExact uses. Increases GC, but objects hold
			// only what they need. i.e. Less runtime overhead if the value is
			// long lived.
			BigInteger bi = new BigInteger(val);
			if (bi.bitLength() <= 31)
			{
				return bi.intValue();
			}
			if (bi.bitLength() <= 63)
			{
				return bi.longValue();
			}
			return bi;
		}
		throw new NumberFormatException("val [" + val + "] is not a valid number.");
	}
}
