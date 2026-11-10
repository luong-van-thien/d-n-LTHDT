@echo off
chcp 65001 > nul
echo Đang biên dịch dự án MiniMartSystem...
javac -encoding UTF-8 *.java
if %ERRORLEVEL% EQU 0 (
    echo [THÀNH CÔNG] Đã biên dịch toàn bộ mã nguồn!
) else (
    echo [LỖI] Quá trình biên dịch xảy ra lỗi.
)
pause
