#!/usr/bin/env pwsh
# API Testing Script for AthukoralaTraders Backend
# Run this script to test all API endpoints

$ErrorActionPreference = "Continue"
$BASE_URL = "http://localhost:8080/api"
$TOKEN = $null

# Colors for output
function Write-Success { Write-Host $args -ForegroundColor Green }
function Write-Error { Write-Host $args -ForegroundColor Red }
function Write-Info { Write-Host $args -ForegroundColor Cyan }
function Write-Warning { Write-Host $args -ForegroundColor Yellow }

# Test results tracking
$script:TotalTests = 0
$script:PassedTests = 0
$script:FailedTests = 0

function Test-API {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Endpoint,
        [object]$Body = $null,
        [hashtable]$Headers = @{},
        [int]$ExpectedStatus = 200
    )
    
    $script:TotalTests++
    Write-Info "`n[$script:TotalTests] Testing: $Method $Endpoint"
    Write-Info "Expected Status: $ExpectedStatus"
    
    try {
        $url = "$BASE_URL$Endpoint"
        $params = @{
            Uri = $url
            Method = $Method
            Headers = $Headers
            ContentType = "application/json"
        }
        
        if ($Body) {
            $params.Body = ($Body | ConvertTo-Json -Depth 10)
        }
        
        $response = Invoke-RestMethod @params -StatusCodeVariable statusCode -ErrorAction Stop
        
        if ($statusCode -eq $ExpectedStatus) {
            $script:PassedTests++
            Write-Success "✓ PASSED: $Name"
            Write-Info "Response: $($response | ConvertTo-Json -Depth 2 -Compress)"
            return @{ Success = $true; Data = $response; StatusCode = $statusCode }
        } else {
            $script:FailedTests++
            Write-Error "✗ FAILED: $Name (Status: $statusCode, Expected: $ExpectedStatus)"
            return @{ Success = $false; Data = $response; StatusCode = $statusCode }
        }
    } catch {
        $script:FailedTests++
        Write-Error "✗ FAILED: $Name"
        Write-Error "Error: $($_.Exception.Message)"
        return @{ Success = $false; Error = $_.Exception.Message }
    }
}

# Test Summary
function Show-TestSummary {
    Write-Host "`n" ("=" * 60) -ForegroundColor White
    Write-Host "TEST SUMMARY" -ForegroundColor White
    Write-Host ("=" * 60) -ForegroundColor White
    Write-Host "Total Tests: $script:TotalTests" -ForegroundColor White
    Write-Success "Passed: $script:PassedTests"
    Write-Error "Failed: $script:FailedTests"
    $successRate = if ($script:TotalTests -gt 0) { [math]::Round(($script:PassedTests / $script:TotalTests) * 100, 2) } else { 0 }
    Write-Host "Success Rate: $successRate%" -ForegroundColor $(if ($successRate -ge 80) { "Green" } elseif ($successRate -ge 50) { "Yellow" } else { "Red" })
    Write-Host ("=" * 60) -ForegroundColor White
}

# Start Testing
Write-Host @"

╔═══════════════════════════════════════════════════════════════╗
║                                                               ║
║        AthukoralaTraders API Test Suite                      ║
║        Automated Backend API Testing                         ║
║                                                               ║
╚═══════════════════════════════════════════════════════════════╝

"@ -ForegroundColor Cyan

Write-Warning "Ensure the backend server is running on $BASE_URL"
Write-Info "Starting tests in 3 seconds..."
Start-Sleep -Seconds 3

# ============================================================================
# 1. AUTHENTICATION TESTS
# ============================================================================
Write-Host "`n╔═══════════════════════════════════════════════════════════════╗" -ForegroundColor Yellow
Write-Host "║  AUTHENTICATION API TESTS                                     ║" -ForegroundColor Yellow
Write-Host "╚═══════════════════════════════════════════════════════════════╝" -ForegroundColor Yellow

