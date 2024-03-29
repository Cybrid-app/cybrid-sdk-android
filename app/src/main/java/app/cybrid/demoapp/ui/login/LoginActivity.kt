package app.cybrid.demoapp.ui.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat.animate
import app.cybrid.demoapp.BuildConfig
import app.cybrid.demoapp.core.App
import app.cybrid.demoapp.R
import app.cybrid.demoapp.api.auth.entity.TokenRequest
import app.cybrid.demoapp.listener.BearerListener
import app.cybrid.demoapp.ui.listComponents.ListComponentsActivity
import app.cybrid.sdkandroid.Cybrid
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        this.initViews()
    }

    private fun initViews() {

        val image = findViewById<ImageView>(R.id.image)
        val clientIDLayout = findViewById<TextInputLayout>(R.id.clientIDLayout)
        val clientSecretLayout = findViewById<TextInputLayout>(R.id.clientSecretLayout)
        val clientGUIDLayout = findViewById<TextInputLayout>(R.id.clientGUIDLayout)
        val login = findViewById<TextView>(R.id.login)
        val demo = findViewById<TextView>(R.id.demo)
        demo.alpha = 1.0f

        image.animate().translationY(-1000f).setDuration(1800).setListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {

                clientIDLayout.animate().alpha(1.0f).setDuration(1200).start()
                clientSecretLayout.animate().alpha(1.0f).setDuration(1200).start()
                clientGUIDLayout.animate().alpha(1.0f).setDuration(1200).start()
                login.animate().alpha(1.0f).setDuration(1200).start()
                demo.animate().alpha(1.0f).setDuration(1200).start()
            }
        }).start()

        login.setOnClickListener {

            val clientId = findViewById<EditText>(R.id.clientID).text
            val clientSecret = findViewById<EditText>(R.id.clientSecret).text
            val customerGuid = findViewById<EditText>(R.id.clientGUID).text

            if (clientId.isEmpty() && clientSecret.isEmpty() && customerGuid.isEmpty()) {
                Toast.makeText(this, getString(R.string.login_fill_error), Toast.LENGTH_LONG).show()
            } else {

                setupCybridSDK(
                    clientId = clientId.toString(),
                    clientSecret = clientSecret.toString(),
                    customerGuid = customerGuid.toString()
                )
                hideViews()
            }
        }

        demo.setOnClickListener {

            setupCybridSDK(
                clientId = BuildConfig.CLIENT_ID,
                clientSecret = BuildConfig.CLIENT_SECRET,
                customerGuid = BuildConfig.CUSTOMER_GUID
            )
            hideViews()
        }
    }

    private fun hideViews() {

        val clientIDLayout = findViewById<TextInputLayout>(R.id.clientIDLayout)
        val clientSecretLayout = findViewById<TextInputLayout>(R.id.clientSecretLayout)
        val clientGUIDLayout = findViewById<TextInputLayout>(R.id.clientGUIDLayout)
        val login = findViewById<TextView>(R.id.login)
        val demo = findViewById<TextView>(R.id.demo)
        val loader = findViewById<ConstraintLayout>(R.id.loader)

        clientIDLayout.visibility = View.INVISIBLE
        clientSecretLayout.visibility = View.INVISIBLE
        clientGUIDLayout.visibility = View.INVISIBLE
        login.visibility = View.INVISIBLE
        demo.visibility = View.INVISIBLE

        loader.visibility = View.VISIBLE
    }

    private fun showViews(errorMessage: String) {

        val clientIDLayout = findViewById<TextInputLayout>(R.id.clientIDLayout)
        val clientSecretLayout = findViewById<TextInputLayout>(R.id.clientSecretLayout)
        val clientGUIDLayout = findViewById<TextInputLayout>(R.id.clientGUIDLayout)
        val login = findViewById<TextView>(R.id.login)
        val demo = findViewById<TextView>(R.id.demo)
        val loader = findViewById<ConstraintLayout>(R.id.loader)

        clientIDLayout.visibility = View.VISIBLE
        clientSecretLayout.visibility = View.VISIBLE
        clientGUIDLayout.visibility = View.VISIBLE
        login.visibility = View.VISIBLE
        demo.visibility = View.VISIBLE

        loader.visibility = View.INVISIBLE
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun setupCybridSDK(clientId: String, clientSecret: String, customerGuid: String) {

        val tokenRequest = TokenRequest(
            client_id = clientId,
            client_secret = clientSecret
        )
        App().getSDKConfig(tokenRequest, customerGuid) { config ->
            Cybrid.setup(sdkConfig = config) {
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, ListComponentsActivity::class.java))
                    finish()
                }, 1000L)
            }
        }
    }
}