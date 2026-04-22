# StudioCar Elite 2026 - Model Download Script
# This script downloads the required AI models for image segmentation.

$AssetDir = Join-Path $PSScriptRoot "..\app\src\main\assets\models"
if (!(Test-Path $AssetDir)) {
    New-Item -ItemType Directory -Force -Path $AssetDir
}

$Models = @(
    @{
        Name = "selfie_multiclass.tflite"
        URL  = "https://storage.googleapis.com/mediapipe-models/image_segmenter/selfie_multiclass_256x256/float32/latest/selfie_multiclass_256x256.tflite"
    },
    @{
        Name = "deeplab_v3.tflite"
        URL  = "https://storage.googleapis.com/mediapipe-models/image_segmenter/deeplab_v3/float32/1/deeplab_v3.tflite"
    },
    @{
        Name = "sam2_hiera_tiny.encoder.onnx"
        URL  = "https://huggingface.co/vietanhdev/segment-anything-2-onnx-models/resolve/main/sam2_hiera_tiny.encoder.onnx"
    },
    @{
        Name = "sam2_hiera_tiny.decoder.onnx"
        URL  = "https://huggingface.co/vietanhdev/segment-anything-2-onnx-models/resolve/main/sam2_hiera_tiny.decoder.onnx"
    }
)

Write-Host "--- StudioCar Elite: Baixando Modelos de IA ---" -ForegroundColor Cyan

foreach ($Model in $Models) {
    $OutFile = Join-Path $AssetDir $Model.Name
    if (Test-Path $OutFile) {
        Write-Host "[-] $($Model.Name) já existe. Pulando..." -ForegroundColor Yellow
    } else {
        Write-Host "[+] Baixando $($Model.Name)..." -ForegroundColor Green
        try {
            Invoke-WebRequest -Uri $Model.URL -OutFile $OutFile -ErrorAction Stop
        } catch {
            Write-Host "[!] Erro ao baixar $($Model.Name): $($_.Exception.Message)" -ForegroundColor Red
        }
    }
}

# Renomear modelos SAM 2 para o padrão esperado pelo código (dots to underscores)
$TinyEncoder = Join-Path $AssetDir "sam2_hiera_tiny.encoder.onnx"
$TinyDecoder = Join-Path $AssetDir "sam2_hiera_tiny.decoder.onnx"
$TargetEncoder = Join-Path $AssetDir "sam2_hiera_tiny_encoder.onnx"
$TargetDecoder = Join-Path $AssetDir "sam2_hiera_tiny_decoder.onnx"

if (Test-Path $TinyEncoder) { Move-Item $TinyEncoder $TargetEncoder -Force }
if (Test-Path $TinyDecoder) { Move-Item $TinyDecoder $TargetDecoder -Force }

# Criar link simbólico ou cópia para o modelo Premium se não existir
$PremiumModel = Join-Path $AssetDir "car_segmenter_v2_2026.tflite"
if (!(Test-Path $PremiumModel)) {
    Write-Host "[*] Configurando modelo Premium (Mapping DeepLabV3)..." -ForegroundColor Magenta
    Copy-Item (Join-Path $AssetDir "deeplab_v3.tflite") $PremiumModel
}

Write-Host "--- Download Concluído! ---" -ForegroundColor Cyan
