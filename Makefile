create-local-env:
	docker-compose up -d
destroy-local-env:
	docker-compose down
run:
	mvn spring-boot:run -Dspring-boot.run.profiles=local