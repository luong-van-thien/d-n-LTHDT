@echo off
chcp 65001 > nul
echo Đang biên dịch mã nguồn Java...
javac -encoding UTF-8 *.java
if %errorlevel% neq 0 (
    echo [LỖI] Biên dịch thất bại!
    pause
    exit /b %errorlevel%
)
echo Đang khởi chạy Hệ Thống Quản Lý MiniMart...
java MainApp
