package com.veryshinnam.myapp.feature.dashboard.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.veryshinnam.myapp.R

@Composable
fun DashboardParentCard(
    username: String,
    advice: String,
    spacer: Dp = 6.dp,
    horizontalPadding: Dp = 22.dp,
    verticalPadding: Dp = 18.dp,
    adviceTextStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = SemiBold),
    cardCorner: Dp = 16.dp,
    cardColor: Color = Color.White,
    borderWidth: Dp = 4.dp,
    borderColor: Color = colorResource(R.color.deep_green),
    imageWidth: Float = 0.25f,
    modifier: Modifier
) {
    var helpPressed by remember { mutableStateOf(false) }
    val helpText = """
        아이가 최근에 만든 동화 5개에 담긴 이야기 표현과 감정 흐름을 AI가 살펴보고, 부모님께 전해드리는 작은 조언이에요!
    """.trimIndent()

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = cardCorner),
        verticalArrangement = Arrangement.spacedBy(spacer)
    ) {
        Box(
            modifier = Modifier.zIndex(20f),
            contentAlignment = Alignment.BottomStart
        ) {
            Box {
                // 제목
                DashboardCardTitle(
                    title = "${username}의 부모님께",
                    borderColor = borderColor,
                    cardCorner = cardCorner,
                    spacer = imageWidth,
                    modifier = Modifier.fillMaxWidth()
                )

                // 원형 도움말
                DashboardHelpButton(
                    onPress = {pressed ->
                        helpPressed = pressed
                    },
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = spacer*2)
                        .semantics(true) {
                            contentDescription = helpText
                        }
                )
            }

            // 이미지
            Image(
                painter = painterResource(R.drawable.img_fox_full),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(imageWidth)
                    .graphicsLayer {
                        scaleX = 1.15f
                        scaleY = 1.15f
                        translationX = -8.dp.toPx() // 왼쪽으로 이동
                        translationY = 12.dp.toPx() // 아래로 이동
                    },
                contentScale = ContentScale.Fit
            )
        }

        // 부모 조언 카드
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(cardColor, shape = RoundedCornerShape(cardCorner))
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = RoundedCornerShape(cardCorner)
                )
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            ) {
                // 텍스트
            Text(
                text = advice,
                style = adviceTextStyle,
                modifier = Modifier.semantics {
                    contentDescription = "조언 내용: $advice"
                }
            )

                if (helpPressed) {
                    DashboardHelpText(
                        text = helpText,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

}