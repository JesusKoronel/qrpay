package com.example.pagos_online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class Registrar extends AppCompatActivity {
    private EditText etpass, etpass2, etemail;
    private Button button;

    AwesomeValidation awesomeValidation;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fires;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        firebaseAuth = firebaseAuth.getInstance();
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        fires = FirebaseFirestore.getInstance();
        awesomeValidation.addValidation(this, R.id.etemail, Patterns.EMAIL_ADDRESS, R.string.invalid_mail);
        awesomeValidation.addValidation(this, R.id.etpass, ".{8,}", R.string.invalid_password);
        awesomeValidation.addValidation(this, R.id.etpass2, ".{8,}", R.string.invalid_password);
        etpass = findViewById(R.id.etpass);
        etpass2 = findViewById(R.id.etpass2);
        etemail = findViewById(R.id.etemail);
        button = findViewById(R.id.button);
    }

    public void registro(View view) {

        String email = etemail.getText().toString();
        String cuenta = etemail.getText().toString();
        String pass = etpass.getText().toString();
        String pass2 = etpass2.getText().toString();
        if (pass.equals(pass2)) {
            if (awesomeValidation.validate()) {

                String dinero = "5000000";
                Map<String, Object> map = new HashMap<>();
                map.put("dinero", dinero);
                fires.collection("cuentab").document(cuenta).set(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });

                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Registrar.this, "Usuario creado con exito", Toast.LENGTH_SHORT).show();

                            finish();
                        } else {
                            String error = ((FirebaseAuthException) task.getException()).getErrorCode();
                            dameToastdeerror(error);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Completa todos los datos", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Las contraseñas deben ser iguales", Toast.LENGTH_SHORT).show();
        }
    }

    public void volver(View view) {
        Intent i = new Intent(Registrar.this, Inicio.class);
        startActivity(i);
    }

    private void dameToastdeerror(String error) {

        switch (error) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(Registrar.this, "El formato del token personalizado es incorrecto. Por favor revise la documentación", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(Registrar.this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(Registrar.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(Registrar.this, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                etemail.setError("La dirección de correo electrónico está mal formateada.");
                etemail.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(Registrar.this, "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                etpass.setError("la contraseña es incorrecta ");
                etpass.requestFocus();
                etpass.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(Registrar.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(Registrar.this, "Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(Registrar.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(Registrar.this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                etemail.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                etemail.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(Registrar.this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(Registrar.this, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(Registrar.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(Registrar.this, "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(Registrar.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(Registrar.this, "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(Registrar.this, "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                etpass.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                etpass.requestFocus();
                break;
            default:
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}