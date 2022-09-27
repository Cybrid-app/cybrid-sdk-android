package app.cybrid.sdkandroid.components.persona

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.cybrid.sdkandroid.R
import com.withpersona.sdk2.inquiry.Environment
import com.withpersona.sdk2.inquiry.Inquiry
import com.withpersona.sdk2.inquiry.InquiryResponse

class PersonaIntegration : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_persona_integration)

        // --
        val getInquiryResult = registerForActivityResult(Inquiry.Contract()) { result ->
                when (result) {
                    is InquiryResponse.Complete -> {}
                    is InquiryResponse.Cancel -> {}
                    is InquiryResponse.Error -> {}
                }
            }
        val TEMPLATE_ID = "itmpl_ArgEXWw8ZYtYLvfC26tr9zmY"
        val inquiry = Inquiry.fromTemplate(TEMPLATE_ID)
            .environment(Environment.SANDBOX)
            .build()
        getInquiryResult.launch(inquiry)
    }
}