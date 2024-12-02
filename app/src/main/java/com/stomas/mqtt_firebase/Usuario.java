package com.stomas.mqtt_firebase;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//Librerias añadidas
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Usuario extends AppCompatActivity {
    //Variables
    private EditText txtCodigo, txtNombre, txtSucursal, txtDireccion;
    private ListView lista;
    private Spinner spTrabajo;
    //Variable FireBase
    private FirebaseFirestore db;
    //Datos Spinner
    String[] Trabajos = {"Informatica", "Ciberseguridad", "Administracion"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);
        //Invocamos un metodo para cargar la lista
        CargarListaFireStore();
        //Inicializamos la conexion de BD
        db = FirebaseFirestore.getInstance();
        //Uno las variables del formulario XML
        txtCodigo = findViewById(R.id.txtCodigo);
        txtNombre = findViewById(R.id.txtNombre);
        txtSucursal = findViewById(R.id.txtSucursal);
        txtDireccion = findViewById(R.id.txtDireccion);
        spTrabajo = findViewById(R.id.spTrabajo);
        lista = findViewById(R.id.lista);
        //Poblamos el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, Trabajos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTrabajo.setAdapter(adapter);
    }

    //Metodos para enviar los datos a Firebase
    public void enviarDatosFirestore(View view) {
        //Obtenemos los campos ingresados en el formulario
        String codigo = txtCodigo.getText().toString();
        String nombre = txtNombre.getText().toString();
        String sucursal = txtSucursal.getText().toString();
        String direccion = txtDireccion.getText().toString();
        String tipoTrabajo = spTrabajo.getSelectedItem().toString();
        //Mapeamos los datos ingresados para enviarlos a Firebase
        Map<String, Object> trabajo = new HashMap<>();
        trabajo.put("codigo", codigo);
        trabajo.put("nombre", nombre);
        trabajo.put("sucursal", sucursal);
        trabajo.put("direccion", direccion);
        trabajo.put("tipoTrabajo", tipoTrabajo);
        //Enviamos los datos a irebase
        db.collection("trabajos")
                .document(codigo)
                .set(trabajo)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Datos Enviados", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Datos No Enviados", Toast.LENGTH_SHORT).show();
                });
    }

    //Boton de evento que carga la lista
    public void CargarLista(View view){
        //Invoco el metodo que carga la lista de Firebase
        CargarListaFireStore();
    }

    //Metodo para cargar los datos de FireBase
    public void CargarListaFireStore(){
        //Obtenemos la instancia de Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Hacemos una consulta a la conexion llamada trabajos
        db.collection("trabajos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //Si la consulta a la coleccion fue exitosa
                        if (task.isSuccessful()){
                            //Creamos un ArrayList para almacenar los elementos
                            List<String> listaTrabajos = new ArrayList<>();
                            //Para mostrar los elementos de la lista utilizo un for
                            for(QueryDocumentSnapshot document: task.getResult()){
                                String linea = "||" + document.getString("codigo")+
                                        "||" + document.getString("nombre")+
                                        "||" + document.getString("sucursal")+
                                        "||" + document.getString("direccion");
                                listaTrabajos.add(linea);
                            }
                            //Creamos un ArrayAdapter para mostrar las mascotas en la lista
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    Usuario.this, android.R.layout.simple_list_item_1, listaTrabajos);
                            lista.setAdapter(adapter);
                        }else{
                            //Imprimo en la consola si hay errores al traer los datos
                            Log.e("TAG", "Error al obtener los datos", task.getException());
                        }
                    }
                });
    }
}
