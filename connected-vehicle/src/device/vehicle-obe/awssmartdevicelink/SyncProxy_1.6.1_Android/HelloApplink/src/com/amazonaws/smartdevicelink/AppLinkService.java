/**
 */

package com.amazonaws.smartdevicelink;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.util.JsonWriter;
import android.util.Log;

import com.amazonaws.com.amazonaws.model.Car;
import com.amazonaws.com.amazonaws.model.GPS;
import com.github.davidmoten.geo.GeoHash;
import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.exception.SdlExceptionCause;
import com.smartdevicelink.proxy.SdlProxyALM;
import com.smartdevicelink.proxy.TTSChunkFactory;
import com.smartdevicelink.proxy.callbacks.OnServiceEnded;
import com.smartdevicelink.proxy.callbacks.OnServiceNACKed;
import com.smartdevicelink.proxy.interfaces.IProxyListenerALM;
import com.smartdevicelink.proxy.rpc.AddCommandResponse;
import com.smartdevicelink.proxy.rpc.AddSubMenuResponse;
import com.smartdevicelink.proxy.rpc.Alert;
import com.smartdevicelink.proxy.rpc.AlertManeuverResponse;
import com.smartdevicelink.proxy.rpc.AlertResponse;
import com.smartdevicelink.proxy.rpc.ChangeRegistrationResponse;
import com.smartdevicelink.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteCommandResponse;
import com.smartdevicelink.proxy.rpc.DeleteFileResponse;
import com.smartdevicelink.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteSubMenuResponse;
import com.smartdevicelink.proxy.rpc.DiagnosticMessageResponse;
import com.smartdevicelink.proxy.rpc.DialNumberResponse;
import com.smartdevicelink.proxy.rpc.EndAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.GenericResponse;
import com.smartdevicelink.proxy.rpc.GetDTCsResponse;
import com.smartdevicelink.proxy.rpc.GetVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.ListFilesResponse;
import com.smartdevicelink.proxy.rpc.OnAudioPassThru;
import com.smartdevicelink.proxy.rpc.OnButtonEvent;
import com.smartdevicelink.proxy.rpc.OnButtonPress;
import com.smartdevicelink.proxy.rpc.OnCommand;
import com.smartdevicelink.proxy.rpc.OnDriverDistraction;
import com.smartdevicelink.proxy.rpc.OnHMIStatus;
import com.smartdevicelink.proxy.rpc.OnHashChange;
import com.smartdevicelink.proxy.rpc.OnKeyboardInput;
import com.smartdevicelink.proxy.rpc.OnLanguageChange;
import com.smartdevicelink.proxy.rpc.OnLockScreenStatus;
import com.smartdevicelink.proxy.rpc.OnPermissionsChange;
import com.smartdevicelink.proxy.rpc.OnStreamRPC;
import com.smartdevicelink.proxy.rpc.OnSystemRequest;
import com.smartdevicelink.proxy.rpc.OnTBTClientState;
import com.smartdevicelink.proxy.rpc.OnTouchEvent;
import com.smartdevicelink.proxy.rpc.OnVehicleData;
import com.smartdevicelink.proxy.rpc.PerformAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.PerformInteractionResponse;
import com.smartdevicelink.proxy.rpc.PutFileResponse;
import com.smartdevicelink.proxy.rpc.ReadDIDResponse;
import com.smartdevicelink.proxy.rpc.ResetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.ScrollableMessageResponse;
import com.smartdevicelink.proxy.rpc.SendLocationResponse;
import com.smartdevicelink.proxy.rpc.SetAppIconResponse;
import com.smartdevicelink.proxy.rpc.SetDisplayLayoutResponse;
import com.smartdevicelink.proxy.rpc.SetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.SetMediaClockTimerResponse;
import com.smartdevicelink.proxy.rpc.ShowConstantTbtResponse;
import com.smartdevicelink.proxy.rpc.ShowResponse;
import com.smartdevicelink.proxy.rpc.SliderResponse;
import com.smartdevicelink.proxy.rpc.SoftButton;
import com.smartdevicelink.proxy.rpc.SpeakResponse;
import com.smartdevicelink.proxy.rpc.StreamRPCResponse;
import com.smartdevicelink.proxy.rpc.SubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.SubscribeVehicleData;
import com.smartdevicelink.proxy.rpc.SubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.SystemRequestResponse;
import com.smartdevicelink.proxy.rpc.TTSChunk;
import com.smartdevicelink.proxy.rpc.UnsubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.UpdateTurnListResponse;
import com.smartdevicelink.proxy.rpc.enums.ButtonName;
import com.smartdevicelink.proxy.rpc.enums.DriverDistractionState;
import com.smartdevicelink.proxy.rpc.enums.HMILevel;
import com.smartdevicelink.proxy.rpc.enums.Language;
import com.smartdevicelink.proxy.rpc.enums.SdlDisconnectedReason;
import com.smartdevicelink.proxy.rpc.enums.SoftButtonType;
import com.smartdevicelink.proxy.rpc.enums.SystemAction;
import com.smartdevicelink.proxy.rpc.enums.TextAlignment;
import com.smartdevicelink.transport.BaseTransportConfig;
import com.smartdevicelink.transport.TCPTransportConfig;
import com.smartdevicelink.util.DebugTool;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.StringWriter;
import java.util.Vector;

