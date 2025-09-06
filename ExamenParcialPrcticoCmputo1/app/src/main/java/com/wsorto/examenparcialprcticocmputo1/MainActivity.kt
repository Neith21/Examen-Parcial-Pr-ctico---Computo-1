package com.wsorto.examenparcialprcticocmputo1

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Referencias a los controles de la interfaz
    private lateinit var txtNombreProducto: EditText
    private lateinit var txtPrecioUnitario: EditText
    private lateinit var txtCantidadComprada: EditText
    private lateinit var radioGroupPago: RadioGroup
    private lateinit var radioContado: RadioButton
    private lateinit var radioCredito: RadioButton
    private lateinit var layoutPlazo: LinearLayout
    private lateinit var txtDiasPlazo: EditText
    private lateinit var btnCalcular: Button
    private lateinit var layoutResultados: LinearLayout
    private lateinit var lblResultados: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar las referencias a los controles
        inicializarControles()

        // Configurar eventos
        configurarEventos()
    }

    private fun inicializarControles() {
        txtNombreProducto = findViewById(R.id.txtNombreProducto)
        txtPrecioUnitario = findViewById(R.id.txtPrecioUnitario)
        txtCantidadComprada = findViewById(R.id.txtCantidadComprada)
        radioGroupPago = findViewById(R.id.radioGroupPago)
        radioContado = findViewById(R.id.radioContado)
        radioCredito = findViewById(R.id.radioCredito)
        layoutPlazo = findViewById(R.id.layoutPlazo)
        txtDiasPlazo = findViewById(R.id.txtDiasPlazo)
        btnCalcular = findViewById(R.id.btnCalcular)
        layoutResultados = findViewById(R.id.layoutResultados)
        lblResultados = findViewById(R.id.lblResultados)
    }

    private fun configurarEventos() {
        // Evento para mostrar/ocultar el campo de plazo según el método de pago
        radioGroupPago.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioCredito -> {
                    layoutPlazo.visibility = View.VISIBLE
                }
                R.id.radioContado -> {
                    layoutPlazo.visibility = View.GONE
                    txtDiasPlazo.setText("")
                }
            }
        }

        // Evento del botón calcular
        btnCalcular.setOnClickListener {
            calcularTotal()
        }
    }

    private fun calcularTotal() {
        // Validar campos obligatorios
        if (!validarCampos()) {
            return
        }

        try {
            // Obtener valores de los campos
            val nombreProducto = txtNombreProducto.text.toString().trim()
            val precioUnitario = txtPrecioUnitario.text.toString().toDouble()
            val cantidadComprada = txtCantidadComprada.text.toString().toInt()
            val esCredito = radioCredito.isChecked
            val diasPlazo = if (esCredito) txtDiasPlazo.text.toString().toIntOrNull() ?: 0 else 0

            // Validar plazo si es crédito
            if (esCredito && diasPlazo <= 0) {
                Toast.makeText(this, "Debe ingresar los días de plazo para crédito", Toast.LENGTH_SHORT).show()
                return
            }

            // Calcular subtotal
            val subtotal = precioUnitario * cantidadComprada

            // Calcular descuento por cantidad
            val porcentajeDescuento = when {
                cantidadComprada in 1..3 -> 0.05  // 5%
                cantidadComprada in 4..11 -> 0.10 // 10%
                cantidadComprada >= 12 -> 0.15    // 15%
                else -> 0.0
            }

            val descuento = subtotal * porcentajeDescuento
            val subtotalConDescuento = subtotal - descuento

            // Calcular IVA (13% sobre subtotal con descuento)
            val iva = subtotalConDescuento * 0.13

            // Calcular total según método de pago
            var total = subtotalConDescuento + iva
            var recargo = 0.0

            // Si es crédito y el plazo es mayor a 30 días, aplicar recargo del 5%
            if (esCredito && diasPlazo > 30) {
                recargo = total * 0.05
                total += recargo
            }

            // Mostrar resultados
            mostrarResultados(
                nombreProducto, cantidadComprada, subtotal, descuento,
                porcentajeDescuento, iva, esCredito, diasPlazo, recargo, total
            )

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Error: Verifique que los valores numéricos sean correctos", Toast.LENGTH_LONG).show()
        }
    }

    private fun validarCampos(): Boolean {
        // Validar nombre del producto
        if (txtNombreProducto.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Debe ingresar el nombre del producto", Toast.LENGTH_SHORT).show()
            txtNombreProducto.requestFocus()
            return false
        }

        // Validar precio unitario
        if (txtPrecioUnitario.text.toString().isEmpty()) {
            Toast.makeText(this, "Debe ingresar el precio unitario", Toast.LENGTH_SHORT).show()
            txtPrecioUnitario.requestFocus()
            return false
        }

        try {
            val precio = txtPrecioUnitario.text.toString().toDouble()
            if (precio <= 0) {
                Toast.makeText(this, "El precio debe ser mayor a 0", Toast.LENGTH_SHORT).show()
                txtPrecioUnitario.requestFocus()
                return false
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Ingrese un precio válido", Toast.LENGTH_SHORT).show()
            txtPrecioUnitario.requestFocus()
            return false
        }

        // Validar cantidad comprada
        if (txtCantidadComprada.text.toString().isEmpty()) {
            Toast.makeText(this, "Debe ingresar la cantidad comprada", Toast.LENGTH_SHORT).show()
            txtCantidadComprada.requestFocus()
            return false
        }

        try {
            val cantidad = txtCantidadComprada.text.toString().toInt()
            if (cantidad <= 0) {
                Toast.makeText(this, "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show()
                txtCantidadComprada.requestFocus()
                return false
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show()
            txtCantidadComprada.requestFocus()
            return false
        }

        return true
    }

    private fun mostrarResultados(
        producto: String,
        cantidad: Int,
        subtotal: Double,
        descuento: Double,
        porcentajeDescuento: Double,
        iva: Double,
        esCredito: Boolean,
        diasPlazo: Int,
        recargo: Double,
        total: Double
    ) {
        val sb = StringBuilder()

        sb.append("📦 Producto: $producto\n\n")
        sb.append("📊 Cantidad: $cantidad unidades\n\n")
        sb.append("💰 Subtotal: $${String.format("%.2f", subtotal)}\n\n")
        sb.append("🎯 Descuento aplicado: $${String.format("%.2f", descuento)} (${(porcentajeDescuento * 100).toInt()}%)\n\n")
        sb.append("📋 IVA (13%): $${String.format("%.2f", iva)}\n\n")
        sb.append("💳 Método de pago: ${if (esCredito) "Crédito" else "Contado"}\n\n")

        if (esCredito) {
            sb.append("📅 Plazo: $diasPlazo días\n\n")
            if (recargo > 0) {
                sb.append("⚠️ Recargo (5%): $${String.format("%.2f", recargo)}\n\n")
            }
        }

        sb.append("💵 TOTAL A PAGAR: $${String.format("%.2f", total)}")

        lblResultados.text = sb.toString()
        layoutResultados.visibility = View.VISIBLE

        // Mostrar mensaje de éxito
        Toast.makeText(this, "Cálculo realizado exitosamente", Toast.LENGTH_SHORT).show()
    }
}