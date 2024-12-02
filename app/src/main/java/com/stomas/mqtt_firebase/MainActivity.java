package com.stomas.mqtt_firebase;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
//Librerias
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    private static String mqttHost = "tcp://sproutsnagglefoot621.cloud.shiftr.io:1883"; //Ip del servidor MQTT
    private static String IdUsuario = "AppAndroid"; //Nombre del dispositivo que se conectara

    private static String Topico = "Mensaje"; //Topico al que se suscribira
    private static String User = "sproutsnagglefoot621"; //Usuario
    private static String Pass = "Y2qkUxnlIougGbxj"; //Contraseña o Token

    //Variable que se utilizara para imprimir los datos del servidor
    private TextView textView;
    private EditText editTextMessage;
    private Button botonEnvio, botonIrFirebase;

    private MqttClient mqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Enlace de la variable del ID que esta en el activity main donde imprimiremos los datos
        textView = findViewById(R.id.textView);
        editTextMessage = findViewById(R.id.txtMensaje);
        botonEnvio = findViewById(R.id.botonEnvioMensaje);
        botonIrFirebase = findViewById(R.id.onClickFirebase);
        try {
            //Creacion del cliente MQTT
            mqttClient = new MqttClient(mqttHost, IdUsuario, null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(User);
            options.setPassword(Pass.toCharArray());
            //Conexion al servidor MQTT
            mqttClient.connect(options);
            //Si se conecta imprimira un mensaje de MQTT
            Toast.makeText(this, "Aplicacion conectada al servidor", Toast.LENGTH_SHORT).show();
            //Manejo de entrega de datos y perdida de conexion
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("MQTT", "Conexion perdida");
                }
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    runOnUiThread(() -> textView.setText(payload));
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("MQTT", "Entrega Completa");
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();

        }

        botonEnvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mensaje = editTextMessage.getText().toString();
                try {
                    if (mqttClient != null && mqttClient.isConnected()) {
                        mqttClient.publish(Topico, mensaje.getBytes(), 0, false);
                        textView.append("\n" + mensaje);
                        Toast.makeText(MainActivity.this, "Mensaje enviado", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this, "Error: No se pudo enviar el mensaje. La conexion MQTT no esta activa", Toast.LENGTH_SHORT).show();
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
        botonIrFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Usuario.class);
                startActivity(intent);
            }
        });
    }
}