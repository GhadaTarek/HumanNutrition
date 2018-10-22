package com.amg.humannutrition.Activity;

import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amg.humannutrition.R;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.Manifest.permission.READ_CONTACTS;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private TextInputLayout mEmailViewLayout;
    private EditText mPasswordView;
    private TextInputLayout mPasswordViewLayout;
    private ActionProcessButton mEmailSignInButton,mEmailSignUpButton ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Get_UserData()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_login);
        mEmailView = findViewById(R.id.email);
        populateAutoComplete();
        mEmailViewLayout = findViewById(R.id.emailLayout);
        mPasswordViewLayout = findViewById(R.id.passwordLayout);
        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mEmailSignInButton = findViewById(R.id.emailSignIn_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmailSignInButton.setMode(ActionProcessButton.Mode.ENDLESS);

                attemptLogin();

            }
        });
        mEmailSignUpButton = findViewById(R.id.emailSignUp_button);
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmailSignUpButton.setMode(ActionProcessButton.Mode.ENDLESS);
                mEmailSignUpButton.setProgress(0);
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                mEmailSignUpButton.setProgress(100);
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mEmailSignInButton.setMode(ActionProcessButton.Mode.PROGRESS);
        mEmailSignInButton.setProgress(0);
        mEmailSignUpButton.setMode(ActionProcessButton.Mode.PROGRESS);
        mEmailSignUpButton.setProgress(0);

    }
    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            mEmailSignInButton.setProgress(-1);
            return;
        }

        // Reset errors.
        mEmailViewLayout.setError(null);
        mPasswordViewLayout.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordViewLayout.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailViewLayout.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailViewLayout.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            mEmailSignInButton.setProgress(-1);
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            showProgress(true);
            mEmailSignInButton.setProgress(0);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {

        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 2;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
//        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;


        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            mEmailSignInButton.setProgress(0);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                mEmailSignInButton.setProgress(30);
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                final String refreshedToken = FirebaseInstanceId.getInstance().getToken();

                StringRequest loginRequest = new StringRequest(Request.Method.POST,
                        getString(R.string.Resource_Address) + getString(R.string.Login_Address),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response != null && !Objects.equals(response, "[]")) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String rEmail = jsonObject.getString("Email");
                                        if (Objects.equals(rEmail.toLowerCase().trim(), mEmail.toLowerCase().trim())) {
                                            Set_UserData(response, rEmail.toLowerCase().trim());
                                            if (Get_UserData()) {
                                                mEmailSignInButton.setProgress(100);
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                mPasswordViewLayout.setError(getString(R.string.error_incorrect_password));
                                                mPasswordView.requestFocus();
                                                mEmailSignInButton.setProgress(-1);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        mEmailSignInButton.setProgress(-1);
                                    }
                                }
                                else
                                {
                                    mPasswordViewLayout.setError(getString(R.string.error_incorrect_password));
                                    mPasswordView.requestFocus();
                                    mEmailSignInButton.setProgress(-1);
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                            mEmailSignInButton.setProgress(-1);

                        //Log.e(getString(R.string.TAG), "Error: " + error.getMessage());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("email", mEmail.toLowerCase().trim());
                        params.put("password", mPassword);
                        params.put("token", refreshedToken);
                        return params;
                    }

                };
                // Adding request to request queue
                queue.add(loginRequest);

            } catch (Exception e) {
                mEmailSignInButton.setProgress(-1);
//                Log.d(getString(R.string.TAG), " Exception = "+ String.valueOf(e));
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mAuthTask = null;
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            mEmailSignInButton.setProgress(-1);
        }


    }

    void Set_UserData(String UserData, String Email) {
       try {
           SharedPreferences.Editor sharedPref = getSharedPreferences("UserData", MODE_PRIVATE).edit();
           sharedPref.putString("Email", Email);
           sharedPref.putString("JsonUserData", UserData);
           sharedPref.apply();
           sharedPref.commit();
       }
       catch (Exception e){
//        Log.e(getString(R.string.TAG), "Error: "+e.toString());
        }}

    boolean Get_UserData() {
        SharedPreferences sharedPref = getSharedPreferences("UserData", MODE_PRIVATE);
        String restoredText = sharedPref.getString("JsonUserData", null);
        if (restoredText != null) {
//            String Name = sharedPref.getString("name", "No name defined");//"No name defined" is the default value.
//            String Email = sharedPref.getString("name", "No name defined");//"No name defined" is the default value.
            return true;
        }
        return false;
    }
}

