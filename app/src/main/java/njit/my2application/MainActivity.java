package njit.my2application;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;


import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    protected static final String CLIENT_ID = "7af8a0889cc34a35b18838154e84c0b4";
    private static final String REDIRECT_URI = "spotifystreamer://callback";
    private static String accessToken;
    private static final int REQUEST_CODE = 1337;

    protected static String getAccessToken() {
        return accessToken;
    }

    private static void setAccessToken(String accessToken) {
        MainActivity.accessToken = accessToken;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // register prefernce change listener
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.registerOnSharedPreferenceChangeListener(this);

        // check premium or free user
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String userType = prefs.getString("user_type", "user_type");

        if (userType.equals("free")) {
            AuthenticationClient.logout(getApplicationContext());
        }
        else {
            AuthenticationRequest.Builder builder =
                    new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
            builder.setScopes(new String[]{"user-read-private", "streaming"});
            AuthenticationRequest request = builder.build();

            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {

                case TOKEN:
                    Toast.makeText(MainActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
                    setAccessToken(response.getAccessToken());

                    break;

                case ERROR:
                    Toast.makeText(MainActivity.this, "Could not log in, please restart app!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_artist, menu);
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // check premium or free user
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String userType = prefs.getString("user_type","user_type");
        if (userType.equals("free")) {
            AuthenticationClient.logout(getApplicationContext());
            //PlayerActivityFragment.premiumPlayer.pause();
            Toast.makeText(this, "Logged out!", Toast.LENGTH_LONG).show();
        } else {
            AuthenticationRequest.Builder builder =
                    new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
            builder.setScopes(new String[]{"user-read-private", "streaming"});
            AuthenticationRequest request = builder.build();
           // if (PlayerActivityFragment.freePlayer != null) {
            //    PlayerActivityFragment.freePlayer.reset();
            //}

            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        }
    }
    }


