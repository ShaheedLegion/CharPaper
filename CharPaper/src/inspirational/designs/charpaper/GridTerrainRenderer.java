package inspirational.designs.charpaper;

import inspirational.designs.charpaper.CharPaperService.CharPaperEngine;

import java.io.BufferedInputStream;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class GridTerrainRenderer {

	private class Terrain2DGrid {
		private class Block2D {
			Terrain2DGrid _grid;
			int _width;
			int _height;
			int _curr_index;
			int _current_x;
			int _current_y;
			int _tree_index;

			public Block2D(Terrain2DGrid _g, int x, int y) {
				_grid = _g;
				_width = _grid._renderer.getBlockWidth();
				_height = _grid._renderer.getBlockHeight();
				_curr_index = _grid._random.nextInt(_grid._num_blocks);
				_current_x = x;
				_current_y = y;
				_tree_index = _grid._random.nextInt(_grid._num_trees + 1) - 1; // some
																				// of
																				// them
																				// will
																				// be
																				// -1,
																				// the
																				// rest
																				// will
																				// be
																				// between
																				// 0
																				// and
																				// 1
			}

			public int getIndex() {
				return _curr_index;
			}

			public int getX(int w, int _speed) {
				// if the current x is below the x threshold of this block, then
				// wrap it around to the end of the screen.
				_current_x -= _speed;

				if (_current_x < -_width) {
					_current_x = (_grid.getLastBlockX() + _width) - _speed; // might
																			// need
																			// much
																			// more
																			// logic
																			// here.
					_current_y = (getNextYPosition(_grid.getLastBlockY()));
					_curr_index = _grid._random.nextInt(_grid._num_blocks);
					_tree_index = _grid._random.nextInt(_grid._num_trees + 1) - 1; // when
																					// the
																					// block
																					// wraps
																					// around
																					// it
																					// will
																					// get
																					// anew
																					// tree.
				}
				return _current_x;
			}

			public int getY() {
				return _current_y;
			}

			public int getTreeIndex() {
				return _tree_index;
			}

			public int getNextYPosition(int prevY) {
				float t = (prevY - _width) / (prevY + _width); // map to [0,1]
																// range
				t = t * t * (3 - 2 * t); // map to cubic S-shaped curve
				float h = prevY + t * prevY + _height;
				return (int) h;
			}
		}

		public int _num_blocks; // possibly not needed, but is easier to match
								// to a specific span of block type variables.
		public int _num_trees;
		private int _num_cols;
		public GridTerrainRenderer _renderer;
		int _width;
		int _height;
		private Block2D _blocks[];
		public Random _random;
		private int _block_speed;

		public Terrain2DGrid(GridTerrainRenderer _r, int numblocks, int numtrees) {
			_renderer = _r;
			_num_blocks = numblocks;
			_num_trees = numtrees;
			_width = 0;
			_height = 0;
			_random = new Random();
			_block_speed = 3;
		}

		private void init(int w, int h) {
			// do something useful with the width of the screen - like work out
			// how many columns there are
			// need to break this up further to handle a grid of blocks?
			_num_cols = (w / _renderer.getBlockWidth()) + 2; // make sure there
																// are no
																// visible gaps
																// on the screen
			_blocks = new Block2D[_num_cols];
			_width = w; // skip reinit on every frame :-)
			_height = h;
			for (int i = 0; i < _num_cols; i++)
				_blocks[i] = new Block2D(this, (i * _renderer.getBlockWidth()),
						h - _renderer.getBlockHeight());
		}

		public void draw(Canvas _c, int w, int h) {
			if (w != _width)
				init(w, h);

			for (Block2D _block : _blocks)
				_renderer.drawBlock(_c, _block.getIndex(),
						_block.getX(w, _block_speed), _block.getY());
		}

		public void drawGrass(Canvas _c, int w, int h) {
			if (w != _width)
				init(w, h);

			for (Block2D _block : _blocks)
				_renderer.drawBlockGrassTree(_c, _block.getIndex(),
						_block.getX(w, _block_speed), _block.getY(),
						_block.getTreeIndex());
		}

		public int getLastBlockX() {
			_renderer._trigger._is_triggered = true; // the trigger occurs the
														// moment this function
														// is called - this is
														// the simplest case.
			int _rightmost_x = 0;

			for (int i = 0; i < _num_cols; i++) {
				int cx = _blocks[i]._current_x; // only perform the lookup once
				if (cx > _rightmost_x)
					_rightmost_x = cx;
			}
			return _rightmost_x;
		}

		public int getLastBlockY() {
			int _rightmost_y = 0;
			int _rightmost_x = 0;
			for (int i = 0; i < _num_cols; i++) {
				int cx = _blocks[i]._current_x; // only perform the lookup twice
				int cy = _blocks[i]._current_y;
				if (cx > _rightmost_x) {
					_rightmost_x = cx;
					_rightmost_y = cy;
				}
			}
			return _rightmost_y;
		}
	}

	Bitmap _blocks[];
	final int _num_blocks = 3;
	final int _num_trees = 3;
	int _block_height;
	int _block_width;
	Terrain2DGrid _grid;
	EventTrigger _trigger;
	public boolean _draw_grass;
	int _block_bias; // the amount of "empty space" there is at the top of the
						// land block image
	int _terrain_top;

	public GridTerrainRenderer(CharPaperEngine _service, EventTrigger _trig) {
		_trigger = _trig;
		_blocks = new Bitmap[_num_blocks + _num_trees + 1];
		_grid = new Terrain2DGrid(this, _num_blocks, _num_trees); // numtrees at
																	// end

		_blocks[0] = BitmapFactory.decodeStream(new BufferedInputStream(
				_service.getRes(R.drawable.block_brown)));// don't mind the g.c.
															// penalty for these
															// throw-away
															// objects
		_blocks[1] = BitmapFactory.decodeStream(new BufferedInputStream(
				_service.getRes(R.drawable.block_plain)));
		_blocks[2] = BitmapFactory.decodeStream(new BufferedInputStream(
				_service.getRes(R.drawable.block_dirt)));

		// the rest of these blocks are "special items" which are rendered atop
		// the blocks, but do not form part of the calculations
		_blocks[3] = BitmapFactory.decodeStream(new BufferedInputStream(
				_service.getRes(R.drawable.block_grass))); // fetch the grass
															// texture, which is
															// rendered on top
															// of each block.
		_blocks[4] = BitmapFactory.decodeStream(new BufferedInputStream(
				_service.getRes(R.drawable.tree_tall)));
		_blocks[5] = BitmapFactory.decodeStream(new BufferedInputStream(
				_service.getRes(R.drawable.tree_short)));
		_blocks[6] = BitmapFactory.decodeStream(new BufferedInputStream(
				_service.getRes(R.drawable.tree_ugly)));

		_block_height = _blocks[0].getHeight();
		_block_width = _blocks[0].getWidth();
		_draw_grass = true;
		_block_bias = 50; // this changes as the tile images change.
		_terrain_top = 0;
	}

	public void draw(Canvas _c, int w, int h) {
		if (_draw_grass)
			_grid.drawGrass(_c, w, h);
		else
			_grid.draw(_c, w, h);
	}

	public void drawBlock(Canvas _c, int index, int x, int y) {
		if (index < 0)
			return;
		if (index > (_num_blocks - 1))
			return;

		_c.drawBitmap(_blocks[index], (float) x, (float) y, null);
		_terrain_top = ((y - _block_height) + _block_bias); // this is the first
															// pixel on top of a
															// block (terrain
															// surface)
	}

	public void drawBlockGrass(Canvas _c, int index, int x, int y) {
		if (index < 0)
			return;
		if (index > (_num_blocks - 1))
			return;

		_c.drawBitmap(_blocks[index], (float) x, (float) y, null);
		_c.drawBitmap(_blocks[3], (float) x,
				(float) ((y - _block_height) + _block_bias), null);
		_terrain_top = (y - _block_height) + (_block_bias * 2);
	}

	public void drawBlockGrassTree(Canvas _c, int index, int x, int y,
			int _tree_type) {
		if (index < 0)
			return;
		if (index > (_num_blocks - 1))
			return;

		_c.drawBitmap(_blocks[index], (float) x, (float) y, null);
		_c.drawBitmap(_blocks[3], (float) x,
				(float) ((y - _block_height) + _block_bias), null);

		_terrain_top = (y - _block_height) + (_block_bias * 3); // figure out
																// where the
																// terrain
																// starts - is
																// we are
																// drawing
																// trees, then
																// the bias
																// become
																// greater
		int tree_bias = _terrain_top - _block_bias;

		if (_tree_type >= 0)
			_c.drawBitmap(_blocks[4 + _tree_type], (float) x,
					(float) ((tree_bias - _block_height) + _block_bias), null);
	}

	public int getBlockHeight() {
		return _block_height;
	}

	public int getBlockWidth() {
		return _block_width;
	}

	public int getTerrainTop(int x) {
		return _terrain_top;
	}
}
