package com.example.ttmrcan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistorialAdapter(

//    var context: Context,
    var listaHistorialM: ArrayList<Historial>

) : RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    private var onClick: OnItemClicked? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.historial_consulta_individual,parent,false)
        return HistorialViewHolder(vista)
    }

    override fun getItemCount(): Int {
        return listaHistorialM.size
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val historial = listaHistorialM.get(position)

        holder.tvFecha.text = historial.proxima_cita_consulta.substring(0,10)
        holder.tvAplico.text = historial.aplico_consulta
        holder.tvProducto.text = historial.producto_consulta

        holder.btnInfo.setOnClickListener {
            onClick?.masInfo(historial)
        }

    }

    inner class HistorialViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvFecha = itemView.findViewById<TextView>(R.id.tvHistorialFecha)
        val tvAplico = itemView.findViewById<TextView>(R.id.tvHistorialAplico)
        val tvProducto = itemView.findViewById<TextView>(R.id.tvHistorialProducto)
        val btnInfo = itemView.findViewById<TextView>(R.id.tvMasInfoHistorial)
    }

    interface OnItemClicked{
        fun masInfo(historial: Historial)
    }
    fun setOnClick(onClick: OnItemClicked?){
        this.onClick = onClick
    }

}