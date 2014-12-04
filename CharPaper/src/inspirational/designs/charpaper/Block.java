package inspirational.designs.charpaper;

/*
 This class handles the movement of the blocks (implements wraparound scrolling) without dealing with pesky drawing details, other than
 indicating the current index of the image to be drawn (which is chosen randomly).
 */
public class Block
{
	TerrainGrid _grid;
	int _width;
	int _curr_index;
	int _current_x;
	int _tree_index;

	public Block(TerrainGrid _g, int x)
	{
		_grid = _g;
		_width = _grid._renderer.getBlockWidth();
		_curr_index = _grid._random.nextInt(_grid._num_blocks);
		_current_x = x;
		_tree_index = _grid._random.nextInt(_grid._num_trees + 1) - 1;	//some of them will be -1, the rest will be between 0 and 1
	}
	
	public int getIndex()
	{
		return _curr_index;
	}
	
	public int getX(int w, int _speed)
	{
		//if the current x is below the x threshold of this block, then wrap it around to the end of the screen.
		_current_x -= _speed;

		if (_current_x < -_width)
		{
			_current_x = (_grid.getLastBlockX() + _width) - _speed;	//might need much more logic here.
			_curr_index = _grid._random.nextInt(_grid._num_blocks);
			_tree_index = _grid._random.nextInt(_grid._num_trees + 1) - 1;	//when the block wraps around it will get anew tree.
		}
		return _current_x;
	}
	
	public int getTreeIndex()
	{
		return _tree_index;
	}
}
