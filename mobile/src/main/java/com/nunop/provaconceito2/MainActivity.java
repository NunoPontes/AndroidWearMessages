package com.nunop.provaconceito2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String WEAR_MESSAGE_PATH = "/message";

    private GoogleApiClient mApiClient;
    private TextView tvNumber;
    private Button mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvNumber=(TextView) findViewById(R.id.tvNumber);
        init();
        initGoogleApiClient();
    }

    public int getRandomNumber(int min, int max)
    {
        //Returns random number between interval
        Random r = new Random();
        return r.nextInt(max - min) + min;
    }

    private void initGoogleApiClient() {
        //Initiate GoogleAPI
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .build();

        mApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    private void init() {
        mSendButton = (Button) findViewById( R.id.btnChangeNumber );

        mSendButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvNumber.setText(Integer.toString(getRandomNumber(1,100)));
                String text = tvNumber.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    //send text
                    sendMessage(WEAR_MESSAGE_PATH, text);
                }
            }
        });
    }

    private void sendMessage( final String path, final String text ) {
        //Create thread to send text
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                }
            }
        }).start();
    }
/*
    @Override
    public void onConnected(Bundle bundle) {
        Log.d("test", "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("test", "Failed to connect to Google API Client");
    }
*/
}
