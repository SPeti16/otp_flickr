package com.test.otp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.otp.BuildConfig
import com.test.otp.network.FlickrServiceI
import com.test.otp.network.NetworkCategory
import com.test.otp.network.NetworkMonitoring
import com.test.otp.network.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.test.otp.db.DbClient

@HiltViewModel
class MainViewModel @Inject constructor(
    private val networkMonitoring: NetworkMonitoring,
    private val service: FlickrServiceI,
    private val db: DbClient
) : ViewModel() {

    private val emptyList = listOf<Photo>()
    private val _allPhoto = MutableStateFlow(emptyList)
    val allPhoto: StateFlow<List<Photo>> = _allPhoto.asStateFlow()
    private fun updateAllPhoto(photo: List<Photo>) = _allPhoto.update { currentList ->
        currentList + photo
    }
    private fun clearAllPhoto() = _allPhoto.update {
        emptyList
    }

    var total = 0

    private val _search = MutableStateFlow("")
    val search: StateFlow<String> get() = _search
    private fun updateSearch(s: String) = _search.update {
        s
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    private fun updateLoading(loading: Boolean) = _isLoading.update {
        loading
    }

    private val _isConnected = MutableStateFlow(NetworkCategory.LOADING)
    val isConnected: StateFlow<NetworkCategory> get() = _isConnected


    init {
        startDataDownload()
    }

    fun startDataDownload(){
        _isConnected.value = networkMonitoring.isConnectedToWifiOrMobileData()
        if(isConnected.value == NetworkCategory.WIFI || isConnected.value == NetworkCategory.CELLULAR){
            viewModelScope.launch {
                updateSearch(db.getSearch())
                getApiData(_search.value, 1)
            }
        }
    }

    private suspend fun getApiData(search : String, page: Int){
        updateLoading(true)
        val data = service.flickrRepository.getData(BuildConfig.API_KEY, search, page)
        total = data.photos.total
        updateAllPhoto(data.photos.photo)
        updateLoading(false)
    }

    fun search(page: Int){
        viewModelScope.launch {
            getApiData(_search.value, page)
        }
    }

    fun changeSearch(s: String){
        updateSearch(s)
        clearAllPhoto()
        viewModelScope.launch {
            db.putSearch(s)
            getApiData(s, 1)
        }
    }

}