package com.example.locationtracker;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.EditText;
import android.widget.TextView;

class TextViewLocationUpdate implements ILocationCallback {
	private Activity parentActivity;
	
	TextViewLocationUpdate(Activity parentActivity) {
		this.parentActivity = parentActivity;
	}
	
	public void call(Location location) {
		TextView tvlat = (TextView) this.parentActivity.findViewById(R.id.text_latitude);
		TextView tvlng = (TextView) this.parentActivity.findViewById(R.id.text_longitude);
		
		tvlat.setText(String.valueOf(location.getLatitude()));
		tvlng.setText(String.valueOf(location.getLongitude()));
	}
}


class UpdateReceiver extends BroadcastReceiver {
	private GPSTracker gpsTracker;
	
	UpdateReceiver(GPSTracker gpsTracker) {
		this.gpsTracker = gpsTracker;
	}
	
@Override
public void onReceive(Context context, Intent intent) {


     ConnectivityManager connectivityManager = (ConnectivityManager) 
                                  context.getSystemService(Context.CONNECTIVITY_SERVICE );
     NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
     boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();   
     if (isConnected) {
         this.gpsTracker.connected();
     };
   }
}


public class MainActivity extends ActionBarActivity {
	public final static String EXTRA_MESSAGE = "com.example.locationtracker.MESSAGE";
	
	private GPSTracker gpsTracker;
	private TextViewLocationUpdate locationUpdater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		this.locationUpdater = new TextViewLocationUpdate(this);
		
		this.gpsTracker = new GPSTracker(this);
		this.gpsTracker.onLocationUpdate(this.locationUpdater);
		
		Context ctx = this.getApplicationContext();
		ctx.registerReceiver(new UpdateReceiver(this.gpsTracker),
					         new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
}
