# Modelos MediaPipe - T-Line Studio

Para que o segmentador funcione corretamente, você deve baixar os seguintes modelos `.tflite` e colocá-los nesta pasta (`src/main/assets/`).

## Modelos Recomendados

### 1. DeepLabV3 (Recomendado para Carros)
Este modelo é excelente para segmentar objetos genéricos como carros, aviões e animais.
- **Arquivo**: `deeplab_v3.tflite`
- **Link**: [Download DeepLabV3](https://storage.googleapis.com/mediapipe-models/image_segmenter/deeplab_v3/float32/1/deeplab_v3.tflite)

### 2. Selfie Segmenter (Opcional)
Otimizado para segmentação de pessoas em tempo real.
- **Arquivo**: `selfie_segmenter.tflite`
- **Link**: [Download Selfie Segmenter](https://storage.googleapis.com/mediapipe-models/image_segmenter/selfie_segmenter/float16/1/selfie_segmenter.tflite)

## Como Adicionar
1. Baixe o arquivo `.tflite` do link acima.
2. Renomeie o arquivo se necessário para coincidir com o nome configurado em `MediaPipeConfig.kt` (padrão: `deeplab_v3.tflite`).
3. Mova o arquivo para a pasta `app/src/main/assets/`.

---
*Configurado para T-Line Studio - 2026*