public class AppLinkService extends Service implements IProxyListenerALM {

	String TAG = "AppLinkService";
	//variable used to increment correlation ID for every request sent to SYNC
	public int autoIncCorrId = 0;
	//variable to contain the current state of the service
	private static AppLinkService instance = null;
	//variable to contain the current state of the main UI ACtivity
	private MainActivity currentUIActivity;
	//variable to access the BluetoothAdapter
	private BluetoothAdapter mBtAdapter;
	//variable to create and call functions of the SyncProxy
	private SdlProxyALM proxy = null;
	//variable that keeps track of whether SYNC is sending driver distractions
	//(older versions of SYNC will not send this notification)
	private boolean driverdistrationNotif = false;
	//variable to contain the current state of the lockscreen
	private boolean lockscreenUP = false;

	//private String emulatorIP = "192.168.6.230"; //lead
	private String emulatorIP = "192.168.123.130"; //follow

	public void setEmulatorIP(String emulatorIP) {
		this.emulatorIP = emulatorIP;
		Log.d(TAG,"emulatorIP set: "+ emulatorIP);
		disposeSyncProxy();
		startProxy();
	}


	private HelloFordApplication helloFordApplication;


	public static AppLinkService getInstance() {
		return instance;
	}
	
	public MainActivity getCurrentActivity() {
		return currentUIActivity;
	}
	
	public SdlProxyALM getProxy() {
		return proxy;
	}

	public void setCurrentActivity(MainActivity currentActivity) {
		this.currentUIActivity = currentActivity;
	}

	public void onCreate() {
		super.onCreate();
		instance = this;
		helloFordApplication = (HelloFordApplication) getApplication();
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) {
  /*      if (intent != null) {
        	mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    		if (mBtAdapter != null){
    			if (mBtAdapter.isEnabled()){
*/    				startProxy();
	/*}
    		}
		}
      */  if (MainActivity.getInstance() != null) {
        	setCurrentActivity(MainActivity.getInstance());
        }
			
        return START_STICKY;
	}
	
	public void startProxy() {
		if (proxy == null) {
			try {
				BaseTransportConfig transport = new TCPTransportConfig(12345, emulatorIP, true);
				proxy = new SdlProxyALM(this,"AWS",false,Language.EN_US, Language.EN_US,"1234567",transport);
				//proxy = new SdlProxyALM(this, "AWS", false,"3387301225");
			} catch (SdlException e) {
				e.printStackTrace();
				//error creating proxy, returned proxy = null
				if (proxy == null){
					stopSelf();
				}
			}
		}
	}
	
	public void onDestroy() {
		disposeSyncProxy();
		clearlockscreen();
		instance = null;
		super.onDestroy();
	}
	
	public void disposeSyncProxy() {
		if (proxy != null) {
			try {
				proxy.dispose();
			} catch (SdlException e) {
				e.printStackTrace();
			}
			proxy = null;
			clearlockscreen();
		}
	}

