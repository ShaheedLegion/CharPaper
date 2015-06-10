package inspirational.designs.charpaper;

import java.io.BufferedInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import inspirational.designs.charpaper.CharPaperService.CharPaperEngine;

/*
 This class deals with a character animation which simply walks across the surface of the terrain.
 EventTriggers have been added to allow for simple events to influence the behavior of the sprites on the screen.

 For now, triggers cannot be repeated, but this is more due to the nature of the animation, and has nothing to do with the
 way the triggers operate - in fact triggers can be executed as many times as required - as long as it makes sense in context
 of the animation or effect it governs.

 Use repeated triggers - program behaviors into the character. I would like to see her run and slow down more frequently.
 */

public class CharacterRenderer {
	Bitmap _character;
	EventTrigger _trigger;
	EventTrigger _slow_trigger;
	int _w;
	int _char_x;
	int _char_y;
	int _surface_level;
	int _anim_frame;
	int _num_anim_frames;
	boolean _flip_frame;
	int _char_width;
	Rect _frame_rect; // the current frame
	Rect _dest_rect; // destination on screen

	public CharacterRenderer(CharPaperEngine _engine, EventTrigger _trig) {
		_character = BitmapFactory.decodeStream(new BufferedInputStream(_engine
				.getRes(R.drawable.chiraku)));
		_w = 0;
		_trigger = _trig;
		_slow_trigger = new EventTrigger();
		_flip_frame = false;
		_frame_rect = new Rect();
		_dest_rect = new Rect();
	}

	private void update(int w, int h, int _surface) {
		if (w != _w) { // init here - make implicit guarantee that this will be
						// entered on the first frame.
			_w = w;
			_surface_level = _surface; // this does not get set much
			_anim_frame = -1; // make sure to start on first frame
			_num_anim_frames = 4; // depends heavily on the images used.
			_char_width = _character.getWidth() / _num_anim_frames;
			_char_x = -_char_width;// (w / 2); //kind of center the character on
									// screen
			_char_y = _surface_level - _character.getHeight();
		} // add extra check here, and add ability for surface level to change
			// even without width/height changing.

		if (!_slow_trigger.isTriggered())
			_anim_frame++; // while the slow trigger is not active, then render
							// by flipping frames quickly
		else { // when we hit our ideal spot on screen, then flip frames slowly
				// because she has caught up.
			_flip_frame = !_flip_frame;
			if (_flip_frame)
				_anim_frame++;
		}
		if (_anim_frame > (_num_anim_frames - 1))
			_anim_frame = 0;

		if (_trigger.isTriggered()) {
			// then make the character walk across the screen towards the center
			if (_char_x < (w / 2))
				_char_x++;
			else
				_slow_trigger._is_triggered = true; // trigger our slow frames
													// when she reaches the
													// center of the screen.
		}

		int _char_frame_start = _char_width * _anim_frame;
		_frame_rect.set(_char_frame_start, 0, _char_frame_start + _char_width,
				_character.getHeight());
		_dest_rect.set(_char_x, _char_y, _char_x + _char_width, _surface_level);
	}

	public void draw(Canvas _c, int w, int h, int _surface) {
		update(w, h, _surface);
		_c.drawBitmap(_character, _frame_rect, _dest_rect, null);
	}

	public void setTarget(int x, int y, int z) {

	}

	public int getX() {
		return _char_x;
	}
}
