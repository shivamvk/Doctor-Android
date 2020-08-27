package ai.mindful.doctor.di

import ai.mindful.doctor.di.AppComponent
import ai.mindful.doctor.di.DaggerAppComponent
import android.app.Application
import android.widget.Toast
import io.shivamvk.networklibrary.NetworkModule


class DoctorApplication : Application() {
    lateinit var dagger: AppComponent

    override fun onCreate() {
        super.onCreate()
        initDagger()
    }

    fun initDagger() {
        dagger = DaggerAppComponent.builder().networkModule(
            NetworkModule(applicationContext)
        ).build()
    }

    fun getDeps(): AppComponent {
        return dagger!!
    }
}