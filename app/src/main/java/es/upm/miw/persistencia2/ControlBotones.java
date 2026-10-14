package es.upm.miw.persistencia2;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class ControlBotones {

    /**
     * Configura el TextWatcher para habilitar o deshabilitar el botón Enviar
     * según el contenido del EditText.
     */
    public static void configurarBotonValidacion(EditText etLineaTexto, Button btBotonEnviar) {
        // Establecer estado inicial según el texto actual
        boolean tieneTexto = etLineaTexto.getText() != null
                && !etLineaTexto.getText().toString().trim().isEmpty();
        btBotonEnviar.setEnabled(tieneTexto);

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
    }
}