	public void onOnStreamRPC(OnStreamRPC onStreamRPC) {

	}

	@Override
	public void onStreamRPCResponse(StreamRPCResponse streamRPCResponse) {

	}

	public void onProxyClosed(String info, Exception e) {
		clearlockscreen();
		
		if((((SdlException) e).getSdlExceptionCause() != SdlExceptionCause.SDL_PROXY_CYCLED))
		{
			if (((SdlException) e).getSdlExceptionCause() != SdlExceptionCause.BLUETOOTH_DISABLED)
			{
				Log.v(TAG, "reset proxy in onproxy closed");
				reset();
			}
		}
	}

   public void reset(){
	   if (proxy != null) {
		   try {
			   proxy.resetProxy();
		   } catch (SdlException e1) {
			   e1.printStackTrace();
			   //something goes wrong, & the proxy returns as null, stop the service.
			   //do not want a running service with a null proxy
			   if (proxy == null){
				   stopSelf();
			   }
		   }
	   }else {
		   startProxy();
	   }
   }

	boolean firstcheck = true;

	public void onOnHMIStatus(OnHMIStatus notification) {

		if(notification.getHmiLevel() != HMILevel.HMI_NONE)
			if (firstcheck){

                String notificationtopic = "notification";
                helloFordApplication.getMainActivity().subscribeTopic(notificationtopic);


                SubscribeVehicleData msg = new SubscribeVehicleData();
				msg.setCorrelationID(autoIncCorrId++);
				//Location functional group
				msg.setSpeed(true);
				msg.setGps(true);
				//VehicleInfo functional group
				msg.setFuelLevel(false);
				msg.setInstantFuelConsumption(false);
				msg.setExternalTemperature(false);
				msg.setTirePressure(false);
				msg.setOdometer(false);

				//DrivingCharacteristics functional group
				msg.setBeltStatus(false);
				msg.setDriverBraking(true);
				msg.setPrndl(false);
				msg.setRpm(false);

				msg.setAirbagStatus(true);
                msg.setBodyInformation(true);
                msg.setEmergencyEvent(true);

				try {
					proxy.sendRPCRequest(msg);
				} catch (SdlException e) {
					e.printStackTrace();
				}




				firstcheck = false;
			}


		switch(notification.getSystemContext()) {
				 case SYSCTXT_MAIN:
					   break;
				 case SYSCTXT_VRSESSION:
					   break;
				 case SYSCTXT_MENU:
					   break;
				 default:
					   return;
		  }
		  
		  switch(notification.getAudioStreamingState()) {
				 case AUDIBLE:
					//play audio if applicable
					   break;
				 case NOT_AUDIBLE:
					//pause/stop/mute audio if applicable
					   break;
				 default:
					   return;
		  }
		  
		  switch(notification.getHmiLevel()) {
				 case HMI_FULL:
					 if (driverdistrationNotif == false) {showLockScreen();}
					 if(notification.getFirstRun()) {
						   //setup app on SYNC
						   //send welcome message if applicable
						 	try {
								proxy.show("Connected to", "AWS", TextAlignment.CENTERED, autoIncCorrId++);
							} catch (SdlException e) {
								DebugTool.logError("Failed to send Show", e);
							}				
						    //send addcommands
						    //subscribe to buttons
						 	subButtons();
						 	if (MainActivity.getInstance() != null) {
					        	setCurrentActivity(MainActivity.getInstance());
					        }
						}
					 else{
						 try {
								proxy.show("Connected to", "AWS", TextAlignment.CENTERED, autoIncCorrId++);
							} catch (SdlException e) {
								DebugTool.logError("Failed to send Show", e);
							}
					 }
					   break;
				 case HMI_LIMITED:
					 if (driverdistrationNotif == false) {showLockScreen();}
					   break;
				 case HMI_BACKGROUND:
					 if (driverdistrationNotif == false) {showLockScreen();}
					   break;
				 case HMI_NONE:
					   Log.i("hello", "HMI_NONE");
					   driverdistrationNotif = false;
					   clearlockscreen();
					   break;
				 default:
					   return;
		  }
   }

