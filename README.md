# PetStayConnect (파이널 팀 프로젝트)

**PetStayConnect**는 반려동물 보호자와 돌봄 제공자를 연결하는 통합 플랫폼입니다.  
예약, 돌봄 공간 검색, 사용자 관리, 공지/후기/문의, 결제 등 다양한 서비스를 제공합니다.

---

## 📝 프로젝트 소개

- 반려동물 보호자와 돌봄 제공자(사업장/개인)의 매칭 및 예약, 후기 중심 서비스
- 강아지·고양이 정보 검색, 즐겨찾기(찜), 마이페이지, 관리자/공지글, 사업장 관리 등 다양한 기능 제공
- 결제 및 환불, 이메일 인증, OAuth2 기반 소셜 로그인(구글), JWT 인증, RESTful API 지원

---

## 🛠️ 사용 기술 스택

| 구분        | 기술/툴                                             |
|-------------|-----------------------------------------------------|
| **프론트엔드** | Vite, React, JavaScript, HTML, CSS                |
| **백엔드**   | Java, Spring Boot, Spring Security, JPA, Gradle     |
| **DB**      | MySQL                                              |
| **인프라**   | GitHub, Git, Postman, VSCode, Windows 11            |
| **협업/관리**| GitHub Project(이슈/PR), Notion, 카카오톡, Discord   |

---

## 🛠️ 주요 기능

- 회원가입, 로그인, 소셜 로그인(구글 OAuth2)
- 돌봄 서비스 예약 및 결제, 환불 시스템
- 사업장 등록/관리, 위치 검색, 사업장 후기
- 공지, 문의, 공지사항 관리, 마이페이지/찜/즐겨찾기
- 강아지/고양이 정보 추천 및 검색, 관리자 페이지
- 이메일 알림, JWT 인증, RESTful API 제공

---

## 🚀 설치 및 실행 방법

### 1. 레포지토리 클론
git clone https://github.com/Suu-a/petstory-platform-final-2025.git
cd petstory-platform-final-2025

text

### 2. 환경 변수 파일 설정
- 윈도우 암호화 정책상, 로컬 개발 시 `.env` 대신 `env.txt` 파일을 사용
- 반드시 본인 환경(DB, API 키 등)값을 수동 작성
- 운영/배포시에는 파일명을 `.env`로 변경

SPRING_DATASOURCE_DRIVER=com.mysql.cj.jdbc.Driver
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/DB명
SPRING_DATASOURCE_USERNAME=DB_ID
SPRING_DATASOURCE_PASSWORD=DB_PW
...

text

### 3. 백엔드(Spring Boot) 실행
gradlew.bat bootRun

text
- 프로젝트 루트(최상단)에서 실행
- 백엔드 서버는 `http://localhost:8080` 구동
- 실행 시 프론트(Vite)도 자동 빌드

### 4. 프론트엔드(Vite) 개발 서버 별도 실행
cd frontend
npm install
npm run dev

text
- 개발 서버: `http://localhost:5173`

---

## 🗂️ 브랜치 및 커밋 관리

- **main**: 최신 통합(정식) 브랜치
- **feat-header-ui-redesign / frontend-qna / qna-refactor** 등: 기능별 개발 브랜치
- 팀 깃허브(원본: `somecreater/PetStayConnect`)의 최신 소스를 upstream으로 연동,  
  주기적으로 fetch/merge로 코드 동기화

---

## 📂 GitHub 동기화 / 관리 절차

### 1. 폴더 이동 및 git 상태 확인
cd [프로젝트경로]
git status

text

### 2. 변경 파일 add, 브랜치 확인
git add .
git branch

text

### 3. 원격 저장소 코드 최신화 및 동기화
git pull origin main
git branch -r
git fetch origin
git checkout main
git pull origin main
git remote add upstream https://github.com/somecreater/PetStayConnect.git
git remote -v
git fetch upstream
git merge upstream/master

text

### 4. 환경 변수 파일 세팅
- `.env.template`을 참고해서 `env.txt`로 복사
- DB/API 등 환경 값 직접 입력, 서버 배포 시 `.env`로 변경

### 5. 프로젝트 빌드 및 실행
gradlew.bat bootRun
gradlew.bat build --warning-mode all

text

---

> ⚠️ mirror clone, fork 없이 기존 로컬 폴더에서 표준 git 명령(git remote/add/fetch/merge)를 사용하여 동기화  
> ⚠️ 환경 변수 파일은 암호화 정책 때문에 로컬에서는 env.txt로 관리, 서버에서는 .env로 사용  
> ⚠️ 프론트는 gradle 자동 빌드 또는 필요시 직접 npm run dev로 실행

---

## 🏅 담당 역할 / 경험한 내용

- GitHub 운영, 프로젝트 전체 git 이력 및 브랜치 관리, 최신 소스 동기화
- 환경변수 세팅(DB/JWT/메일/구글 등) 및 Windows 보안환경 이슈 직접 해결
- 기능별 브랜치 전략, PR, Merge, 코드리뷰, 이슈 관리 경험
- Spring Boot 기반 API 및 예약/결제 등 서비스 로직 직접 구현 및 협업
- MySQL, Vite, React, Gradle, JWT, 이메일 인증, OAuth2 실무 경험

---

## 💡 기타 안내

- 로컬 개발 환경에서만 env.txt 사용, 실제 운영/배포 시 .env 사용  
- 프로젝트 전체 진행 과정(커밋/이슈/PR) 히스토리는 GitHub에서 상세히 확인 가능  
- 추가적인 어드민 기능, 서버/DB 구조, 상세 플로우 등은 별도 문서 참고
