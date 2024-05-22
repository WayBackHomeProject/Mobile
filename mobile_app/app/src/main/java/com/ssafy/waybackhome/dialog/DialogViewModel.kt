package com.ssafy.waybackhome.dialog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.waybackhome.data.tip.TipService
import kotlinx.coroutines.launch

private const val TAG = "DialogViewModel_싸피"
class DialogViewModel : ViewModel() {
    private var _safeTip = MutableLiveData<String>()
    val safeTip : LiveData<String> get() = _safeTip

    fun getSafeTip(){
        viewModelScope.launch {
            try{
                val result = TipService.api.getTip()
                _safeTip.value = result.answer
                Log.d(TAG, "getSafeTip: ${result.answer}")
            } catch(e : Exception) {
                _safeTip.value = "요청에 문제가 생겼습니다."
            }
        }
    }
}