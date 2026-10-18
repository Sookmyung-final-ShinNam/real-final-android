package com.veryshinnam.myapp.feature.classroom.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.feature.classroom.model.AssignmentData

/**
 * 학급 동화 과제 필터
 * : 보관함 성별 필터 버튼과 같은 파란 알약 모양. "전체" + "과제 1, 과제 2 ..."
 *   과제가 많으면 옆으로 스크롤
 *
 * - selectedAssignmentId: 선택된 과제 (null이면 전체)
 * - onSelect: 과제 클릭 시 (null이면 전체)
 */
@Composable
fun ClassroomFilterChips(
    assignments: List<AssignmentData>,
    selectedAssignmentId: Long?,
    onSelect: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            FilterChip(
                text = "전체",
                description = "전체 과제 동화",
                isSelected = selectedAssignmentId == null,
                onClick = { onSelect(null) }
            )
        }

        itemsIndexed(assignments, key = { _, item -> item.id }) { index, assignment ->
            FilterChip(
                text = "과제 ${index + 1}",
                description = "과제 ${index + 1}, ${assignment.title}",
                isSelected = selectedAssignmentId == assignment.id,
                onClick = { onSelect(assignment.id) }
            )
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // 버튼 및 텍스트 색상 (보관함 성별 필터와 동일)
    val backgroundColor = Brush.verticalGradient(
        colors = if (isSelected) {
            listOf(colorResource(R.color.blue_sky), colorResource(R.color.blue_gray))
        } else {
            listOf(Color.White, Color.White, colorResource(R.color.light_gray))
        }
    )

    Box(
        modifier = Modifier
            .background(backgroundColor, CircleShape)
            .border(2.dp, colorResource(R.color.blue_sky), CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .semantics(true) {
                contentDescription = description
                role = Role.RadioButton
                selected = isSelected
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color.Black
            ),
            modifier = Modifier.clearAndSetSemantics { }
        )
    }
}

// 과제 상태 필터
enum class AssignmentFilter(val label: String) {
    ALL("전체"),
    ONGOING("진행중"),  // 마감 전
    CLOSED("마감")      // 마감 지남
}

/**
 * 과제 상태 필터 (전체 / 진행중 / 마감)
 * : 보관함 성별 필터 버튼과 같은 파란 알약 모양
 */
@Composable
fun AssignmentFilterChips(
    selected: AssignmentFilter,
    onSelect: (AssignmentFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AssignmentFilter.entries.forEach { filter ->
            FilterChip(
                text = filter.label,
                description = "${filter.label} 과제",
                isSelected = filter == selected,
                onClick = { onSelect(filter) }
            )
        }
    }
}
