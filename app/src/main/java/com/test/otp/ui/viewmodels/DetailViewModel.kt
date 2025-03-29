package com.test.otp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.otp.BuildConfig
import com.test.otp.network.FlickrServiceI
import com.test.otp.network.Info
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val service: FlickrServiceI
) : ViewModel() {

    private val _photoInfo = MutableStateFlow<Info?>(null)
    val photoInfo: StateFlow<Info?> = _photoInfo.asStateFlow()
    private fun updatePhotoInfo(info: Info) = _photoInfo.update {
        info
    }

    fun startDownloadInfo(id : String){
        viewModelScope.launch {
            getInfo(id)
        }
    }

    private suspend fun getInfo(id : String){
        val data = service.flickrRepository.getInfo(BuildConfig.API_KEY, id)
        updatePhotoInfo(data.photo)
    }

}