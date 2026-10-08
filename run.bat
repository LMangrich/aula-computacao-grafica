@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"
set FILES=
for /r src %%f in (*.java) do (
    set "p=%%f"
    set FILES=!FILES! "!p:%CD%\=!"
)
javac -d bin %FILES% && java -cp bin app.MainClass
