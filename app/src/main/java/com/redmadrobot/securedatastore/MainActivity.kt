package com.redmadrobot.securedatastore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userDataStore: DataStore<User>

    private val userDataFlow: Flow<User> by lazy {
        userDataStore.data.catch { e ->
            if (e is IOException) {
                emit(User())
            } else {
                throw e
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        save_button.setOnClickListener {
            saveUserData()
        }

        load_button.setOnClickListener {
            loadStoredUserData()
        }
    }

    private fun loadStoredUserData() {
        lifecycleScope.launch {
            userDataFlow.collect {
                user_name.setText(it.name)
                user_password.setText(it.password)
            }
        }
    }

    private fun saveUserData() {
        lifecycleScope.launch {
            userDataStore.updateData { user ->
                user.copy(
                    name = user_name.text.toString(),
                    password = user_password.text.toString(),
                )
            }
        }.invokeOnCompletion {
            user_name.text.clear()
            user_password.text.clear()
        }
    }
}
