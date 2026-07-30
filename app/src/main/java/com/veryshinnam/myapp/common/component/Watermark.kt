package com.veryshinnam.myapp.common.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.veryshinnam.myapp.R

/**
 * AI 생성물 의무 표시제를 위한 워터 마크
 * - 아이콘 + 텍스트 조합
 */
@Composable
fun Watermark(
    text: String = "AI 생성 캐릭터",
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
    modifier: Modifier
) {
    // -- 아이콘 + 텍스트
    Row(
        modifier = modifier
            .fillMaxHeight()
            .alpha(0.65f)
            .background(
                color = colorResource(R.color.main_orange),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- 아이콘
        Image(
            painter = painterResource(id = R.drawable.ic_dotory_white),
            contentDescription = null,
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 2.dp),
            contentScale = ContentScale.FillHeight // 높이 맞게 이미지 채움
        )

        Spacer(modifier = Modifier.width(4.dp))

        // --- 텍스트
        Text(
            text = text,
            style = textStyle.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            maxLines = 1,
            modifier = Modifier.clearAndSetSemantics { }
        )
    }
}