# FLUX.1 Optimization Guide: Elite Automotive Photography

Este guia detalha como configurar o motor de IA do T-Line Studio para obter resultados fotográficos de nível catálogo usando o modelo FLUX.1.

---

## ⚙️ Parâmetros Técnicos Recomendados

### 1. Strength (Força de Mudança)
O parâmetro mais crítico. Controla quanto a IA pode "alucinar" sobre a imagem original.
- **Vidros e Rodas (Preservação)**: 0.35 — Mantém a estrutura física idêntica.
- **Modo Ultra (Showroom)**: 0.45 ~ 0.55 — Equilíbrio perfeito entre brilho e realismo.
- **Mudança Criativa**: 0.65+ — Útil apenas se desejar mudar a cor do carro ou detalhes grandes.

### 2. Guidance Scale
Controla a aderência ao prompt de texto.
- **Ideal FLUX**: 3.5 — Evita o look "over-sharpened" ou "deep fried".
- **Contraste Alto**: 4.5 — Útil para carros pretos brilhantes.

### 3. Steps (Passos de Inferência)
- **Produção Rápida**: 28 steps (Euler a).
- **Qualidade Master**: 40 steps (DPM++ 2M Karras).

---

## 🚗 Estratégias por Cenário

### Cenário A: Carros Pretos Brilhantes (O Grande Desafio)
Carros pretos tendem a absorver ou refletir demais o ambiente sujo do pátio.
- **Solução**: Use o `GlossBlackPrompt`. Ele força a IA a ignorar os reflexos reais do pátio e criar "Softboxes" de estúdio virtuais que acompanham a curvatura do carro.
- **Config**: Strength 0.50 | Guidance 4.0.

### Cenário B: Vidros Muito Escuros / Filmados
- **Problema**: A IA pode tratar vidros filmados como metal preto.
- **Solução**: O `TransparentGlassPrompt` injeta instruções de refração que "furam" a película digitalmente, inserindo profundidade onde havia apenas um bloco preto.

### Cenário C: Pisos Irregulares ou Sujos
- **Técnica**: O motor V13 utiliza o Gemini para reconstruir a perspectiva do piso primeiro, e então o FLUX texturiza o concreto polido. Isso evita que o carro pareça "flutuar".

---

## 🚀 Performance e Economia
1. **Multi-Pass vs Single-Pass**: 
   - Use Single-Pass para 90% dos carros de estoque.
   - Use Multi-Pass apenas para o "Destaque da Semana" do site.
2. **Resolução**: 
   - Enviar imagens acima de 3072px para a IA raramente melhora o resultado e aumenta drasticamente o custo e o tempo de processamento. O app faz o downscale automático para 2048px por padrão.

---
**T-Line Studio Pro — V1.3 Elite Edition**
