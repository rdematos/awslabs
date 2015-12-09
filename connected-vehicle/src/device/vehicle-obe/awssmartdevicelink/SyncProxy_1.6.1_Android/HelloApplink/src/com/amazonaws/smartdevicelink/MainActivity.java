/**
 */

package com.amazonaws.smartdevicelink;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

       Car car = new Car();
        try {
            JsonReader reader = new JsonReader(new StringReader(receivedPayload));
            reader.beginObject();
            String state = reader.nextName();
            if (state.equals("state")) {
                reader.beginObject();
                String reported = reader.nextName();
                if (reported.equals("reported")) {
                    car.readJson(reader);
                }
                reader.endObject();
            }
            reader.endObject();
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
}
