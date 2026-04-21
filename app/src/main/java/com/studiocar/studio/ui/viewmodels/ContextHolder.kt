package com.studiocar.studio.ui.viewmodels

import android.content.Context
import android.annotation.SuppressLint

/**
 * Utilitário temporário para prover contexto aos ViewModels (padrão StudioCar Elite).
 */
@SuppressLint("StaticFieldLeak")
object ContextHolder {
    lateinit var context: Context
}
