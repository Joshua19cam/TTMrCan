package com.example.ttmrcan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey

class MascotaAdapter(
    var context: FragmentoListaMascotas,
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
        val imageUrl = mascota.foto_mascota

        if(imageUrl!!.isNotEmpty()){
            val uniqueId = System.currentTimeMillis().toString()
            Glide.with(context).load(imageUrl).signature(ObjectKey(uniqueId)).into(holder.iViewMascota)
        }

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

        /*if(imageUrl.isNullOrEmpty()){
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
        }else{
            holder.tvNombre.text = mascota.nombre_mascota.toString()

            Glide.with(context).load(imageUrl).into(holder.iViewMascota)
                //.diskCacheStrategy(DiskCacheStrategy.ALL) // Opcional: Almacenar en caché la imagen
                //.apply(RequestOptions().placeholder(R.drawable.placeholder_image)) // Opcional: Establecer imagen de carga


            holder.btnEditar.setOnClickListener {
                onClick?.editarMascota(mascota)
            }
            holder.btnBorrar.setOnClickListener {
                onClick?.borrarMascota(mascota.id_mascota)
            }
            holder.iViewMascota.setOnClickListener {
                onClick?.verPerfil(mascota)
            }
        }*/



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