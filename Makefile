.PHONY:test
test:
	./gradlew clean test

.PHONY:watch_test
watch_test:
	./gradlew test --continuous --tests "${CLASS}"

.PHONY:build
build:
	./gradlew clean build copyJars

.PHONY:stop_application
stop_application:
	docker-compose rm --stop

.PHONY:start_application
start_application:docker_build
	docker-compose up -d

.PHONY:docker_build_db
docker_build_db: build
	docker build --no-cache -t mysql-test-db ./mysqldb

.PHONY:docker_build_user_service
docker_build_user_service: docker_build_db
	docker build --no-cache -t user-service ./user-service

.PHONY:docker_build_book_service
docker_build_book_service: docker_build_user_service
	docker build --no-cache -t book-service ./book-service

.PHONY:docker_build
docker_build: docker_build_book_service
	echo "Completed build chain ..."

.PHONY:docker_build_only
docker_build_only:
	docker build --no-cache -t mysql-test-db ./mysqldb
	docker build --no-cache -t user-service ./user-service
	docker build --no-cache -t book-service ./book-service

.PHONY:start_application_only
start_application_only:
	docker-compose up -d

.PHONY:start_mysql_db
start_mysql_db:
	docker run -dp 3306:3306  -e "MARIADB_ALLOW_EMPTY_PASSWORD=true" mysql-test-db




