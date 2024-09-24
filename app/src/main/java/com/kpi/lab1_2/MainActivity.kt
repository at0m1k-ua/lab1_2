package com.kpi.lab1_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    private var calculationResult by mutableStateOf("Показники ще не обчислено")

    private var inputs by mutableStateOf(mapOf<String, String>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InputsScreen(
                inputs = inputs,
                calculationResult = calculationResult,
                onInputsChange = { updatedInputs ->
                    inputs = updatedInputs
                },
                onCalculate = {
                    calculate()
                }
            )
        }
    }

    private fun calculate() {
        val delta = 0.01

        val hg = inputs["Hг"]?.toDoubleOrNull() ?: .0
        val cg = inputs["Cг"]?.toDoubleOrNull() ?: .0
        val sg = inputs["Sг"]?.toDoubleOrNull() ?: .0
        val og = inputs["Oг"]?.toDoubleOrNull() ?: .0
        val vg = inputs["Vг"]?.toDoubleOrNull() ?: .0
        val wg = inputs["Wг"]?.toDoubleOrNull() ?: .0
        val ag = inputs["Aг"]?.toDoubleOrNull() ?: .0
        val qidaf = inputs["Qidaf"]?.toDoubleOrNull() ?: .0
        if (abs(hg + cg + sg + og - 100) > delta) {
            calculationResult = "Помилка введення\nHг + Cг + Sг + Oг повинне дорівнювати 100"
            return
        }

        val cr = cg*(100 - wg - ag)/100
        val hr = hg*(100 - wg - ag)/100
        val or = og*(100 - wg - ag)/100
        val sr = sg*(100 - wg - ag)/100
        val ar = ag*(100 - wg)/100
        val vr = vg*(100 - wg)/100
        val qri = qidaf*(100 - wg - ar)/100 - 0.025

        calculationResult =
            """
                Hр = %.2f%%, Cр = %.2f%%
                Sр = %.2f%%, Oр = %.2f%%
                Vр = %.2f мг/кг, Aр = %.2f%%
                Qri = %.2f МДж/кг
            """.trimIndent().format(hr, cr, sr, or, vr, ar, qri)
    }
}

@Composable
fun InputsScreen(
    inputs: Map<String, String>,
    calculationResult: String,
    onInputsChange: (Map<String, String>) -> Unit,
    onCalculate: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            mapOf(
                "Hг" to "%",
                "Cг" to "%",
                "Sг" to "%",
                "Oг" to "%",
                "Vг" to "мг/кг",
                "Wг" to "%",
                "Aг" to "%",
                "Qidaf" to "МДж/кг",
            ).forEach { (inputName, units) ->
                Input(
                    label = inputName,
                    units = units,
                    value = inputs[inputName] ?: "",
                    onValueChange = { newValue ->
                        onInputsChange(inputs.toMutableMap().apply { put(inputName, newValue) })
                    }
                )
            }
        }

        Text(calculationResult)

        Column {
            Button(
                modifier = Modifier
                    .padding(8.dp)
                    .height(72.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                onClick = {
                    onCalculate()
                }
            ) {
                Text(
                    "Calculate",
                    fontSize = 24.sp
                )
            }
        }
    }
}

@Composable
fun Input(label: String, units: String, value: String, onValueChange: (String) -> Unit) {
    val regex = Regex("^\\d*\\.?\\d*\$")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
    ) {
        Text("$label, ", modifier = Modifier.width(40.dp))
        OutlinedTextField(
            value = value,
            onValueChange = {
                if (it.isEmpty() || it.matches(regex)) {
                    onValueChange(it)
                }
            },
            modifier = Modifier.height(48.dp).padding(horizontal = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )
        Text(units, modifier = Modifier.width(80.dp))
    }
}
