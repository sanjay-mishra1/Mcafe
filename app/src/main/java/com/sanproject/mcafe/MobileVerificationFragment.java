package com.sanproject.mcafe;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.sanproject.mcafe.MainActivity.MYPREFERENCES;

public class MobileVerificationFragment extends BottomSheetDialogFragment {
    private String verificationId;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private EditText editText;
    private String phonenumber;
    private Button resendBt;
    private Button signinBt;
    private PhoneAuthProvider.ForceResendingToken token;
    private String uid;
    private String email;
    private String pwd;
    View mobileView;
    View otpView;
    boolean isOtpVisible=false;
    private EditText mobileText;
    public MobileVerificationFragment(){}
    Dialog mobileViewDialog;
    public MobileVerificationFragment(Dialog mobileViewDialog){
        this.mobileViewDialog=mobileViewDialog;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify_mobile, container, false);
        uid=FirebaseAuth.getInstance().getUid();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resendBt = view.findViewById(R.id.resendSignIn);
        signinBt = view.findViewById(R.id.buttonSignIn);
        mAuth = FirebaseAuth.getInstance();
        otpView=view.findViewById(R.id.codeLayout);
        mobileView=view.findViewById(R.id.mobileView);
        progressBar = view.findViewById(R.id.progressbar);
        editText = view.findViewById(R.id.editTextCode);
        if (getArguments() != null) {
            if (getArguments().getString("phonenumber")==null)
            initializeMobileView();
            else{
                phonenumber="+91"+getArguments().getString("phonenumber");
                otpView.setVisibility(View.VISIBLE);
                mobileView.setVisibility(View.GONE);
                SharedPreferences sharedPreferences=getActivity().getSharedPreferences(MYPREFERENCES,Context.MODE_PRIVATE);
                email=sharedPreferences.getString("Email",null);
                pwd=sharedPreferences.getString("Password",null);
                sendVerificationCode( phonenumber);
                view.findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String code = editText.getText().toString().trim();
                        if (code.isEmpty() || code.length() < 6) {
                            editText.setError("Enter code...");
                            editText.requestFocus();
                            return;
                        }
                        progressBar.setVisibility(View.VISIBLE);
                        verifyCode(code);
                    }
                });
                resendBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resendCode();
                    }
                });
            }
        }
    }
    private void initializeMobileView() {
        mobileText=getView().findViewById(R.id.mobile_number);
        SharedPreferences sharedPreferences=getActivity().
                getSharedPreferences(MYPREFERENCES,Context.MODE_PRIVATE);
        Log.e("Phonenumber","->"+phonenumber);
        phonenumber= sharedPreferences.getString("MobileNumber",null);
        if (phonenumber!=null)
            mobileText.setText(phonenumber);
        phonenumber="+91"+phonenumber;
        getView().findViewById(R.id.buttonMobileConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mobileText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Enter mobile number", Toast.LENGTH_LONG).show();
                } else if (mobileText.getText().toString().trim().length() != 10) {
                    Toast.makeText(getContext(), "Invalid mobile number", Toast.LENGTH_LONG).show();
                } else {
                    phonenumber = mobileText.getText().toString().trim();//getIntent().getStringExtra("phonenumber");
                    sendToOTPVerification();
                }
            }
        });
    }

    private void sendToOTPVerification() {
        Bundle startIntent=new Bundle();
        Bundle intent = getArguments();
        startIntent.putString("Order",intent.getString("Order"));
        startIntent.putString("TodaysTime",  intent.getString("TodaysTime"));
        startIntent.putString("DeliveryTime", intent.getString("DeliveryTime"));
        startIntent.putString("Status",intent.getString("Status"));
        startIntent.putString("Total_Amount",intent.getString("Total_Amount"));
        startIntent.putString("Pay_Amount",intent.getString("Pay_Amount"));
        startIntent.putString("UId", (uid));
        startIntent.putString( "From",intent.getString("From"));
        startIntent.putString("Total_Food",intent.getString("Total_Food"));
        startIntent.putString("Address",intent.getString("Address"));
        startIntent.putString("Tax",intent.getString("Tax"));
        startIntent.putString("phonenumber",phonenumber);
        startIntent.putString("OtherPayment",intent.getString("OtherPayment"));
//            Log.e("Address","Payment "+Address);
        try {
            startIntent.putString(  "Image0",   intent.getString("Image0"));
            startIntent.putString( "Image1",  intent.getString("Image1" ));
            startIntent.putString("Image2",  intent.getString("Image2"));
            startIntent.putString( "Image3", intent.getString("Image3" ));
            MobileVerificationFragment fragment=new MobileVerificationFragment(getDialog());
            fragment.setArguments(startIntent);
            fragment.showNow(getActivity().getSupportFragmentManager(), "Verify OTP");
    }catch (Exception e){e.printStackTrace();}
    }

    private void verifyCode(String code) {
        Log.e("Verifycode","Called");
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithCredential(credential);
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            editText.setError("Invalid code...");
        }
    }
    private void signInWithCredential(PhoneAuthCredential credential) {
        progressBar.setVisibility(View.VISIBLE);
        signinBt.setText("Verifying");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                     try {
                         if (task.isSuccessful()) {
                             signinBt.setEnabled(false);

                             checkUser();
                         } else {
                             Toast.makeText(getActivity(), "Invalid code", Toast.LENGTH_LONG).show();
                             progressBar.setVisibility(View.GONE);
                         }
                     }catch (Exception e){}finally {
                                    automatic_auth(null,email,pwd);
                     }
                    }
                });
    }

    private void checkUser() {
        //success code here
        SharedPreferences shared = getActivity().getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        String email = shared.getString("Email", "");
        String password = shared.getString("Password", "");
       try {
           automatic_auth(shared,email,password);
       }catch (Exception e){automatic_auth(null,email,pwd);}
    }

    private void automatic_auth(final SharedPreferences shared, final String email, final String password){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Log.e("Login","Previous uid login successful");
          try {
              SharedPreferences.Editor editor = shared.edit();
              //editor.putString("Email", email);
              editor.putString("MobileNumber", phonenumber.replace("+91",""));
              editor.remove("verify_phone");
              editor.apply();
              SharedPreferences preferences=getActivity().getSharedPreferences("User_Credentials",Context.MODE_PRIVATE);
              SharedPreferences.Editor editor1=preferences.edit();
              editor1.putString("MobileNumber",phonenumber.replace("+91",""));
              editor1.remove("verify_phone");
              editor1.apply();
              updateCredentials();
          }catch (Exception e){}
                } else {
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateCredentials() {
        openPayment();
        FirebaseDatabase.getInstance().getReference("User Informations").child(uid)
                .child("Mobile_number").setValue(phonenumber.replace("+91",""));
        FirebaseDatabase.getInstance().getReference("User Informations").child(uid)
                .child("is_mobile_verified").setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("VerifyPhone","Updated db");
            }
        });
    }
    private void openPayment(){
        try {
            Intent startIntent=new Intent(getActivity(),payment.class);
            Bundle intent = getArguments();
            startIntent.putExtra("Order",intent.getString("Order"));
            startIntent.putExtra("TodaysTime",  intent.getString("TodaysTime"));
            startIntent.putExtra("DeliveryTime", intent.getString("DeliveryTime"));
            startIntent.putExtra("Status",intent.getString("Status"));
            startIntent.putExtra("Total_Amount",intent.getString("Total_Amount"));
            startIntent.putExtra("Pay_Amount",intent.getString("Pay_Amount"));
            startIntent.putExtra("UId", (uid));
            startIntent.putExtra( "From",intent.getString("From"));
            startIntent.putExtra("Total_Food",intent.getString("Total_Food"));
            startIntent.putExtra("Address",intent.getString("Address"));
            startIntent.putExtra("Tax",intent.getString("Tax"));
            startIntent.putExtra("OtherPayment",intent.getString("OtherPayment"));
//            Log.e("Address","Payment "+Address);
            try {
                startIntent.putExtra(  "Image0",   intent.getString("Image0"));
                startIntent.putExtra( "Image1",  intent.getString("Image1" ));
                startIntent.putExtra("Image2",  intent.getString("Image2"));
                startIntent.putExtra( "Image3", intent.getString("Image3" ));
                startActivity(startIntent);
                dismiss();
                mobileViewDialog.dismiss();
            }catch (Exception NullPointerException){NullPointerException.printStackTrace();}
        }catch (Exception e){e.printStackTrace();}
    }
    private void sendVerificationCode(String number) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NotNull String s, @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
        try {
            Log.e("OTP", "Code sent");
            token = forceResendingToken;
            verificationId = s;
            countDown();
            progressBar.setVisibility(View.GONE);
            signinBt.setEnabled(true);
            signinBt.setTextColor(getResources().getColor(R.color.white));
            signinBt.setText("Verify");
            resendBt.setVisibility(View.VISIBLE);
        }catch (Exception ignored){}
        }
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            progressBar.setVisibility(View.GONE);
            if (code != null) {
                Log.e("OTP","Code verfication =>"+code);
                editText.setText(code);
                if (!isDetached()|| !isVisible())
                    verifyCode(code);
            }else {
                //    showAlertDialog("Unable to send the code. Please check your mobile number.\nIf problem persists try interchanging the sim slots of your mobile.", false);
                    ///here error handler required
                signinBt.setEnabled(false);
                signinBt.setText("Verifying");
                checkUser();
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            progressBar.setVisibility(View.GONE);
            Log.e("Error","->"+e.getMessage());
            Toast.makeText(getActivity(), "Verification failed: Invalid code", Toast.LENGTH_LONG).show();
        }
        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            super.onCodeAutoRetrievalTimeOut(s);
            progressBar.setVisibility(View.GONE);
        }
    };

    private void resendCode(){
        try {
            editText.setText("");
            progressBar.setVisibility(View.VISIBLE);
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phonenumber,
                    60,
                    TimeUnit.SECONDS,
                    TaskExecutors.MAIN_THREAD,
                    mCallBack,
                    token
            );
            signinBt.setTextColor(getResources().getColor(R.color.disable_color));
            signinBt.setText(R.string.sending_code);
            signinBt.setEnabled(false);
            resendBt.setText("00:60");
            resendBt.setTextColor(getResources().getColor(R.color.disable_color));
            resendBt.setEnabled(false);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("ResendError","Mo="+phonenumber+" token"+token);
        }
    }
    void countDown(){
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resendBt.setText(String.format(Locale.UK,"00:%d", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
try {
    resendBt.setEnabled(true);
    resendBt.setTextColor(getResources().getColor(R.color.white));
    resendBt.setText(R.string.resend_code);
}catch (Exception ignored){}
            }
        }.start();
    }


}
