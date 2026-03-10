$ErrorActionPreference = "Stop"

# Register Admin
Write-Host "Registering Admin..."
try {
    $registerUrl = "http://localhost:8083/auth/register"
    $body = '{"username":"superadmin", "password":"password123"}'
    Invoke-RestMethod -Uri $registerUrl -Method POST -ContentType "application/json" -Body $body
} catch {
    Write-Host "Already registered or error: $_"
}

# Login Admin
Write-Host "`nLogging in Admin..."
$loginUrl = "http://localhost:8083/auth/login"
$body = '{"username":"superadmin", "password":"password123"}'
$token = Invoke-RestMethod -Uri $loginUrl -Method POST -ContentType "application/json" -Body $body
Write-Host "Token: $token"

# Test Admin Endpoint Directly on Loan Service
Write-Host "`nTesting /admin/ping on port 8083..."
$headers = @{
    "Authorization" = "Bearer $token"
}

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8083/admin/ping" -Method GET -Headers $headers
    Write-Host "Response (/admin/ping): $response"
} catch {
    Write-Host "GET /admin/ping failed: $_"
}

Write-Host "`nTesting /admin/loans on port 8083..."
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8083/admin/loans" -Method GET -Headers $headers
    Write-Host "Response (/admin/loans): $response"
} catch {
    Write-Host "GET /admin/loans failed: $_"
}
