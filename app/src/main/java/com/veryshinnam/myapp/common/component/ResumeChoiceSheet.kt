package com.veryshinnam.myapp.common.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.veryshinnam.myapp.R

/**
 * 이어하기 선택 시트
 * : 끝내지 않은 동화가 있을 때 "이어서 만들기"와 "새로 만들기" 중 고르게 함
 *   (경고창 시트와 같은 주황 시트)
 *
 * - message: 안내 문구
 * - resumeText: 이어하기 버튼 문구
 * - newText: 새로 만들기 버튼 문구
 * - onResume: 이어하기 선택 시
 * - onNew: 새로 만들기 선택 시
 * - onDismiss: 시트를 닫을 때 (아무것도 선택하지 않음)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeChoiceSheet(
    message: String,
    resumeText: String,
    newText: String,
    onResume: () -> Unit,
    onNew: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null, // 손잡이 제거
        containerColor = colorResource(R.color.main_orange)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 이미지
            Image(
                painter = painterResource(R.drawable.img_speak_on),
                contentDescription = null, // 장식용
                modifier = Modifier.fillMaxWidth(0.2f),
                contentScale = ContentScale.Fit
            )

            // 안내 문구
            Text(
                text = message,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 1.2.em
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics { contentDescription = message.replace("\n", " ") }
            )

            Spacer(Modifier.height(4.dp))

            // 이어하기 (주요 선택)
            SheetChoiceButton(text = resumeText, isPrimary = true, onClick = onResume)

            // 새로 만들기
            SheetChoiceButton(text = newText, isPrimary = false, onClick = onNew)
        }
    }
}

@Composable
private fun SheetChoiceButton(
    text: String,
    isPrimary: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) colorResource(R.color.main_orange_50) else Color.White.copy(alpha = 0.35f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp), clip = false)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = if (isPrimary) Color.Black else Color.White,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(4.dp)
        )
    }
}
