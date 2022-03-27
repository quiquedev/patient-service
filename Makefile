create-local-env:
	docker-compose up -d
destroy-local-env:
	docker-compose down
run:
	./gradlew bootRun