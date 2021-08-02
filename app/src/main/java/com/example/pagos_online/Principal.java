package com.example.pagos_online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;

public class Principal extends AppCompatActivity {
    private TextView tv1;
    private String email;
    private TextView txt, txt2, txt3;
    FirebaseFirestore fires;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        tv1 = findViewById(R.id.correo);
        txt2 = findViewById(R.id.uid);
        txt3 = findViewById(R.id.dinero);

        fires = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getUid();
        String mail = user.getEmail();
        String elcorreo = user.getEmail();
        txt2.setText(name);
        Bundle extras1 = getIntent().getExtras();
        String d2 = extras1.getString("valorqr");
        fires.collection("cuentab").document(elcorreo).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String dinero = documentSnapshot.getString("dinero");
                    int din = Integer.parseInt(dinero);
                    txt3.setText(String.valueOf(din));
                    if (d2 == null)
                        txt3.setText(String.valueOf(din));
                    else {
                        if (TextUtils.isDigitsOnly(d2)) {
                            int num = Integer.parseInt(d2);
                            int total = din + num;
                            if (total >= 0) {
                                String string = String.valueOf(total);
                                DocumentReference actua = fires.collection("cuentab").document(elcorreo);
                                actua
                                        .update("dinero", string)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "Se actualizaron los datos correctamente");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error actualizando", e);
                                            }
                                        });
                                txt3.setText(string);
                            } else
                                Toast.makeText(Principal.this, "No tiene fondos suficientes", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(Principal.this, "El QR no contiene dinero", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(Principal.this, "Error al encontrar el dinero", Toast.LENGTH_SHORT).show();
            }
        });
        tv1.setText(mail);
    }

    public void finalizar(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(Principal.this, "Sesion cerrada", Toast.LENGTH_SHORT).show();
        inicio();
    }

    public void inicio() {
        Intent intent = new Intent(this, Inicio.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void escanear(View view) {
        Intent i = new Intent(this, Escanear.class);
        startActivity(i);
    }

    public void pasar(View view) {
        Intent enviar = new Intent(this, PasarDinero.class);
        startActivity(enviar);
    }

}