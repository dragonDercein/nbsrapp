package com.example.nbsrapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class crearUsuario extends AppCompatActivity {
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_usuario);
    }

    */

    private static final String TAG = "CREAR_USUARIO";
    private EditText nombreCom,genero, correo,telefono,direccion,pass1,pass2;
    private TextView informacion;
    private String mensajeError;
    private Button btCrearUsuario;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private String p1;
    private String p2;
    private String corr;
    private String tel;
    private String dir;
    private String nomC;
    private String gen;
    private boolean existe;
    private String listaErrores;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_usuario);
        nombreCom = (EditText)findViewById(R.id.idNombre);
        genero = (EditText)findViewById(R.id.idGenero);
        correo = (EditText)findViewById(R.id.idemail);
        telefono = (EditText)findViewById(R.id.idtelefono);
        direccion = (EditText)findViewById(R.id.idDireccion);
        pass1 = (EditText)findViewById(R.id.idpass);
        pass2 = (EditText)findViewById(R.id.idconfirmarpass);
        informacion = (TextView)findViewById(R.id.idinformación);
        btCrearUsuario = (Button)findViewById(R.id.idCrearCuenta);
        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference("Users");

    }

    public void crearU(View view){
        if(verificarCorreo()){
            verificarDatos();
        }
        else {
            btCrearUsuario.setText("VERIFICAR");
            Toast.makeText(this, "Verfique el correo Ingresado.", LENGTH_SHORT).show();
            informacion.setText("Correo erroneo.");
        }
    }

    public void cancelar(View view){
        finish();
    }

    private boolean verificarCorreo() {
        corr = correo.getText().toString();
        if (corr.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private void verificarDatos(){
        listaErrores = "";
        //nEOIns=nombreEOI.getText().toString();
        if(verificarNom() && verificarDir() && verificarGen() && verificarTelefono()){
            verificarPassword();
        }else{
            Log.i(TAG,"REVISAR DATOS");
            informacion.setText(listaErrores);
            btCrearUsuario.setText("VERIFICAR");
        }
    }

    private boolean verificarNom() {
        nomC=nombreCom.getText().toString();
        if(nomC.isEmpty()){
            listaErrores = listaErrores + " NOMBRE COMPLETO";
            Log.i(TAG,"REVISAR NOMBRE DEL USUARIO.");
            return false;
        }else{
            return true;
        }
    }

    private boolean verificarDir() {
        dir=direccion.getText().toString();
        if(dir.isEmpty()){
            mensajeError = mensajeError + " DIRECCION. ";
            Log.i(TAG,"REVISAR DIRECCION.");
            return false;
        }else{
            return true;
        }
    }

    private boolean verificarGen() {
        gen=genero.getText().toString();
        if(gen.isEmpty() ){
            listaErrores = listaErrores + "GENERO NO INGRESADO.";
            Log.i(TAG,"REVISAR GENERO");
            return false;
        }else{
            return true;
        }
    }

    private boolean verificarTelefono() {
        tel=telefono.getText().toString();
        if(tel.isEmpty()){
            listaErrores = listaErrores + "NUMERO DE TELEFONO.";
            Log.i(TAG,"REVISAR TELEFONO. ");
            return false;
        }else{
            return true;
        }
    }

    private void verificarPassword(){
        p1=pass1.getText().toString();
        p2=pass2.getText().toString();
        if(p1.length()<3){
            listaErrores = "SU CONTRASEÑA DEBE TENER MAS DE 3 DIGITOS";
            Log.i(TAG,"CONTRASEÑA NO SEGURA");
            informacion.setText(listaErrores);
            btCrearUsuario.setText("VERIFICAR");
        }else{
            if(p1.equals(p2)){
                actualizarRTDB();
            }else{
                listaErrores = "SU CONTRASEÑA NO COINCIDE.";
                Log.i(TAG,"CONTRASEÑA NO COINCIDE");
                informacion.setText(listaErrores);
                btCrearUsuario.setText("VERIFICAR");
            }
        }
    }

    private void actualizarRTDB() {
        //implementar agregar datos
        mAuth.createUserWithEmailAndPassword(corr, p1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i(TAG,"Se creo cuenta exitosamente - RTDB");
                    informacion.setText("FELICIDADES UD. CREO SU CUENTA CORRECTAMENTE");
                    Map<String,Object> map = new HashMap<>();
                    map.put("name",nombreCom);
                    map.put("address",dir);
                    map.put("phone",tel);
                    map.put("gender",gen);
                    map.put("email",corr);
                    map.put("password",p1);
                    map.put("tipo",verificarTipo());
                    map.put("idIns",getIntent().getStringExtra("idIns"));

                    String id =mAuth.getCurrentUser().getUid();
                    mDatabase.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "El usuario se agrego exitosamente", LENGTH_SHORT).show();
                                Log.i(TAG,"Se Agrego datos - RTDB");
                                finish();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "No se pudo crear cuenta en RTDB", LENGTH_SHORT).show();
                                Log.i(TAG,"no se pudo agregar - RTDB");
                            }
                        }
                    });

                }else{
                    Log.e(TAG,"NO SE PUDO AÑADIRT CORREO Y PASSWORD - RTDB");
                    informacion.setText("Correo Electronico ya existe.");

                }
            }
        });
    }

    private String verificarTipo(){
        return getIntent().getStringExtra("tipo");
    }

}