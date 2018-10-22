package com.amg.humannutrition.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.amg.humannutrition.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.github.florent37.materialtextfield.MaterialTextField;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private EditText username, email, password, password2, gender, age, height, weight;
    private MaterialTextField genderLayout;
    private ActionProcessButton signUp_btn;
    private CheckBox termsCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUp_btn = findViewById(R.id.btn_signUp);
        username = findViewById(R.id.signup_username);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        password2 = findViewById(R.id.signup_password2);
        gender = findViewById(R.id.signup_Gender);
        age = findViewById(R.id.signup_age);
        height = findViewById(R.id.signup_height);
        weight = findViewById(R.id.signup_weight);
        genderLayout = findViewById(R.id.signup_Gender_layout);
        termsCheckBox = findViewById(R.id.chkBox1);
        genderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseGender();
            }
        });
        /*
        genderLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (genderLayout.hasFocus() || genderLayout.hasFocusable() || gender.hasFocus() || gender.hasFocusable()) {
                    chooseGender();
                }
            }
        });
        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseGender();
            }
        });
        gender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (genderLayout.hasFocus() || genderLayout.hasFocusable() || gender.hasFocus() || gender.hasFocusable()) {
                    chooseGender();
                }
            }
        });*/
        signUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp_btn.setMode(ActionProcessButton.Mode.ENDLESS);
                signUp_btn.setProgress(0);
                attemptSignUp();

            }
        });
    }
@Override
protected void onStart() {
    super.onStart();
    signUp_btn.setMode(ActionProcessButton.Mode.PROGRESS);
    signUp_btn.setProgress(0);

}

    private void attemptSignUp() {
        // Reset errors.
        username.setError(null);
        email.setError(null);
        password.setError(null);
        password2.setError(null);
        gender.setError(null);
        age.setError(null);
        height.setError(null);
        weight.setError(null);
        termsCheckBox.setError(null);

        // Store values at the time of the login attempt.
        String username_text = username.getText().toString();
        String email_text = email.getText().toString();
        String password_text = password.getText().toString();
        String gender_text = gender.getText().toString();
        String age_text = age.getText().toString();
        String height_text = height.getText().toString();
        String weight_text = weight.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password_text) && !isPasswordValid(password_text)) {
            password.setError(getString(R.string.error_invalid_password));
            focusView = password;
            cancel = true;
        } else if (password.getText().toString() == password2.getText().toString()) {
            password2.setError("The Password aren't the same");
            focusView = password2;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email_text)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!isEmailValid(email_text)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(username_text)) {
            username.setError(getString(R.string.error_field_required));
            focusView = username;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(gender_text)) {
            gender.setError(getString(R.string.error_field_required));
            focusView = gender;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(age_text)) {
            age.setError(getString(R.string.error_field_required));
            focusView = age;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(height_text)) {
            height.setError(getString(R.string.error_field_required));
            focusView = height;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(weight_text)) {
            weight.setError(getString(R.string.error_field_required));
            focusView = weight;
            cancel = true;
        }
        if (!termsCheckBox.isChecked()) {
            termsCheckBox.setError(getString(R.string.error_field_required));
            focusView = termsCheckBox;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            signUp_btn.setProgress(-1);
        } else {
            signUpReq();

        }

    }

    private boolean isEmailValid(String email) {

        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 2;
    }


    private void chooseGender() {
        final CharSequence[] GenderChoice = {getString(R.string.male), getString(R.string.female)};
        final AlertDialog.Builder alert = new AlertDialog.Builder(SignUpActivity.this);
        alert.setTitle(R.string.select_gender);
        alert.setSingleChoiceItems(GenderChoice, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                genderLayout.expand();
                gender.setText(GenderChoice[which]);
            }
        });
        alert.show();
    }

    public void signUpReq() {
        try {
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);
            final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            StringRequest signupRequest = new StringRequest(Request.Method.POST,
                    getString(R.string.Resource_Address) + getString(R.string.SignUp_Address),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(getString(R.string.TAG), "responce: " + response);
                            if (Objects.equals(response, "Email Exist")) {
                                email.setError(getString(R.string.error_deplicate_email));
                                email.requestFocus();
                                signUp_btn.setProgress(-1);
                                return;
                            } else if (response != null && !Objects.equals(response, "[]" )&& !Objects.equals(response, "Missed Data") && !Objects.equals(response, "Wrong Method"))
                            {
                                try {
                                    Set_UserData(response, email.getText().toString().toLowerCase().trim());
                                    if (Get_UserData()) {
                                        signUp_btn.setProgress(100);
                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } catch (Exception e) {
                                    signUp_btn.setProgress(-1);
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
                    signUp_btn.setProgress(-1);
                }
                //Log.e(getString(R.string.TAG), "Error: " + error.getMessage());
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("U_name", username.getText().toString().trim());
                    params.put("Email", email.getText().toString().toLowerCase().trim());
                    params.put("Password", password.getText().toString().trim());
                    params.put("Gender", Objects.equals(gender.getText().toString().toLowerCase(), "male") ?"1":"2");
                    params.put("Age", age.getText().toString().trim());
                    params.put("Height", height.getText().toString().trim());
                    params.put("Weight", weight.getText().toString().trim());
                    params.put("Token", refreshedToken);
                    return params;
                }

            };
            // Adding request to request queue
            queue.add(signupRequest);
        } catch (Exception e) {
            signUp_btn.setProgress(-1);
//            if (dialog.isShowing()) {
//                dialog.dismiss();
        }
//                Log.d(getString(R.string.TAG), " Exception = "+ String.valueOf(e));
    }

    void Set_UserData(String UserData, String Email) {
        try {
            SharedPreferences.Editor sharedPref = getSharedPreferences("UserData", MODE_PRIVATE).edit();
            sharedPref.putString("Email", Email);
            sharedPref.putString("JsonUserData", UserData);
            sharedPref.apply();
            sharedPref.commit();
        } catch (Exception e) {
            Log.e(getString(R.string.TAG), "Error: " + e.toString());
        }
    }

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