# Modelos de IA - StudioCar Elite 2026

Para que os motores de segmentação (MediaPipe e SAM 2) funcionem corretamente, os modelos devem estar localizados na pasta `src/main/assets/models/`.

## Modelos Necessários

### 1. MediaPipe (Segmentação Base)
- **Arquivo**: `selfie_multiclass.tflite`
- **Uso**: Segmentação rápida e fallback para o modo offline.

### 2. Segment Anything 2 (SAM 2 Ultra)
- **Arquivos**: 
  - `sam2_hiera_tiny_encoder.onnx`
  - `sam2_hiera_tiny_decoder.onnx`
- **Uso**: Recorte cirúrgico de alta precisão no modo Ultra.

### 3. Modelo Premium StudioCar (Opcional)
- **Arquivo**: `car_segmenter_v2_2026.tflite`
- **Uso**: Especializado em veículos. Se não existir, o sistema utiliza o fallback automaticamente.

## Como Adicionar os Modelos

Para facilitar a configuração, utilize o script de download automatizado:

1. Abra o terminal na raiz do projeto.
2. Execute o script:
   ```powershell
   .\scripts\download_models.ps1
   ```

Este script baixará todos os modelos necessários diretamente para a pasta correta.

---
*StudioCar Elite - Excelência em Fotografia Automotiva (2026)*
