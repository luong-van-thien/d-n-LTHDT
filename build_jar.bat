@echo off
setlocal
echo ========================================================
echo   TIEN ICH DONG GOI BAN BUILD NOP BAI (MINIMART SYSTEM)
echo ========================================================
echo.

set JAR_CMD=jar
where jar >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    if exist "C:\Program Files\Java\jdk-21.0.12\bin\jar.exe" (
        set JAR_CMD="C:\Program Files\Java\jdk-21.0.12\bin\jar.exe"
    )
)

echo [1/3] Dang bien dich ma nguon Java...
javac -encoding UTF-8 *.java
if %ERRORLEVEL% NEQ 0 (
    echo [LOI] Qua trinh bien dich that bai!
    pause
    exit /b %ERRORLEVEL%
)

echo [2/3] Dang tao file manifest...
echo Main-Class: MainApp>manifest.txt

echo [3/3] Dang dong goi file MiniMartSystem.jar...
%JAR_CMD% cfm MiniMartSystem.jar manifest.txt *.class

del manifest.txt

if exist MiniMartSystem.jar (
    echo.
    echo ========================================================
    echo [THANH CONG] Da xuat ban file: MiniMartSystem.jar
    echo Ban co the nhap dup chuot vao file MiniMartSystem.jar
    echo hoac chay lenh: java -jar MiniMartSystem.jar
    echo ========================================================
) else (
    echo [LOI] Khong the dong goi file jar.
)
echo.
pause
