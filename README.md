# DDD 12기 모여락 API 🍳

<img src="image/모여락 썸네일.png" alt="모여락 썸네일" width="30%"/>

> 회사 팀 단위로 점심 식당을 공유·리뷰할 수 있는 서비스입니다. <br />
> <br />
> 팀 단위 식사 장소 선택을 돕고, 지속적인 리뷰 아카이빙을 지원합니다. <br />
> 단순 리뷰 공유뿐만 아니라, 팟 기능을 통해 팀원들과 유연하게 모여 함께 식사할 수 있도록 지원합니다.

## 📌 주요 기능
- OAuth2 (Google) 기반 로그인
- 지도 기반 식당 탐색 및 위치 표시
- 팀 단위 점심 식당 리뷰 작성 및 공유
- 식사 태그(한식/일식/중식/양식 등) 등록 및 필터링
- 관리자 페이지를 통한 팀원 관리
- 식사 모임을 위한 팟 기능을 통해 투표 기반 기능 제공

## 🛠️ 기술 스택
- Backend: Java 21, Spring Boot 3.4.5, Spring Data JPA
- Database: MySQL, H2
- Infra: AWS EC2, RDS, S3, GitHub Actions
- ETC: Gradle, Github, Spotless, Git Hooks

## 🚀 로컬 실행 방법
```bash
# 1. 필수 설정
./gradlew clean

# 2. 로컬 실행
./gradlew bootRun

# 3. 테스트 실행
./gradlew test
```

## 👥 모여락 기여자
### 🎁 PM
<table>
 <tr>
    <td align="center" colspan=""><a href="https://github.com/EllaDeve"><img src="https://avatars.githubusercontent.com/EllaDeve" width="130px;" alt=""></a></td>
 </tr>
 <tr>
    <td align="center"><a href="https://github.com/EllaDeve"><b>EllaDeve</b></a></td>
 </tr>
 <tr>
    <td align="center"><a href="mailto:qkrtjsals17@gmail.com"><b>qkrtjsals17@gmail.com</b></a></td>
 </tr>
</table>

### 💻 Backend
<table>
 <tr>
    <td align="center"><a href="https://github.com/zinzoddari"><img src="https://avatars.githubusercontent.com/zinzoddari" width="130px;" alt=""></a></td>
    <td align="center"><a href="https://github.com/myeongha"><img src="https://avatars.githubusercontent.com/myeongha" width="130px;" alt=""></a></td>
    <td align="center"><a href="https://github.com/Jiwon-cho"><img src="https://avatars.githubusercontent.com/Jiwon-cho" width="130px;" alt=""></a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/zinzoddari"><b>Zinzo</b></a></td>
    <td align="center"><a href="https://github.com/myeongha">Myeongha Joo<b></b></a></td>
    <td align="center"><a href="https://github.com/Jiwon-cho"><b>Cho jiwon</b></a></td>
  </tr>
</table>

### 🏞️ Frontend
<table>
 <tr>
    <td align="center"><a href="https://github.com/dlantjdgkgk"><img src="https://avatars.githubusercontent.com/dlantjdgkgk" width="130px;" alt=""></a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/dlantjdgkgk"><b>이무성</b></a></td>
  </tr>
</table>

### 🎨 Designer
<table>
 <tr>
    <td align="center">
        나선아
    </td>
    <td align="center">
        최지현
    </td>
 </tr>
 <tr>
    <td align="center"><a href="mailto:nasa2183@naver.com"><b>nasa2183@naver.com</b></a></td>
    <td align="center"><a href="mailto:gr5631@gmail.com"><b>gr5631@gmail.com</b></a></td>
 </tr>
</table>

## 참고 문서
- 모여락 개발 WIKI : https://github.com/DDD-Community/DDD-12-MOYORAK-API/wiki
