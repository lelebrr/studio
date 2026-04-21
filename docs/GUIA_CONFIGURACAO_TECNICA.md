# 🛠️ Guia de Configuração Técnica (Gerência / TI)

Este documento contém os passos necessários para configurar e manter o sistema T-Line Car Studio Pro operacional na sua concessionária.

## 1. Instalação do Aplicativo

O app é fornecido como um arquivo APK de instalação direta (Enterprise).
1. Transfira o arquivo `T-Line-Studio-Pro-v1.7.apk` para o celular.
2. Ative "Instalar aplicativos de fontes desconhecidas" nas configurações do Android.
3. Execute o APK e siga os passos de instalação.

## 2. Configuração da API OpenRouter (O "Cérebro" do App)

O sistema utiliza a tecnologia OpenRouter para processar as imagens via IA de Elite.
1. Acesse [openrouter.ai](https://openrouter.ai/).
2. Crie uma conta e adicione créditos (recomenda-se um valor inicial de $10 a $20).
3. Gere uma **API KEY** (Chave de API).
4. No aplicativo T-Line, vá em **Configurações** e cole a chave no campo indicado.
5. Toque em **TESTAR CONEXÃO**. Se aparecer o ícone verde, o sistema está pronto.

## 3. Modo Concessionária (Produção)

Para garantir que todos os vendedores operem no mesmo padrão de qualidade:
*   Ative o **"Modo Concessionária"** nas configurações principais.
*   Isso habilitará automaticamente a resolução **4K**, o processamento híbrido (Gemini + FLUX) e o salvamento na pasta 'Vendas'.
*   Mantenha a opção **"Salvar na Galeria"** ativada para backup local.

## 4. Manutenção e Atualizações

*   **Custos de IA**: Monitore o saldo no painel do OpenRouter. O consumo médio por foto editada em alta qualidade é de alguns centavos de dólar.
*   **Atualização**: Quando uma nova versão for enviada, basta instalar o novo APK por cima do antigo. As configurações de chave API serão preservadas.

---
**Suporte Técnico**: Caso a conexão com a IA falhe consistentemente, verifique o status do site openrouter.ai ou o saldo da sua conta.
