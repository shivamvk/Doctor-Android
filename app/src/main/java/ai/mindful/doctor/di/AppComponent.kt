package ai.mindful.doctor.di

import ai.mindful.doctor.EditProfileActivity
import ai.mindful.doctor.LoginActivity
import ai.mindful.doctor.MainActivity
import ai.mindful.doctor.SplashActivity
import dagger.Component
import io.shivamvk.networklibrary.NetworkModule
import javax.inject.Singleton

@Component(modules = [NetworkModule::class])
@Singleton
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(activity: LoginActivity)
    fun inject(activity: SplashActivity)
    fun inject(activity: EditProfileActivity)
    //create fun inject() for all the activities, fragments etc using dagger
}