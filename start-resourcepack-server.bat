@echo off
cd /d "%~dp0build\resourcepack"
python -m http.server 8000
pause