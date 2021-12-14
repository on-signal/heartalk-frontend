# 하톡시그널
러브라인 예능 프로그램 '하트시그널' 기반의 3:3 소개팅 어플
![하톡시그널 포스터](https://user-images.githubusercontent.com/87048860/145799474-6a48e175-731b-4847-8fd7-0954948706d6.png)


<br>

## 1. 프로젝트의 목표 및 내용
  
- 남자 3, 여자 3이 매칭되어 시그널 룸으로 이동한다.
- 시그널 룸에서 음성통화, 말풍선 채팅을 하며 커플 선택을 통해 총 2차례의 1:1 음성 통화 기회를 갖는다.
- 남녀의 최종 선택이 일치할 경우, 1:1 영상통화를 진행하고 친구 등록을 할 수 있다.

<br><br>

## 2. SW 기능 및 UI
- 협업툴 : Git, GitHub, Trello
- Client : Kotlin, Android Studio
- Server : TypeScript, Nest.js, MongoDB

### 1) 카카오톡 로그인을 이용해 로그인
<img src="https://user-images.githubusercontent.com/57060164/145927435-69f30b25-cf52-46c7-b227-6dc89da18f9f.jpg" height="600"> <image src="https://user-images.githubusercontent.com/57060164/145927439-36913e87-55bd-459d-9e6d-37d20014b464.jpg" height="600"/>
<br>
주민등록번호를 이용해 나이, 성별을 가져옵니다.
<br>
### 2) 홈 화면, 1:1 채팅, 프로필 화면
<image src="https://user-images.githubusercontent.com/57060164/145933941-fa399853-f816-4c01-bb3e-e726f3ffb328.jpg" height="600"/> <image src="https://user-images.githubusercontent.com/57060164/145934585-dd17e30d-4806-4d2c-924c-7d83421db95b.jpg" height="600"/> <image src="https://user-images.githubusercontent.com/57060164/145933932-b7a90909-6da0-4c1f-93c8-6c5c470e168e.jpg" height="600"/>
<br>
홈 화면, 채팅 목록, 프로필 화면을 하단 NavBar로 구현
<br>
### 3) 시그널 룸 화면
<image src="https://user-images.githubusercontent.com/57060164/145933538-305b2aef-0a5c-41b8-ac83-206852f6af16.jpg" height="600"/> <image src="https://user-images.githubusercontent.com/57060164/145933528-7d84f2be-e7d2-4317-be13-e4c1f9b33539.jpg" height="600"/> <image src="https://user-images.githubusercontent.com/57060164/145933535-0017ddc9-a165-4ed3-8ed8-f4ccca10ae55.jpg" height="600"/>
<br>
- 남자 3명, 여자 3명이 매칭되어 입장
- 1명씩 돌아가며 10초씩 자기소개 후 첫인상 선택 -> 매칭된 상대와 1:1 음성 통화 (선택이 엇갈려도 3커플이 매칭됩니다!)
- 남자가 연애관 질문 답변 후, 여자가 답변 선택 -> 선택한 상대와 1:1 음성 통화 (선착순으로 구현하여, 동일 선택은 불가능합니다!)
- 다시 시그널 룸에서 최종 선택 -> 서로를 지목 시, 1:1 **영상** 통화 -> 이후 서로 승인하면 1:1 채팅방 생성 (1명이라도 거절 시 채팅방 생성 불가)

<br><br>

## 3. 시행착오

### 1) 매칭 시, 6명 중 일부 음성통화 연결 오류
음성 통화는 CometChat API를 사용했습니다. 매칭 시스템은 Web Socket을 이용해 구현했습니다.
매칭 버튼 (매칭 대기열 입장) -> 매칭 완료 팝업 (서버에서 event 발생) -> 매칭 확인 클릭 (서버로 매칭 Yes / no 결과 전달) -> 입장 (서버에서 입장 여부를 전달해줌)
위와 같이 구성했고, 문제가 발생한 부분은 입장으로 넘어가는 부분입니다.

CometChat을 통한 음성통화 구현은 CometChat 내의 Group 생성 -> 매칭된 6명의 유저를 같은 Group에 포함시킨 후, 시그널 룸으로 화면 전환 ->  6명 중 1명이 전화를 걺
그러나 Caller가 전화를 거는 시점에, 아직 **Group에 포함되지 않은 유저**가 생겨 통화가 오류가 생겼습니다. (클라이언트마다 Join Group되는 시점이 달랐습니다.)
초기에는 이 문제를 Group에 포함한 후, 약 0.5 ~ 1초정도의 delay를 준 후 화면을 전환하여 해결했습니다.

기기마다 네트워크 환경, 성능이 달라 1초 미만의 delay로는 약 20번중 1번정도 통화 오류가 생겼고 이를 개선하고자
서버에서 입장하라는 data를 받으면, CometChat Group에 입장한 후 서버에 group join을 완료했음을 emit하고, 6명의 유저가 모두 완료되면 서버에서 응답을 주어
일괄 입장 하는 방식으로 변경했습니다. 

소켓 통신을 추가적으로 1번 더 하기 때문에 0.5초 미만의 delay 이후 화면이 전환됐고, 음성 통화 오류는 해결됐습니다.


### 2) 시행착오2

