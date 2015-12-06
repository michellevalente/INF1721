@echo off
for /f %%a in ('dir /b^|findstr "32"^|findstr ".dat" ') do (run.exe --file %%a >> [saida]%%a.txt)
