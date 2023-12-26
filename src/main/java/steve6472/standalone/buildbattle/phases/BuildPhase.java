package steve6472.standalone.buildbattle.phases;

import steve6472.funnylib.minigame.AbstractGamePhase;
import steve6472.funnylib.minigame.Game;
import steve6472.funnylib.util.RandomUtil;
import steve6472.standalone.buildbattle.BuildBattleGame;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 8/6/2023
 * Project: StevesFunnyLibrary <br>
 */
public class BuildPhase extends AbstractGamePhase
{
	private final List<String> themes;

	public BuildPhase(Game game)
	{
		super(game);
		themes = new ArrayList<>(BuildBattleGame.POSSIBLE_THEMES);
	}

	private String pickRandomTheme()
	{
		if (themes.isEmpty())
			themes.addAll(BuildBattleGame.POSSIBLE_THEMES);
		return themes.remove(RandomUtil.randomInt(0, themes.size() - 1));
	}

	@Override
	public void start()
	{

	}

	@Override
	public void end()
	{

	}
}
