package com.example.severapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.severapp.Common.Common;
import com.example.severapp.Model.SeverUserModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    private static  int APP_REQUEST_CODE = 7171 ;
    private FirebaseAuth firebaseAuth ;
    private FirebaseAuth.AuthStateListener listener ;
    private AlertDialog dialog ;
    private DatabaseReference severRef ;
    private List<AuthUI.IdpConfig> providers ;


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener( listener );
    }

    @Override
    protected void onStop() {
        if(listener != null)
            firebaseAuth.removeAuthStateListener( listener );
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        init();
    }

    private void init() {
        providers = Arrays.asList( new AuthUI.IdpConfig.PhoneBuilder().build() );

        severRef = FirebaseDatabase.getInstance().getReference( Common.SEVER_REF);
        firebaseAuth = FirebaseAuth.getInstance();
        dialog = new SpotsDialog.Builder().setContext( this ).setCancelable( false ).build();
        listener = firebaseAuthLocal ->{

           FirebaseUser user = firebaseAuthLocal.getCurrentUser();

           if(user != null)
           {
               checkUserFromFirebase(user);

           }
           else {
               PhoneLogin();

           }

        };

    }

    private void checkUserFromFirebase(FirebaseUser user) {
        dialog.show();
        severRef.child(user.getUid())
                .addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists())
                        {
                            SeverUserModel userModel = snapshot.getValue(SeverUserModel.class);
                            if(userModel.isActive())
                            {
                                goToHomeActivity( userModel );
                            }
                            else {
                                dialog.dismiss();
                                Toast.makeText( MainActivity.this, "You must be from Admin to access this app", Toast.LENGTH_SHORT ).show();
                            }

                        }
                        else
                        {
                            dialog.dismiss();
                            showRegisterDiaLog(user);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText( MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT ).show();

                    }
                } );
    }

    private void showRegisterDiaLog( FirebaseUser user) {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( this );
        builder.setTitle( "Register" );
        builder.setMessage( "Please fill information" );

        View itemView = LayoutInflater.from( this )
                .inflate( R.layout.layout_register ,null);

        EditText edt_name  = (EditText)itemView.findViewById( R.id.edt_name );
        EditText edt_phone = (EditText)itemView.findViewById( R.id.edt_phone );

        edt_phone.setText( user.getPhoneNumber() );
        builder.setNegativeButton( "CANCEL", (dialogInterface, i) -> dialogInterface.dismiss() )
                .setPositiveButton( "REGISTER", (dialogInterface, i) -> {
                    if(TextUtils.isEmpty( edt_name.getText().toString() ))
                    {
                        Toast.makeText( MainActivity.this, "Please enter your name", Toast.LENGTH_SHORT ).show();
                        return;
                    }

                    SeverUserModel severUserModel  = new SeverUserModel(  );
                    severUserModel.setUid( user.getUid() );
                    severUserModel.setName( edt_name.getText().toString() );
                    severUserModel.setPhone( edt_phone.getText().toString() );
                    severUserModel.setActive( false );


                    dialog.show();

                    severRef.child( severUserModel.getUid() )
                            .setValue( severUserModel )
                            .addOnFailureListener( new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText( MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();

                                }
                            } ).addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialog.dismiss();
                            Toast.makeText( MainActivity.this, "Congratulation ! Register succes ", Toast.LENGTH_SHORT ).show();
                            //goToHomeActivity(severUserModel);
                        }
                    } );

                } );

        builder.setView( itemView );

        androidx.appcompat.app.AlertDialog registerDialog = builder.create();
        registerDialog.show();

    }

    private void goToHomeActivity(SeverUserModel severUserModel) {

        dialog.dismiss();

        Common.currentSeverUser = severUserModel;
        startActivity( new Intent( this,HomeActivity.class ) );
        finish();
    }



    private void PhoneLogin() {
        startActivityForResult( AuthUI.getInstance(  )
        .createSignInIntentBuilder()
        .setAvailableProviders( providers )
        .build(),APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if(requestCode == APP_REQUEST_CODE)
        {
            IdpResponse response = IdpResponse.fromResultIntent( data );
            if(resultCode == RESULT_OK)
            {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            }
            else
            {
                Toast.makeText( this, "Sign in Failed ", Toast.LENGTH_SHORT ).show();
            }
        }
    }
}