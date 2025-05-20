package com.example.login4;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private List<HistorialItem> items;

    public HistorialAdapter(List<HistorialItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public HistorialAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialAdapter.ViewHolder holder, int position) {
        HistorialItem item = items.get(position);

        holder.txtSlug.setText("https://ojglez.com/" + item.getSlug());
        holder.txtUrlOriginal.setText(item.getUrl());

        String fullUrl = "https://ojglez.com/" + item.getSlug();

        holder.btnCopiar.setOnClickListener(v -> {
            copiarAlPortapapeles(v.getContext(), fullUrl);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtSlug, txtUrlOriginal;
        Button btnCopiar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSlug = itemView.findViewById(R.id.txtSlug);
            txtUrlOriginal = itemView.findViewById(R.id.txtUrlOriginal);
            btnCopiar = itemView.findViewById(R.id.btnCopiar);
        }
    }

    private void copiarAlPortapapeles(Context context, String texto) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Enlace corto", texto);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "¡Enlace copiado!", Toast.LENGTH_SHORT).show();
    }
}
