package com.example.ttmrcan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class MascotaAdapter(
    //var context: Context,
    var listaMascotas: ArrayList<Mascota>
): RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder>(){

    private var onClick: OnItemClicked? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.perfil_mascota_individual,parent,false)
        return MascotaViewHolder(vista)
    }

    override fun getItemCount(): Int {
        return listaMascotas.size
    }

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {
        val mascota = listaMascotas.get(position)

        holder.tvNombre.text = mascota.nombre_mascota.toString()

        holder.btnEditar.setOnClickListener {
            onClick?.editarMascota(mascota)
        }
        holder.btnBorrar.setOnClickListener {
            onClick?.borrarMascota(mascota.id_mascota)
        }
        holder.iViewMascota.setOnClickListener {
            onClick?.verPerfil(mascota)
        }
    }

    inner class MascotaViewHolder(itemView: View): ViewHolder(itemView){
        val tvNombre = itemView.findViewById<TextView>(R.id.textViewMascotaNombre)
        val btnEditar = itemView.findViewById<Button>(R.id.buttonEditarMisMascotas)
        val btnBorrar = itemView.findViewById<Button>(R.id.buttonDarBaja)
        val iViewMascota = itemView.findViewById<ImageView>(R.id.imageViewMascota)
    }

    interface OnItemClicked{
        fun editarMascota(mascota: Mascota)
        fun borrarMascota(id: Int)
        fun verPerfil(mascota: Mascota)
    }
    fun setOnClick(onClick: OnItemClicked?){
        this.onClick = onClick
    }
}