@echo off
chcp 65001 > nul
if not exist "MainApp.class" (
    echo Đang tự động biên dịch mã nguồn lần đầu...
    javac -encoding UTF-8 *.java
)
echo Đang khởi chạy Hệ Thống Quản Lý MiniMart...
java MainApp
