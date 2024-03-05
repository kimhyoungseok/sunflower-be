FROM openjdk:11-jdk
# 변수 설정 (빌드 파일 경로)
ARG JAR_FILE=build/libs/sunflowerPlate-0.0.1-SNAPSHOT.jar
# 환경 변수 설정
ENV app.auth.refreshTokenExpiry=${app.auth.refreshTokenExpiry}
app.auth.refreshTokenSecret=${app.auth.refreshTokenSecret}
app.auth.tokenExpiry=${app.auth.tokenExpiry}
app.auth.tokenSecret=${app.auth.tokenSecret}
cloud.aws.credentials.access-key=${cloud.aws.credentials.access-key}
cloud.aws.credentials.secret-key=${cloud.aws.credentials.secret-key}
cloud.aws.s3.restaurant-bucket=${cloud.aws.s3.restaurant-bucket}
cloud.aws.s3.review-img=${cloud.aws.s3.review-img}
cloud.aws.s3.user-bucket=${cloud.aws.s3.user-bucket}
MARIADB_NAME=${MARIADB_NAME}
MARIADB_PASSWORD=${MARIADB_PASSWORD}
MARIADB_URL=${MARIADB_URL}
MARIADB_USERNAME=${MARIADB_USERNAME}
# 빌드 파일 컨테이너로 복사
COPY ${JAR_FILE} app.jar
# jar 파일 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]