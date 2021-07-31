package com.example.pagos_online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import static android.content.ContentValues.TAG;

public class PasarDinero extends AppCompatActivity {

    private EditText ettexto;
    private Button botongenerador;
    private ImageView verqr;
    FirebaseFirestore fires;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasardinero);

        ettexto = findViewById(R.id.ettexto);
        botongenerador = findViewById(R.id.botongenerador);
        verqr = findViewById(R.id.verqr);

        fires = FirebaseFirestore.getInstance();
    }

    public void botongenerar(View view) {
        String texti = ettexto.getText().toString().trim();
        if (texti.length() != 0) {
            if (TextUtils.isDigitsOnly(texti)) {
                mostrarDialogo();
            } else {
                Toast.makeText(this, "Por favor introduzca la cantidad numerica", Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(this, "Introduzca una cantidad de dinero por favor", Toast.LENGTH_SHORT).show();
    }

    public void mostrarDialogo() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(PasarDinero.this);
        alerta.setTitle("Alerta");
        alerta.setMessage("¿Seguro que quieres generar el codigo?, se descontara la cantidad de tu cuenta.")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        generarqr();
                    }

                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(PasarDinero.this, "QR cancelado", Toast.LENGTH_SHORT).show();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void generarqr() {
        String texto = ettexto.getText().toString().trim();
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(texto, BarcodeFormat.QR_CODE, 350, 350);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            verqr.setImageBitmap(bitmap);
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(ettexto.getApplicationWindowToken(), 0);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        fires = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String mail = user.getEmail();
        String elcorreo = user.getEmail();
        fires.collection("cuentab").document(elcorreo).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String dinero = documentSnapshot.getString("dinero");
                    int dine = Integer.parseInt(dinero);
                    int codigoqr = Integer.parseInt(texto);
                    int total = dine - codigoqr;
                    if (total >= 0) {
                        String string = String.valueOf(total);
                        DocumentReference actua = fires.collection("cuentab").document(elcorreo);
                        actua
                                .update("dinero", string)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Actualizado correctamente");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error al actualizar", e);
                                    }
                                });
                    } else
                        Toast.makeText(PasarDinero.this, "No tiene fondos suficientes", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(PasarDinero.this, "No se encontro el usuario", Toast.LENGTH_SHORT).show();
            }
        });

        SQLite admin = new SQLite(this, "usuariosdb", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor datos = bd.rawQuery("select dinero from usuarios WHERE email=\'" + mail + "\'", null);

        if (datos.moveToFirst()) {
            String dato = datos.getString(datos.getColumnIndex("dinero"));

            int dine = Integer.parseInt(dato);
            int codigoqr = Integer.parseInt(texto);
            int total = dine - codigoqr;
            if (total >= 0) {
                ContentValues cv = new ContentValues();
                cv.put("dinero", total);
                bd.execSQL("UPDATE usuarios set dinero=" + total + " WHERE email=\'" + mail + "\'");
            } else
                Toast.makeText(this, "No tiene fondos suficientes", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(PasarDinero.this, "QR generado", Toast.LENGTH_SHORT).show();
    }

    public void volver(View view) {
        Intent in = new Intent(this, Principal.class);
        in.putExtra("valorqr", "0");
        startActivity(in);
        finish();
    }
}