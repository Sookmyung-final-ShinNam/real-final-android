package com.veryshinnam.myapp.feature.permit.ui

import android.content.pm.ActivityInfo
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.veryshinnam.myapp.R
import com.veryshinnam.myapp.common.component.LogoBar
import com.veryshinnam.myapp.core.orientation.OrientationManager

@Composable
fun SignUpScreen(
    tempCode: String,
    onHome: () -> Unit,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 6.dp,
    titleTextStyle: TextStyle = MaterialTheme.typography.titleSmall.copy(fontWeight = Bold),
    subTextStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = SemiBold),
    descTextStyle: TextStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = SemiBold),
    vm: PermitViewModel = hiltViewModel(),
) {
    var checked by remember { mutableStateOf(false) }
    val state by vm.permitUiState.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    // 세로 모드 고정
    SideEffect {
        OrientationManager.setOrientation?.invoke(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        )
    }

    // 로그인 결과 감지
    LaunchedEffect(state) {
        if (state is PermitUiState.User) {
            onHome()
        }
    }

    Scaffold(
        containerColor = colorResource(id = R.color.background_yellow),
        topBar = {
            // 상태바 만큼 여백 & 상단 로고
            Column {
                Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                LogoBar(modifier = Modifier.clearAndSetSemantics { })
            }
        },
        bottomBar = {
            // 네비게이션바 만큼 여백
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = horizontalPadding, start = horizontalPadding, end = horizontalPadding),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(verticalPadding)
            ) {

                // 제목
                Text(
                    text = "스토릭터(Storictor) 개인정보처리방침",
                    style = titleTextStyle,
                    modifier = Modifier
                        .semantics {
                            contentDescription = "스토릭터 개인정보 처리방침"
                        }
                )

                // 부설명
                Text(
                    text = """스토릭터는 2025년 현대오토에버 배리어프리 앱 개발 공모전의 지원을 받아 개발 중이며, 서비스는 무료로 제공됩니다."""
                        .trimIndent().replace("", "\u200B"),
                    style = descTextStyle,
                    modifier = Modifier
                        .semantics {
                            contentDescription = "스토릭터는 2025년 현대오토에버 배리어프리 앱 개발 공모전의 지원을 받아 개발 중이며, 서비스는 무료로 제공됩니다."
                        }
                )

                Spacer(Modifier.height(verticalPadding))

                // 이용약관
                Column(modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = verticalPadding*2)
                ){
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = Color.White, shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                border = BorderStroke(2.dp, colorResource(R.color.main_orange)),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .verticalScroll(scrollState)
                            .padding(verticalPadding*2),
                        verticalArrangement = Arrangement.spacedBy(verticalPadding*2)
                    ) {
                        Text(
                            text = """
                    본 개인정보처리방침은 스토릭터(Storictor) 앱(이하 “앱”)이 제공하는 서비스 이용과 관련하여 사용자의 개인정보 및 민감한 정보를 어떻게 수집·이용·보관·삭제·보호하는지 설명합니다.
                    스토릭터는 유치원생 및 초등학교 저학년을 포함한 만 13세 미만의 어린이를 주요 이용자로 하는 서비스로, 어린이 개인정보 보호 규정 및 관련 법령을 준수합니다.
                """.trimIndent().replace("", "\u200B"),
                            style = subTextStyle,
                            modifier = Modifier
                                .semantics {
                                    contentDescription = "본 개인정보처리방침은 스토릭터 앱(이하 “앱”)이 제공하는 서비스 이용과 관련하여 사용자의 개인정보 및 민감한 정보를 어떻게 수집·이용·보관·삭제·보호하는지 설명합니다." +
                                            "스토릭터는 유치원생 및 초등학교 저학년을 포함한 만 13세 미만의 어린이를 주요 이용자로 하는 서비스로, 어린이 개인정보 보호 규정 및 관련 법령을 준수합니다."
                                }
                        )
                        HorizontalDivider(
                            color = colorResource(R.color.main_orange),
                            thickness = 2.dp
                        )

                        Text(
                            text = """
                        1. 수집하는 정보
                        
                        앱은 서비스 제공을 위해 아래의 정보를 수집할 수 있습니다.

                        ① 사용자가 직접 제공하는 정보
                        - 아이 이름 또는 닉네임
                        - 대화 녹음 파일(일시적 저장)
                        - 아이의 질문/답변 내용(STT 변환 텍스트 포함)
                        - 앱 내 선택 항목(관심사 선택, 테마 선택 등)
                        
                        ② 자동으로 수집되는 정보
                        - 기기 정보 (모델명, OS 버전 등)
                        - 앱 사용 기록 (사용 기능, 접속 시간 등)
                        - 오류 로그 및 서비스 이용 중 발생하는 비식별 정보
                        
                        ③ 민감정보
                        음성 녹음(STT 변환용)이 필요하지만, 이는 오직 동화 생성 기능 제공을 위해서만 사용됩니다.

                        얼굴 이미지, 위치 정보, 연락처, 광고 ID 등은 수집하지 않습니다.
                        """.trimIndent().replace("", "\u200B"),
                            style = descTextStyle,
                            modifier = Modifier
                                .semantics {
                                    contentDescription = "1. 수집하는 정보.\n" +
                                            "앱은 서비스 제공을 위해 아래의 정보를 수집할 수 있습니다.\n" +
                                            "첫째, 사용자가 직접 제공하는 정보. 아이 이름 또는 닉네임, 대화 녹음 파일(일시적 저장), 아이의 질문/답변 내용(STT 변환 텍스트 포함), 앱 내 선택 항목(관심사 선택, 테마 선택 등).\n" +
                                            "둘째, 자동으로 수집되는 정보. 기기 정보 (모델명, OS 버전 등), 앱 사용 기록 (사용 기능, 접속 시간 등), 오류 로그 및 서비스 이용 중 발생하는 비식별 정보.\n" +
                                            "셋째, 민감정보. 음성 녹음(STT 변환용)이 필요하지만, 이는 오직 동화 생성 기능 제공을 위해서만 사용됩니다. 얼굴 이미지, 위치 정보, 연락처, 광고 ID 등은 수집하지 않습니다."
                                }
                        )
                        HorizontalDivider(
                            color = colorResource(R.color.main_orange),
                            thickness = 2.dp
                        )

                        Text(
                            text = """
                        2. 개인정보 이용 목적
                        
                        - LLM 기반 대화형 동화 생성 기능 제공
                        - 사용자 음성(STT 변환)을 활용한 자연스러운 상호작용 제공
                        - 아이의 대화·선호 기반 지도(언어/정서/관심사) 대시보드 제공
                        - 맞춤형 콘텐츠 품질 개선
                        - 출석 체크 및 포인트(도장) 시스템 운영
                        - 서비스 안정성 확보 및 오류 분석
                        """.trimIndent().replace("", "\u200B"),
                            style = descTextStyle,
                            modifier = Modifier.semantics {
                                contentDescription = "2. 개인정보 이용 목적.\n" +
                                        "LLM 기반 대화형 동화 생성 기능 제공.\n" +
                                        "사용자 음성(STT 변환)을 활용한 자연스러운 상호작용 제공.\n" +
                                        "아이의 대화·선호 기반 지도(언어/정서/관심사) 대시보드 제공.\n" +
                                        "맞춤형 콘텐츠 품질 개선.\n" +
                                        "출석 체크 및 포인트(도장) 시스템 운영.\n" +
                                        "서비스 안정성 확보 및 오류 분석."
                            }
                        )
                        HorizontalDivider(
                            color = colorResource(R.color.main_orange),
                            thickness = 2.dp
                        )

                        Text(
                            text = """
                        3. 개인정보 보관 및 삭제
                       
                        ① 보관 기간
                        - 스토리, 캐릭터: 운영자 검수 및 기능 특성상 삭제 요청 시 별도 문의 필요
                        - 대화 기록 및 즐겨찾기: 회원 탈퇴 즉시 자동 삭제
                        - 음성 녹음 파일: STT 변환 후 즉시 삭제(서버에 장기 저장하지 않음)
                        
                        ② 사용자 권리
                        - 탈퇴 시 개인정보는 즉시 삭제됩니다.
                        - 스토리 및 캐릭터 삭제 요청은 운영자 문의를 통해 처리됩니다.
                        - 기타 개인정보 열람/정정/삭제 요청 가능
                        """.trimIndent().replace("", "\u200B"),
                            style = descTextStyle,
                            modifier = Modifier.semantics {
                                contentDescription = "3. 개인정보 보관 및 삭제.\n" +
                                        "첫째, 보관 기간. 스토리, 캐릭터: 운영자 검수 및 기능 특성상 삭제 요청 시 별도 문의 필요. 대화 기록 및 즐겨찾기: 회원 탈퇴 즉시 자동 삭제. 음성 녹음 파일: STT 변환 후 즉시 삭제(서버에 장기 저장하지 않음).\n" +
                                        "둘째, 사용자 권리. 탈퇴 시 개인정보는 즉시 삭제됩니다. 스토리 및 캐릭터 삭제 요청은 운영자 문의를 통해 처리됩니다. 기타 개인정보 열람/정정/삭제 요청 가능."
                            }
                        )
                        HorizontalDivider(
                            color = colorResource(R.color.main_orange),
                            thickness = 2.dp
                        )

                        Text(
                            text = """
                        4. 개인정보 제3자 제공
                        
                        스토릭터는 법령에 의한 경우를 제외하고 제3자에게 개인정보를 제공하지 않습니다.

                        단, 동화 및 이미지 생성, 동영상 변환, 대시보드 분석 등 서비스 제공에 필요한 목적에 한하여, AI 처리 엔진 등 외부 서비스에 비식별 처리된 데이터 또는 서비스 제공에 필요한 최소한의 데이터를 전송할 수 있습니다.
                        
                        이 경우:
                        - 사용자 인증 정보는 전송하지 않습니다.
                        - 전송 데이터는 안전한 방식으로 암호화됩니다.
                        - 전송 데이터가 서비스 제공 목적 외로 사용되지 않도록 관리합니다.
                        """.trimIndent().replace("", "\u200B"),
                            style = descTextStyle,
                            modifier = Modifier.semantics {
                                contentDescription = "4. 개인정보 제 삼자 제공.\n" +
                                        "스토릭터는 법령에 의한 경우를 제외하고 제 삼자에게 개인정보를 제공하지 않습니다.\n" +
                                        "단, 동화 및 이미지 생성, 동영상 변환, 대시보드 분석 등 서비스 제공에 필요한 목적에 한하여, AI 처리 엔진 등 외부 서비스에 비식별 처리된 데이터 또는 서비스 제공에 필요한 최소한의 데이터를 전송할 수 있습니다.\n" +
                                        "이 경우: 사용자 인증 정보는 전송하지 않습니다. 전송 데이터는 안전한 방식으로 암호화됩니다. 전송 데이터가 서비스 제공 목적 외로 사용되지 않도록 관리합니다."
                            }
                        )
                        HorizontalDivider(
                            color = colorResource(R.color.main_orange),
                            thickness = 2.dp
                        )

                        Text(
                            text = """
                        5. 어린이 개인정보 보호
                        
                        스토릭터는 만 13세 미만 어린이를 대상으로 하므로 다음을 준수합니다.

                        - 최소한의 정보만 수집
                        - 수집 목적 외 절대 사용 금지
                        - 광고 및 마케팅 목적의 데이터 사용 금지
                        """.trimIndent().replace("", "\u200B"),
                            style = descTextStyle,
                            modifier = Modifier.semantics {
                                contentDescription = "5. 어린이 개인정보 보호.\n" +
                                        "스토릭터는 만 13세 미만 어린이를 대상으로 하므로 다음을 준수합니다.\n" +
                                        "최소한의 정보만 수집. 수집 목적 외 절대 사용 금지. 광고 및 마케팅 목적의 데이터 사용 금지."
                            }
                        )
                        HorizontalDivider(
                            color = colorResource(R.color.main_orange),
                            thickness = 2.dp
                        )

                        Text(
                            text = """
                        6. 포인트(도장) 시스템 정책
                        
                        - 앱 내 포인트는 상업적 목적이 아닌 순수한 참여 유도 기능
                        - 출석 체크를 통해 하루 1개씩 적립
                        - 신규 가입 시 5개 제공
                        - 포인트 사용 기능은 2가지:
                            ① LLM 기반 대화형 동화 제작 – 1개 사용
                            ② 이미지 동화(기본 제공)를 동영상으로 변환 – 1개 사용
                        - 광고 시청 등을 통한 포인트 획득 기능 없음
                        """.trimIndent().replace("", "\u200B"),
                            style = descTextStyle,
                            modifier = Modifier.semantics {
                                contentDescription = "6. 포인트(도장) 시스템 정책.\n" +
                                        "앱 내 포인트는 상업적 목적이 아닌 순수한 참여 유도 기능." +
                                        "출석 체크를 통해 하루 1개씩 적립." +
                                        "신규 가입 시 5개 제공." +
                                        "포인트 사용 기능은 2가지. 첫째, LLM 기반 대화형 동화 제작에 1개 사용, 이미지 동화(기본 제공)를 동영상으로 변환에 1개 사용." +
                                        "광고 시청 등을 통한 포인트 획득 기능 없음."
                            }
                        )
                        HorizontalDivider(
                            color = colorResource(R.color.main_orange),
                            thickness = 2.dp
                        )

                        Text(
                            text = """
                        7. 데이터 보안
                        
                        - 전송 구간 데이터 암호화(HTTPS)
                        - 서버 접근 제어 및 권한 관리
                        - 비식별 처리 시스템 적용
                        - 불필요한 데이터 저장 금지 정책
                        """.trimIndent().replace("", "\u200B"),
                            style = descTextStyle,
                            modifier = Modifier.semantics {
                                contentDescription = "7. 데이터 보안.\n" +
                                        "전송 구간 데이터 암호화(HTTPS)." +
                                        "서버 접근 제어 및 권한 관리." +
                                        "비식별 처리 시스템 적용." +
                                        "불필요한 데이터 저장 금지 정책."
                            }
                        )
                        HorizontalDivider(
                            color = colorResource(R.color.main_orange),
                            thickness = 2.dp
                        )

                        Text(
                            text = """
                        8. 사용자 권리 및 문의처
                        
                        아래를 통해 문의하실 수 있습니다.

                        - 이메일: <veryshinnam@gmail.com>
                        - 앱 내 문의 기능
                        """.trimIndent().replace("", "\u200B"),
                            style = descTextStyle,
                            modifier = Modifier.semantics {
                                contentDescription = "8. 사용자 권리 및 문의처.\n" +
                                        "아래를 통해 문의하실 수 있습니다.\n" +
                                        "이메일, veryshinnam@gmail.com 및 앱 내 문의 기능."
                            }
                        )
                        HorizontalDivider(
                            color = colorResource(R.color.main_orange),
                            thickness = 2.dp
                        )

                        Text(
                            text = """
                        9. 약관 및 운영 규칙
                        
                        ① 스토리/캐릭터 삭제 정책
                        스토리와 캐릭터는 운영자만 삭제 가능합니다. 앱의 지속성을 위해 유튜브 채널에 완성된 동화를 업로드하기에, 다른 사용자들이 캐릭터에 애정을 느낄 수 있어 중대한 사유가 아닌 경우 삭제가 제한됩니다.

                        ② 탈퇴 시 데이터 삭제
                        사용자의 대화 기록과 즐겨찾기는 회원 탈퇴 시 자동 삭제됩니다.
                        """.trimIndent().replace("", "\u200B"),
                            style = descTextStyle,
                            modifier = Modifier.semantics {
                                contentDescription = "9. 약관 및 운영 규칙.\n" +
                                        "첫째, 스토리/캐릭터 삭제 정책. 스토리와 캐릭터는 운영자만 삭제 가능합니다. 앱의 지속성을 위해 유튜브 채널에 완성된 동화를 업로드하기에, 다른 사용자들이 캐릭터에 애정을 느낄 수 있어 중대한 사유가 아닌 경우 삭제가 제한됩니다.\n" +
                                        "둘째,탈퇴 시 데이터 삭제. 사용자의 대화 기록과 즐겨찾기는 회원 탈퇴 시 자동 삭제됩니다."
                            }
                        )
                        HorizontalDivider(
                            color = colorResource(R.color.main_orange),
                            thickness = 2.dp
                        )

                        Text(
                            text = """
                        10. 개인정보처리방침 변경
                        
                        본 개인정보처리방침은 법령, 정책, 서비스 변경에 따라 갱신될 수 있으며, 변경 시 앱 또는 공지사항을 통해 안내합니다.
                        """.trimIndent().replace("", "\u200B"),
                            style = descTextStyle,
                            modifier = Modifier.semantics {
                                contentDescription = "10. 개인정보처리방침 변경\n" +
                                        "본 개인정보 처리방침은 법령, 정책, 서비스 변경에 따라 갱신될 수 있으며, 변경 시 앱 또는 공지사항을 통해 안내합니다."
                            }
                        )
                        HorizontalDivider(
                            color = colorResource(R.color.main_orange),
                            thickness = 2.dp
                        )

                        Text(
                            text = """
                        11. 생성형 인공지능 활용 안내
                        
                        ① 스토릭터는 서비스 제공을 위해 다음 기능에 생성형 인공지능 기술을 활용합니다.
                        - 아이와의 대화 진행 및 질문/피드백 생성
                        - 아이의 대화 내용에 기반한 동화 제목 및 내용 생성
                        - 캐릭터 이미지 및 캐릭터 성격 생성
                        - 동화 썸네일 이미지 및 동화 페이지 이미지 생성
                        - 움직이는 동화 영상 썸네일 및 동화 페이지 영상 생성
                        - 동화 내용 기반 대시보드 분석 및 부모님 조언 생성

                        ② 이용자가 입력하거나 제공한 음성 답변(STT 변환 텍스트 포함), 선택 항목 및 대화 내용은 동화 콘텐츠 생성과 대시보드 분석을 위해 활용됩니다.

                        ③ 서비스에서 제공되는 대화 응답, 동화 제목 및 내용, 캐릭터 이미지 및 성격, 동화 이미지·영상 콘텐츠, 대시보드 분석 결과는 생성형 인공지능에 의해 생성됩니다.

                        ④ 스토릭터는 사용자가 서비스에서 제공되는 콘텐츠가 생성형 인공지능에 의해 생성되었음을 인지할 수 있도록 서비스 화면 또는 결과물에 안내 문구, 식별 아이콘 등 각 콘텐츠의 특성에 맞는 적절한 방식을 적용하여 표시합니다.

                        ⑤ 생성형 인공지능의 특성상 결과물에는 부정확하거나 예상과 다른 내용이 포함될 수 있으며, 스토릭터는 어린이에게 부적절한 결과물이 제공되지 않도록 안전 조치와 검수 절차를 적용하기 위해 노력합니다.

                        ⑥ 대시보드의 정서분석 및 부모님 조언은 참고용 정보로 제공되며, 전문적인 진단, 상담 또는 의학적 판단을 대체하지 않습니다.
                        """.trimIndent().replace("", "\u200B"),
                            style = descTextStyle,
                            modifier = Modifier.semantics {
                                contentDescription = "11. 생성형 인공지능 활용 안내\n" +
                                        "첫째 스토릭터는 서비스 제공을 위해 다음 기능에 생성형 인공지능 기술을 활용합니다. " +
                                        "아이와의 대화 진행 및 질문/피드백 생성. 아이의 대화 내용에 기반한 동화 제목 및 내용 생성." +
                                        " 캐릭터 이미지 및 캐릭터 성격 생성. 동화 썸네일 이미지 및 동화 페이지 이미지 생성. " +
                                        "움직이는 동화 영상 썸네일 및 동화 페이지 영상 생성. 동화 내용 기반 대시보드 분석 및 부모님 조언 생성\n" +
                                        "둘째 이용자가 입력하거나 제공한 음성 답변(STT 변환 텍스트 포함), 선택 항목 및 대화 내용은 동화 콘텐츠 생성과 대시보드 분석을 위해 활용됩니다." +
                                        "셋째 서비스에서 제공되는 대화 응답, 동화 제목 및 내용, 캐릭터 이미지 및 성격, 동화 이미지·영상 콘텐츠, 대시보드 분석 결과는 생성형 인공지능에 의해 생성됩니다." +
                                        "넷째 스토릭터는 사용자가 서비스에서 제공되는 콘텐츠가 생성형 인공지능에 의해 생성되었음을 인지할 수 있도록 서비스 화면 또는 결과물에 안내 문구, 식별 아이콘 등 각 콘텐츠의 특성에 맞는 적절한 방식을 적용하여 표시합니다." +
                                        "다섯째 생성형 인공지능의 특성상 결과물에는 부정확하거나 예상과 다른 내용이 포함될 수 있으며, 스토릭터는 어린이에게 부적절한 결과물이 제공되지 않도록 안전 조치와 검수 절차를 적용하기 위해 노력합니다." +
                                        "여섯째 대시보드의 정서분석 및 부모님 조언은 참고용 정보로 제공되며, 전문적인 진단, 상담 또는 의학적 판단을 대체하지 않습니다."
                            }
                        )
                        HorizontalDivider(
                            color = colorResource(R.color.main_orange),
                            thickness = 2.dp
                        )

                        Text(
                            text = "최종 업데이트: 2026-05-06",
                            style = descTextStyle,
                            modifier = Modifier.semantics {
                                contentDescription = "최종 업데이트. 2026년 5월 6일"
                            }
                        )
                    }
                }

                // 모두 동의
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.clickable { checked = !checked }
                        .padding(horizontal = verticalPadding*2)
                        .semantics {
                            role = Role.Checkbox
                            contentDescription = "위 약관을 모두 확인하였으며, 이에 동의합니다."
                            stateDescription = if (checked) "동의됨" else "동의되지 않음"
                        }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "동의 버튼",
                        tint = if (checked) colorResource(R.color.main_orange) else colorResource(R.color.light_gray),
                        modifier = Modifier.size(24.dp)
                            .clearAndSetSemantics { }
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "위 약관을 모두 확인하였으며, 이에 동의합니다.".replace("", "\u200B"),
                        style = subTextStyle.copy(
                            color = if (checked) Color.Black else Color.LightGray,
                            fontWeight = Bold
                        ),
                        modifier = Modifier.clearAndSetSemantics { }
                    )
                }

                // 하단 버튼
                Spacer(Modifier.height(verticalPadding))
                Button(
                    onClick = { if (checked) vm.signup(tempCode) },
                    enabled = checked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = Color.White.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = if (checked) {
                                    listOf(
                                        colorResource(R.color.main_orange),
                                        colorResource(R.color.dark_orange),
                                    )
                                } else {
                                    listOf(
                                        colorResource(R.color.main_orange).copy(alpha = 0.3f),
                                        colorResource(R.color.dark_orange).copy(alpha = 0.3f)
                                    )
                                }
                            ),
                            shape = RoundedCornerShape(16.dp),
                        )
                        .border(
                            width = 2.dp,
                            color =
                                if (checked) colorResource(id = R.color.main_orange)
                                else colorResource(id = R.color.main_orange).copy(0.3f),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Text(
                        text = "시작하기",
                        style = titleTextStyle.copy(
                            fontSize = titleTextStyle.fontSize * 1.2f
                        ),
                        modifier = Modifier.padding(vertical = verticalPadding)
                    )
                }
            }
        }
    }
}