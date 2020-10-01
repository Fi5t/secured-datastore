package com.redmadrobot.securedatastore.di

import android.app.Application
import androidx.datastore.DataStore
import androidx.datastore.createDataStore
import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AesGcmKeyManager
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.redmadrobot.securedatastore.User
import com.redmadrobot.securedatastore.datastore.UserSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
class AppModule {
    companion object {
        private const val KEYSET_NAME = "master_keyset"
        private const val PREFERENCE_FILE = "master_key_preference"
        private const val MASTER_KEY_URI = "android-keystore://master_key"

        private const val DATASTORE_FILE = "user.pb"
    }

    @Singleton
    @Provides
    fun provideAead(application: Application): Aead {
        AeadConfig.register()

        return AndroidKeysetManager.Builder()
            .withSharedPref(application, KEYSET_NAME, PREFERENCE_FILE)
            .withKeyTemplate(AesGcmKeyManager.aes256GcmTemplate())
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
            .getPrimitive(Aead::class.java)
    }

    @Provides
    fun provideDataStore(application: Application, aead: Aead): DataStore<User> {
        return application.createDataStore(
            fileName = DATASTORE_FILE,
            serializer = UserSerializer(aead)
        )
    }
}
