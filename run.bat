@echo off
echo Compiling Java files...
if not exist bin mkdir bin
javac -d bin src\*.java
echo Done compiling.

echo Running program...
java -cp bin Main
