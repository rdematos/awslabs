/**
 */

package com.amazonaws.smartdevicelink;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.amazonaws.com.amazonaws.model.Car;
import com.smartdevicelink.proxy.SdlProxyALM;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Set;

public class MainActivity extends Activity implements MqttCallback {

    private static final String TAG = "MainActivity";

	private static MainActivity instance = null;

    private HelloFordApplication helloFordApplication;

    private TextView publishPayloadTextView;
    private EditText publishTopicEditText;
    private Button publishButton;

    private TextView subscribePayloadTextView;
    private EditText subscribeTopicEditText;
    private Button subscribeButton;

    private boolean activityOnTop;
	
	public static MainActivity getInstance() {
		return instance;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        publishPayloadTextView = (TextView) findViewById(R.id.publishPayloadTextView);
        publishPayloadTextView.setMovementMethod(new ScrollingMovementMethod());
        publishTopicEditText = (EditText) findViewById(R.id.publishTopicEditText);
        publishButton = (Button) findViewById(R.id.publishButton);

        subscribePayloadTextView = (TextView) findViewById(R.id.subscribePayloadTextView);
        subscribePayloadTextView.setMovementMethod(new ScrollingMovementMethod());
        subscribeTopicEditText = (EditText) findViewById(R.id.subscribeTopicEditText);
        subscribeButton = (Button) findViewById(R.id.subscribeButton);

        String publishPayload = readTextAsset("publish.json");
        publishPayloadTextView.setText(publishPayload);

        instance = this;
        helloFordApplication = (HelloFordApplication) getApplication();
        helloFordApplication.setMainActivity(this);
        try {
            helloFordApplication.getMqttConnection().connectToMqttServer(helloFordApplication);
        }
        catch (MqttException e) {
            e.printStackTrace();
        }
        startSyncProxyService();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	public void newMqttConnectionSettings(View view) {
		Intent intent = new Intent(this, MqttSettingsActivity.class);
		startActivityForResult(intent, 1);
	}

    private String readTextAsset(String assetFileName) {
        LineNumberReader reader = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            reader = new LineNumberReader(new InputStreamReader(getAssets().open(assetFileName)));
            String line = null;
            while ((line = reader.readLine()) != null) {
                pw.println(line);
            }
            return sw.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void publishButtonClicked(View view) {
        helloFordApplication.getMqttConnection().getClient().setCallback(this);
        if (helloFordApplication.getMqttConnection().isConnected()) {
            String publishTopic = publishTopicEditText.getText().toString();
            if (publishTopic == null || publishTopic.trim().length() == 0) {
                Log.e(TAG, "No topic to publish on");
                return;
            }
            String payload = publishPayloadTextView.getText().toString();
            if (payload == null || payload.trim().length() == 0) {
                Log.e(TAG, "No payload to publish");
                return;
            }
            try {
                helloFordApplication.getMqttConnection().publishJsonPayload(publishTopic, payload, this);
            }
            catch (MqttException e) {
                e.printStackTrace();
            }
        }
        else {
            Log.e(TAG, "MQTT is not connected");
        }
    }

    public void subscribeButtonClicked(View view) {
        helloFordApplication.getMqttConnection().getClient().setCallback(this);
        if (helloFordApplication.getMqttConnection().isConnected()) {
            String subscribeTopic = subscribeTopicEditText.getText().toString();
            if (subscribeTopic == null || subscribeTopic.trim().length() == 0) {
                Log.e(TAG, "No topic to subscribe to");
                return;
            }
            try {
                helloFordApplication.getMqttConnection().subscribeToTopic(subscribeTopic, this);
            }
            catch (MqttException e) {
                e.printStackTrace();
            }
        }
        else {
            Log.e(TAG, "MQTT is not connected");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
            case R.id.connection_settings:
                createConnectionDialog();
                return true;
            case R.id.exit:
	        	super.finish();
	            return true;
	        case R.id.reset:
	        	endSyncProxyInstance();
	        	startSyncProxyService();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

    public void startSyncProxyService() {
        Intent startIntent = new Intent(this, AppLinkService.class);
        startService(startIntent);
        boolean isSYNCpaired = false;
		// Get the local Bluetooth adapter
		BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		//BT Adapter exists, is enabled, and there are paired devices with the name SYNC
		//Ideally start service and start proxy if already connected to sync
		//but, there is no way to tell if a device is currently connected (pre OS 4.0)

		if (mBtAdapter != null)
		{
			if ((mBtAdapter.isEnabled() && mBtAdapter.getBondedDevices().isEmpty() != true))
			{
				// Get a set of currently paired devices
				Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

				// Check if there is a paired device with the name "SYNC"
				if (pairedDevices.size() > 0) {
					for (BluetoothDevice device : pairedDevices) {
						if (device.getName().toString().equals("SYNC")) {
							isSYNCpaired = true;
							break;
						}
					}
				} else {
					Log.i("TAG", "A No Paired devices with the name sync");
				}

				if (isSYNCpaired == true) {
					if (AppLinkService.getInstance() == null) {
						Intent BTstartIntent = new Intent(this, AppLinkService.class);
						startService(startIntent);
					} else {
						//if the service is already running and proxy is up, set this as current UI activity
						AppLinkService.getInstance().setCurrentActivity(this);
						Log.i("TAG", " proxyAlive == true success");
					}
				}
			}
		}
	}


	//upon onDestroy(), dispose current proxy and create a new one to enable auto-start
	//call resetProxy() to do so
	public void endSyncProxyInstance() {
		AppLinkService serviceInstance = AppLinkService.getInstance();
		if (serviceInstance != null){
			SdlProxyALM proxyInstance = serviceInstance.getProxy();
			//if proxy exists, reset it
			if(proxyInstance != null){
				serviceInstance.reset();
				//if proxy == null create proxy
			} else {
				serviceInstance.startProxy();
			}
		}
	}

	protected void onDestroy() {
		Log.v(TAG, "onDestroy main");
		endSyncProxyInstance();
		instance = null;
		AppLinkService serviceInstance = AppLinkService.getInstance();
		if (serviceInstance != null){
			serviceInstance.setCurrentActivity(null);
		}
		super.onDestroy();
	}

	protected void onResume() {
		activityOnTop = true;
		//check if lockscreen should be up
		AppLinkService serviceInstance = AppLinkService.getInstance();
		if (serviceInstance != null){
			if (serviceInstance.getLockScreenStatus() == true) {
				if(LockScreenActivity.getInstance() == null) {
					Intent i = new Intent(this, LockScreenActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
					startActivity(i);
				}
			}
		}
		super.onResume();
	}

	@Override
    protected void onPause() {
		activityOnTop = false;
		super.onPause();
	}

	public boolean isActivityonTop(){
		return activityOnTop;
	}

	public void successfullyPublishedJsonPayload() {
		Log.e(TAG, "Message Publish Successful");
	}

	public void failedPublishJsonPayload() {
		Log.e(TAG, "Message Publish Failed");
	}

    public void subscribeTopicFailed() {
    }

    public void subscribeTopicSucceeded() {
    }

    @Override
    public void connectionLost(Throwable throwable) {
        Log.e(TAG, "MQTT Connection lost to Server");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        final String receivedPayload = new String(mqttMessage.getPayload());
        Log.e(TAG, "Message received on topic: " + topic + ",  " + receivedPayload);

        Car car = new Car(null); //Null because we don't want to set a vin yet
        Log.i(TAG, "Generic Car object was created");
        try {

            // {"state":{"reported":{"vin":"02:00:00:00:00:00","prndl":"REVERSE","gps":{"speed":0,"latitudeDegrees":42.4725335678,"longitudeDegrees":-83.1235775395,"heading":173},"externalTemperature":23}}}

            JsonReader reader = new JsonReader(new StringReader(receivedPayload));
            reader.beginObject();
            String state = reader.nextName();
            if (state.equals("state")) {
                reader.beginObject();
                String reported = reader.nextName();
                if (reported.equals("reported")) {
                    car.readJson(reader);
                }else{
                    Log.e(TAG, "JSON object didn't start with reported");
                }
                reader.endObject();
            }else{
                Log.e(TAG, "JSON object didn't start with state");
            }
            reader.endObject();
            Log.i(TAG, "Event was created with new Car object vin = " + car.getVin());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                subscribePayloadTextView.setText(receivedPayload);
            }
        });

       if (!car.getVin().equals(helloFordApplication.getCar().getVin())) {
           AppLinkService linkService = AppLinkService.getInstance();
           linkService.sendAlert(receivedPayload);
           Log.d(TAG, "inside vin check: " + car.getVin() + "  " + helloFordApplication.getCar().getVin());

       }else{
           Log.i(TAG, "We received a message with the same VIN, discarding.");
       }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        Log.e(TAG, "Message Delivery Complete");
    }

    public void publishCarJson(final String topicPublish, final String publishPayload) {
        if (!helloFordApplication.getMqttConnection().isConnected()) {
            Log.e(TAG, "MQTT Server is not connected");
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                publishTopicEditText.setText(topicPublish);
                publishPayloadTextView.setText(publishPayload);
                publishButton.performClick();
            }
        });
    }

    public void subscribeTopic(final String topicSubscribe) {
        if (!helloFordApplication.getMqttConnection().isConnected()) {
            Log.e(TAG, "MQTT Server is not connected");
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                subscribeTopicEditText.setText(topicSubscribe);
                subscribeButton.performClick();
            }
        });
    }


    /*
	 * Dialog settings
	 */


    private void createConnectionDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.connection_dialog);
        dialog.setTitle("Setting Connection Type");
        final Spinner spin = (Spinner)dialog.findViewById(R.id.connectionSpinner);
        final EditText edit = (EditText)dialog.findViewById(R.id.cnt_ip_et);
        final EditText editPort = (EditText)dialog.findViewById(R.id.cnt_port_et);
        final SharedPreferences settings = getSharedPreferences(AppLinkService.SDL_PREFS, MODE_PRIVATE);

        String type = settings.getString(AppLinkService.SDL_PREF_KEY_CONNECTION_TYPE, AppLinkService.CNT_TYPE_BLUETOOTH);
        ArrayAdapter myAdap = (ArrayAdapter) spin.getAdapter(); //cast to an ArrayAdapter

        int spinnerPosition = myAdap.getPosition(type);

        //set the default according to value
        spin.setSelection(spinnerPosition);

        //spin.setSelection();
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                String mod = (String) parent.getItemAtPosition(position);
                if(AppLinkService.CNT_TYPE_WIFI.equalsIgnoreCase(mod)){
                    edit.setFocusable(true);
                    edit.setFocusableInTouchMode(true);
                    edit.setClickable(true);

                    editPort.setFocusable(true);
                    editPort.setFocusableInTouchMode(true);
                    editPort.setClickable(true);
                    String ip = settings.getString(AppLinkService.SDL_PREF_KEY_IP_ADDRESS, "");
                    int port = settings.getInt(AppLinkService.SDL_PREF_KEY_PORT, 12345);
                    edit.setText(ip);
                    editPort.setText(port +"");
                }else{
                    edit.setFocusable(false);
                    edit.setFocusableInTouchMode(false);
                    edit.setClickable(false);

                    editPort.setFocusable(false);
                    editPort.setFocusableInTouchMode(false);
                    editPort.setClickable(false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                edit.setFocusable(false);
                edit.setFocusableInTouchMode(false);
                edit.setClickable(false);

                editPort.setFocusable(false);
                editPort.setFocusableInTouchMode(false);
                editPort.setClickable(false);

            }

        });
        Button okBtn = (Button)dialog.findViewById(R.id.cnt_okBtn);
        okBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = settings.edit();
                String cntType = (String)spin.getSelectedItem();
                if(cntType!=null){
                    editor.putString(AppLinkService.SDL_PREF_KEY_CONNECTION_TYPE, cntType);
                    if(cntType.equalsIgnoreCase("wifi")){
                        Editable ip = edit.getText();
                        if(ip!=null){
                            editor.putString(AppLinkService.SDL_PREF_KEY_IP_ADDRESS, ip.toString());
                        }
                        Editable port = editPort.getText();
                        if(port!=null){
                            editor.putInt(AppLinkService.SDL_PREF_KEY_PORT, Integer.parseInt(port.toString()));
                        }
                    }

                }
                editor.commit();
                //Maybe add prefrence listner to shut down service or something

                restartService();

                dialog.dismiss();

            }

        });
        Button cancelBtn = (Button)dialog.findViewById(R.id.cnt_cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                dialog.cancel();

            }

        });

        dialog.show();
    }

    private void restartService(){
        Intent serviceIntent = new Intent(this, AppLinkService.class);
        serviceIntent.putExtra(AppLinkService.FORCE_RESET, true);
        startService(serviceIntent);


    }
}