# Test 1: Login with Admin
$loginResult = Test-API `
    -Name "Admin Login" `
    -Method "POST" `
    -Endpoint "/auth/login" `
    -Body @{
        username = "admin.user"
        password = "password123"
    } `
    -ExpectedStatus 200

if ($loginResult.Success -and $loginResult.Data.token) {
    $script:TOKEN = $loginResult.Data.token
    Write-Success "Token saved: $($TOKEN.Substring(0, 20))..."
}

# Test 2: Login with invalid credentials
Test-API `
    -Name "Login with Invalid Credentials" `
    -Method "POST" `
    -Endpoint "/auth/login" `
    -Body @{
        username = "invalid.user"
        password = "wrongpassword"
    } `
    -ExpectedStatus 401

# Test 3: Register new user
$randomUsername = "testuser$(Get-Random -Minimum 1000 -Maximum 9999)"
Test-API `
    -Name "Register New User" `
    -Method "POST" `
    -Endpoint "/auth/register" `
    -Body @{
        username = $randomUsername
        password = "password123"
        email = "$randomUsername@test.com"
        role = "CUSTOMER"
    } `
    -ExpectedStatus 201

# ============================================================================
# 2. PRODUCT API TESTS
# ============================================================================
Write-Host "`n╔═══════════════════════════════════════════════════════════════╗" -ForegroundColor Yellow
Write-Host "║  PRODUCT API TESTS                                            ║" -ForegroundColor Yellow
Write-Host "╚═══════════════════════════════════════════════════════════════╝" -ForegroundColor Yellow

# Test 4: Get all products
$productsResult = Test-API `
    -Name "Get All Products" `
    -Method "GET" `
    -Endpoint "/products" `
    -ExpectedStatus 200

$firstProductId = $null
if ($productsResult.Success -and $productsResult.Data.Count -gt 0) {
    $firstProductId = $productsResult.Data[0].id
    Write-Info "First product ID: $firstProductId"
}

# Test 5: Search products
Test-API `
    -Name "Search Products" `
    -Method "GET" `
    -Endpoint "/products/search?query=drill" `
    -ExpectedStatus 200

# Test 6: Get product by ID
if ($firstProductId) {
    Test-API `
        -Name "Get Product by ID" `
        -Method "GET" `
        -Endpoint "/products/$firstProductId" `
        -ExpectedStatus 200
}

# Test 7: Get product details
if ($firstProductId) {
    Test-API `
        -Name "Get Product Details" `
        -Method "GET" `
        -Endpoint "/products/$firstProductId/details" `
        -ExpectedStatus 200
}

# ============================================================================
# 3. CATEGORY API TESTS
# ============================================================================
Write-Host "`n╔═══════════════════════════════════════════════════════════════╗" -ForegroundColor Yellow
Write-Host "║  CATEGORY API TESTS                                           ║" -ForegroundColor Yellow
Write-Host "╚═══════════════════════════════════════════════════════════════╝" -ForegroundColor Yellow

# Test 8: Get all categories
Test-API `
    -Name "Get All Categories" `
    -Method "GET" `
    -Endpoint "/categories" `
    -ExpectedStatus 200

# ============================================================================
# 4. ORDER API TESTS (Authenticated)
# ============================================================================
Write-Host "`n╔═══════════════════════════════════════════════════════════════╗" -ForegroundColor Yellow
Write-Host "║  ORDER API TESTS (Authenticated)                              ║" -ForegroundColor Yellow
Write-Host "╚═══════════════════════════════════════════════════════════════╝" -ForegroundColor Yellow

