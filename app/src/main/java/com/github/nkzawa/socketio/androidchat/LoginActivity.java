package com.github.nkzawa.socketio.androidchat;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.nkzawa.socketio.androidchat.TTS.ConversationActivity;
import com.github.nkzawa.socketio.androidchat.TTS.GlobalVars;
import com.github.nkzawa.socketio.androidchat.TTS.LanguageModel;
import com.github.nkzawa.socketio.androidchat.TTS.QueryUtils;
import com.github.nkzawa.socketio.androidchat.TTS.TranslationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.github.nkzawa.socketio.androidchat.TTS.GlobalVars.BASE_REQ_URL;
import static com.github.nkzawa.socketio.androidchat.TTS.GlobalVars.DEFAULT_LANG_POS;
import static com.github.nkzawa.socketio.androidchat.TTS.GlobalVars.LANGUAGE_CODES;


/**
 * A login screen that offers login via username.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText mUsernameView;
    private Spinner spinner;
    private String mUsername;
    private Socket mSocket;

    private int langSelectPos = 0;
    volatile boolean activityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activityRunning = true;
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();

        // Set up the login form.
        mUsernameView = findViewById(R.id.username_input);
        mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        spinner = findViewById(R.id.lngSpinner);
        GlobalVars.initializeCodes();
        ArrayAdapter<LanguageModel> dataAdapter = new ArrayAdapter<LanguageModel>(this,
                android.R.layout.simple_spinner_item, GlobalVars.LANGUAGE_MODEL);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                langSelectPos = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mSocket.on("login", onLogin);

        Button bConversation = (Button) findViewById(R.id.start_new_conversation);
        bConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ConversationActivity.class);
                startActivity(intent);
            }
        });

        Button bTranslation = (Button) findViewById(R.id.start_new_translation);
        bTranslation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, TranslationActivity.class);
                startActivity(intent);
            }
        });

        //  GET LANGUAGES LIST
        //new GetLanguages().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityRunning = false;
        mSocket.off("login", onLogin);
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString().trim();

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mUsernameView.setError(getString(R.string.error_field_required));
            mUsernameView.requestFocus();
            return;
        }

        mUsername = username;

        JSONObject body = new JSONObject();
        try {
            body.put("username", username);
            body.put("lang", GlobalVars.LANGUAGE_MODEL.get(langSelectPos).code);
            // perform the user login attempt.
            mSocket.emit("add user", body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            int numUsers;
            try {
                numUsers = data.getInt("numUsers");
            } catch (JSONException e) {
                return;
            }

            Intent intent = new Intent();
            intent.putExtra("username", mUsername);
            intent.putExtra("lang", langSelectPos);
            intent.putExtra("numUsers", numUsers);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    private void setSpinnerError() {
        View selectedView = spinner.getSelectedView();
        if (selectedView instanceof TextView) {
            spinner.requestFocus();
            TextView selectedTextView = (TextView) selectedView;
            selectedTextView.setError("error"); // any name of the error will do
            selectedTextView.setTextColor(Color.RED); //text color in which you want your error message to be displayed
            selectedTextView.setText("Please select your language"); // actual error message
            spinner.performClick(); // to open the spinner list if error is found.

        }
    }

//    //  SUBCLASS TO GET LIST OF LANGUAGES ON BACKGROUND THREAD
//    private class GetLanguages extends AsyncTask<Void, Void, ArrayList<String>> {
//        @Override
//        protected ArrayList<String> doInBackground(Void... params) {
//            Uri baseUri = Uri.parse(BASE_REQ_URL);
//            Uri.Builder uriBuilder = baseUri.buildUpon();
//            uriBuilder.appendPath("getLangs")
//                    .appendQueryParameter("key", getString(R.string.API_KEY))
//                    .appendQueryParameter("ui", "en");
//            Log.e("String Url ---->", uriBuilder.toString());
//            return QueryUtils.fetchLanguages(uriBuilder.toString());
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<String> result) {
//            if (activityRunning) {
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_spinner_item, result);
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                spinner.setAdapter(adapter);
//                //  SET DEFAULT LANGUAGE SELECTIONS
//                spinner.setSelection(DEFAULT_LANG_POS);
//            }
//        }
//    }
}



