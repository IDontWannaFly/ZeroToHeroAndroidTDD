package ru.easycode.zerotoheroandroidtdd.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.easycode.zerotoheroandroidtdd.ui.theme.ZeroToHeroAndroidTDDTheme

@Composable
internal fun MainButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) = Button(
    modifier = modifier,
    onClick = onClick,
    content = {
        Text(
            text = text,
        )
    }
)

@Preview
@Composable
private fun MainButtonPreview() = ZeroToHeroAndroidTDDTheme {
    MainButton(
        modifier = Modifier,
        text = "Sample",
        onClick = {  }
    )
}