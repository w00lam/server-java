# 1. 자바 17 환경 사용 (본인의 프로젝트 자바 버전에 맞춰 17 혹은 21로 변경 가능)
FROM eclipse-temurin:17-jdk-alpine

# 2. 컨테이너 내부 작업 디렉토리 설정
WORKDIR /app

# 3. 1단계에서 빌드한 jar 파일을 컨테이너 내부로 복사
# build/libs/ 폴더 안의 jar 파일을 app.jar라는 이름으로 가져옵니다.
COPY build/libs/*.jar app.jar

# 4. 앱 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]