package inspirational.designs.charpaper;

import java.io.BufferedInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import inspirational.designs.charpaper.CharPaperService.CharPaperEngine;

/*
 * Stores terrain block images and exposes methods to access them. Also contains code to render the images.
 * By making small changes to the behavior of this class, we can achieve interesting effects on the "surface"
 * of terrain that this class manages.
 * 
 * UPDATE (2015/06/09) - Adding randomness by implmenting a way to get the top of the terrain at a specific position.
 * */

public class TerrainRenderer
{
	Bitmap _blocks[];
	final int _num_blocks = 3;
	final int _num_trees = 3;
	int _block_height;
	int _block_width;
	TerrainGrid _grid;
	EventTrigger _trigger;
	public boolean _draw_grass;
	int _block_bias;	//the amount of "empty space" there is at the top of the land block image
	int _terrain_top;

	public TerrainRenderer(CharPaperEngine _service, EventTrigger _trig)
	{
		_trigger = _trig;
		_blocks = new Bitmap[_num_blocks + _num_trees + 1];
		_grid = new TerrainGrid(this, _num_blocks, _num_trees);	//numtrees at end
		
		_blocks[0] = BitmapFactory.decodeStream(new BufferedInputStream(_service.getRes(R.drawable.block_brown)));//don't mind the g.c. penalty for these throw-away objects
		_blocks[1] = BitmapFactory.decodeStream(new BufferedInputStream(_service.getRes(R.drawable.block_plain)));
		_blocks[2] = BitmapFactory.decodeStream(new BufferedInputStream(_service.getRes(R.drawable.block_dirt)));

		//the rest of these blocks are "special items" which are rendered atop the blocks, but do not form part of the calculations
		_blocks[3] = BitmapFactory.decodeStream(new BufferedInputStream(_service.getRes(R.drawable.block_grass)));	//fetch the grass texture, which is rendered on top of each block.
		_blocks[4] = BitmapFactory.decodeStream(new BufferedInputStream(_service.getRes(R.drawable.tree_tall)));
		_blocks[5] = BitmapFactory.decodeStream(new BufferedInputStream(_service.getRes(R.drawable.tree_short)));
		_blocks[6] = BitmapFactory.decodeStream(new BufferedInputStream(_service.getRes(R.drawable.tree_ugly)));

		_block_height = _blocks[0].getHeight();
		_block_width = _blocks[0].getWidth();
		_draw_grass = true;
		_block_bias = 50;	//this changes as the tile images change.
		_terrain_top = 0;
	}
	
	public void draw(Canvas _c, int w, int h)
	{
		if (_draw_grass)
			_grid.drawGrass(_c, w, h);
		else
			_grid.draw(_c,  w, h);
	}
	
	public void drawBlock(Canvas _c, int index, int x, int y)
	{
		if (index < 0)
			return;
		if (index > (_num_blocks-1))
			return;
		
		_c.drawBitmap(_blocks[index], (float)x, (float)y, null);
		_terrain_top = ((y - _block_height) + _block_bias);	//this is the first pixel on top of a block (terrain surface)
	}
	
	public void drawBlockGrass(Canvas _c, int index, int x, int y)
	{
		if (index < 0)
			return;
		if (index > (_num_blocks-1))
			return;
		
		_c.drawBitmap(_blocks[index], (float)x, (float)y, null);
		_c.drawBitmap(_blocks[3], (float)x, (float)((y - _block_height) + _block_bias), null);
		_terrain_top = (y - _block_height) + (_block_bias * 2);
	}

	public void drawBlockGrassTree(Canvas _c, int index, int x, int y, int _tree_type)
	{
		if (index < 0)
			return;
		if (index > (_num_blocks-1))
			return;
		
		_c.drawBitmap(_blocks[index], (float)x, (float)y, null);
		_c.drawBitmap(_blocks[3], (float)x, (float)((y - _block_height) + _block_bias), null);

		_terrain_top = (y - _block_height) + (_block_bias * 3);	//figure out where the terrain starts - is we are drawing trees, then the bias become greater
		int tree_bias = _terrain_top - _block_bias;

		if (_tree_type >= 0)
			_c.drawBitmap(_blocks[4 + _tree_type], (float)x, (float)((tree_bias - _block_height) + _block_bias), null);
	}

	public int getBlockHeight()
	{
		return _block_height;
	}
	
	public int getBlockWidth()
	{
		return _block_width;
	}
	
	public int getTerrainTop(int x)
	{
		return _terrain_top;
	}
}
