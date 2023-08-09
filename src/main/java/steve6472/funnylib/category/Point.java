package steve6472.funnylib.category;

/**
 * Created by steve6472
 * Date: 7/7/2023
 * Project: StevesFunnyLibrary <br>
 */
record Point(int x, int y)
{
	static Point getItemLocation(int item)
	{
		int page = item / 45;
		int x = ((item % 45) % 9) + (page * 9);
		int y = (item % 45) / 9;
		return new Point(x, y);
	}
}
