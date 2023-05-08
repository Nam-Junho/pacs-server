docker build . -t pacs-server:$1
docker save -o pacs-server.$1.tar pacs-server:$1