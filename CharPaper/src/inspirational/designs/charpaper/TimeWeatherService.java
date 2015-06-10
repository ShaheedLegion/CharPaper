package inspirational.designs.charpaper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.content.Context;

public class TimeWeatherService extends Service {

	private class TimerReceiver extends BroadcastReceiver {

		/*
		 * Using weather underground api. usage:
		 * http://api.wunderground.com/api/
		 * Your_Key/conditions/q/CA/San_Francisco.json Must add support for
		 * getting user city from *somewhere* Possibly from settings, user
		 * identity, or location. My WU API Key: ccf4f6e8104c2e07
		 * 
		 * Uses the alarm manager to wake up the device to fetch weather
		 * updates.
		 */

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("CharPaper", "TimerReceiver - onReceive.");
			new Thread(new Runnable() {
				public void run() {
					getWeatherAndBroadcast();
				}
			}).start();
		}

		public void getWeatherAndBroadcast() {
			// Fetch the weather from wu server as json.
			// Broadcast to all interested parties.
			// For testing purposes, we set the country manually.
			String query = "http://api.wunderground.com/api/ccf4f6e8104c2e07/conditions/q/ZA/Cape_town.json";
			boolean debugMode = false;
			try {
				URL url = new URL(query);
				if (debugMode)
					Log.d("CharPaper", "Setting URL " + url);

				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();

				if (debugMode)
					Log.d("CharPaper", "Fetching weather data from " + url);

				InputStream is;
				if (connection.getResponseCode() >= 400) {
					is = connection.getErrorStream();
					Log.d("CharPaper",
							"Could not load info "
									+ connection.getResponseCode());
					return;
				} else {
					is = connection.getInputStream();
					Log.d("CharPaper",
							"God valid input " + connection.getResponseCode());
				}

				Log.d("CharPaper", "Opened input stream.");

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));

				if (debugMode)
					Log.d("CharPaper", "Got input stream from " + url);

				StringBuffer json = new StringBuffer(1024);
				String tmp = "";
				while ((tmp = reader.readLine()) != null)
					json.append(tmp).append("\n");
				reader.close();

				if (debugMode)
					Log.d("CharPaper", "Got weather data " + json.toString());

				JSONObject data = new JSONObject(json.toString());

				if (debugMode)
					Log.d("CharPaper", "Broadcasting weather data");
				// Now we broadcast the weather info.
				Intent i = new Intent(
						"inspirational.designs.charpaper.handle_weather");
				i.putExtra("weather", data.toString(4));
				sendBroadcast(i);
			} catch (Exception e) {
				Log.d("CharPaper",
						"Exception while getting the weather " + e.getMessage());
				Log.d("CharPaper", "Exception info " + e.toString());
			}
		}
	}

	private AlarmManager m_alarmMgr;
	private PendingIntent m_alarmIntent;
	private TimerReceiver m_receiver;

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d("CharPaper", "Binding the service.");
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("CharPaper", "Starting the service.");

		m_alarmMgr = (AlarmManager) getBaseContext().getSystemService(
				Context.ALARM_SERVICE);

		m_receiver = new TimerReceiver();
		registerReceiver(m_receiver, new IntentFilter(
				"inspirational.designs.charpaper.fetch_weather"));
		m_alarmIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				"inspirational.designs.charpaper.fetch_weather"), 0);

		// Set the alarm manager to trigger every fifteen minutes.
		m_alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				AlarmManager.INTERVAL_FIFTEEN_MINUTES,
				AlarmManager.INTERVAL_FIFTEEN_MINUTES, m_alarmIntent);

		// In case we don't immediately receive an alarm, we trigger the main
		// function of the weather fetcher service.
		new Thread(new Runnable() {
			public void run() {
				m_receiver.getWeatherAndBroadcast();
			}
		}).start();
	}

	@Override
	public void onDestroy() {
		Log.d("CharPaper", "Destroying the service.");
		super.onDestroy();
	}
}