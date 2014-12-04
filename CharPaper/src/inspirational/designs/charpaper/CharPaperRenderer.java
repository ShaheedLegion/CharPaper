package inspirational.designs.charpaper;

import inspirational.designs.charpaper.CharPaperService.CharPaperEngine;
import android.graphics.Canvas;

public class CharPaperRenderer
{
	private SkyRenderer _sky;
	private CloudsRenderer _clouds;
	private TerrainRenderer _terrain;
	private BirdRenderer _pet;
	private CharacterRenderer _char;
	private EventTrigger _motion_trigger;

	public CharPaperRenderer(CharPaperEngine _service)
	{
		_motion_trigger = new EventTrigger();
		_sky = new SkyRenderer(_service);
		_clouds = new CloudsRenderer(_service);
		_terrain = new TerrainRenderer(_service, _motion_trigger);
		_pet = new BirdRenderer(_service);
		_char = new CharacterRenderer(_service, _motion_trigger);
	}
	
	public void draw(Canvas _c, int w, int h)
	{
		_sky.draw(_c, w, h);
		_clouds.draw(_c, w, h);
		_terrain.draw(_c, w, h);
		_pet.draw(_c, w, h);
		_char.draw(_c, w, h, _terrain.getTerrainTop());	//render the character on the surface of the terrain.
	}
	
	public void handleTap(int x, int y, int z)
	{
		_pet.setTarget(x, y, z);
		_char.setTarget(x, y, z);
	}
	
	public void handleDown(float x, float y)
	{
		_clouds.handleDown(x, y);
	}
	public void handleMove(float x, float y)
	{
		_clouds.handleMove(x, y);
	}
	public void handleUp(float x, float y)
	{
		_clouds.handleUp(x, y);
	}
}
