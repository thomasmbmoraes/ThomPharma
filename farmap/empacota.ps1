# empacota.ps1 - gera a distribuicao do ThomPharma para pendrive/execucao offline
# Uso: .\empacota.ps1
# Prerequisito: Java e Maven no PATH, ou os caminhos abaixo configurados

$mvn       = "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd"
$m2        = "$env:USERPROFILE\.m2\repository"
$pendrive  = "$PSScriptRoot\pendrive"
$target    = "$PSScriptRoot\target"

Write-Host ""
Write-Host "=== ThomPharma - Empacotador ===" -ForegroundColor Cyan

# ── 1. compilar ───────────────────────────────────────────────────────────────
Write-Host "`n[1/4] Compilando..." -ForegroundColor Yellow
& $mvn -f "$PSScriptRoot\pom.xml" clean package -q -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERRO: Falha na compilacao!" -ForegroundColor Red
    exit 1
}
Write-Host "      OK" -ForegroundColor Green

# ── 2. copiar JAR principal ───────────────────────────────────────────────────
Write-Host "[2/4] Copiando JAR..." -ForegroundColor Yellow
$jar = Get-ChildItem "$target\ThomPharma-*.jar" | Where-Object { $_.Name -notlike "*original*" } | Select-Object -First 1
if (-not $jar) {
    Write-Host "ERRO: JAR nao encontrado em target\!" -ForegroundColor Red
    exit 1
}
Copy-Item $jar.FullName "$pendrive\ThomPharma.jar" -Force
Write-Host "      $($jar.Name) -> pendrive\ThomPharma.jar" -ForegroundColor Green

# ── 3. copiar dependencias para pendrive\deps ─────────────────────────────────
Write-Host "[3/4] Verificando dependencias..." -ForegroundColor Yellow

$deps = @(
    @{ src = "$m2\org\mindrot\jbcrypt\0.4\jbcrypt-0.4.jar";             dst = "jbcrypt-0.4.jar" },
    @{ src = "$m2\org\postgresql\postgresql\42.7.3\postgresql-42.7.3.jar"; dst = "postgresql-42.7.3.jar" },
    @{ src = "$m2\org\openjfx\javafx-base\13\javafx-base-13-win.jar";     dst = "javafx-base-13-win.jar" },
    @{ src = "$m2\org\openjfx\javafx-controls\13\javafx-controls-13-win.jar"; dst = "javafx-controls-13-win.jar" },
    @{ src = "$m2\org\openjfx\javafx-fxml\13\javafx-fxml-13-win.jar";     dst = "javafx-fxml-13-win.jar" },
    @{ src = "$m2\org\openjfx\javafx-graphics\13\javafx-graphics-13-win.jar"; dst = "javafx-graphics-13-win.jar" }
)

foreach ($dep in $deps) {
    if (Test-Path $dep.src) {
        Copy-Item $dep.src "$pendrive\deps\$($dep.dst)" -Force
        Write-Host "      OK: $($dep.dst)" -ForegroundColor Green
    } else {
        Write-Host "      AVISO: nao encontrado no cache Maven: $($dep.dst)" -ForegroundColor Yellow
        Write-Host "             Execute o sistema pelo NetBeans ao menos uma vez para baixar." -ForegroundColor DarkYellow
    }
}

# ── 4. gerar launcher ThomPharma.bat ─────────────────────────────────────────
Write-Host "[4/4] Gerando launcher..." -ForegroundColor Yellow

$bat = @'
@echo off
SET "DIR=%~dp0"

REM Verifica se config.properties existe
IF NOT EXIST "%DIR%config.properties" (
    echo.
    echo  ATENCAO: arquivo config.properties nao encontrado!
    echo  Copie config.properties.template para config.properties
    echo  e preencha os dados do banco de dados.
    echo.
    pause
    exit /b 1
)

REM Inicia o ThomPharma com o modulo system completo
"%DIR%jdk\bin\java" ^
  --module-path "%DIR%deps;%DIR%ThomPharma.jar" ^
  --module thompharma/thompharma.App

IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo  Erro ao iniciar o ThomPharma. Verifique o config.properties.
    pause
)
'@

$bat | Set-Content -Path "$pendrive\ThomPharma.bat" -Encoding ASCII
Write-Host "      pendrive\ThomPharma.bat gerado" -ForegroundColor Green

# ── config.properties ─────────────────────────────────────────────────────────
if (-not (Test-Path "$pendrive\config.properties")) {
    Copy-Item "$PSScriptRoot\config.properties.template" "$pendrive\config.properties"
    Write-Host ""
    Write-Host "  IMPORTANTE: edite pendrive\config.properties com os dados do banco!" -ForegroundColor Yellow
}

# ── resumo ────────────────────────────────────────────────────────────────────
Write-Host ""
Write-Host "=== Distribuicao pronta em: pendrive\ ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Para executar: abra pendrive\ThomPharma.bat" -ForegroundColor White
Write-Host "  Para distribuir: copie a pasta pendrive\ para um pendrive ou ZIP" -ForegroundColor White
Write-Host ""
