package uk.ac.tees.mad.w9628563.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay
import uk.ac.tees.mad.w9628563.R

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    val animationProgress = remember {
        Animatable(0f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(300.dp))
            AnimatedLoader(
                modifier = Modifier
                    .size(250.dp)
                    .scale(animationProgress.value),
                animationResource = R.raw.book
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(animationProgress.value)
            )
            Text(
                text = "Your Gateway to Endless Stories",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(animationProgress.value)
            )
            Spacer(modifier = Modifier.height(50.dp))
        }
    }

    LaunchedEffect(key1 = true) {
        animationProgress.animateTo(1f, animationSpec = tween(1500))
        delay(2000L)
        onSplashComplete()
    }
}

@Composable
fun AnimatedLoader(modifier: Modifier, animationResource: Int) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(animationResource))

    LottieAnimation(
        composition = composition,
        modifier = modifier,
    )
}