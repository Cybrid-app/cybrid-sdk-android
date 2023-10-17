package app.cybrid.sdkandroid.ui.lib

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cybrid.sdkandroid.R

@Composable
fun ContinueButton(
    modifier: Modifier,
    text: String
) {
    Row(
        modifier = modifier
            .background(color = colorResource(id = R.color.primary_color), shape = RoundedCornerShape(size = 10.dp)),
            //.padding(start = 20.dp, top = 13.dp, end = 20.dp, bottom = 13.dp)
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        // -- Text
        Text(
            text = text,
            style = TextStyle(
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(300),
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        )
        // -- Icon
        Image(
            modifier = Modifier
                .padding(1.dp)
                .width(11.6.dp)
                .height(11.3.dp),
            painter = painterResource(id = R.drawable.ic_continue_button),
            contentDescription = "image description",
            contentScale = ContentScale.None
        )

    }
}