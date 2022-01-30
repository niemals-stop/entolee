install:
	./mvnw clean install

release:
	./mvnw --batch-mode -Pentolee-publishing clean release:prepare release:perform