if ($TOKEN) {
    $authHeaders = @{
        "Authorization" = "Bearer $TOKEN"
    }

    # Test 9: Get customer orders
    Test-API `
        -Name "Get Customer Orders" `
        -Method "GET" `
        -Endpoint "/customer/orders" `
        -Headers $authHeaders `
        -ExpectedStatus 200

    # Test 10: Get all orders (Admin)
    Test-API `
        -Name "Get All Orders (Admin)" `
        -Method "GET" `
        -Endpoint "/admin/orders" `
        -Headers $authHeaders `
        -ExpectedStatus 200

    # Test 11: Get pending orders
    Test-API `
        -Name "Get Pending Orders" `
        -Method "GET" `
        -Endpoint "/admin/orders/pending" `
        -Headers $authHeaders `
        -ExpectedStatus 200
} else {
    Write-Warning "Skipping authenticated order tests - no token available"
}

# ============================================================================
# 5. DELIVERY STAFF API TESTS
# ============================================================================
Write-Host "`n╔═══════════════════════════════════════════════════════════════╗" -ForegroundColor Yellow
Write-Host "║  DELIVERY STAFF API TESTS                                     ║" -ForegroundColor Yellow
Write-Host "╚═══════════════════════════════════════════════════════════════╝" -ForegroundColor Yellow

# Login as delivery staff
$deliveryLogin = Test-API `
    -Name "Delivery Staff Login" `
    -Method "POST" `
    -Endpoint "/auth/login" `
    -Body @{
        username = "staff.user"
        password = "password123"
    } `
    -ExpectedStatus 200

if ($deliveryLogin.Success -and $deliveryLogin.Data.token) {
    $deliveryToken = $deliveryLogin.Data.token
    $deliveryHeaders = @{
        "Authorization" = "Bearer $deliveryToken"
    }

    # Test 12: Get my deliveries
    Test-API `
        -Name "Get My Deliveries" `
        -Method "GET" `
        -Endpoint "/delivery-staff/my-orders" `
        -Headers $deliveryHeaders `
        -ExpectedStatus 200

    # Test 13: Get delivery stats
    Test-API `
        -Name "Get Delivery Stats" `
        -Method "GET" `
        -Endpoint "/delivery-staff/stats" `
        -Headers $deliveryHeaders `
        -ExpectedStatus 200
}

# ============================================================================
# 6. PROMOTION API TESTS
# ============================================================================
Write-Host "`n╔═══════════════════════════════════════════════════════════════╗" -ForegroundColor Yellow
Write-Host "║  PROMOTION API TESTS                                          ║" -ForegroundColor Yellow
Write-Host "╚═══════════════════════════════════════════════════════════════╝" -ForegroundColor Yellow

# Test 14: Get active promotions
Test-API `
    -Name "Get Active Promotions" `
    -Method "GET" `
    -Endpoint "/promotions/active" `
    -ExpectedStatus 200

# ============================================================================
# 7. QUICK SALE API TESTS
# ============================================================================
Write-Host "`n╔═══════════════════════════════════════════════════════════════╗" -ForegroundColor Yellow
Write-Host "║  QUICK SALE API TESTS                                         ║" -ForegroundColor Yellow
Write-Host "╚═══════════════════════════════════════════════════════════════╝" -ForegroundColor Yellow

if ($TOKEN) {
    $authHeaders = @{
        "Authorization" = "Bearer $TOKEN"
    }

    # Test 15: Get all quick sales
    Test-API `
        -Name "Get All Quick Sales" `
        -Method "GET" `
        -Endpoint "/staff/quick-sales" `
        -Headers $authHeaders `
        -ExpectedStatus 200
}

# ============================================================================
# FINAL SUMMARY
# ============================================================================
Show-TestSummary

# Save results to file
$timestamp = Get-Date -Format "yyyy-MM-dd_HH-mm-ss"
$reportPath = ".\test-results-$timestamp.txt"
$summary = @"
API Test Results - $timestamp
============================================
Total Tests: $script:TotalTests
Passed: $script:PassedTests
Failed: $script:FailedTests
Success Rate: $(if ($script:TotalTests -gt 0) { [math]::Round(($script:PassedTests / $script:TotalTests) * 100, 2) } else { 0 })%
============================================
"@
$summary | Out-File -FilePath $reportPath
Write-Info "`nTest results saved to: $reportPath"

# Exit with appropriate code
if ($script:FailedTests -eq 0) {
    Write-Success "`n✓ All tests passed!"
    exit 0
} else {
    Write-Error "`n✗ Some tests failed. Please check the output above."
    exit 1
}
