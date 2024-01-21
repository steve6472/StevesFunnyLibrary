package steve6472.funnylib.minigame.config.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import steve6472.funnylib.menu.Menu;
import steve6472.funnylib.menu.Response;
import steve6472.funnylib.menu.slots.buttons.ButtonSlot;
import steve6472.funnylib.util.JSONMessage;

/**
 * Created by steve6472
 * Date: 1/20/2024
 * Project: StevesFunnyLibrary <br>
 */
public class Vec3iConfigMenu extends Menu
{
	public Vec3iConfigMenu(String title)
	{
		super(4, "Vec 3i - " + title, false);
	}

	@Override
	protected void setup()
	{
		setSlot(8, 3, new ButtonSlot(JSONMessage.create("Save").color(ChatColor.GREEN), Material.EMERALD).setClick(click ->
		{
//			gameConfig.save();
//			saved = true;
			JSONMessage.create("Settings saved!").color(ChatColor.GREEN).send(click.player());
			if (click.type().isShiftClick())
				return Response.cancel();
			return Response.exit();
		}));

		setSlot(7, 3, new ButtonSlot(JSONMessage.create("Discard").color(ChatColor.RED), Material.REDSTONE).setClick(click ->
		{
//			gameConfig.load();
			return Response.exit();
		}));
	}
}
