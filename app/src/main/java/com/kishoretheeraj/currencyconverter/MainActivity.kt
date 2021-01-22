package com.kishoretheeraj.currencyconverter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    var baseCurrency = "EUR"
    var convertedToCurrency = "USD"
    var conversionRate = 0f
    private lateinit var spinner_firstConversion: Spinner
    private lateinit var spinner_secondConversion: Spinner
    private lateinit var et_firstConversion: EditText
    private lateinit var et_secondConversion: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        et_firstConversion = findViewById(R.id.et_firstConversion)
        et_secondConversion = findViewById(R.id.et_secondConversion)
        spinner_firstConversion = findViewById(R.id.spinner_firstConversion)
        spinner_secondConversion = findViewById(R.id.spinner_secondConversion)

        spinnerCreation()
        textChange()

    }

    private fun textChange() {

        et_firstConversion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    getapiResult()
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, "Type a value", Toast.LENGTH_SHORT).show()
                    Log.e("main", "$e")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("Main", "Before Text Changed")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("Main", "OnTextChanged")
            }


        })

    }

    private fun getapiResult() {

        if (et_firstConversion.text.isNotEmpty() && et_firstConversion.text.isNotBlank()) {
            val API = "https://api.ratesapi.io/api/latest?base=$baseCurrency&symbols=$convertedToCurrency"

            if (baseCurrency == convertedToCurrency) {
                Toast.makeText(applicationContext, "Same Currency cannot be Converted", Toast.LENGTH_SHORT).show()
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val apiResult = URL(API).readText()
                        val jsonObject = JSONObject(apiResult)

                        conversionRate = jsonObject.getJSONObject("rates").getString(convertedToCurrency).toFloat()

                        withContext(Dispatchers.Main) {
                            val text = ((et_firstConversion.text.toString().toFloat()) * conversionRate).toString()
                            et_secondConversion.setText(text)
                        }

                    } catch (e: Exception) {
                        Log.e("main", "$e")
                    }
                }
            }
        }
    }

    private fun spinnerCreation() {

        ArrayAdapter.createFromResource(this, R.array.currencies, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_firstConversion.adapter = adapter
        }
        ArrayAdapter.createFromResource(this, R.array.currencies2, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_secondConversion.adapter = adapter
        }

        spinner_firstConversion.onItemSelectedListener = (object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {


            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                baseCurrency = parent?.getItemAtPosition(position).toString()
                getapiResult()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                TODO("Not yet implemented")
            }

        })

        spinner_secondConversion.onItemSelectedListener = (object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                convertedToCurrency = parent?.getItemAtPosition(position).toString()
                getapiResult()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })

    }

}