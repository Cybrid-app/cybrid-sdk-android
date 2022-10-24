package app.cybrid.sdkandroid.components.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import app.cybrid.sdkandroid.R
import app.cybrid.sdkandroid.components.KYCView
import app.cybrid.sdkandroid.components.kyc.view.IdentityVerificationViewModel
import com.withpersona.sdk2.inquiry.Environment
import com.withpersona.sdk2.inquiry.Inquiry
import com.withpersona.sdk2.inquiry.InquiryResponse

class KYCActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kycactivity)

        val identityVerificationViewModel: IdentityVerificationViewModel by viewModels()
        val kycView = findViewById<KYCView>(R.id.kycview)

        kycView.setViewModel(identityVerificationViewModel)
    }
}