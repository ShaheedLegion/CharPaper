package inspirational.designs.charpaper;

import inspirational.designs.charpaper.CharPaperService.CharPaperEngine;
import android.graphics.Canvas;

public class CloudsRenderer
{
	private Cloud [] _clouds;
	private final int _num_clouds = 8;

	public CloudsRenderer(CharPaperEngine _engine)
	{
		_clouds = new Cloud[_num_clouds];	//when I have time, separate the clouds form the cloud images
		_clouds[0] = new Cloud(_engine, R.drawable.cloud1);
		_clouds[1] = new Cloud(_engine, R.drawable.cloud2);
		_clouds[2] = new Cloud(_engine, R.drawable.cloud3);
		_clouds[3] = new Cloud(_engine, R.drawable.cloud4);
		_clouds[4] = new Cloud(_engine, R.drawable.cloudsm1);
		_clouds[5] = new Cloud(_engine, R.drawable.cloudsm2);
		_clouds[6] = new Cloud(_engine, R.drawable.cloudsm3);
		_clouds[7] = new Cloud(_engine, R.drawable.cloudsm4);
	}
	
	public void draw(Canvas _c, int w, int h)
	{
		for (Cloud _cloud : _clouds)
		{
			_cloud.renderCloud(_c, w, h);
		}
	}
	
	public void render_half(Canvas _c, int w, int h, int whichHalf)
	{
		switch (whichHalf)//will only ever be 1 or 2
		{
		case 1:
			for (int i = 0; i < _num_clouds / 2; ++i)
				_clouds[i].renderCloud(_c, w, h);
			break;
		case 2:
			for (int i = (_num_clouds / 2); i < _num_clouds; ++i)
				_clouds[i].renderCloud(_c, w, h);
			break;
		}
	}
	
	public void handleDown(float x, float y)
	{
		for (Cloud _cloud : _clouds) 
		{
			_cloud.handleDown(x, y);
		}
	}
	public void handleMove(float x, float y)
	{
		for (Cloud _cloud : _clouds) 
		{
			_cloud.handleMove(x, y);
		}
	}
	public void handleUp(float x, float y)
	{
		for (Cloud _cloud : _clouds) 
		{
			_cloud.handleUp(x, y);
		}
	}
}
