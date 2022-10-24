package steve6472.funnylib.menu;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 9/13/2022
 * Project: StevesFunnyLibrary <br>
 */
public class Mask
{
	private List<String> patterns = new ArrayList<>();
	private Map<Character, SlotBuilder> characterMap = new HashMap<>();
	private Map<Character, ISlotBuilder> characterIMap = new HashMap<>();

	private int offsetX, offsetY;
	private char emptySlot = '.';

	private Mask()
	{

	}

	public static Mask createMask()
	{
		return new Mask();
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

	public Mask addItem(char character, SlotBuilder slot)
	{
		characterMap.put(character, slot);
		return this;
	}

	public Mask addItem(char character, ISlotBuilder slot)
	{
		characterIMap.put(character, slot);
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

				SlotBuilder slotBuilder = characterMap.get(c);
				if (slotBuilder != null)
				{
					menu.setSlot(j + offsetX, i + offsetY, slotBuilder);
				}
			}
		}

		menu.reload();
	}

	void applyMask(MenuBuilder builder)
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

				SlotBuilder slotBuilder = characterMap.get(c);
				if (slotBuilder != null)
				{
					builder.slot(j + offsetX, i + offsetY, slotBuilder);
				} else
				{
					ISlotBuilder iSlotBuilder = characterIMap.get(c);
					if (iSlotBuilder != null)
					{
						builder.slot(j + offsetX, i + offsetY, iSlotBuilder);
					}
				}

			}
		}
	}
}
