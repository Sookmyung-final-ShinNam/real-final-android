package com.veryshinnam.myapp.feature.classroom.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.veryshinnam.myapp.R

/**
 * 주황 채움 메뉴 카드
 * : 왼쪽 흰색 동그라미 아이콘 + 흰색 글씨 (학급 목록, 새 학급/과제 추가 카드에서 사용)
 *
 * - title: 카드 제목
 * - subtitle: 제목 아래 보조 문구 (선택)
 * - icon: 동그라미 안 아이콘 (학급: School, 추가: Add)
 * - description: 접근성 설명 (없으면 title + subtitle)
 * - onClick: 카드 클릭 시
 */
@Composable
fun ClassroomMenuCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector = Icons.Filled.School,
    description: String = title + (subtitle?.let { ", $it" } ?: ""),
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.main_orange)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 클릭 영역 (trailing은 따로 클릭되도록 제외)
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onClick)
                .semantics {
                    contentDescription = description
                    role = Role.Button
                }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 흰색 동그라미 아이콘
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null, // 장식용
                    tint = colorResource(R.color.main_orange),
                    modifier = Modifier.size(30.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        trailing?.invoke()
    }
}

// "새로운 ○○ 만들기/가입하기" 추가 카드 (+ 아이콘)
@Composable
fun ClassroomAddCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ClassroomMenuCard(
        title = title,
        onClick = onClick,
        icon = Icons.Filled.Add,
        modifier = modifier
    )
}

/**
 * 주황 정사각 메뉴 버튼
 * : 학급 화면 메뉴 (동화 모두 보기, 학급 도토리 충전, 과제 관리 등)
 */
@Composable
fun ClassroomSquareButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.main_orange))
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = text.replace("\n", " ")
                role = Role.Button
            }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        )
    }
}

/**
 * 보관함 아이콘 버튼
 * : 홈 화면 하단의 보관함 버튼과 같은 이미지 (학급 동화를 모아 보는 학급 보관함으로 이동)
 */
@Composable
fun ClassroomCollectionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(R.drawable.img_bottom_collection),
        contentDescription = null, // 대체 텍스트는 아래 semantics에서 설정
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = "학급 보관함"
                role = Role.Button
            }
    )
}
