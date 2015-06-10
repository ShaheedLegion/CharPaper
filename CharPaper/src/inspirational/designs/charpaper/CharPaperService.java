package inspirational.designs.charpaper;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class CharPaperService extends WallpaperService {

	private class WeatherReceiver extends BroadcastReceiver {

		private CharPaperRenderer m_renderer = null;

		public WeatherReceiver(CharPaperRenderer mRenderer) {
			m_renderer = mRenderer;
		}

		/*
		 * This receiver handles the json string that is pushed from the
		 * background service which contains weather data. The string is split
		 * into components which we are interested in and the relevant data is
		 * passed to the renderer which modifies the UI based on these
		 * parameters.
		 */

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("CharPaper", "TimerReceiver - onReceive.");
			if (m_renderer == null) {
				Log.d("CharPaper",
						"Cannot pass weather data to renderer, it's null.");
				return;
			}

			// Get the interesting parts of the json string and pass it to the
			// renderer for display.
			String weatherData = intent.getExtras().getString("weather");
			if (weatherData == null) {
				Log.d("CharPaper", "Could not fetch weather data.");
				return;
			}

			String city = "";
			String temp = "";
			String weather = "";
			String iconString = "";

			try {
				JSONObject data = new JSONObject(weatherData);
				{
					JSONObject currentObservation = data
							.getJSONObject("current_observation");
					{
						JSONObject displayLocation = currentObservation
								.getJSONObject("display_location");
						city = displayLocation.getString("full");
					}

					temp = currentObservation.getString("temp_c");
					weather = currentObservation.getString("weather");
					iconString = currentObservation.getString("icon_url");
				}

				m_renderer.SetWeatherData(getBaseContext(), city, temp,
						weather, iconString);

			} catch (JSONException e) {
				Log.d("CharPaper",
						"Could not construct json object " + e.getMessage());
				Log.d("CharPaper", "From data " + weatherData);
			}
		}
	}

	@Override
	public Engine onCreateEngine() {
		return new CharPaperEngine();
	}

	public class CharPaperEngine extends Engine {
		private final Handler handler = new Handler();
		private final Runnable drawRunner = new Runnable() {
			public void run() {
				draw();
			}
		};

		private CharPaperRenderer mRenderer;
		private WeatherReceiver m_receiver;
		private int width;
		private int height;
		boolean visible = true;

		public CharPaperEngine() {
			mRenderer = new CharPaperRenderer(this);
			m_receiver = new WeatherReceiver(mRenderer);
			registerReceiver(m_receiver, new IntentFilter(
					"inspirational.designs.charpaper.handle_weather"));

			handler.post(drawRunner);
			startService(new Intent(getBaseContext(), TimeWeatherService.class));
		}

		public InputStream getRes(int id) {
			return getResources().openRawResource(id);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			this.visible = visible;
			if (visible)
				handler.post(drawRunner);
			else
				handler.removeCallbacks(drawRunner);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			this.visible = false;
			handler.removeCallbacks(drawRunner);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			this.width = width;
			this.height = height;
			super.onSurfaceChanged(holder, format, width, height);
		}

		public boolean isVisible() {
			return visible;
		}

		private void draw() {
			if (isVisible()) {
				SurfaceHolder holder = getSurfaceHolder();
				Canvas canvas = null;
				try {
					canvas = holder.lockCanvas();
					if (canvas != null)
						mRenderer.draw(canvas, width, height);
				} finally {
					if (canvas != null)
						holder.unlockCanvasAndPost(canvas);
				}
			}
			// Come back and check how this is working
			// this reschedules a paint operation at the given interval.
			handler.removeCallbacks(drawRunner);
			if (isVisible())
				handler.postDelayed(drawRunner, 50);
		}

		@Override
		public Bundle onCommand(String action, int x, int y, int z,
				Bundle extras, boolean resultRequested) {
			if (action.contains("android.wallpaper.tap"))
				mRenderer.handleTap(x, y, z);

			return null;
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN)
				mRenderer.handleDown(event.getX(), event.getY());
			if (event.getAction() == MotionEvent.ACTION_MOVE)
				mRenderer.handleMove(event.getX(), event.getY());
			if (event.getAction() == MotionEvent.ACTION_UP)
				mRenderer.handleUp(event.getX(), event.getY());
		}

	}

}
