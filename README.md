# Задача по JAVA
Необходимо создать сервис на Java, который будет принимать данные в формате XML из топика Kafka1, используя произвольную схему XSD с автогенерацией классов. 
После получения данных, определенные поля будут сохранены в базе данных PostgreSQL,
а часть полей будет отправлена в формате JSON в топик Kafka2. В случае успешной обработки сообщения, будет отправлено уведомление в топик Kafka1 в формате XML. 
Если обработка не удалась - будет отправлено сообщение об ошибке.
Реализация данного сервиса производится с использованием Spring Boot и Apache Camel.
Также реализованы метрики для подсчета полученных сообщений, времени обработки и количества успешных/неуспешных обработок. Весь код покрыт тестами.