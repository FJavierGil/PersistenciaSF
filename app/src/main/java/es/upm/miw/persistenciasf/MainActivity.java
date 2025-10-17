package es.upm.miw.persistenciasf;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
            // @TODO
        } catch (Exception e) {
            // @TODO
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

        try {   // Leer fichero y mostrarlo en TextView
			// @TODO
        } catch (Exception e) {
            // @TODO
        }
        if (!hayContenido) {
            // @TODO
        }
    }
}