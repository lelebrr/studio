# Checklist de Teste Final: StudioCar Pro

Execute estes 8 testes antes de entregar o aparelho ou o APK para a concessionária.

---

## ✅ Lista de Verificação Obrigatória

1. **[ ] Persistência da Chave**: Insira uma API Key, feche o app completamente, reabra e verifique se ela ainda está lá nas configurações.
2. **[ ] Modo Demo (Offline)**: Ative o Modo Demo e processe uma foto. O app deve terminar em < 2 segundos e mostrar apenas o recorte do MediaPipe com fundo cinza.
3. **[ ] Vidros Transparentes**: Tire foto de um carro com fundo complexo atrás. Verifique se o Gemini 3.1 reconstruiu o showroom visível através dos vidros laterais.
4. **[ ] Salvamento Automático**: Verifique se a imagem final apareceu na pasta `/Pictures/Vendas/` e se o nome do arquivo segue o padrão `T-Line_TIMESTAMP`.
5. **[ ] Compartilhamento WhatsApp**: Clique no botão WhatsApp. Verifique se o app abre a lista de contatos e se a legenda de venda aparece como texto acompanhando a imagem.
6. **[ ] Troca de Modelos**: Mude para o modelo **FLUX.1.1 Pro** e verifique se a qualidade da textura e pintura melhorou significativamente (mesmo com tempo de espera maior).
7. **[ ] Limite de Resolução**: Configure o limite para 2048px e verifique se o app processa imagens menores com sucesso, economizando dados e tempo.
8. **[ ] Estabilidade de Conexão**: Desative o Wi-Fi e tente processar (sem Modo Demo). Verifique se o app mostra a mensagem: *"Sem internet? Use o modo básico com MediaPipe"*.

---
**Data do Teste**: ___ / ___ / 2026
**Assinatura do QA**: _______________________
