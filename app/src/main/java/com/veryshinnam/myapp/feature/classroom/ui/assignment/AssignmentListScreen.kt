package com.veryshinnam.myapp.feature.classroom.ui.assignment

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.BackButton
import com.veryshinnam.myapp.common.component.CircleButton
import com.veryshinnam.myapp.common.component.LoadErrorView
import com.veryshinnam.myapp.common.component.LogoBar
import com.veryshinnam.myapp.feature.classroom.data.model.AssignmentData
import com.veryshinnam.myapp.core.orientation.OrientationManager

@Composable
fun AssignmentListScreen(
    classroomId: Long,
    onBack: () -> Unit,
    onStartAssignment: (Long) -> Unit,
    vm: AssignmentListViewModel = hiltViewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    SideEffect { OrientationManager.setOrientation?.invoke(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) }

    Scaffold(
        containerColor = colorResource(R.color.background_yellow),
        topBar = {
            Column {
                Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                LogoBar()
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is AssignmentListUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = colorResource(R.color.main_orange), trackColor = Color.Gray.copy(0.3f))
                }
                is AssignmentListUiState.Error -> LoadErrorView(message = state.message, onRetry = { vm.reload() })
                is AssignmentListUiState.Success -> {
                    if (state.assignments.isEmpty()) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Text(
                                "아직 등록된 과제가 없어요.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item { Spacer(Modifier.height(8.dp)) }
                            item {
                                Text(
                                    "과제 목록",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = colorResource(R.color.main_orange)
                                )
                            }
                            itemsIndexed(state.assignments) { index, assignment ->
                                AssignmentCard(
                                    week = index + 1,
                                    assignment = assignment,
                                    onStart = { onStartAssignment(assignment.assignmentId) }
                                )
                            }
                            item { Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)) }
                        }
                    }
                }
            }

            BackButton(modifier = Modifier.align(Alignment.TopStart).padding(start = 16.dp, top = 8.dp), onBackClick = onBack)
        }
    }
}

@Composable
private fun AssignmentCard(week: Int, assignment: AssignmentData, onStart: () -> Unit) {
    val dDay = assignment.dDay
    val dDayText = when {
        dDay > 0 -> "D-$dDay"
        dDay == 0L -> "D-Day"
        else -> "마감됨"
    }
    val dDayColor = when {
        dDay > 3 -> colorResource(R.color.main_orange)
        dDay >= 0 -> Color(0xFFE53935)
        else -> Color.Gray
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(2.dp, colorResource(R.color.main_orange), RoundedCornerShape(16.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .background(colorResource(R.color.main_orange_50), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("${week}주차", style = MaterialTheme.typography.labelSmall.copy(color = colorResource(R.color.main_orange)))
                }
                Text(assignment.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
            Text(dDayText, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = dDayColor))
        }

        if (!assignment.description.isNullOrBlank()) {
            Text(
                assignment.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
                maxLines = 2
            )
        }

        Text(
            "마감: ${assignment.dueAt.take(10)}",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )

        Spacer(Modifier.height(4.dp))

        CircleButton(
            text = if (dDay < 0) "마감된 과제" else "동화 만들기 시작",
            onClick = { if (dDay >= 0) onStart() },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
