package steve6472.funnylib.menu;

import java.util.Objects;

/**
 * Created by steve6472
 * Date: 9/13/2022
 * Project: StevesFunnyLibrary <br>
 */
public record SlotLoc(int x, int y)
{
	int toIndex(int ox, int oy, int rows)
	{
		if (x - ox < 0 || x - ox > 8 || y - oy < 0 || y - oy > rows) return -1;

		return (x - ox) + (y - oy) * 9;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SlotLoc slotLoc = (SlotLoc) o;
		return x == slotLoc.x && y == slotLoc.y;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(x, y);
	}
}
