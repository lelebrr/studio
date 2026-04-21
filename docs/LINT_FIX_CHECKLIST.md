# T-Line Studio — Lista de Correções de Lint

> Lista organizada baseada nos resultados do Android Lint Inspector.

## ✅ Prioridade Alta (Corrigir Primeiro)

### Correções de Correctness
- [ ] MediaPipeSegmenter.kt: Substituir `Log` por `Timber` (4 ocorrência)
- [ ] AndroidManifest.xml: Atualizar permissions para Android 14+ (Scoped Storage)
- [ ] gradle.properties: Corrigir escaping de paths Windows
- [ ] PostProcessor.kt: Remover anotação @Nullable redundante

### Dependências Obsoletas (Atualizar libs.versions.toml)
- [ ] org.jetbrains.kotlin.plugin.compose: 2.2.10 → 2.3.20
- [ ] org.jetbrains.kotlin.plugin.serialization: 2.2.10 → 2.3.20
- [ ] kotlinx-coroutines: 1.9.0 → 1.10.2
- [ ] Compose BOM: 2026.02.01 → 2026.03.01
- [ ] androidx.room: 2.6.1 → 2.8.4
- [ ] com.google.mediapipe:tasks-vision: 0.10.14 → 0.20230731
- [ ] androidx.camera:*: 1.3.4 → 1.6.0
- [ ] androidx.navigation: 2.8.5 → 2.9.7
- [ ] io.coil-kt:coil-compose: 2.6.0 → 2.7.0
- [ ] com.squareup.retrofit2:*: 2.11.0 → 3.0.0
- [ ] okhttp: 4.12.0 → 5.3.2
- [ ] androidx.lifecycle: 2.6.1 → 2.10.0
- [ ] androidx.activity: 1.8.0 → 1.13.0
- [ ] androidx.datastore: 1.1.1 → 1.2.1
- [ ] androidx.exifinterface: 1.3.7 → 1.4.2
- [ ] Gradle: 9.3.1 → 9.4.1

## 📝 Prioridade Média (Code Style)

### Usar KTX Extensions
- [ ] CarBackgroundSegmenter.kt: createBitmap() KTX
- [ ] ImageEditorService.kt: createBitmap() e Bitmap.scale() KTX
- [ ] ImageSaveHelper.kt: Bitmap.scale() KTX
- [ ] MediaPipeSegmenter.kt: createBitmap() KTX
- [ ] PostProcessor.kt: createBitmap(), Bitmap.get(), Bitmap.set(), String.toColorInt() KTX

### Conversões var → val
- [ ] ImageEditorService.kt: Converter var para val onde não modificado
- [ ] OpenRouterConfig: Considerar const para valores fixos

### Imports Não Utilizados (Remover)
- [ ] AboutScreen.kt
- [ ] BackgroundSelector.kt (~6 imports)
- [ ] CameraScreen.kt
- [ ] CarBackgroundSegmenter.kt (~2 imports)
- [ ] EditorScreen.kt
- [ ] HistoryScreen.kt
- [ ] MainActivity.kt (~2 imports)
- [ ] MainScreen.kt
- [ ] MediaPipeSegmenter.kt (~6 imports)
- [ ] MediaPipeSegmenterExample.kt (~2 imports)
- [ ] OnboardingScreen.kt (~2 imports)
- [ ] SegmenterTest.kt
- [ ] TestScreen.kt
- [ ] Theme.kt

## 🔧 Prioridade Baixa (Cleanup)

### Recursos Não Utilizados (Remover de colors.xml)
- [ ] purple_200, purple_500, purple_700
- [ ] teal_200, teal_700
- [ ] black, white

### Strings Não Utilizadas (Remover de strings.xml)
- [ ] splash_slogan
- [ ] btn_share_whatsapp
- [ ] share_title
- [ ] editor_save_success/error
- [ ] app_description_long

### Código Morto (Funções/Classes Não Usadas)
Diversos archivos listados no relatório — remover ou implementar uso.

---

## 📚 Documentação (Erros de Digitação)

> Nota: Estes são centenas de erros nos arquivos .md e .kt. Recomenda-se usar 
> ferramenta de spell-check ou corrigir manualmente conforme necessidade.

---

*Lista gerada automaticamente a partir dos resultados do Android Lint.*