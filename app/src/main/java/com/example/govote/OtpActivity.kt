package com.example.govote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.govote.databinding.ActivityOtpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding
    lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        auth = FirebaseAuth.getInstance()
        val login = binding.btnSendOTP
        val verify = binding.btnOTP
        val currentUser = auth.currentUser
        if(currentUser  != null){
            Log.i("asd","Ptani ye kya ha ")
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential?) {
                Toast.makeText(this@OtpActivity , "Chal gya bhai ! $p0 is the user  ",Toast.LENGTH_SHORT).show()
            }

            override fun onVerificationFailed(p0: FirebaseException?) {
                Toast.makeText(this@OtpActivity , "Na chala bhai ",Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(p0: String?, p1: PhoneAuthProvider.ForceResendingToken?) {
                Log.i("TAG","onCodeSent:$p0")
                if (p0 != null && p1!=null) {
                    storedVerificationId = p0
                    resendToken =p1
                }

            }

        }

        login.setOnClickListener {
            getLogIn()
        }

        verify.setOnClickListener {
            val code = binding.tinOTP.text.toString().trim()
            if(TextUtils.isEmpty(code)){
                Toast.makeText(this@OtpActivity , "Can not eave this field empty ",Toast.LENGTH_SHORT).show()
            }else {
                getUserVerification(storedVerificationId, code)
            }
        }

    }

    private fun getLogIn(){
        val mobileNumber = binding.tinPhoneNumber
        var number = mobileNumber.text.toString().trim()

        if(!number.isEmpty()){
            number = "+91"+number
            sendVerificationCode(number)
        }else{
            Toast.makeText(this@OtpActivity , "Try phone Number again!",Toast.LENGTH_SHORT).show()
        }

    }

    private fun sendVerificationCode(number : String){
        val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(number)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun getUserVerification(verificationId : String? , code: String){
        val cred = PhoneAuthProvider.getCredential(verificationId,code)
        signIN(cred)
    }

    private fun signIN(cred: PhoneAuthCredential) {
        auth.signInWithCredential(cred)
                .addOnSuccessListener {
                val phone = auth.currentUser.phoneNumber
                    Toast.makeText(this@OtpActivity , "Success, Logged in as $phone",Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this@OtpActivity , "Try OTP again!",Toast.LENGTH_SHORT).show()
                }
    }


}