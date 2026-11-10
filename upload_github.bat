@echo off
chcp 65001 > nul
echo ======================================================
echo    CÔNG CỤ TỰ ĐỘNG CẬP NHẬT CODE LÊN GITHUB
echo ======================================================
echo.

:: Kiểm tra xem đã kết nối tới GitHub từ trước chưa
git remote get-url origin > nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [LẦN ĐẦU TIÊN ĐẨY CODE]
    set /p GITHUB_URL="Dán đường link Repository GitHub của bạn vào đây: "
    if "%GITHUB_URL%"=="" (
        echo [LỖI] Bạn chưa nhập link GitHub!
        pause
        exit /b
    )
    git remote add origin %GITHUB_URL%
) else (
    echo [ĐÃ KẾT NỐI SẴN VỚI GITHUB]
    echo Đang chuẩn bị cập nhật phiên bản code mới nhất...
)

echo.
set /p COMMIT_MSG="Nhập nội dung bạn vừa sửa đổi (để trống nếu không cần): "
if "%COMMIT_MSG%"=="" set COMMIT_MSG="Cap nhat code moi"

echo.
echo 1. Đang lưu lại các thay đổi mới...
git add .
git commit -m "%COMMIT_MSG%"
git branch -M main

echo.
echo 2. Đang đồng bộ và đẩy code mới lên GitHub...
git push -u origin main

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ======================================================
    echo [THÀNH CÔNG] Code trên GitHub đã được cập nhật bản mới nhất!
    echo Link nộp bài vẫn giữ nguyên không đổi.
    echo ======================================================
) else (
    echo.
    echo [LƯU Ý] Nếu thất bại, hãy kiểm tra lại kết nối mạng hoặc thử lại.
)

pause
