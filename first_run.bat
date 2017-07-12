@echo off
rem ____________________________
echo Launching hsql server
start ant hsql.server
pause

rem ____________________________
echo Creating the sample database schema
ant database.create
pause

rem ____________________________
echo Initializing the sample data in database
ant database.init
pause

rem ____________________________
echo Launching the compilation
ant
pause

rem ____________________________
echo If you have tomcat up & running
echo   the application will be deployed
ant deploy
