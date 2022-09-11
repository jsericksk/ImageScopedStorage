package com.kproject.imagescopedstorage.presentation.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val context by lazy {
        application.applicationContext
    }


}