	@Override
	public void onProxyClosed(String s, Exception e, SdlDisconnectedReason sdlDisconnectedReason) {

	}

	@Override
	public void onServiceEnded(OnServiceEnded onServiceEnded) {

	}

	public void showLockScreen() {
	//only throw up lockscreen if main activity is currently on top
	//else, wait until onResume() to throw lockscreen so it doesn't 
	//pop-up while a user is using another app on the phone
	if(currentUIActivity != null) {
		if(currentUIActivity.isActivityonTop() == true){
			if(LockScreenActivity.getInstance() == null) {
				Intent i = new Intent(this, LockScreenActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
				startActivity(i);
			}
		}
	}
	lockscreenUP = true;		
}

private void clearlockscreen() {
	if(LockScreenActivity.getInstance() != null) {  
		LockScreenActivity.getInstance().exit();
	}
	lockscreenUP = false;
}

public boolean getLockScreenStatus() {return lockscreenUP;}

public void subButtons() {
	try {
        proxy.subscribeButton(ButtonName.OK, autoIncCorrId++);
        proxy.subscribeButton(ButtonName.SEEKLEFT, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.SEEKRIGHT, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.TUNEUP, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.TUNEDOWN, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_1, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_2, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_3, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_4, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_5, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_6, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_7, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_8, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_9, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_0, autoIncCorrId++);
	} catch (SdlException e) {}
}

public void onOnDriverDistraction(OnDriverDistraction notification) {
	driverdistrationNotif = true;
	//Log.i(TAG, "dd: " + notification.getStringState());
	if (notification.getState() == DriverDistractionState.DD_OFF)
	{
		Log.i(TAG,"clear lock, DD_OFF");
		clearlockscreen();
	} else {
		Log.i(TAG,"show lockscreen, DD_ON");
		showLockScreen();
	}
}

public void onError(String info, Exception e) {
	// TODO Auto-generated method stub
}

public void onGenericResponse(GenericResponse response) {
	// TODO Auto-generated method stub
}
	public void onServiceNACKed(OnServiceNACKed onServiceNACKed) {

	}

public void onOnCommand(OnCommand notification) {
	// TODO Auto-generated method stub
}

public void onAddCommandResponse(AddCommandResponse response) {
	// TODO Auto-generated method stub
}

public void onAddSubMenuResponse(AddSubMenuResponse response) {
	// TODO Auto-generated method stub
}

public void onCreateInteractionChoiceSetResponse(
		CreateInteractionChoiceSetResponse response) {
	// TODO Auto-generated method stub
}

public void onAlertResponse(AlertResponse response) {
	// TODO Auto-generated method stub
}

public void onDeleteCommandResponse(DeleteCommandResponse response) {
	// TODO Auto-generated method stub
}

public void onDeleteInteractionChoiceSetResponse(
		DeleteInteractionChoiceSetResponse response) {
	// TODO Auto-generated method stub
}

public void onDeleteSubMenuResponse(DeleteSubMenuResponse response) {
	// TODO Auto-generated method stub
}


public void onPerformInteractionResponse(PerformInteractionResponse response) {
	// TODO Auto-generated method stub
}

public void onResetGlobalPropertiesResponse(
		ResetGlobalPropertiesResponse response) {
	// TODO Auto-generated method stub
}

public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse response) {
}

public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse response) {
	// TODO Auto-generated method stub
}

public void onShowResponse(ShowResponse response) {
	// TODO Auto-generated method stub
}

public void onSpeakResponse(SpeakResponse response) {
	// TODO Auto-generated method stub
}

public void onOnButtonEvent(OnButtonEvent notification) {
	// TODO Auto-generated method stub
}

public void onOnButtonPress(OnButtonPress notification) {
	// TODO Auto-generated method stub
}

public void onSubscribeButtonResponse(SubscribeButtonResponse response) {
	// TODO Auto-generated method stub
}

public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse response) {
	// TODO Auto-generated method stub	
}

public void onOnPermissionsChange(OnPermissionsChange notification) {
	// TODO Auto-generated method stub	
}


