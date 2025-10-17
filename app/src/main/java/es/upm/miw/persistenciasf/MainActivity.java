package es.upm.miw.persistenciasf;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

        EdgeToEdge.enable(this);
        // Establece las inserciones de recortes de pantalla
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener vistas
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
     * Al pulsar el botón añadir → añadir al fichero.
     * Después de añadir → mostrarContenido()
     *
     * @param v Botón Enviar (btBotonEnviar)
     */
    public void accionAniadir(View v) {

        try {  // Añadir al fichero
            FileOutputStream fos;

            fos = openFileOutput(obtenerNombreFichero(), Context.MODE_APPEND); // Memoria interna
            fos.write(etLineaTexto.getText().toString().getBytes());
            fos.write('\n');
            fos.close();
            Log.i(LOG_TAG, "Click botón Añadir -> AÑADIR al fichero");
            mostrarContenido();
        } catch (Exception e) {
            Log.e(LOG_TAG, "FILE I/O ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        etLineaTexto.setText(null);  // Vacía la línea de texto
    }

    /**
     * Muestra el contenido del fichero en el TextView (tvContenidoFichero)
     * <p>
     * Si está vacío → mostrar un Toast
     */
    void mostrarContenido() {
        boolean hayContenido = false;
        BufferedReader fin;
        tvContenidoFichero.setText("");

        try {   // Leer fichero y mostrarlo en TextView
            fin = new BufferedReader(
                    new InputStreamReader(openFileInput(obtenerNombreFichero()))); // Memoria interna
            String linea = fin.readLine();
            while (linea != null) {
                hayContenido = true;
                tvContenidoFichero.append(linea + '\n');
                linea = fin.readLine();
            }
            fin.close();
            Log.i(LOG_TAG, "MOSTRAR fichero");
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

    /**
     * Vaciar el contenido del fichero, la línea de edición y actualizar
     */
    protected void borrarContenido() {
        try {  // Vaciar el fichero
            FileOutputStream fos;
            fos = openFileOutput(obtenerNombreFichero(), Context.MODE_PRIVATE); // Memoria interna
            fos.close();
            Log.i(LOG_TAG, "opción BORRAR -> VACIAR el fichero");
            etLineaTexto.setText(""); // limpio línea de edición
            mostrarContenido();
        } catch (Exception e) {
            Log.e(LOG_TAG, "FILE I/O ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Añade el menú con la opcion de vaciar el fichero
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflador del menú: añade elementos a la action bar
        getMenuInflater().inflate(R.menu.menu_opciones, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accionVaciar:
                Log.i(LOG_TAG, "opción BORRAR");
                BorrarDialogFragment dialogFragment = new BorrarDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "frgEliminar");
                break;
            case R.id.settings: // Ajustes
                Log.i(LOG_TAG, "opción AJUSTES");
                Intent intent = new Intent(this, ActividadPreferencias.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}