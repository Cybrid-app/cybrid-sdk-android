package app.cybrid.demoapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import app.cybrid.demoapp.core.App
import app.cybrid.demoapp.R
import app.cybrid.demoapp.listener.BearerListener
import app.cybrid.demoapp.ui.listComponents.ListComponentsActivity

class LoginActivity : AppCompatActivity(), BearerListener {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        App().getBearer(this)
    }

    override fun onBearerReady() {

        App().setupCybridSDK()
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, ListComponentsActivity::class.java))
        }, 2500L)

    }

    override fun onBearerError() {
        Toast.makeText(this, getString(R.string.auth_toast_error), Toast.LENGTH_LONG).show()
    }
}