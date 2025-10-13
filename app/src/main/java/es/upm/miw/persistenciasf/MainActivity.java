package es.upm.miw.persistenciasf;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = "MiW";
    private final int LONGITUD_MENSAJE = 140; // Máxima longitud mensajes

    private EditText etLineaTexto;
    private Button btBotonEnviar;
    private TextView tvContenidoFichero;

    private SharedPreferences preferencias;

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
        etLineaTexto       = findViewById(R.id.etTextoIntroducido);
        btBotonEnviar      = findViewById(R.id.btBotonEnviar);
        tvContenidoFichero = findViewById(R.id.tvContenidoFichero);

        preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        activarBotonEnviar();

        enviarPulsarEnter();
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
        String nombreFichero = preferencias.getString(
                getString(R.string.key_NombreFickero),
                getString(R.string.default_NombreFich)
        );
        Log.i(LOG_TAG, "Nombre fichero: " + nombreFichero);

        return nombreFichero;
    }

    /**
     * Determina si prefiere memoria interna o externa
     *
     * @return valor lógico
     */
    private boolean utilizarMemInterna() {
        boolean utilizarMemInterna = !preferencias.getBoolean(
                getString(R.string.key_TarjetaSD),
                getResources().getBoolean(R.bool.default_prefTarjetaSD)
        );
        Log.i(LOG_TAG, "Memoria SD: " + ((!utilizarMemInterna) ? "ON" : "OFF"));

        return utilizarMemInterna;
    }

    /**
     * Al pulsar el botón añadir → añadir al fichero.
     * Después de añadir → mostrarContenido()
     *
     * @param v Botón Enviar (btBotonEnviar)
     */
    public void accionAniadir(View v) {
        FileOutputStream fos;

        try {  // Añadir al fichero
            if (utilizarMemInterna()) {
                fos = openFileOutput(obtenerNombreFichero(), Context.MODE_APPEND); // Memoria interna
            } else {    // Comprobar estado SD card
                String estadoTarjetaSD = Environment.getExternalStorageState();
                if (estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {
                    String rutaFich = getExternalFilesDir(null) + "/" + obtenerNombreFichero();
                    fos = new FileOutputStream(rutaFich, true);
                } else {
                    Toast.makeText(
                            this,
                            getString(R.string.txtErrorMemExterna),
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }
            }

            // Escribir fichero
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
            if (utilizarMemInterna()) {
                fin = new BufferedReader(
                        new InputStreamReader(openFileInput(obtenerNombreFichero()))); // Memoria interna
            } else {
                String estadoTarjetaSD = Environment.getExternalStorageState();
                if (estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) { /* SD card */
                    String rutaFich = getExternalFilesDir(null) + "/" + obtenerNombreFichero();
                    Log.i(LOG_TAG, "rutaSD=" + rutaFich);
                    fin = new BufferedReader(new FileReader(new File(rutaFich)));
                } else {
                    Log.i(LOG_TAG, "Estado SDcard=" + estadoTarjetaSD);
                    Toast.makeText(this, getString(R.string.txtErrorMemExterna), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Procesar fichero
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
            if (utilizarMemInterna()) {
                File f = new File(getFilesDir().getAbsolutePath(), obtenerNombreFichero());
                if (!f.delete()) throw new FileNotFoundException();
            } else {    // Comprobar estado SD card
                String estadoTarjetaSD = Environment.getExternalStorageState();
                if (estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {
                    String rutaFich = getExternalFilesDir(null) + "/" + obtenerNombreFichero();
                    File f = new File(rutaFich);
                    if (!f.delete()) throw new FileNotFoundException();
                } else {
                    Toast.makeText(this, getString(R.string.txtErrorMemExterna), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
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

    /**
     * Activa el botón enviar sólo cuando hay nuevo texto
     */
    private void activarBotonEnviar() {
        etLineaTexto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btBotonEnviar.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        etLineaTexto.setFilters(
                new InputFilter[]{new InputFilter.LengthFilter(LONGITUD_MENSAJE)}
        );
    }

    /**
     * Controla la tecla <Enter> -> Provoca el envío al pulsar la tecla <Enter>
     */
    private void enviarPulsarEnter() {
        // Provoca el envío al pulsar la tecla <Enter>
        etLineaTexto.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Se ha pulsado una tecla y es <Enter>
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    btBotonEnviar.performClick();
                    return true;
                }
                return false;
            }
        });
    }
}