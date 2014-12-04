package inspirational.designs.charpaper;

import java.util.Random;

import android.graphics.Canvas;

/*
 Represents a moving terrain grid, which scrolls and fills up with specific "blocks" of terrain.
 The blocks are loaded and drawn outside of this class since it is easier to work with flat arrays outside, and simply
 attach the rules of movement to this class.
 
 Later, we will add functionality to render a grass layer above the blocks which are drawn, simply to provide a uniform 
 "surface" effect for the canvas.
 
 I want to add a "trigger" to this class so that we can add "events" to the animation - like an event to make the character
 walk in from the side of the screen towards the center ... there is a quick and dirty method, and then there is the right way. 
 * */
public class TerrainGrid
{
	public int _num_blocks;	//possibly not needed, but is easier to match to a specific span of block type variables.
	public int _num_trees;
	private int _num_cols;
	public TerrainRenderer _renderer;
	int _width;
	private Block _blocks[];
	public Random _random;
	private int _block_speed;
	
	public TerrainGrid(TerrainRenderer _r, int numblocks, int numtrees)
	{
		_renderer = _r;
		_num_blocks = numblocks;
		_num_trees = numtrees;
		_width = 0;
		_random = new Random();
		_block_speed = 3;
	}
	
	private void init(int w)
	{
		//do something useful with the width of the screen - like work out how many columns there are
		//need to break this up further to handle a grid of blocks?
		_num_cols = (w / _renderer.getBlockWidth()) + 2;	//make sure there are no visible gaps on the screen
		_blocks = new Block[_num_cols];
		_width = w;	//skip reinit on every frame :-)
		for (int i = 0; i < _num_cols; i++)
			_blocks[i] = new Block(this, (i * _renderer.getBlockWidth()));
	}

	public void draw(Canvas _c, int w, int h)
	{
		if (w != _width)
			init(w);

		int _block_height = _renderer.getBlockHeight();	//fetch the height of each block.
		for (Block _block : _blocks)
			_renderer.drawBlock(_c, _block.getIndex(), _block.getX(w, _block_speed), h - _block_height);
	}

	public void drawGrass(Canvas _c, int w, int h)
	{
		if (w != _width)
			init(w);

		int _block_height = _renderer.getBlockHeight();	//fetch the height of each block.
		for (Block _block : _blocks)
			_renderer.drawBlockGrassTree(_c, _block.getIndex(), _block.getX(w, _block_speed), h - _block_height, _block.getTreeIndex());
	}
	
	public int getLastBlockX()
	{
		_renderer._trigger._is_triggered = true;	//the trigger occurs the moment this function is called - this is the simplest case.
		int _rightmost_x = 0;

		for (int i = 0; i < _num_cols; i++)
		{
			int cx = _blocks[i]._current_x;	//only perform the lookup once
			if (cx > _rightmost_x)
				_rightmost_x = cx; 
		}
		return _rightmost_x;
	}
}
