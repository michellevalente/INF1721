@echo off
for /f %%a in ('dir /b^|findstr ".dat"') do (run.exe --file %%a)