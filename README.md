PetStayConnect (파이널 팀 프로젝트)
🐾 반려동물 돌봄 서비스를 위한 예약/매칭/후기 플랫폼
📝 프로젝트 소개
PetStayConnect는 반려동물 보호자와 돌봄 제공자를 연결하는 통합 플랫폼입니다.

예약, 돌봄 공간 검색, 사용자 관리, 공지/후기/문의, 결제 등 다양한 서비스를 제공합니다.

⚙️ 사용 기술 스택
구분	기술/툴
프론트엔드	Vite, React, JavaScript, HTML, CSS
백엔드	Spring Boot, Java, JPA (Hibernate), Gradle, MySQL
인프라	GitHub, Git, Postman, VSCode, Windows 11
협업/관리	Git, GitHub Project(이슈/PR), Notion, 카카오톡
🛠️ 주요 기능
회원가입/로그인/소셜로그인(Google)

예약 및 결제 관리

돌봄 서비스 예약

결제 및 환불 처리

돌봄 사업장 관리

사업장 등록/위치 검색/상세정보/후기

공지/문의/공지사항 관리

마이페이지, 찜/즐겨찾기

강아지/고양이 정보 추천/검색

관리자 페이지 / 공지글 등록

알림(이메일), OAuth2, JWT 인증

RESTful API 제공

🚀 설치 및 실행 방법
1. 레포지토리 클론
bash
git clone https://github.com/Suu-a/petstory-platform-final-2025.git
cd petstory-platform-final-2025
2. 환경 변수 설정
⚠️ 윈도우 암호화 정책으로 인해 .env 대신 env.txt로 작성
(실제 서버에서는 .env로 이름 변경 필요)

text
# env.txt 또는 .env 파일 내부 예시
SPRING_DATASOURCE_DRIVER=com.mysql.cj.jdbc.Driver
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/DB명
SPRING_DATASOURCE_USERNAME=DB_ID
SPRING_DATASOURCE_PASSWORD=DB_PW
... (기타 API/메일 환경변수 등)
3. 백엔드(Spring Boot) 실행
bash
gradlew.bat bootRun
정상 실행 시 localhost:8080에서 백엔드가 구동됨

4. 프론트엔드(Vite) 실행
bash
cd frontend
npm install      # 처음 한 번만 실행
npm run dev      # 개발 서버 실행 (localhost:5173)
🗂️ 브랜치 및 커밋 관리
main: 최신 통합(정식) 브랜치

feat-header-ui-redesign / frontend-qna / qna-refactor 등 기능별 개발 브랜치

팀 깃허브(원본: somecreater/PetStayConnect) 최신 코드 upstream 연동 및 주기적 코드 동기화

🏅 담당 역할 / 경험한 내용 
깃허브 운영 및 프로젝트 전체 이관(Mirror Clone, Upstream 동기화)

Spring Boot 기반 백엔드 환경 구성, DB/JWT/메일 환경 변수 직접 세팅

윈도우/보안 환경에서 env 파일 이슈 직접 해결 및 실행 경험

브랜치 전략(기능별)과 PR, Merge, 코드 리뷰, 이슈 트래킹

실서비스 기준 CRUD/예약/결제/검색/관리자/알림 API 개발(협업/구현)

[MySQL, Vite, React, Gradle, JWT, 이메일 인증, OAuth2 등 실무 경험]

💡 기타 안내
로컬 개발 환경에서는 env.txt, 서버 배포시 .env 사용

프로젝트 진행 과정 전체는 커밋/이슈/PR에서 상세 확인 가능

[추가 문서/어드민 기능/서버, DB 구조 등은 별도 문서에 작성]
