package inspirational.designs.charpaper;

import java.io.InputStream;

import inspirational.designs.charpaper.CharPaperService.CharPaperEngine;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class CharPaperRenderer {
	private SkyRenderer m_sky;
	private CloudsRenderer m_clouds;
	//private TerrainRenderer m_terrain;
	private GridTerrainRenderer m_terrain;
	private BirdRenderer m_pet;
	private CharacterRenderer m_char;
	private EventTrigger m_motion_trigger;

	// Weather-specific stuff.
	String m_city = "";
	String m_temp = "";
	String m_weather = "";
	Bitmap m_weatherIcon = null;
	Paint m_paint = null;

	public CharPaperRenderer(CharPaperEngine _service) {
		m_paint = new Paint();
		m_motion_trigger = new EventTrigger();
		m_sky = new SkyRenderer(_service);
		m_clouds = new CloudsRenderer(_service);
		m_terrain = new GridTerrainRenderer(_service, m_motion_trigger);
		m_pet = new BirdRenderer(_service);
		m_char = new CharacterRenderer(_service, m_motion_trigger);
	}

	public void draw(Canvas _c, int w, int h) {
		m_sky.draw(_c, w, h);
		m_clouds.draw(_c, w, h);
		m_terrain.draw(_c, w, h);
		m_pet.draw(_c, w, h);
		m_char.draw(_c, w, h, m_terrain.getTerrainTop(m_char.getX())); // render
																		// the
																		// character
		// on the surface of the
		// terrain.

		// Deal with showing the different weather stuff later, for now we just
		// draw it.
		{
			m_paint.setTextSize(20f);
			m_paint.setARGB(255, 255, 255, 255);
			m_paint.setTextAlign(Align.LEFT);
			float leftEdge = 20f;
			if (m_weatherIcon != null) {
				// Draw the downloaded bitmap.
				leftEdge = m_weatherIcon.getWidth() + 10f;
				_c.drawBitmap(m_weatherIcon, 10f, 60f, m_paint);
			}
			if (m_city != null) {
				// Draw the city name.
				_c.drawText(m_city, leftEdge, 80f, m_paint);
			}
			if (m_weather != null) {
				// Draw the temperature string.
				_c.drawText(m_weather, leftEdge, 110f, m_paint);
			}
			if (m_temp != null) {
				// Draw the temp value.
				_c.drawText(m_temp, leftEdge, 140f, m_paint);
			}
		}
	}

	public void handleTap(int x, int y, int z) {
		m_pet.setTarget(x, y, z);
		m_char.setTarget(x, y, z);
	}

	public void handleDown(float x, float y) {
		m_clouds.handleDown(x, y);
	}

	public void handleMove(float x, float y) {
		m_clouds.handleMove(x, y);
	}

	public void handleUp(float x, float y) {
		m_clouds.handleUp(x, y);
	}

	public void SetWeatherData(Context context, final String city,
			final String temp, final String weather, final String iconString) {
		// Ok, we can now add weather data overlays to the wallpaper.
		Handler mainHandler = new Handler(context.getMainLooper());

		mainHandler.post(new Runnable() {

			public void run() {
				Log.d("CharPaper", "Setting weather values :");
				Log.d("CharPaper", "City: " + city);
				SetCity(city);
				Log.d("CharPaper", "Temp: " + temp);
				SetTemp(temp);
				Log.d("CharPaper", "Weather: " + weather);
				SetWeather(weather);

			}
		});
		new DownloadImageTask(this).execute(iconString);
	}

	public void SetCity(String city) {
		m_city = city;
	}

	public void SetTemp(String temp) {
		m_temp = temp;
	}

	public void SetWeather(String weather) {
		m_weather = weather;
	}

	public void SetWeatherIcon(Bitmap icon) {
		m_weatherIcon = icon;
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		CharPaperRenderer m_renderer;

		public DownloadImageTask(CharPaperRenderer renderer) {
			m_renderer = renderer;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			if (urldisplay == null) {
				Log.d("CharPaper", "Got null url string in icon loader.");
				return null;
			}
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.d("CharPaper", "Could not download image.");
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			Log.d("CharPaper", "Fetched Icon w[" + mIcon11.getWidth() + "] h["
					+ mIcon11.getHeight() + "]");
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			m_renderer.SetWeatherIcon(result);
		}
	}

}
