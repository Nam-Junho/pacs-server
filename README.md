# 1. Pacs Server
- 사용 용도는 Pacs 서버에서 데이터를 주고 받기 위해 만들어짐

# 2. 구현
- scp 구현 (All)
- scu 구현 (cEcho, cStore)

# 3. Package 구조
```
└common: 공통 모듈
└controller
    └ScpController: SCP에 대한 api 정의 
    └ScuController: SCU에 대한 api 정의 (cEcho, cStore)
└dto: Request, Response
└module: Scp, Scu 
└service: File 저장
```

# 4. 구동
- 이미지 tar 파일 저장
```shell
./DockerBuild.sh {version}
```
- docker compose 실행
```shell
cd docker-compose && docker-compose up -d --build
```

# 5. Docker-compose 환경 변수
```dotenv
# 파일 마운트 경로
BASE_PATH=~/apps
# 컨테이너 타임존
BASE_TZ=Asia/Seoul

# 서버 포트
SERVER_PORT=9090
# 서버 버전
VERSION=1.0.4
# PACS aetitle
PACS_AET=pacs-server
# PACS port
PACS_PORT=4100
```