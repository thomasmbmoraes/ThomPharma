@echo off 
SET DIR=%%~dp0 
"%%DIR%%jdk\bin\java" --module-path "%%DIR%%deps\javafx-controls-13-win.jar;%%DIR%%deps\javafx-graphics-13-win.jar;%%DIR%%deps\javafx-base-13-win.jar;%%DIR%%deps\javafx-fxml-13-win.jar" --add-modules javafx.controls,javafx.fxml -cp "%%DIR%%farmap-1.0-SNAPSHOT.jar;%%DIR%%deps\*" farmap.App 
