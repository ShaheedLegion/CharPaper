package inspirational.designs.charpaper;

import inspirational.designs.charpaper.CharPaperService.CharPaperEngine;

import java.io.BufferedInputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

/*
 * This class is not unique, and could have been a slight specialization of a Sprite class, which internally knows the boundaries of it's sprite sheet.
 * */
public class BirdRenderer {
	private int _x;
	private int _y;
	private int _speed;
	private int _target_x;
	private int _target_y;
	private int _w;
	private int _h;
	private Rect _src_rect;
	private Rect _dest_rect;

	private Bitmap _image;
	private int _num_frames;
	private int _current_frame;

	public BirdRenderer(CharPaperEngine _engine) {
		_image = BitmapFactory.decodeStream(new BufferedInputStream(_engine
				.getRes(R.drawable.bird_red)));

		_x = -(int) ((Math.random() * _image.getWidth() * 2) + _image
				.getWidth());
		_y = -_image.getHeight();
		_w = 0;
		_h = 0;
		_speed = 1;
		_src_rect = new Rect();
		_dest_rect = new Rect();
		_num_frames = 4; // not the best way to do this, but is easier than
							// specifying metadata with the images.
		_current_frame = 0;
	}

	/*
	 * Incorporate special logic into the x-coordinate handling of the birds,
	 * makes them a bit more lifelike.
	 */
	private void initX() {
		if (_x > _w) {
			_x = -_image.getWidth(); // move the bird to the beginning of the
										// scene
		}

		// make birds all move uniformly across screen (migrate behavior)
		_target_x = (int) (Math.random() * _w) + (_w / 2);
		_speed = 2;
	}

	private void initY() {
		_target_y = (int) (Math.random() * _h);
		_speed = 2;
	}

	private void update(int w, int h) {
		if (_w != w) {
			_w = w;
			_h = h;
			initX();
			initY();
		}

		if (_x < (_target_x - _speed))
			_x += _speed;
		else if (_x > (_target_x + _speed))
			_x -= _speed;
		else
			initX(); // we should never get to this case, but leave it here for
						// safety sake

		if (_y < (_target_y - _speed))
			_y += _speed;
		else if (_y > (_target_y + _speed))
			_y -= _speed;
		else
			initY();

		// source location of the bird bitmap (which image in sprite sheet)
		int _src_w = (_image.getWidth() / _num_frames);
		int _src_loc = _src_w * _current_frame;
		_src_rect.set(_src_loc, 0, _src_loc + _src_w, _image.getHeight());

		_dest_rect.set(_x, _y, _x + _image.getWidth() / _num_frames, _y
				+ _image.getHeight()); // the bird location on screen

		_current_frame++; // this will make the bird flap wildly.
		if (_current_frame > (_num_frames - 1))
			_current_frame = 0;
	}

	public void draw(Canvas _c, int w, int h) {
		update(w, h);
		_c.drawBitmap(_image, _src_rect, _dest_rect, null);
	}

	public void setTarget(int x, int y, int z) {
		_target_x = x;
		_target_y = y;
		_speed = 10;
	}
}
