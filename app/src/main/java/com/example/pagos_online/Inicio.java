package com.example.pagos_online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class Inicio extends AppCompatActivity {
    private EditText etpass;
    private EditText etemail;
    AwesomeValidation awesomeValidation;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        firebaseAuth = firebaseAuth.getInstance();
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.etemail, Patterns.EMAIL_ADDRESS, R.string.invalid_mail);
        awesomeValidation.addValidation(this, R.id.etpass, ".{8,}", R.string.invalid_password);

        etemail = findViewById(R.id.etemail);
        etpass = findViewById(R.id.etpass);

    }

    public void sesion(View view) {
        if (awesomeValidation.validate()) {

            String email = etemail.getText().toString();
            String pass = etpass.getText().toString();
            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        principio();
                    } else {
                        String error = ((FirebaseAuthException) task.getException()).getErrorCode();
                        dameToastDeError(error);
                    }
                }
            });
        }
    }

    public void registrarse(View view) {
        Intent i = new Intent(Inicio.this, Registrar.class);
        startActivity(i);
    }

    public void recuperar(View view) {
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.etemail, Patterns.EMAIL_ADDRESS, R.string.invalid_mail);

        if (awesomeValidation.validate()) {

            String email = etemail.getText().toString();
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Inicio.this, "Correo de recuperacion enviado", Toast.LENGTH_SHORT).show();
                    } else {
                        String error = ((FirebaseAuthException) task.getException()).getErrorCode();
                        dameToastDeError(error);
                    }
                }
            });
        }


    }

    public void principio() {
        //String mail = etemail.getText().toString();
        Intent i = new Intent(this, Principal.class);
        i.putExtra("email_obtenido", etemail.getText().toString());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void dameToastDeError(String error) {

        switch (error) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(Inicio.this, "El formato del token personalizado es incorrecto. Por favor revise la documentación", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(Inicio.this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(Inicio.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(Inicio.this, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                etemail.setError("La dirección de correo electrónico está mal formateada.");
                etemail.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(Inicio.this, "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                etpass.setError("la contraseña es incorrecta ");
                etpass.requestFocus();
                etpass.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(Inicio.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(Inicio.this, "Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(Inicio.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(Inicio.this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                etemail.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                etemail.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(Inicio.this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(Inicio.this, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(Inicio.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(Inicio.this, "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(Inicio.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(Inicio.this, "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(Inicio.this, "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                etpass.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                etpass.requestFocus();
                break;
            default:
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}