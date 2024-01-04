package com.example.hokiemoneymanager

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.vt.cs3714.retrofitrecyclerviewguide.RetrofitService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal
import java.math.RoundingMode

class ViewModel : ViewModel() {
    private val apiKey = "c73d9c228b7b09ff34329d3e95fe8f77"
    private val apiBaseUrl = "http://data.fixer.io/api/" // latest?access_key=

    private val _locationEnabled = MutableLiveData<Boolean>()
    val locationEnabled: LiveData<Boolean> = _locationEnabled

    private val _countryName: MutableLiveData<String> = MutableLiveData()
    var countryName: LiveData<String> = _countryName

    private val _countrySymbol: MutableLiveData<String> = MutableLiveData()
    var countrySymbol: LiveData<String> = _countrySymbol

    private val _currency: MutableLiveData<BigDecimal> = MutableLiveData()
    val currency: LiveData<BigDecimal> = _currency

    private val _exchangeRate: MutableLiveData<BigDecimal> = MutableLiveData()
    val exchangeRate: LiveData<BigDecimal> /*get()*/ = _exchangeRate

    private val currencySymbol: MutableLiveData<String> = MutableLiveData()

    private val _balance: MutableLiveData<BigDecimal> = MutableLiveData()
    var balance: LiveData<BigDecimal> /*get()*/ = _balance

    private val _incomeTotal: MutableLiveData<Double> = MutableLiveData()
    var incomeTotal: LiveData<Double> = _incomeTotal

    private val _expensesTotal: MutableLiveData<Double> = MutableLiveData()
    var expensesTotal: LiveData<Double> = _expensesTotal

    private val _spinnerPosition: MutableLiveData<Int> = MutableLiveData()
    var spinnerPosition: LiveData<Int> /*get()*/ = _spinnerPosition

    private var disposable: Disposable? = null

    init {
        setExchangeRate("USD")
        setCurrencySymbol("USD")
    }

    /**
     * Return string with 2 decimal places
     */
    fun formatCurrency(inputCurrency: BigDecimal): String {
        return inputCurrency.setScale(2, RoundingMode.HALF_UP).toString()
    }

    fun setCountryName(countryName: String) {
        _countryName.value = countryName
    }

    fun setBalance(balance: BigDecimal) {
        _balance.value = balance
    }

    fun setCurrencySymbol(value: String) {
        _countrySymbol.value = value
    }

    fun setSpinnerPosition(value: Int) {
        _spinnerPosition.value = value
    }
    fun setIncomeTotal(value: Double) {
        _incomeTotal.value = value
    }

    fun setExpensesTotal(value: Double) {
        _expensesTotal.value = value
    }

    fun setExchangeRate(countrySymbol: String) {
        disposable =
            RetrofitService.create(apiBaseUrl).getCurrencyExchangeRate(apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ currency ->
                    Log.d("ViewModel.kt currency (gson)", currency.toString())
                    val rate = currency.rates[countrySymbol]
                    if (rate != null) {
                        _exchangeRate.value = rate!!

                        Log.d("API test", _exchangeRate.value.toString())
                    } else {
                        println("Rate for currency $countrySymbol not found.")
                    }
                }, { error ->
                    Log.e("API Error", "Error fetching currency exchange rate", error)
                })
    }
}



