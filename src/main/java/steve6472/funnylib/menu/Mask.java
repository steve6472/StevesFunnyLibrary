package steve6472.funnylib.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 9/13/2022
 * Project: StevesFunnyLibrary <br>
 */
public class Mask
{
	private final List<String> patterns = new ArrayList<>();
	private final Map<Character, Supplier<Slot>> characterMap = new HashMap<>();

	private int offsetX, offsetY;
	private char emptySlot = '.';

	public Mask()
	{

	}

	public Mask addRow(String pattern)
	{
		patterns.add(pattern);
		return this;
	}

	public Mask addRow(String pattern, int repeat)
	{
		for (int i = 0; i < repeat; i++)
		{
			patterns.add(pattern);
		}
		return this;
	}

	public Mask addItem(char character, Supplier<Slot> slot)
	{
		characterMap.put(character, slot);
		return this;
	}

	public Mask setOffset(int offsetX, int offsetY)
	{
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		return this;
	}

	public Mask setEmptySlotChar(char c)
	{
		this.emptySlot = c;
		return this;
	}

	void applyMask(Menu menu)
	{
		for (int i = 0; i < patterns.size(); i++)
		{
			String pattern = patterns.get(i);
			char[] charArray = pattern.toCharArray();
			for (int j = 0; j < charArray.length; j++)
			{
				char c = charArray[j];
				if (c == emptySlot)
				{
					continue;
				}

				Slot slot = characterMap.get(c).get();
				if (slot != null)
				{
					menu.setSlot(j + offsetX, i + offsetY, slot);
				}
			}
		}

		menu.reload();
	}
}
