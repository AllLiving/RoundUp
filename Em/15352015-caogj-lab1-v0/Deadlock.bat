cd /d %~dp0
@echo off
:start
set /a var+=1
echo %var%
java Deadlock
if %var% leq 1000 GOTO start
pause