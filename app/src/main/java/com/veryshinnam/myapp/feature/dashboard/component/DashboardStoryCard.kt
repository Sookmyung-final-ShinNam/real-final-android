package com.veryshinnam.myapp.feature.dashboard.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration.Companion.None
import androidx.compose.ui.text.style.TextDecoration.Companion.Underline
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.StrokeTitle
import com.veryshinnam.myapp.feature.dashboard.model.Attempt
import com.veryshinnam.myapp.feature.dashboard.model.StoryAnalysisData

@Composable
fun DashboardStoryCard(
    story: StoryAnalysisData,
    total: Int = 10,
    index: Int,
    onStoryClick: (Long) -> Unit,
    onHelpRect: (Rect) -> Unit,
    spacer: Dp = 6.dp,
    verticalPadding: Dp = 24.dp,
    cardCorner: Dp = 16.dp,
    cardColor: Color = Color.White,
    borderWidth: Dp = 4.dp,
    borderCorner: Dp = 16.dp,
    borderColor: Color = colorResource(R.color.deep_green),
    orangeColor: Color = colorResource(R.color.main_orange),
    lGreenColor: Color = colorResource(R.color.light_green),
    imageWidth: Float = 0.25f,
    subTitleTextStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = Bold),
    linkTextStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = orangeColor, fontWeight = SemiBold, textDecoration = Underline),
    summaryTextStyle: TextStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = SemiBold),
    modifier: Modifier
) {
    // 화면 밀도 정보
    val density = LocalDensity.current

    // 도움말 관련 변수
    var titlePressed by remember { mutableStateOf(false) }
    var attemptPressed by remember { mutableStateOf(false) }
    var wordPressed by remember { mutableStateOf(false) }
    var emotionPressed by remember { mutableStateOf(false) }
    var attemptTop by remember { mutableFloatStateOf(0f) }
    var wordTop by remember { mutableFloatStateOf(0f) }
    var emotionTop by remember { mutableFloatStateOf(0f) }
    var eHelpHeight by remember { mutableStateOf<Float?>(null) }
    val tHelpText = """
        최근에 만든 동화 10개를 바탕으로
        아이의 이야기 말하기와 감정 표현을 살펴볼 수 있어요.

        • 언어 분석
        동화는 '기-승-전-결' 네 단계로 만들어져요.
        각 단계에서 아이가 몇 번씩 시도했는지를 살펴보며,
        어떤 부분을 어려워하고, 어떤 부분을 잘하는지 알 수 있어요.
        또, 아이가 자주 사용하는 단어와 말하는 습관도 함께 볼 수 있어요.

        • 정서 분석
        AI가 동화 속 이야기에 담긴 아이의 감정을 분석해 기쁨, 슬픔, 화남, 두려움, 놀람, 평온 여섯 가지로 나누어 보여줘요.
    """.trimIndent()
    val aHelpText = """
        기가 어려웠다면,
        누가 나오고, 어디에서 이야기가 시작되는지 천천히 떠올려 보세요.

        승이 어려웠다면,
        그다음에 어떤 일이 일어났을지 이야기를 이어서 생각해 보세요.

        전이 어려웠다면,
        문제나 예상하지 못한 사건을 하나 더 추가해 보세요.

        결이 어려웠다면,
        이야기가 어떻게 끝나면 좋을지 차분히 정리해 보세요.
        
        다음 동화를 만들 때, 각 단계의 설명을 떠올리며 질문에 하나씩 답해 보세요!
    """.trimIndent()

    val wHelpText = """
        새 단어는 아이에게 익숙한 단어를 제외하고,
        동화를 만들면서 새롭게 사용한 단어만을 기준으로 해요.
        
        이를 통해 아이가 새로 배운 단어를 확인할 수 있어요.
    """.trimIndent()

    val eHelpText = """
        각 정서의 비율은 동화 속에서 사용된 말과 표현을 바탕으로 계산돼요. (총합 100%)
        
        • 기쁨: 즐겁고 행복한 느낌의 표현
        • 슬픔: 속상하거나 외로운 느낌의 표현
        • 화남: 짜증이나 화가 난 표현
        • 두려움: 걱정되거나 무서운 느낌의 표현
        • 놀람: 예상하지 못한 일에 대한 반응
        • 평온: 차분하고 안정된 느낌의 표현
    """.trimIndent()

    // ui 변수
    val scrollState = rememberScrollState()
    val textHeight = summaryTextStyle.lineHeight.value.dp * 6
    val barHeight = 180.dp
    var isMore by remember { mutableStateOf(false) }

    val colors = listOf(
        colorResource(R.color.emotion_1),
        colorResource(R.color.emotion_2),
        colorResource(R.color.emotion_3),
        colorResource(R.color.emotion_4),
        colorResource(R.color.emotion_5),
        lGreenColor
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = cardCorner),
        verticalArrangement = Arrangement.spacedBy(spacer)
    ) {

        Box(
            modifier = Modifier.zIndex(20f),
            contentAlignment = Alignment.BottomStart
        ) {
            Box {
                // 제목
                DashboardCardTitle(
                    title = "동화 언어 및 정서 분석",
                    borderColor = borderColor,
                    cardCorner = cardCorner,
                    spacer = imageWidth,
                    modifier = Modifier.fillMaxWidth()
                )
                // 원형 도움말
                DashboardHelpButton(
                    onPress = {pressed ->
                        titlePressed = pressed
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = spacer*2)
                        .onGloballyPositioned { coords ->
                            val rect = coords.boundsInRoot()
                            if (rect.width > 0 && rect.height > 0) {
                                onHelpRect(rect)
                            }
                        }
                        .semantics(true) {
                            contentDescription = tHelpText
                        }
                )
            }

            // 이미지
            Image(
                painter = painterResource(R.drawable.img_book),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(imageWidth)
                    .graphicsLayer {
                        scaleX = 1.3f
                        scaleY = 1.3f
                        translationX = -12.dp.toPx() // 왼쪽으로 이동
                        translationY = 22.dp.toPx() // 아래로 이동
                    },
                contentScale = ContentScale.Fit
            )
        }

        // 섹션 내용
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(cardColor, shape = RoundedCornerShape(cardCorner))
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = RoundedCornerShape(borderCorner)
                )
        ) {

            if (titlePressed) {
                Box(
                    modifier = Modifier
                        .zIndex(10f)
                        .padding(horizontal = cardCorner, vertical = verticalPadding)
                ) {
                    DashboardHelpText(
                        text = tHelpText,
                        modifier = Modifier.wrapContentHeight()
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = cardCorner, vertical = verticalPadding),
            ) {
                // -- 동화 제목
                Text(
                    text = story.storyTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = summaryTextStyle.copy(fontWeight = Bold),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .semantics {
                            contentDescription = "전체 ${total}개 중 ${index}번째 분석, 동화 제목: ${story.storyTitle}"
                        }
                )
                Spacer(Modifier.height(spacer))

                Text(
                    text = "동화 보러 가기",
                    modifier = Modifier.align(Alignment.End)
                        .clickable {
                            onStoryClick(story.storyId)
                        }
                        .semantics {
                            role = Role.Button
                        },
                    style = linkTextStyle
                )

                Spacer(Modifier.height(cardCorner*2)) // 간격

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = verticalPadding)
                ) {
                    if (attemptPressed) {
                        DashboardHelpText(
                            text = aHelpText,
                            modifier = Modifier
                                .absoluteOffset(
                                    y = with(density) { attemptTop.toDp() }
                                )
                                .wrapContentHeight()
                                .zIndex(10f)
                        )
                    }

                    Column{
                        // -- 1. 시도 횟수 영역
                        Box(modifier = Modifier.fillMaxWidth()) {
                            // 원형 도움말
                            DashboardHelpButton(
                                onPress = {pressed ->
                                    attemptPressed = pressed
                                },
                                modifier = Modifier.align(Alignment.TopEnd)
                                    .semantics(true) {
                                        contentDescription = aHelpText
                                    }
                            )

                            // 평균 값
                            Column{
                                Text("• 평균 시도 횟수: ${story.avgAttemptPerStage}회", style = subTitleTextStyle)
                                Spacer(Modifier.height(spacer))

                                // 기승전결
                                Column(
                                    modifier = Modifier.onGloballyPositioned { coords ->
                                        attemptTop = coords.positionInParent().y
                                    }
                                ) {
                                    Attempt.values().forEach { attempt ->
                                        DashboardAttemptRow(
                                            title = attempt.label,
                                            count = story.attempts[attempt] ?: 1,
                                            subTextStyle = summaryTextStyle
                                        )
                                    }
                                }
                            }
                        }

                        // -- 2. 새 단어 분석
                        Spacer(Modifier.height(verticalPadding*2))
                        Column {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .semantics {
                                    isTraversalGroup = true
                                }
                            ) {
                                // 원형 도움말
                                DashboardHelpButton(
                                    onPress = {pressed ->
                                        wordPressed = pressed
                                    },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                        .semantics(true) {
                                            traversalIndex = 1f
                                            contentDescription = wHelpText
                                        }
                                )

                                Column(
                                    Modifier.semantics(true) {
                                        traversalIndex = 0f
                                    }
                                ) {
                                    Text("• 평균 답변 길이: ${story.avgAnswerLength}자", style = subTitleTextStyle)
                                    Text("• 획득한 새 단어 개수: ${story.newWords.size}개", style = subTitleTextStyle)
                                }
                            }
                            Text(
                                text = if (isMore) "닫기" else "자세히 보기",
                                style = linkTextStyle,
                                modifier = Modifier.clickable { isMore = !isMore }
                                    .semantics {
                                        role = Role.Button
                                        stateDescription = if (isMore) "자세히 보기 창 열림. 획득한 새 단어를 확인해 보세요." else "자세히 보기 창 닫힘"
                                    }
                            )
                        }
                        Spacer(Modifier.height(spacer))

                        Box {
                            if (wordPressed) {
                                DashboardHelpText(
                                    text = wHelpText,
                                    modifier = Modifier
                                        .absoluteOffset(
                                            y = with(density) { wordTop.toDp() })
                                        .wrapContentHeight()
                                        .zIndex(20f)
                                )
                            }

                            if (isMore) {
                                Box (modifier = Modifier
                                    .matchParentSize()
                                    .onGloballyPositioned { coords ->
                                        wordTop = coords.positionInParent().y }
                                    .zIndex(10f)
                                    .border(
                                        width = 2.dp,
                                        color = borderColor,
                                        shape = RoundedCornerShape(cardCorner)
                                    )
                                    .background(lGreenColor, shape = RoundedCornerShape(cardCorner)))
                                {
                                    FlowRow(
                                        modifier = Modifier
                                            .verticalScroll(scrollState) // 스크롤
                                            .padding(8.dp, 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        story.newWords.forEach { word ->
                                            // 버블
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        color = Color.White,
                                                        shape = CircleShape
                                                    )
                                                    .border(
                                                        width = 2.dp,
                                                        color = borderColor,
                                                        shape = CircleShape
                                                    )
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                // 단어
                                                Text(
                                                    text = word,
                                                    style = linkTextStyle.copy(
                                                        textDecoration = None
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    // 스크롤 가능 여부 효과
                                    if (scrollState.canScrollForward) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp)
                                                .align(Alignment.BottomCenter)
                                                .background(
                                                    Brush.verticalGradient(
                                                        colors = listOf(
                                                            Color.Transparent,
                                                            Color.Gray.copy(alpha = 0.5f)
                                                        )
                                                    ),
                                                    shape = RoundedCornerShape(cardCorner)
                                                )
                                        )
                                    }
                                }
                            }

                            if (emotionPressed) {
                                DashboardHelpText(
                                    text = eHelpText,
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .onGloballyPositioned { coords ->
                                            eHelpHeight = coords.size.height.toFloat()
                                        }
                                        .absoluteOffset(
                                            y = with(density) {
                                                eHelpHeight?.let { (emotionTop - it).toDp() } ?: 0.dp
                                            }
                                        )
                                        .alpha(if (eHelpHeight != null) 1f else 0f)
                                        .zIndex(20f)
                                )
                            }

                            // -- 3. 정서 분석
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Spacer(Modifier.height(verticalPadding*2))

                                // 원형 도움말
                                DashboardHelpButton(
                                    onPress = {pressed ->
                                        emotionPressed = pressed
                                    },
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .onGloballyPositioned { coords ->
                                            emotionTop = coords.positionInParent().y }
                                        .semantics(true) {
                                            contentDescription = "정서 분석 비율 설명: $eHelpText"
                                        }
                                )

                                Box {
                                    // 정서 분석
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        // 막대 바
                                        Column(
                                            modifier = Modifier.semantics(true) {
                                                contentDescription = "해당 동화의 정서 분석 비율: ${story.emotionText}"
                                            }
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.Bottom
                                            ) {
                                                story.emotions.forEachIndexed { index, item ->
                                                    val barColor = colors[index]

                                                    BoxWithConstraints(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .height(barHeight)
                                                    ) {
                                                        val height = maxHeight * item.ratio // item별 막대 높이

                                                        // 퍼센트
                                                        if (item.ratio > 0) {
                                                            Text(
                                                                text = "${(item.ratio * 100).toInt()}%",
                                                                style = linkTextStyle.copy(color = barColor, textDecoration = None),
                                                                softWrap = false,
                                                                modifier = Modifier
                                                                    .align(Alignment.BottomCenter)
                                                                    .offset(y = -(height + spacer))
                                                                    .clearAndSetSemantics { },
                                                                textAlign = TextAlign.Center
                                                            )
                                                        }

                                                        // 막대 그래프
                                                        Box(
                                                            modifier = Modifier
                                                                .align(Alignment.BottomCenter)
                                                                .fillMaxWidth(0.6f)
                                                                .height(height)
                                                                .background(
                                                                    color = barColor ,
                                                                    shape = RoundedCornerShape(
                                                                        topStart = 4.dp,
                                                                        topEnd = 4.dp
                                                                    )
                                                                )
                                                        )
                                                    }
                                                }
                                            }

                                            // x축 선
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(2.dp)
                                                    .background(borderColor)
                                            )

                                            // x축
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                                verticalAlignment = Alignment.Bottom,
                                            ) {
                                                story.emotions.forEachIndexed { index, item ->
                                                    val barColor = colors[index]

                                                    StrokeTitle(
                                                        titleText = item.name,
                                                        titleColor = Color.White,
                                                        strokeColor = barColor,
                                                        titleTextStyle = linkTextStyle.copy(textDecoration = None),
                                                        strokeWidth = 10f,
                                                        softWrap = false,
                                                        modifier = Modifier
                                                            .graphicsLayer { rotationZ = -30f }
                                                            .weight(1f)
                                                            .align(Alignment.CenterVertically)
                                                            .clearAndSetSemantics { }
                                                    )
                                                }
                                            }
                                        }

                                        // 요약 및 날짜
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = cardCorner)
                                                .height(textHeight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = story.summary + "\n\n" + story.createdAt,
                                                style = subTitleTextStyle.copy(
                                                    fontWeight = SemiBold
                                                ), textAlign = TextAlign.Center,
                                                modifier = Modifier.semantics(true) {
                                                    contentDescription = "해당 동화의 정서 분석 요약: ${story.summary}"
                                                }
                                            )
                                        }

                                        // 인덱스
                                        Row(verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clearAndSetSemantics { }
                                        ) {
                                            Text(
                                                "$index"
                                                , style = summaryTextStyle.copy(color = borderColor)
                                            )
                                            Text(
                                                " / $total",
                                                color = borderColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}