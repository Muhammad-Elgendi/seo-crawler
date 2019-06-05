A SEO Spider in Java

How can I create an executable JAR with dependencies using Maven?
mvn clean compile assembly:single

run crawler
java -jar SEO-Spider-1.0.0-SNAPSHOT-jar-with-dependencies.jar https://is.net.sa 10000 20 1 1

drop connections
SELECT pg_terminate_backend(pg_stat_activity.pid)
FROM pg_stat_activity
WHERE pg_stat_activity.datname = 'TARGET_DB' -- ← change this to your DB
  AND pid <> pg_backend_pid();
  
Current problems :-
