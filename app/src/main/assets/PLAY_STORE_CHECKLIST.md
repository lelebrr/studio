# Play Store Publication Checklist – StudioCar Pro (V1.3)

Este guia fornece todos os passos e textos necessários para publicar o **StudioCar Pro** com sucesso.

---

## 1. Conta de Desenvolvedor
- **Custo**: Taxa única de $25 USD.
- **Link**: [Google Play Console](https://play.google.com/console/signup)
- **Atualizações**: Visite a Play Store mensalmente para garantir que você está usando os prompts de IA mais recentes (V1.3.0 atualizada com FLUX Ultra).

## 2. Geração do App Bundle (AAB)
No Android Studio:
1. Vá em `Build` > `Generate Signed Bundle / APK`.
2. Selecione `Android App Bundle`.
3. Crie uma nova **Key Store** (mantenha a senha e o arquivo `.jks` salvos em local ultra-seguro).
4. Selecione a variante `release`.
5. O arquivo gerado estará em `app/release/app-release.aab`.

## 3. Metadados para a Loja (SEO)

### Título do App
- **Português**: StudioCar — Estúdio de Carros IA

### Descrição Curta (80 caracteres)
- **Texto**: Transforme o pátio da sua loja em um estúdio fotográfico 4K com IA.

### Descrição Completa
- **Texto**: 
O **StudioCar** é a plataforma definitiva de fotografia automotiva para concessionárias que buscam o máximo em profissionalismo e agilidade nas vendas.

**Por que usar o StudioCar Pro?**
• **Motor FLUX.1 Elite**: Refinamento multi-pass para realismo fotográfico imbatível.
• **IA Generativa de Ponta**: Substitua cenários reais por showrooms de luxo instantaneamente com transparência X-Ray em vidros.
• **Recorte Perfeito**: Tecnologia MediaPipe para remoção de fundo com precisão milimétrica.
• **Qualidade Ultra 4K**: Fotos em alta resolução prontas para portais de venda.
• **Legendas Inteligentes**: O Gemini 3.1 analisa o carro e gera textos comerciais impactantes.
• **Foco em Vendas**: Envie as fotos tratadas diretamente para o cliente via WhatsApp.

Elimine gastos com estúdios físicos e fotógrafos terceirizados. Transforme seu estoque hoje mesmo com a tecnologia T-Line!

---

## 4. Assets Gráficos Sugeridos (8 Screenshots)
1. **Screen 1**: "Tire fotos no pátio" (Câmera com guia).
2. **Screen 2**: "Processamento IA" (Foto original vs Recorte).
3. **Screen 3**: "Showroom de Luxo" (Carro em fundo elegante).
4. **Screen 4**: "Vidros Realistas" (Foco na transparência lateral).
5. **Screen 5**: "Reflexos 4K" (Foco no brilho da pintura).
6. **Screen 6**: "Legendas de Venda" (IA gerando texto comercial).
7. **Screen 7**: "Envio Instantâneo" (Botão WhatsApp).
8. **Screen 8**: "Histório de Vendas" (Grid de carros editados).

---

## 5. Política de Privacidade (Simplified Text)
- **URL**: [https://tline.studio/privacy](https://tline.studio/privacy) (Exemplo)
- **Conteúdo**: "O StudioCar solicita permissão de CÂMERA para captura e ARMAZENAMENTO para salvar as fotos editadas na sua galeria. Não coletamos nem vendemos dados pessoais ou localização. As fotos são processadas de forma efêmera via IA e retornadas ao seu dispositivo."

---

## 6. Precificação e Distribuição
- **Sugestão**: App Gratuito para a equipe, mas com controle de acesso corporativo via chave de API gerenciada centralmente (no `local.properties`).
- **Alternativa**: Cobrar por assento/loja através de fatura externa ao Google Play (modelo B2B).
