package core

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import font

@Composable
fun createTypography(): Typography {
    val montserratReg = FontFamily(
        font("Montserrat", "montserrat_regular", "ttf", FontWeight.Normal, FontStyle.Normal)
    )
    val montserratBold = FontFamily(
        font("Montserrat", "montserrat_bold", "ttf", FontWeight.Normal, FontStyle.Normal)
    )
    val handmade = FontFamily(
        font("Handmade", "handmade", "otf", FontWeight.Bold, FontStyle.Normal)
    )

    return Typography(
        h6 = TextStyle(
            fontFamily = montserratBold,
            fontSize = 20.sp,
        ),
        subtitle1 = TextStyle(
            fontFamily = montserratReg,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
        ),
        subtitle2 = TextStyle(
            fontFamily = montserratReg,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
        ),
        body1 = TextStyle(
            fontFamily = handmade,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
        ),
        body2 = TextStyle(
            fontFamily = handmade,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
        ),
        caption = TextStyle(
            fontFamily = montserratBold,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
        )
    )
}
