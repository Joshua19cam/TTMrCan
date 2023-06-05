package com.example.ttmrcan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class CitasAdapter(
    //var context: FragmentoCitas,
    var listaCitas: ArrayList<Cita>,
): RecyclerView.Adapter<CitasAdapter.CitaViewHolder>() {

    private var onClick: OnItemClicked? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.cardview_cita,parent,false)
        return CitaViewHolder(vista)
    }

    override fun getItemCount(): Int {
        return listaCitas.size
    }


    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val cita = listaCitas.get(position)

        // NO.1 **** Holder para la parte del TextView FechaCita -- Sostiene el TV del cardview Citas Proximas
        holder.tvTipoCita.text = cita.tipo_cita
        //NO.2 **** Toma la fecha de la listaCitas de la posicion fecha_cita de la base de datos para convertitlo unicamente a dd//mm/yyyy
        val fechaCita = listaCitas[position].fecha_cita // Obtener la fecha de la cita desde el objeto Cita en la posición dada
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Crear un objeto SimpleDateFormat con el formato deseado
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(fechaCita) // Convertir la fecha de cita en un objeto Date
        val fechaFormateada = sdf.format(date) // Formatear la fecha a la cadena deseada
        // Holder para la parte del TextView FechaCita -- Sostiene el TV del cardview Citas Proximas
        // Establecer la fecha formateada en el TextView correspondiente
        holder.tvFechaCita.text = fechaFormateada

        // NO.3 ****  Configurar listener para botón de cancelar
        holder.ivCancelarCita.setOnClickListener {
            onClick?.cancelarCita(cita.id_cita)
        }

        // NO.4 **** Toma la hora de la listaCitas de la posicion hora_cita de la base de datos para convertitlo unicamente a hh:mm -- Horas y minutos
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        // Obtener la hora de la cita de tu objeto de datos
        val horaCita = listaCitas[position].hora_cita
        // Parsear la hora de la cita al objeto Date
        val hora = inputFormat.parse(horaCita)
        // Dar formato a la fecha en el formato deseado
        val horaFormateada = outputFormat.format(hora)
        // Holder para la parte del TextView FechaCita -- Sostiene el TV del cardview Citas Proximas
        // Asignar la hora formateada al TextView correspondiente
        holder.tvHoraCita.text = horaFormateada

    }

    inner class CitaViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvTipoCita = itemView.findViewById(R.id.tipoCitaTextView) as TextView
        val tvFechaCita = itemView.findViewById(R.id.fechaCitaTextView) as TextView
        val tvHoraCita = itemView.findViewById(R.id.horaCitaTextView) as TextView
        val ivCancelarCita = itemView.findViewById(R.id.ivCancelarCita) as ImageView
    }

    interface OnItemClicked{
        fun cancelarCita(id: Int)
    }
    fun setOnClick(onClick: OnItemClicked?){
        this.onClick = onClick
    }

}