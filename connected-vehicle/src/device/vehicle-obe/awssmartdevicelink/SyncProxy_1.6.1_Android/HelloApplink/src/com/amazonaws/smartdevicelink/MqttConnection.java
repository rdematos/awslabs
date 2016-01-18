package com.amazonaws.smartdevicelink;

import android.content.Context;
import android.util.Log;

import com.amazonaws.com.amazonaws.model.ClientId;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by sanjoyg on 9/25/15.
 */
public class MqttConnection {

    private static final String TAG = "MqttConnection";
    private String clientId = ClientId.getUID();
    private String server = "data.iot.us-east-1.amazonaws.com";
    private int port = 8883;
    private boolean cleanSession = true;
    private boolean useSsl = true;
    private String keystoreFile = "icebreaker.bks";
    private String passphrase = "icestore";
    private int timeout = 60;
    private int keepAlive = 200;

    private boolean connected;
    private MqttAndroidClient client;

    public boolean isCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public boolean isUseSsl() {
        return useSsl;
    }

    public void setUseSsl(boolean useSsl) {
        this.useSsl = useSsl;
    }

    public String getKeystoreFile() {
        return keystoreFile;
    }

    public void setKeystoreFile(String keystoreFile) {
        this.keystoreFile = keystoreFile;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isConnected() {
        return connected;
    }

    public void connectToMqttServer(Context context) throws MqttException {
        MqttConnectOptions conOpt = new MqttConnectOptions();

        /*
         * Mutual Auth connections could do something like this
         *
         *
         * SSLContext context = SSLContext.getDefault();
         * context.init({new CustomX509KeyManager()},null,null); //where CustomX509KeyManager proxies calls to keychain api
         * SSLSocketFactory factory = context.getSSLSocketFactory();
         *
         * MqttConnectOptions options = new MqttConnectOptions();
         * options.setSocketFactory(factory);
         *
         * client.connect(options);
         *
         */



        String uri = null;
        if (useSsl) {
            uri = "ssl://";
        } else {
            uri = "tcp://";
        }
        uri = uri + server + ":" + port;
        Log.e(TAG, "MQTT Server url: " + uri);

        client = new MqttAndroidClient(context, uri, clientId);
        if (useSsl) try {
            if (keystoreFile != null && !keystoreFile.equalsIgnoreCase("")) {


                try {
                    conOpt.setSocketFactory(SslUtil.getSocketFactory(
                            context.getResources().openRawResource(R.raw.ca_crt),
                            context.getResources().openRawResource(R.raw.aws_iot_crt),
                            context.getResources().openRawResource(R.raw.aws_iot_key)));
                } catch (Exception e2) {
                    e2.printStackTrace();

                }
            }
        } catch (Exception e) {
            Log.e(TAG, "MqttException Occured: SSL Key file not found", e);
        }


        conOpt.setCleanSession(cleanSession);
        conOpt.setConnectionTimeout(timeout);
        conOpt.setKeepAliveInterval(keepAlive);

        // create a client handle
        String clientHandle = uri + clientId;
        client.connect(conOpt, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Log.e(TAG, "MQTT Connection succeeded: " + iMqttToken.isComplete());
                connected = true;
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Log.e(TAG, "MQTT Connection failed: " + throwable.getMessage(), throwable);
                throwable.printStackTrace();
                connected = false;
            }
        });
    }

    public void publishJsonPayload(String topicPublish, String jsonPayload, MainActivity mainActivity) throws MqttException {
        client.publish(topicPublish, jsonPayload.getBytes(), 0, false, mainActivity, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Log.e(TAG, "Message Successfully Published: " + iMqttToken.isComplete());
                MainActivity mainActivity = (MainActivity) iMqttToken.getUserContext();
                mainActivity.successfullyPublishedJsonPayload();
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Log.e(TAG, "Message Publish Failed: " + throwable.getMessage());
                MainActivity mainActivity = (MainActivity) iMqttToken.getUserContext();
                mainActivity.failedPublishJsonPayload();
            }
        });
    }

    public void subscribeToTopic(String topicSubscribe, MainActivity mainActivity) throws MqttException {
        client.subscribe(topicSubscribe, 0, mainActivity, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Log.e(TAG, "Subscribe succeeded to topic: " + iMqttToken.getTopics()[0]);
                MainActivity mainActivity = (MainActivity) iMqttToken.getUserContext();
                mainActivity.subscribeTopicSucceeded();
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Log.e(TAG, "Subscribe failed to topic: " + iMqttToken.getTopics()[0], throwable);
                MainActivity mainActivity = (MainActivity) iMqttToken.getUserContext();
                mainActivity.subscribeTopicFailed();
            }
        });
    }

    public void ubsubscribeTopic(String topicUnsubscribe) throws MqttException {
        if (topicUnsubscribe != null) {
            client.unsubscribe(topicUnsubscribe);
        }
    }

    public MqttAndroidClient getClient() {
        return client;
    }
}
