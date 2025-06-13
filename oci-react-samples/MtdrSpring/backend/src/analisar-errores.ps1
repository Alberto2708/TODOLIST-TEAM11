# Ejecutar Spring Boot y guardar todos los logs
Write-Host "Ejecutando mvn spring-boot:run y guardando logs..."
Start-Process -NoNewWindow -FilePath "mvn" -ArgumentList "spring-boot:run" -RedirectStandardOutput "full-log.txt"

# Esperar unos segundos para que se generen logs
Write-Host "Esperando 10 segundos para acumular logs..."
Start-Sleep -Seconds 10

# Verificar que el log existe
if (Test-Path "full-log.txt") {
    # Extraer errores y warnings
    Write-Host "Extrayendo líneas con ERROR o WARN..."
    Select-String "ERROR|WARN" full-log.txt | Out-File -FilePath errors-warnings.log

    # Agrupar y contar
    Write-Host "Agrupando errores y generando resumen..."
    $grouped = Get-Content errors-warnings.log | Group-Object | Sort-Object Count -Descending

    # Mostrar en consola
    $grouped | Select-Object -First 10 | Format-Table Count, Name -AutoSize

    # Exportar a CSV
    Write-Host "Exportando reporte a error-report.csv..."
    $grouped | Select-Object Name, Count | Export-Csv -Path error-report.csv -NoTypeInformation

    Write-Host "`n✅ ¡Análisis completado! Abre 'error-report.csv' para ver el resumen."
} else {
    Write-Host "❌ No se encontró el archivo full-log.txt. Asegúrate de que mvn spring-boot:run esté generando salida."
}
