package inspirational.designs.charpaper;

import inspirational.designs.charpaper.CharPaperService.CharPaperEngine;
import android.graphics.Canvas;
import android.graphics.Paint;

public class SkyRenderer
{
		private Paint _paint;
		char [] _start_col = {0, 106, 200};
		char [] _end_col = {230, 241, 207};

		public SkyRenderer(CharPaperEngine _engine)
		{
			_paint = new Paint();
		}

		public void draw(Canvas _c, int w, int h)
		{
			int numbands = (h / 10) + 1;	//render extra band for odd screen height cases (e.g. 752 / 10 = 75 remainder 2 visible pixels)
			double r_lerp = (_end_col[0] - _start_col[0]) / numbands;
			double g_lerp = (_end_col[1] - _start_col[1]) / numbands;
			double b_lerp = (_end_col[2] - _start_col[2]) / numbands;
			char r = _start_col[0];
			char g = _start_col[1];
			char b = _start_col[2];

			for (int i = 0; i < numbands; i++)
			{
				_paint.setARGB(255, r, g, b);
				_c.drawRect(0, i * 10, w, (i * 10) + 10, _paint);

				r += r_lerp;
				g += g_lerp;
				b += b_lerp;
			}
		}
}
