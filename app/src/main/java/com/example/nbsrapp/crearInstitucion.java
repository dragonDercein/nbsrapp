package com.example.nbsrapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class crearInstitucion extends AppCompatActivity {

    private static final String TAG = "CREAR_INSTITUCION";
    private EditText nombreI,direccionI,gerenteI,telefonoI,nombreEOI,emailI,pass1,pass2;
    private TextView informacion;
    private String mensajeError;
    private Button crearIns;

    FirebaseAuth mAuth;
    DatabaseReference mDatabaseInst,mDatabaseUser;

    private String nIns = "";
    private String dirIns = "";
    private String gerIns = "";
    private String telIns = "";
    //private String nEOIns = "";
    private String emailIns = "";
    private String p1Ins = "";
    private String p2Ins = "";

    private String listaErrores = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_institucion);
        nombreI = (EditText)findViewById(R.id.idNI);
        direccionI = (EditText)findViewById(R.id.idDI);
        gerenteI = (EditText)findViewById(R.id.idGI);
        telefonoI = (EditText)findViewById(R.id.idtelefono);
        //nombreEOI = (EditText)findViewById(R.id.idNEOI);
        emailI = (EditText)findViewById(R.id.idCE);
        pass1 = (EditText)findViewById(R.id.idpass);
        pass2 = (EditText)findViewById(R.id.idconfirmarpass);
        informacion = (TextView)findViewById(R.id.idTv2);
        crearIns = (Button)findViewById(R.id.idCrearIns);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference("Users");
        mDatabaseInst = FirebaseDatabase.getInstance().getReference("Inst");


    }

    public void crearI(View view){
        if(verificarCorreo()){
            verificarDatos();
        }
        else {
            crearIns.setText("VERIFICAR");
            Toast.makeText(this, "Verfique el correo Ingresado.", LENGTH_SHORT).show();
            informacion.setText("Correo erroneo.");
        }
    }

    public void regresar(View view){
        finish();
    }

    private boolean verificarCorreo() {
        emailIns = emailI.getText().toString();
        if (emailIns.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private void verificarDatos(){
        listaErrores = "";
        //nEOIns=nombreEOI.getText().toString();
        if(verificarNomIns() && verificarDirIns() && verificarNombre() && verificarTelefono()){
            verificarPassword();
        }else{
            Log.i(TAG,"REVISAR DATOS");
            informacion.setText(listaErrores);
            crearIns.setText("VERIFICAR");
        }
    }

    private boolean verificarNomIns() {
        nIns=nombreI.getText().toString();
        if(nIns.isEmpty()){
            listaErrores = listaErrores + " NOMBRE DE LA INSTITUCIÓN";
            Log.i(TAG,"NOMBRE DE INSTITUCION NO INGRESADO.");
            return false;
        }else{
            return true;
        }
    }

    private boolean verificarDirIns() {
        dirIns=direccionI.getText().toString();
        if(dirIns.isEmpty()){
            mensajeError = mensajeError + " DIRECCION DE LA INSTITUCION. ";
            Log.i(TAG,"DIRECCION DE INSTITUCION NO INGRESADO..");
            return false;
        }else{
            return true;
        }
    }

    private boolean verificarNombre() {
        gerIns=gerenteI.getText().toString();
        if(gerIns.isEmpty() ){
            listaErrores = listaErrores + "GERENTE O ENCARGADO DE OPERACIONES.";
            Log.i(TAG,"GERENTE O ENCARGADO DE OPERACIONES NO INGRESADOS");
            return false;
        }else{
            return true;
        }
    }

    private boolean verificarTelefono() {
        telIns=telefonoI.getText().toString();
        if(telIns.isEmpty()){
            listaErrores = listaErrores + "NUMERO DE TELEFONO.";
            Log.i(TAG,"TELEFONO NO INGREASDO. ");
            return false;
        }else{
            return true;
        }
    }

    private void verificarPassword(){
        p1Ins=pass1.getText().toString();
        p2Ins=pass2.getText().toString();
        if(p1Ins.length()<3){
            listaErrores = "SU CONTRASEÑA DEBE TENER MAS DE 3 DIGITOS";
            Log.i(TAG,"CONTRASEÑA NO SEGURA");
            informacion.setText(listaErrores);
            crearIns.setText("VERIFICAR");
        }else{
            if(p1Ins.equals(p2Ins)){
                actualizarRTDB();
            }else{
                listaErrores = "SU CONTRASEÑA NO COINCIDE.";
                Log.i(TAG,"CONTRASEÑA NO COINCIDE");
                informacion.setText(listaErrores);
                crearIns.setText("VERIFICAR");
            }
        }
    }

    private void actualizarRTDB() {
        //implementar agregar datos
        Toast.makeText(this,emailIns + " " + p1Ins,LENGTH_SHORT).show();
        Toast.makeText(this, "errores " + listaErrores,LENGTH_SHORT).show();

        mAuth.createUserWithEmailAndPassword(emailIns,p1Ins).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Log.i(TAG,"Se creo cuenta exitosamente - RTDB");
                    informacion.setText("FELICIDADES UD. CREO LA CUENTA INSTITUCIONAL EXITOSAMENTE.");
                    Map<String,Object> mapI = new HashMap<>();
                    mapI.put("nameIns",nIns);
                    mapI.put("addIns",dirIns);
                    mapI.put("gerIns",gerIns);
                    mapI.put("telfIns",telIns);
                   // mapI.put("nameEOI",nEOIns);
                    mapI.put("emailIns",emailIns);
                    mapI.put("passIns",p1Ins);
                    //FirebaseUser user = mAuth.getCurrentUser();
                    //updateUI(user);

                    String id = mAuth.getCurrentUser().getUid();
                    //String idInst = mDatabaseInst.push().getKey();

                    Toast.makeText(crearInstitucion.this, id, Toast.LENGTH_LONG).show();
                    mDatabaseInst.child(id).setValue(mapI).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()) {
                                Log.i(TAG, "Se Agrego datos de la Institución - RTDB");
                                Toast.makeText(crearInstitucion.this, "Cuenta Institucional creada exitosamente", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(),crearUsuario.class);
                                intent.putExtra("tipo","EOI");
                                intent.putExtra("idIns","id");
                                startActivity(intent);

                                finish();

                                /*Map<String,Object> mapU = new HashMap<>();
                                mapU.put("nombre",nEOIns);
                                mapU.put("Genero","dato no insertado");
                                mapU.put("FNac","dato no insertado");
                                mapU.put("Especialidad","dato no insertado");
                                mapU.put("Direccioón","dato no insertado");
                                mapU.put("Cargo","Encargado de Operaciones");
                                mapU.put("Institucion",);

                                mDatabaseUser.child(id).setValue(mapU).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.i(TAG, "Se Agrego datos de usuario - RTDB");
                                            Toast.makeText(crearInstitucion.this, "Cuenta de usuario creada exitosamente", Toast.LENGTH_LONG).show();

                                        }else{
                                            Toast.makeText(crearInstitucion.this, "No se pudo crear cuenta de usuario en RTDB", LENGTH_SHORT).show();
                                            Log.i(TAG,"no se pudo agregar Usuario - RTDB");
                                        }
                                    }
                                });*/

                            }else {
                                Toast.makeText(crearInstitucion.this, "No se pudo crear cuenta de Insitutcion en RTDB", LENGTH_SHORT).show();
                                Log.i(TAG,"no se pudo agregar Institucion- RTDB");
                            }
                        }
                    });

                }else{
                    Log.e(TAG,"NO SE PUDO AÑADIR CORREO Y PASSWORD - RTDB");
                    informacion.setText("CORREO ELECTRONICO YA EXISTE.");
                }
            }
        });
    }
}