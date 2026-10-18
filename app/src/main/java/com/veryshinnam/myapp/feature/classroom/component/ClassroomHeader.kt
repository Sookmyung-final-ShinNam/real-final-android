package com.veryshinnam.myapp.feature.classroom.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.UserItem

/**
 * 학급 화면 상단
 * : 홈 화면과 같은 구성 (다람쥐 이미지 + 흰색 메시지 박스)
 *
 * - message: 메시지 박스 문구
 * - acornCount: 값이 있으면 학급 도토리 수를 다람쥐 옆에 표시 (선생님 학급 화면)
 */
@Composable
fun ClassroomHeader(
    message: String,
    modifier: Modifier = Modifier,
    acornCount: Int? = null,
    messageBorder: Dp = 4.dp,
    messageCorner: Dp = 16.dp,
    messagePadding: Dp = 20.dp,
    bottomPadding: Dp = 10.dp
) {
    // 이미지 높이: 기기 높이 * 0.12 (홈 화면과 동일)
    val imageHeight = (LocalConfiguration.current.screenHeightDp * 0.12f).dp

    Column(modifier = modifier) {
        // 다람쥐 이미지 + 도토리 수
        Box(modifier = Modifier.fillMaxWidth().height(imageHeight)) {
            Image(
                painter = painterResource(R.drawable.img_home_squirrel),
                contentDescription = null, // 장식용 - 대체 텍스트 제거
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.BottomStart)
                    .padding(start = 26.dp),
                contentScale = ContentScale.Fit
            )

            if (acornCount != null) {
                UserItem(
                    painter = painterResource(R.drawable.ic_dotory),
                    contentDesc = "학급 도토리 수",
                    value = "$acornCount",
                    color = colorResource(R.color.main_orange),
                    modifier = Modifier
                        .padding(bottom = bottomPadding)
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(0.3f)
                        .fillMaxHeight(0.6f)
                )
            }
        }

        // 메시지 박스
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White, shape = RoundedCornerShape(messageCorner))
                .border(
                    messageBorder,
                    colorResource(R.color.main_orange),
                    RoundedCornerShape(messageCorner)
                )
                .padding(all = messagePadding)
                .semantics { contentDescription = "안내 메시지, $message" },
            contentAlignment = Alignment.TopStart
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