public void onOnTBTClientState(OnTBTClientState notification) {
	// TODO Auto-generated method stub
}

@Override
public IBinder onBind(Intent intent) {
	// TODO Auto-generated method stub
	return null;
}
	@Override
	public void onShowConstantTbtResponse(ShowConstantTbtResponse showConstantTbtResponse) {

	}

	@Override
	public void onAlertManeuverResponse(AlertManeuverResponse alertManeuverResponse) {

	}

	@Override
	public void onUpdateTurnListResponse(UpdateTurnListResponse updateTurnListResponse) {

	}

	@Override
	public void onServiceDataACK() {

	}

	@Override
	public void onDiagnosticMessageResponse(DiagnosticMessageResponse diagnosticMessageResponse) {

	}

	@Override
	public void onReadDIDResponse(ReadDIDResponse readDIDResponse) {

	}

	@Override
	public void onGetDTCsResponse(GetDTCsResponse getDTCsResponse) {

	}

	@Override
	public void onOnSystemRequest(OnSystemRequest onSystemRequest) {

	}
	@Override
	public void onSendLocationResponse(SendLocationResponse sendLocationResponse) {

	}

	@Override
	public void onOnHashChange(OnHashChange onHashChange) {

	}

	@Override
	public void onSliderResponse(SliderResponse sliderResponse) {

	}

	@Override
	public void onOnLockScreenNotification(OnLockScreenStatus onLockScreenStatus) {

	}

	@Override
	public void onDialNumberResponse(DialNumberResponse dialNumberResponse) {

	}

	@Override
	public void onOnLanguageChange(OnLanguageChange onLanguageChange) {

	}
	@Override
	public void onSystemRequestResponse(SystemRequestResponse systemRequestResponse) {

	}

	@Override
	public void onOnKeyboardInput(OnKeyboardInput onKeyboardInput) {

	}

	@Override
	public void onOnTouchEvent(OnTouchEvent onTouchEvent) {

	}

	@Override
	public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse subscribeVehicleDataResponse) {

	}

	@Override
	public void onUnsubscribeVehicleDataResponse(UnsubscribeVehicleDataResponse unsubscribeVehicleDataResponse) {

	}

	@Override
	public void onGetVehicleDataResponse(GetVehicleDataResponse getVehicleDataResponse) {

	}

	@Override
	public void onOnVehicleData(OnVehicleData onVehicleData) {
		if (onVehicleData.getSpeed() != null) {
			Log.i("getSpeed", "S getSpeed: " + onVehicleData.getSpeed());
		}
		if (onVehicleData.getGps() != null) {
			Log.i("getGps", "S getGps: Lat: " + onVehicleData.getGps().getLatitudeDegrees().toString() + " Lon: " +
					onVehicleData.getGps().getLongitudeDegrees().toString() + " Heading: " + onVehicleData.getGps().getCompassDirection().toString());
		}
		if (onVehicleData.getDriverBraking() != null) {
			Log.i("getDriverBraking", "B getDriverBraking: " + onVehicleData.getDriverBraking());
		}

		if (onVehicleData.getEmergencyEvent() != null) {
			Log.i("getEmergencyEvent", "B getEmergencyEvent: " + onVehicleData.getEmergencyEvent().getEmergencyEventType().toString());
		}

		if (onVehicleData.getPrndl() != null) {
			Log.i("getPrndl", "S getPrndl: " + onVehicleData.getPrndl().name());
		}

		Car car = helloFordApplication.getCar();
		GPS gps = car.getGps();

		final Double newLat = onVehicleData.getGps().getLatitudeDegrees();
		final Double newLong = onVehicleData.getGps().getLongitudeDegrees();

		if (car.getCurrentGeohash() == null || !GeoHash.hashContains(car.getCurrentGeohash(), newLat, newLong)) {
			String newHash = GeoHash.encodeHash(newLat, newLong, 6);
			String newTopic = geohashToTopic(newHash);

			try {
				if (car.getCurrentTopicSubscribe() != null) {
					helloFordApplication.getMqttConnection().ubsubscribeTopic(car.getCurrentTopicSubscribe());
				}
				helloFordApplication.getMainActivity().subscribeTopic(newTopic);
				car.setCurrentTopicSubscribe(newTopic);
				car.setCurrentGeohash(newHash);
			}
			catch (MqttException e) {
				e.printStackTrace();
			}
		}

		car.setPrndl(onVehicleData.getPrndl().name());
		gps.setSpeed(onVehicleData.getSpeed());
		gps.setLatitudeDegrees(newLat);
		gps.setLongitudeDegrees(newLong);

		StringWriter sw = new StringWriter();
		JsonWriter writer = new JsonWriter(sw);
		try {
			writer.beginObject();
			writer.name("state");
			writer.beginObject();
			writer.name("reported");
			car.writeJson(writer);
			writer.endObject();
			writer.endObject();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		String publishPayload = sw.toString();
		Log.e(TAG, "Car JSON: " + publishPayload);

		HelloFordApplication helloFordApplication = (HelloFordApplication) getApplication();
		helloFordApplication.getMainActivity().publishCarJson(helloFordApplication.getTopicPublish(), publishPayload);
	}

	private String geohashToTopic(String geohash) {
		String topic = "notification/";
		for (int i = 0; i < geohash.length(); i++) {
			topic += geohash.charAt(i) + "/";
		}
		topic = topic.substring(0, topic.length() - 1)+"/#";
		return topic;
	}

	@Override
	public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse performAudioPassThruResponse) {

	}

	@Override
	public void onEndAudioPassThruResponse(EndAudioPassThruResponse endAudioPassThruResponse) {

	}

	@Override
	public void onOnAudioPassThru(OnAudioPassThru onAudioPassThru) {

	}

	@Override
	public void onPutFileResponse(PutFileResponse putFileResponse) {

	}

	@Override
	public void onDeleteFileResponse(DeleteFileResponse deleteFileResponse) {

	}

	@Override
	public void onListFilesResponse(ListFilesResponse listFilesResponse) {

	}

	@Override
	public void onSetAppIconResponse(SetAppIconResponse setAppIconResponse) {

	}

	@Override
	public void onScrollableMessageResponse(ScrollableMessageResponse scrollableMessageResponse) {

	}

	@Override
	public void onChangeRegistrationResponse(ChangeRegistrationResponse changeRegistrationResponse) {

	}

	@Override
	public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse setDisplayLayoutResponse) {

	}

	public void sendAlert(String receivedPayload) {
		int corrId = autoIncCorrId++;
		String sTextToSpeak = "Hazard Detected";

		Vector<SoftButton> currentSoftButtons;
		currentSoftButtons = new Vector<SoftButton>();

		SoftButton sb1 = new SoftButton();
		sb1.setSoftButtonID(200);
		sb1.setText("Correct"); //also possible to show built-in icons instead of text
		sb1.setType(SoftButtonType.SBT_TEXT);
		sb1.setIsHighlighted(false);
		sb1.setSystemAction(SystemAction.STEAL_FOCUS);

		SoftButton sb2 = new SoftButton();
		sb2.setSoftButtonID(201);
		sb2.setText("Incorrect");
		sb2.setType(SoftButtonType.SBT_TEXT);
		sb2.setIsHighlighted(false);
		sb2.setSystemAction(SystemAction.DEFAULT_ACTION);

		currentSoftButtons.add(sb1);
		currentSoftButtons.add(sb2);
		//Build Request and send to proxy object:
		Alert msg = new Alert();
		msg.setCorrelationID(corrId);

		msg.setAlertText1("Hazard Detected");
		msg.setDuration(5000); //Time in milliseconds
		msg.setPlayTone(true);

		Vector<TTSChunk> ttsChunks = TTSChunkFactory.createSimpleTTSChunks(sTextToSpeak);
		msg.setTtsChunks(ttsChunks);
		msg.setSoftButtons(currentSoftButtons);

		try {
			proxy.sendRPCRequest(msg);
		} catch (SdlException e) {
			Log.i(TAG, "sync exception" + e.getMessage() + e.getSdlExceptionCause());
			e.printStackTrace();
		}
	}
}
