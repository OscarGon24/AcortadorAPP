package com.example.login4;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class Modal extends DialogFragment {

    public interface ModalListener {
        void onCerrarSesionSelected();
    }

    private ModalListener listener;

    public void setModalListener(ModalListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.Acciones)
                .setPositiveButton(R.string.Premium, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (getActivity() != null) {
                            getActivity().startActivity(new Intent(getActivity(), PagoTarjeta.class));
                        }
                    }
                })
                .setNegativeButton(R.string.cerrarSesion, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onCerrarSesionSelected();
                        }
                    }
                });
        return builder.create();
    }
}
