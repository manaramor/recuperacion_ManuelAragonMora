package com.example.recuperacion_manuelaragonmora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.contratartareas.data.DataSource
import com.example.contratartareas.data.Tarea
import com.example.recuperacion_manuelaragonmora.ui.theme.Recuperacion_ManuelAragonMoraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Recuperacion_ManuelAragonMoraTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TareasApp()
                }
            }
        }
    }
}

@Composable
fun TareasApp() {

    var listaTareas by remember {mutableStateOf(DataSource.tareas)}

    var ultimaAccion: MutableState<String> = remember{ mutableStateOf("") }

    var nuevaTarea by remember { mutableStateOf("") }

    var resumen : MutableState<String> = remember { mutableStateOf("") }



    Column(){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ){
            ListaTareas(listaTareas = listaTareas, resumen = resumen, ultimaAccion = ultimaAccion)
        }

        Column(
            modifier = Modifier.padding(10.dp)
        ){
            Row(){
                InputText(value = nuevaTarea, onValueChanged = {nuevaTarea = it}, label = "Nombre nueva tarea", modifier = Modifier.width(250.dp).padding(8.dp) )

                Button(onClick = {

                    try{

                        var tareaEncontrada = false

                        for (tarea in listaTareas) {

                            if(nuevaTarea == tarea.nombre){
                                tareaEncontrada = true

                                ultimaAccion.value = "Ya existe una tarea llamada así"
                            } else if (nuevaTarea.isEmpty()){
                                ultimaAccion.value = "El valor es vacío o blanco"
                            }


                            if (!tareaEncontrada){
                                val tareaCreada = Tarea(nuevaTarea,10,0)

                                listaTareas.add(tareaCreada)

                                ultimaAccion.value = "Añadida la tarea ${tareaCreada.nombre}"
                            } else{
                                println("Se ha producido un error")
                            }
                        }

                    }
                    catch(e: Exception){
                        println("Se ha producido un error ${e.message}")
                    }



                }, modifier = Modifier.padding(10.dp)) {
                    Text ( text = "Nueva tarea")
                }
            }
        }

        Column(
            modifier = Modifier
                .background(Color.LightGray)
                .padding(30.dp)
        ){
            Column(
                modifier = Modifier
                    .background(Color.Yellow)
                    .fillMaxWidth()
            ){
                Text (text = "Ultima acción \n")
                Text(text= "${ultimaAccion.value}")

            }

            Column(
                modifier = Modifier.background(Color.White)
                    .fillMaxWidth()
            ){
                Text (text = "Resumen \n")
                Text (text= "${resumen.value}")

            }

            Column(
                modifier = Modifier
                    .background(Color.Yellow)
                    .fillMaxWidth()
            ){

                var horasTotales: Int = 0

                for (tareas in listaTareas){
                    horasTotales += tareas.cantidadHoras
                }

                Text (text = "\"Total horas ${horasTotales}")
            }
        }
    }
}

@Composable
fun TarjetaTarea(tarea:Tarea, listaTareas: List<Tarea>, resumen: MutableState<String>,ultimaAccion: MutableState<String>){

    Card(
        modifier = Modifier
        .padding(5.dp)
    ){
        Column(
        ){
        
            Column(
                modifier = Modifier
                .background(Color.Yellow)
                .fillMaxWidth()
                .padding(5.dp)
            ){
                Text(text = "Tarea: ${tarea.nombre}")
            }

            Column(
                modifier = Modifier
                    .background(Color.Cyan)
                    .fillMaxWidth()
            ){
                Text(text = "€/hora: ${tarea.precio}")
            }
            
            Column(modifier = Modifier.height(50.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Button(onClick = {

                    try {
                        tarea.cantidadHoras++

                        ultimaAccion.value = "Se añade 1 hora a ${tarea.nombre}"

                        var actualizarResumen = ActualizarResumen(listaTareas = listaTareas)

                        resumen.value = actualizarResumen
                    } catch(e: Exception){
                        println("Se ha producido un error ${e.message}")
                    }

                }) {
                    Text(text = "+")
                }
            }
        }
    }
}

@Composable
fun ListaTareas(listaTareas: List<Tarea>, resumen: MutableState<String>, ultimaAccion: MutableState<String>){
    LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier ){
        items (listaTareas) { tarea ->
            TarjetaTarea(tarea = tarea, listaTareas = listaTareas, resumen = resumen, ultimaAccion = ultimaAccion)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputText(value:String, onValueChanged: (String) -> Unit, label:String, modifier: Modifier){
    TextField(value = value, onValueChange = onValueChanged, label = {Text (text = label)}, singleLine = true, modifier = modifier )
}

fun ActualizarResumen(listaTareas: List<Tarea>):String{

    val resumenActualizado = buildString {

        for (tarea in listaTareas){
            if (tarea.cantidadHoras > 0){
                append("La tarea: ${tarea.nombre} Precio: ${tarea.precio} Horas: ${tarea.cantidadHoras} \n")
            }
        }


    }

    return resumenActualizado
}