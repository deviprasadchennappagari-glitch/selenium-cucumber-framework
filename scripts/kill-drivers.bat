@echo off
REM Manually kill any leftover browser-driver processes (Windows).
REM The framework already does this automatically after every scenario and
REM at the end of the suite - use this script only if you need to clean up
REM by hand (e.g. after a hard JVM crash or manually killed build).

taskkill /F /IM chromedriver.exe /T 2>nul
taskkill /F /IM geckodriver.exe /T 2>nul
taskkill /F /IM msedgedriver.exe /T 2>nul

echo Done.
