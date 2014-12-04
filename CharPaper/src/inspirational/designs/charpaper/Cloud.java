package inspirational.designs.charpaper;

import inspirational.designs.charpaper.CharPaperService.CharPaperEngine;

import java.io.BufferedInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Cloud
{
	private Bitmap _image;
	int _x;
	int _y;
	int _vw;
	int _vh;
	int _speed;	//for now the clouds will only move in one direction
	private boolean m_down = false;
	private float m_down_x = 0.0f;
	private float m_down_y = 0.0f;
	
	public Cloud(CharPaperEngine _engine, int resid)
	{
		_image = BitmapFactory.decodeStream(new BufferedInputStream(_engine.getRes(resid)));
		_vw = 0;
	}
	
	private void init()
	{
		_x = (int) ((Math.random() * (_vw / 2)) + _vw);
		_y = (int) ((Math.random() * (_vh / 2)));
		_speed = -(int) ((Math.random() * 10) + 2);
	}
	
	public void renderCloud(Canvas _c, int w, int h)
	{
		if (w != _vw)
		{	//get first case - nothing initialized yet
			_vw = w;
			_vh = h;
			init();
		}

		if (!m_down)
			_x += _speed;

		if (_x < -_image.getWidth())
			init();

		_c.drawBitmap(_image, _x, _y, null);
	}
	
	public void handleDown(float x, float y)
	{
		m_down = true;
		m_down_x = x;
		m_down_y = y;
	}

	public void handleMove(float x, float y)
	{
		if (!m_down)
			return;
		
		float xdist = m_down_x - x;
		float ydist = m_down_y - y;
		
		_x += xdist;
		_y += ydist;
		
		m_down_x = x;
		m_down_y = y;
	}
	
	public void handleUp(float x, float y)
	{
		m_down = false;
	}
}
