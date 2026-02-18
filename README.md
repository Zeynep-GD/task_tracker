# Task Tracker Backend API

Spring Boot ile geliştirilmiş, JWT tabanlı kimlik doğrulama ve rol bazlı yetkilendirme içeren bir Görev Yönetim Sistemi backend uygulamasıdır.

##Tech Stack

- Java 21  
- Spring Boot  
- Spring Security  
- JWT  
- PostgreSQL + PostGIS  
- Redis  
- Maven  
- Docker  

##Features

- JWT ile stateless authentication  
- Role-Based Access Control (OWNER, EDITOR, VIEWER)  
- Konum bazlı sorgulama (PostGIS)  
- Dosya yükleme ve indirme desteği  
- Global exception handling  
- Cache mekanizması (Redis)  

##Run the Project

1. `application-example.properties` dosyasını kopyalayın:
cp src/main/resources/application-example.properties src/main/resources/application.properties


2. Veritabanı bilgilerinizi girin.

3. Uygulamayı başlatın:
mvn spring-boot:run


veya

docker-compose up --build


---

Developed by Zeynep Gerçekdoğan
