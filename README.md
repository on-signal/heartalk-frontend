# 하톡시그널
러브라인 예능 프로그램 '하트시그널' 기반의 3:3 소개팅 어플

> GitHub Repo : https://github.com/KHU-Pearl/SMP-Android  
> 필요시 GitHub권한 드리겠습니다.

<br>

## 1. 프로젝트의 목표 및 내용

<image src="https://s3.us-west-2.amazonaws.com/secure.notion-static.com/3db97e0d-16a1-489a-9ad3-d6186b9586d1/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20201218%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20201218T185849Z&X-Amz-Expires=86400&X-Amz-Signature=ff721940c2327e2e635a436ad622178a0bf734eafde3f7aae6d57e7a47140390&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22" width="500">  
- 남자 3, 여자 3이 매칭되어 시그널 룸으로 이동한다.
- 시그널 룸에서 음성통화, 말풍선 채팅을 하며 커플 선택을 통해 총 2차례의 1:1 음성 통화 기회를 갖는다.
- 남녀의 최종 선택이 일치할 경우, 1:1 영상통화를 진행하고 친구 등록을 할 수 있다.

<br><br>

## 2. SW 기능 및 UI
- 협업툴 : Git, GitHub, Notion
- Client : Android Studio, JAVA
- Server : KINX IX Cloud(Ubuntu 14.04), MySQL, PHP

### 1) 멘티와 멘토 회원가입을 나눠 구현 및 서버연동
<image src="https://s3.us-west-2.amazonaws.com/secure.notion-static.com/6de45ba5-e6e5-4b4d-80c5-be25b57a49c4/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20201218%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20201218T190321Z&X-Amz-Expires=86400&X-Amz-Signature=3b82c86b2adb58d1dfc1cdff6af52e8e2afce982489c16b77cfacb045f34a450&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22" width="600">
중복확인 : 이메일이 이미 회원가입된 아이디라면 사용할 수 없다.

### 2) 멘티 로그인 및 메인화면
<image src="https://user-images.githubusercontent.com/37680108/102651159-64228c00-41af-11eb-8299-9c50987ff550.png" width="600">

### 3) 멘토 로그인 및 메인화면
<image src="https://user-images.githubusercontent.com/37680108/102651215-81575a80-41af-11eb-932b-1de99961088a.png" width="600">

### 4) 멘토 팀만들기 기능
<image src="https://user-images.githubusercontent.com/37680108/102651274-9f24bf80-41af-11eb-8e40-91d245a0f1d4.png" width="500">

### 5)WBS
#### 팀별 목록 및 세부사항 확인 : 칸반보드 형태로 구현 (예정, 진행, 완료)
<image src="https://user-images.githubusercontent.com/37680108/102651476-f75bc180-41af-11eb-9b74-acd7b5826275.png" width="600">

#### 할 일 (프로젝트 작업) 추가 :
<image src="https://user-images.githubusercontent.com/37680108/102651491-fd51a280-41af-11eb-9109-5c251e1e6ff7.png" width="600">

<br><br>
