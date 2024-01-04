package edu.vt.cs3714.tutorial_05

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _audioFileName = MutableLiveData<String>()
    val audioFileName: LiveData<String> = _audioFileName
    private val _soundtrackPic = MutableLiveData<Int>()
    val soundtrackPic: LiveData<Int> = _soundtrackPic
    private val _shouldRemoveCallback = MutableLiveData<Boolean>()
    val shouldRemoveCallback: LiveData<Boolean> = _shouldRemoveCallback
    private val _mainTrack = MutableLiveData<Int>()
    val mainTrack: LiveData<Int> = _mainTrack

    fun setAudioFileName(fileName: String) {
        _audioFileName.value = fileName
    }

    fun setSoundtrackPic(pic: Int) {
        _soundtrackPic.value = pic
    }

    fun requestToRemoveCallback() {
        _shouldRemoveCallback.value = true
    }

    fun callbackRemoved() {
        _shouldRemoveCallback.value = false
    }

    fun mainTrackSelected(track: Int) {
        _mainTrack.value = track
    }
}
