package es.upm.miw.persistenciasf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = "MiW";

    private EditText etLineaTexto;
    private TextView tvContenidoFichero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etLineaTexto = findViewById(R.id.etTextoIntroducido);
        tvContenidoFichero = findViewById(R.id.tvContenidoFichero);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mostrarContenido();
    }

    /**
     * Devuelve el nombre del fichero
     *
     * @return nombre del fichero
     */
    private String obtenerNombreFichero() {

        return getResources().getString(R.string.default_NombreFich);
    }

    /**
     * Al pulsar el botón añadir -> añadir al fichero.
     * Después de añadir -> mostrarContenido()
     *
     * @param v Botón añadir
     */
    public void accionAniadir(View v) {

        try {  // Añadir al fichero
            FileOutputStream fos;

            fos = openFileOutput(obtenerNombreFichero(), Context.MODE_APPEND); // Memoria interna
            fos.write(etLineaTexto.getText().toString().getBytes());
            fos.write('\n');
            fos.close();
            mostrarContenido();
            Log.i(LOG_TAG, "Click botón Añadir -> AÑADIR al fichero");
        } catch (Exception e) {
            Log.e(LOG_TAG, "FILE I/O ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        etLineaTexto.setText(null);
    }

    /**
     * Muestra el contenido del fichero en el TextView contenidoFichero
     * <p>
     * Si está vacío -> mostrar un Toast
     */
    void mostrarContenido() {
        boolean hayContenido = false;
        BufferedReader fin;
        tvContenidoFichero.setText("");

        try {
            fin = new BufferedReader(
                    new InputStreamReader(openFileInput(obtenerNombreFichero()))); // Memoria interna
            String linea = fin.readLine();
            while (linea != null) {
                hayContenido = true;
                tvContenidoFichero.append(linea + '\n');
                linea = fin.readLine();
            }
            fin.close();
            Log.i(LOG_TAG, "Click contenido Fichero -> MOSTRAR fichero");
        } catch (Exception e) {
            Log.e(LOG_TAG, "FILE I/O ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        if (!hayContenido) {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.txtFicheroVacio),
                    Snackbar.LENGTH_SHORT
            ).show();
        }
    }
}