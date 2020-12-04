package com.example.nbsrapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class loguser extends AppCompatActivity {

    private static final String TAG = "LOG_USER";
    private EditText usuario,clave;
    private FirebaseAuth mAuth;
    private DatabaseReference mdatabase,mdatabaseaux;
    private String usuarioId,tipoUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //realiza el cast en de los componentes del layout
        usuario = (EditText) findViewById(R.id.txtname);
        clave = (EditText) findViewById(R.id.txtpass);
        //String [] tiposUsuarios= {"Ud. es...","...Jefe de Operaciones","...Rescatista"};
        //Crear un Adaptador que maneje las lista que queremos mostrar
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,tiposUsuarios);
        mAuth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void ingresar(View view) {
        String txtusr = usuario.getText().toString();
        String txtpass = clave.getText().toString();
        verificarUsuario(txtusr, txtpass);
    }

    public void salir(View view){
        this.finish();
    }

    private void verificarUsuario(String txtusr, String txtpass) {
        Toast.makeText(this, "El Usuario es:" + txtusr + " y la contraseña es " + txtpass, Toast.LENGTH_SHORT).show();

        mAuth.signInWithEmailAndPassword(txtusr, txtpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent;
                    usuarioId = mAuth.getCurrentUser().getUid();
                    if(!mdatabase.child(usuarioId).getKey().isEmpty()){

                        tipoUsuario =mdatabase.child(usuarioId+"/tipo").getKey();

                        if (tipoUsuario.equals("EOI")) {
                            intent = new Intent(getApplicationContext(), eoperaciones.class);
                            intent.putExtra("usrID", usuarioId);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Ingresara como Encargado de Operaciones de la institución ", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "Ingresa E. Operaciones");
                        } else {
                            if(tipoUsuario.equals("VOL")){

                                intent = new Intent(getApplicationContext(), voluntario.class);
                                intent.putExtra("usrID", usuarioId);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Ingresara como Voluntario ", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "Ingresa un Voluntario");
                            }else{

                                Toast.makeText(getApplicationContext(), "Su cuenta ha sido observada ", Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                            }
                        }
                    }else{
                        if(!mdatabaseaux.child(usuarioId).getKey().isEmpty()){

                            intent = new Intent(getApplicationContext(), institucion.class);
                            intent.putExtra("idInst",usuarioId);

                            Toast.makeText(getApplicationContext(), "Ingresara como Gerente ", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "Ingresara un Gerente");

                        }
                    }

                    Toast.makeText(getApplicationContext(), "Id Usuario: " + usuarioId, Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Usuario no existe.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}