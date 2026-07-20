import json

# Define the notebook structure
notebook = {
 "cells": [
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "# ChillyWatts - Inteligência Energética para Sorveterias 🍦⚡\n",
    "**Hackathon ONE – Projetos G9 | Alura + Oracle**\n",
    "\n",
    "Este notebook documenta todo o processo de desenvolvimento da inteligência analítica do projeto **ChillyWatts**. A solução foi desenhada especificamente para **pequenas sorveterias** (como o exemplo da Sorveteria do Bruno), auxiliando seus proprietários a identificar desperdícios ocultos nos freezers, entender seus hábitos de consumo e tomar decisões eficientes de consumo de energia com base em Ciência de Dados.\n",
    "\n",
    "---\n",
    "\n",
    "### 🎯 Objetivo do Projeto ChillyWatts\n",
    "Muitas sorveterias operam no escuro em relação ao consumo individual de seus freezers. O projeto ChillyWatts cruza dados operacionais simplificados com um modelo de Machine Learning treinado para:\n",
    "1. **Analisar** o consumo real em relação a um Consumo Teórico de Referência baseado em inventário.\n",
    "2. **Classificar** a eficiência energética do estabelecimento em: *Eficiente*, *Moderado* ou *Ineficiente*.\n",
    "3. **Sinalizar Alertas** críticos de anomalia de consumo e desperdício em horário de pico.\n",
    "4. **Gerar Recomendações Sustentáveis** personalizadas e calcular a projeção de economia financeira (baseada na tarifa padrão de R$ 0,75 por kWh).\n",
    "5. **Disponibilizar os Resultados em JSON** estruturado para fácil consumo pela API Rest (Java/Spring Boot) e Frontend.\n",
    "\n",
    "---\n",
    "\n",
    "### 🚀 Conteúdo do Notebook:\n",
    "1. **Exploração e Limpeza de Dados (EDA):** Carregamento e análise estrutural do dataset de consumo de energia elétrica de sorveterias.\n",
    "2. **Análise de Padrões de Consumo (Visualização):** Estudo de impacto sobre a Sazonalidade Climática (Bloco A), Tarifa Horária / Pico Diário (Bloco B) e Desgaste / Borrachas de Vedação (Bloco C).\n",
    "3. **Tratamento e Transformação de Variáveis:** Codificação e preparação dos dados para os algoritmos de ML.\n",
    "4. **Treinamento de Modelos Supervisionados:** Construção e comparação de modelos de classificação (*Regressão Logística*, *Árvore de Decisão* e *Random Forest*).\n",
    "5. **Avaliação Detalhada dos Modelos:** Comparação de métricas clássicas (Acurácia, Precisão, Recall, F1-Score) e Matriz de Confusão do melhor classificador.\n",
    "6. **Motor de Análise (Regras & Predição):** Junção da predição do modelo com as regras de negócio para geração completa do payload JSON de resposta.\n",
    "7. **Validação nos 3 Cenários Exigidos (RNF04):** Testes práticos nos cenários Real/Simulado (Eficiente, Moderado e Ineficiente) exigidos no edital.\n",
    "8. **Serialização dos Modelos:** Salvamento dos artefatos para fácil integração no backend."
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "## 0. Configurações de Ambiente e Dependências"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "import os\n",
    "import json\n",
    "import numpy as np\n",
    "import pandas as pd\n",
    "import matplotlib.pyplot as plt\n",
    "import seaborn as sns\n",
    "from sklearn.model_selection import train_test_split\n",
    "from sklearn.preprocessing import LabelEncoder\n",
    "from sklearn.linear_model import LogisticRegression\n",
    "from sklearn.tree import DecisionTreeClassifier\n",
    "from sklearn.ensemble import RandomForestClassifier\n",
    "from sklearn.metrics import classification_report, accuracy_score, confusion_matrix, ConfusionMatrixDisplay\n",
    "import joblib\n",
    "\n",
    "# Configurações gráficas\n",
    "%matplotlib inline\n",
    "sns.set_theme(style=\"whitegrid\")\n",
    "plt.rcParams['figure.figsize'] = [10, 6]\n",
    "plt.rcParams['font.size'] = 11\n",
    "\n",
    "print(\"Ambiente de Data Science configurado com sucesso!\")"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "## 1. Exploração e Limpeza de Dados (EDA)\n",
    "Carregamos a base de dados `dataset_energia_sorveteria.csv` gerada a partir das especificações e regras operacionais de sorveterias reais."
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "csv_path = 'dataset_energia_sorveteria.csv'\n",
    "\n",
    "# Se o CSV não existir, rodamos o script gerador\n",
    "if not os.path.exists(csv_path):\n",
    "    print(\"Arquivo CSV não encontrado. Gerando base de dados...\")\n",
    "    import subprocess\n",
    "    subprocess.run([\"python\", \"gerador_dados.py\"], capture_output=True)\n",
    "\n",
    "df = pd.read_csv(csv_path)\n",
    "print(f\"Dataset carregado com {df.shape[0]} linhas e {df.shape[1]} colunas.\")\n",
    "print(\"\\nVisualização das primeiras 5 linhas:\")\n",
    "df.head()"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "### 1.1 Análise de Tipos e Valores Faltantes\n",
    "Antes de modelar, realizamos o diagnóstico clássico de limpeza: checar tipos de dados e valores nulos ou duplicados."
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "print(\"--- Tipos de Dados e Informações do Dataset ---\")\n",
    "print(df.info())\n",
    "\n",
    "print(\"\\n--- Verificação de Valores Faltantes (Nulos) ---\")\n",
    "print(df.isnull().sum())\n",
    "\n",
    "print(\"\\n--- Verificação de Duplicatas ---\")\n",
    "print(f\"Quantidade de registros duplicados: {df.duplicated().sum()}\")"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "### 1.2 Estatísticas Descritivas Básicas\n",
    "Analisamos as características numéricas e a distribuição das variáveis categóricas centrais."
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "print(\"--- Estatísticas de Variáveis Numéricas ---\")\n",
    "display(df.describe())\n",
    "\n",
    "print(\"\\n--- Distribuição das Classes de Eficiência Energética ---\")\n",
    "display(df['categoria_eficiencia'].value_counts(normalize=True) * 100)"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "## 2. Análise de Padrões de Consumo (EDA Visual)\n",
    "Investigamos visualmente os fatores descritos nos Blocos A, B e C da documentação para embasar nossas decisões de Ciência de Dados."
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "### 2.1 Distribuição de Consumo por Categoria de Eficiência"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "plt.figure(figsize=(10, 5))\n",
    "sns.boxplot(data=df, x='categoria_eficiencia', y='consumo_real_kwh', palette='Set2', hue='categoria_eficiencia', legend=False)\n",
    "plt.title('Consumo Real Mensal (kWh) por Perfil de Eficiência', fontsize=13, fontweight='bold')\n",
    "plt.xlabel('Perfil de Eficiência', fontsize=11)\n",
    "plt.ylabel('Consumo Real (kWh)', fontsize=11)\n",
    "plt.tight_layout()\n",
    "plt.show()"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "### 2.2 Sazonalidade Climática (O Pico Anual - Bloco A)\n",
    "A análise de sazonalidade nos revela como a temperatura externa e as estações influenciam os perfis operacionais e o consumo."
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "plt.figure(figsize=(11, 5.5))\n",
    "sns.barplot(data=df, x='estacao_ano', y='consumo_real_kwh', hue='categoria_eficiencia', palette='viridis', ci=None)\n",
    "plt.title('Média de Consumo por Estação do Ano e Perfil de Eficiência', fontsize=13, fontweight='bold')\n",
    "plt.xlabel('Estação do Ano', fontsize=11)\n",
    "plt.ylabel('Média de Consumo Real (kWh)', fontsize=11)\n",
    "plt.legend(title='Perfil de Eficiência')\n",
    "plt.tight_layout()\n",
    "plt.show()"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "### 2.3 Uso no Horário de Pico (Tarifa Horária - Bloco B)\n",
    "O preço da energia comercial muda. Abrir as portas ou abastecer os freezers na faixa crítica (18h-21h) acarreta em consumo alto quando a energia é cara."
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "plt.figure(figsize=(10, 5))\n",
    "sns.violinplot(data=df, x='uso_horario_pico', y='consumo_real_kwh', palette='coolwarm', hue='uso_horario_pico', legend=False)\n",
    "plt.title('Consumo Real (kWh) de acordo com a Intensidade de Uso no Horário de Pico', fontsize=13, fontweight='bold')\n",
    "plt.xlabel('Intensidade de Abertura/Abastecimento entre 18h e 21h', fontsize=11)\n",
    "plt.ylabel('Consumo Real (kWh)', fontsize=11)\n",
    "plt.tight_layout()\n",
    "plt.show()"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "### 2.4 Estado das Borrachas de Vedação (Cegueira por Equipamento - Bloco C)\n",
    "Equipamentos antigos ou com borrachas gastas aumentam silenciosamente o consumo total."
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "plt.figure(figsize=(10, 5))\n",
    "sns.boxplot(data=df, x='estado_borracha', y='consumo_real_kwh', hue='categoria_eficiencia', palette='Set1')\n",
    "plt.title('Impacto do Estado da Borracha e Categoria no Consumo Real', fontsize=13, fontweight='bold')\n",
    "plt.xlabel('Estado da Borracha de Vedação', fontsize=11)\n",
    "plt.ylabel('Consumo Real (kWh)', fontsize=11)\n",
    "plt.legend(title='Perfil de Eficiência')\n",
    "plt.tight_layout()\n",
    "plt.show()"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "### 2.5 Matriz de Correlação das Variáveis\n",
    "Codificamos as classes e visualizamos a correlação linear entre os indicadores numéricos e as condições simuladas."
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "# Criação de DataFrame numérico temporário\n",
    "df_corr = df.copy()\n",
    "df_corr['estacao_num'] = LabelEncoder().fit_transform(df['estacao_ano'])\n",
    "df_corr['pico_num'] = LabelEncoder().fit_transform(df['uso_horario_pico'])\n",
    "df_corr['borracha_num'] = LabelEncoder().fit_transform(df['estado_borracha'])\n",
    "\n",
    "plt.figure(figsize=(9, 7))\n",
    "features_corr = ['estacao_num', 'pico_num', 'quantidade_equipamentos', 'borracha_num', 'consumo_teorico_estimado_kwh', 'consumo_real_kwh']\n",
    "corr = df_corr[features_corr].corr()\n",
    "sns.heatmap(corr, annot=True, cmap='RdBu_r', fmt=\".2f\", linewidths=0.5, vmin=-1, vmax=1)\n",
    "plt.title('Matriz de Correlação Linear (Pearson)', fontsize=13, fontweight='bold')\n",
    "plt.tight_layout()\n",
    "plt.show()"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "## 3. Tratamento e Transformação de Variáveis\n",
    "Preparamos o conjunto de dados convertendo variáveis categóricas estruturadas usando `LabelEncoder` e dividindo as amostras em dados de treino (80%) e teste (20%)."
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "# Instanciação dos codificadores\n",
    "le_estacao = LabelEncoder()\n",
    "le_pico = LabelEncoder()\n",
    "le_borracha = LabelEncoder()\n",
    "\n",
    "df_proc = df.copy()\n",
    "df_proc['estacao_num'] = le_estacao.fit_transform(df_proc['estacao_ano'])\n",
    "df_proc['pico_num'] = le_pico.fit_transform(df_proc['uso_horario_pico'])\n",
    "df_proc['borracha_num'] = le_borracha.fit_transform(df_proc['estado_borracha'])\n",
    "\n",
    "# Definição de X (features) e y (target)\n",
    "features_cols = ['estacao_num', 'pico_num', 'quantidade_equipamentos', 'borracha_num', 'consumo_teorico_estimado_kwh', 'consumo_real_kwh']\n",
    "X = df_proc[features_cols]\n",
    "y = df_proc['categoria_eficiencia']\n",
    "\n",
    "# Divisão de treino/teste de forma estratificada para manter a distribuição do target\n",
    "X_train, X_test, y_train, y_test = train_test_split(\n",
    "    X, y, test_size=0.20, random_state=42, stratify=y\n",
    ")\n",
    "\n",
    "print(f\"X_train shape: {X_train.shape}, y_train shape: {y_train.shape}\")\n",
    "print(f\"X_test shape: {X_test.shape}, y_test shape: {y_test.shape}\")"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "## 4. Treinamento de Modelos Supervisionados\n",
    "Treinamos e comparamos algoritmos sugeridos pelo edital:\n",
    "1. **Regressão Logística**\n",
    "2. **Árvore de Decisão**\n",
    "3. **Random Forest Classifier**"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "# 1. Regressão Logística\n",
    "lr_clf = LogisticRegression(max_iter=1000, random_state=42)\n",
    "lr_clf.fit(X_train, y_train)\n",
    "y_pred_lr = lr_clf.predict(X_test)\n",
    "\n",
    "# 2. Árvore de Decisão\n",
    "dt_clf = DecisionTreeClassifier(max_depth=5, random_state=42)\n",
    "dt_clf.fit(X_train, y_train)\n",
    "y_pred_dt = dt_clf.predict(X_test)\n",
    "\n",
    "# 3. Random Forest (Ensemble)\n",
    "rf_clf = RandomForestClassifier(n_estimators=100, max_depth=8, random_state=42)\n",
    "rf_clf.fit(X_train, y_train)\n",
    "y_pred_rf = rf_clf.predict(X_test)\n",
    "\n",
    "print(\"Modelos supervisionados treinados com sucesso!\")"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "## 5. Avaliação Utilizando Métricas Adequadas"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "print(\"===============================================\")\n",
    "print(\"            COMPARATIVO DE ACURÁCIA            \")\n",
    "print(\"===============================================\")\n",
    "print(f\"Regressão Logística: {accuracy_score(y_test, y_pred_lr) * 100:.2f}%\")\n",
    "print(f\"Árvore de Decisão:   {accuracy_score(y_test, y_pred_dt) * 100:.2f}%\")\n",
    "print(f\"Random Forest:       {accuracy_score(y_test, y_pred_rf) * 100:.2f}%\")\n",
    "print(\"===============================================\")"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "### 5.1 Relatório de Classificação Detalhado (Melhor Modelo: Random Forest)\n",
    "Examinamos métricas por classe de eficácia: *Eficiente*, *Moderado* e *Ineficiente*."
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "print(\"Relatório de Classificação:\")\n",
    "print(classification_report(y_test, y_pred_rf))"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "### 5.2 Matriz de Confusão\n",
    "Garante que o modelo não está confundindo lojas eficientes com ineficientes."
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "cm = confusion_matrix(y_test, y_pred_rf, labels=rf_clf.classes_)\n",
    "fig, ax = plt.subplots(figsize=(7, 5))\n",
    "disp = ConfusionMatrixDisplay(confusion_matrix=cm, display_labels=rf_clf.classes_)\n",
    "disp.plot(cmap='Blues', ax=ax)\n",
    "plt.title('Matriz de Confusão - Classificador ChillyWatts', fontsize=12, fontweight='bold')\n",
    "plt.grid(False)\n",
    "plt.show()"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "## 6. Motor Analítico ChillyWatts (Regras, Custos e Economia)\n",
    "De acordo com a documentação, implementamos uma função unificada de negócio para receber os dados do usuário, calcular o consumo teórico do inventário, invocar a IA para obter classificação e probabilidade, mapear alertas críticos e listar as recomendações sustentáveis personalizadas."
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "def analisar_cliente_completo(dados_cliente):\n",
    "    # RN02.2 - Consumo Teórico Mensal\n",
    "    # Definir potência base com base no tipo predominante e tecnologia cadastrada\n",
    "    if dados_cliente['tipo_predominante'] == 'Exibicao':\n",
    "        potencia = 0.25 if dados_cliente['tecnologia_predominante'] == 'Convencional' else 0.18\n",
    "    else:\n",
    "        potencia = 0.15 if dados_cliente['tecnologia_predominante'] == 'Convencional' else 0.10\n",
    "        \n",
    "    fator_desgaste = 1.25 if dados_cliente['estado_borracha'] == 'Gasta' else 1.0\n",
    "    \n",
    "    # Fórmula: Potência * 24 horas * 30 dias * quantidade * Fator Desgaste\n",
    "    consumo_teorico = round(dados_cliente['quantidade_equipamentos'] * (potencia * 24 * 30 * fator_desgaste), 2)\n",
    "    \n",
    "    # Codificar variáveis de entrada para alimentar a IA\n",
    "    estacao_num = le_estacao.transform([dados_cliente['estacao_ano']])[0]\n",
    "    pico_num = le_pico.transform([dados_cliente['uso_horario_pico']])[0]\n",
    "    borracha_num = le_borracha.transform([dados_cliente['estado_borracha']])[0]\n",
    "    \n",
    "    input_df = pd.DataFrame([{ \n",
    "        'estacao_num': estacao_num,\n",
    "        'pico_num': pico_num,\n",
    "        'quantidade_equipamentos': dados_cliente['quantidade_equipamentos'],\n",
    "        'borracha_num': borracha_num,\n",
    "        'consumo_teorico_estimado_kwh': consumo_teorico,\n",
    "        'consumo_real_kwh': dados_cliente['consumo_real_kwh']\n",
    "    }])\n",
    "    \n",
    "    # Predição\n",
    "    perfil = rf_clf.predict(input_df)[0]\n",
    "    probabilidade = round(float(np.max(rf_clf.predict_proba(input_df))), 2)\n",
    "    \n",
    "    # RN03 - Geração de Alertas\n",
    "    alertas = []\n",
    "    if dados_cliente['consumo_real_kwh'] > (consumo_teorico * 1.15):\n",
    "        diff = round(((dados_cliente['consumo_real_kwh'] - consumo_teorico) / consumo_teorico) * 100)\n",
    "        alertas.append({\n",
    "            \"tipo\": \"ANOMALIA\",\n",
    "            \"mensagem\": f\"Seu consumo real está {diff}% acima do esperado para o seu inventário de freezers. Risco alto de problemas de vedação.\"\n",
    "        })\n",
    "    if dados_cliente['uso_horario_pico'] == 'Alto':\n",
    "        alertas.append({\n",
    "            \"tipo\": \"HORARIO_PICO\",\n",
    "            \"mensagem\": \"Uso crítico de freezers de exibição detectado entre 18h e 21h.\"\n",
    "        })\n",
    "        \n",
    "    # RN04 - Cálculos Financeiros (R$ 0,75/kWh de referência do edital)\n",
    "    tarifa = 0.75\n",
    "    custo_atual = round(dados_cliente['consumo_real_kwh'] * tarifa, 2)\n",
    "    \n",
    "    # RN04.2 - Projeção de Economia de Energia (20% fixo)\n",
    "    energia_poupada_kwh = round(dados_cliente['consumo_real_kwh'] * 0.20, 2)\n",
    "    economia_reais = round(energia_poupada_kwh * tarifa, 2)\n",
    "    \n",
    "    # Geração de Recomendações (RF06)\n",
    "    recomendacoes = []\n",
    "    if dados_cliente['uso_horario_pico'] in ['Alto', 'Medio']:\n",
    "        recomendacoes.append(\"Abasteça os freezers de exibição antes das 18h para evitar a abertura de portas no horário de pico tarifário.\")\n",
    "    \n",
    "    if dados_cliente['estacao_ano'] in ['Inverno', 'Outono']:\n",
    "        recomendacoes.append(\"Estamos na temporada de Outono/Inverno. Desligue o freezer de armazenamento secundário (estoque) e consolide os produtos.\")\n",
    "        \n",
    "    if dados_cliente['estado_borracha'] == 'Gasta':\n",
    "        marca_freezer = dados_cliente.get('marca', 'cadastrado')\n",
    "        recomendacoes.append(f\"Agende uma manutenção preventiva para trocar a borracha do Freezer {dados_cliente['tipo_predominante']} ({marca_freezer}).\")\n",
    "        \n",
    "    if dados_cliente['tecnologia_predominante'] == 'Convencional':\n",
    "        recomendacoes.append(\"Substitua gradualmente equipamentos convencionais por tecnologia Inverter, reduzindo o consumo de partida do motor.\")\n",
    "        \n",
    "    if not recomendacoes:\n",
    "        recomendacoes.append(\"Parabéns! Sua operação está eficiente. Continue monitorando o inventário periodicamente.\")\n",
    "        \n",
    "    return {\n",
    "        \"estabelecimentoId\": dados_cliente[\"estabelecimentoId\"],\n",
    "        \"analise\": {\n",
    "            \"perfilEnergetico\": perfil,\n",
    "            \"probabilidade\": probabilidade,\n",
    "            \"consumoRealKwh\": dados_cliente[\"consumo_real_kwh\"],\n",
    "            \"consumoTeoricoEstimadoKwh\": consumo_teorico,\n",
    "            \"custoMensalAtual\": custo_atual,\n",
    "            \"energiaEconomizadaPotencialKwh\": energia_poupada_kwh,\n",
    "            \"economiaEstimadaPotencialReais\": economia_reais\n",
    "        },\n",
    "        \"alertas\": alertas,\n",
    "        \"recomendacoes\": recomendacoes\n",
    "    }"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "## 7. Conjunto Mínimo de Testes (RNF04 - Massa de Dados)\n",
    "Para demonstração imediata aos jurados, avaliamos os 3 cenários de teste exigidos na documentação oficial:"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "cenarios_teste_oficiais = [\n",
    "    {\n",
    "        \"titulo\": \"Cenário 1: Loja Eficiente (Inverter, Baixa Temporada, Baixo Pico, Borracha Íntegra)\",\n",
    "        \"estabelecimentoId\": \"loja_eficiente_01\",\n",
    "        \"marca\": \"Gelopar\",\n",
    "        \"estacao_ano\": \"Inverno\",\n",
    "        \"uso_horario_pico\": \"Baixo\",\n",
    "        \"quantidade_equipamentos\": 4,\n",
    "        \"tipo_predominante\": \"Armazenamento\",\n",
    "        \"tecnologia_predominante\": \"Inverter\",\n",
    "        \"estado_borracha\": \"Integra\",\n",
    "        \"consumo_real_kwh\": 310.0\n",
    "    },\n",
    "    {\n",
    "        \"titulo\": \"Cenário 2: Loja Moderada (Inverter, Pico Médio, Borracha Íntegra)\",\n",
    "        \"estabelecimentoId\": \"loja_moderado_02\",\n",
    "        \"marca\": \"Metalfrio\",\n",
    "        \"estacao_ano\": \"Primavera\",\n",
    "        \"uso_horario_pico\": \"Medio\",\n",
    "        \"quantidade_equipamentos\": 5,\n",
    "        \"tipo_predominante\": \"Exibicao\",\n",
    "        \"tecnologia_predominante\": \"Inverter\",\n",
    "        \"estado_borracha\": \"Integra\",\n",
    "        \"consumo_real_kwh\": 750.0\n",
    "    },\n",
    "    {\n",
    "        \"titulo\": \"Cenário 3: Loja Crítica/Ineficiente (Convencional, Alta Temporada, Alto Pico, Borracha Gasta)\",\n",
    "        \"estabelecimentoId\": \"sorveteria_bruno_01\",\n",
    "        \"marca\": \"Fricon\",\n",
    "        \"estacao_ano\": \"Verao\",\n",
    "        \"uso_horario_pico\": \"Alto\",\n",
    "        \"quantidade_equipamentos\": 7,\n",
    "        \"tipo_predominante\": \"Exibicao\",\n",
    "        \"tecnologia_predominante\": \"Convencional\",\n",
    "        \"estado_borracha\": \"Gasta\",\n",
    "        \"consumo_real_kwh\": 1850.0\n",
    "    }\n ]\n",
    "\n",
    "for cenario in cenarios_teste_oficiais:\n",
    "    titulo = cenario.pop('titulo')\n",
    "    print(f\"\\n>>> {titulo} \")\n",
    "    resultado_json = analisar_cliente_completo(cenario)\n",
    "    print(json.dumps(resultado_json, indent=4, ensure_ascii=False))\n",
    "    print(\"-\"*80)"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "## 8. Serialização do Modelo\n",
    "Salvamos o classificador treinado (`Random Forest`) e os codificadores (`LabelEncoder`) em formato binário `.pkl` usando `joblib`, garantindo a persistência dos modelos para integração com a API Backend de forma performática."
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "joblib.dump(rf_clf, 'modelo_chillywatts.pkl')\n",
    "joblib.dump(le_estacao, 'le_estacao.pkl')\n",
    "joblib.dump(le_pico, 'le_pico.pkl')\n",
    "joblib.dump(le_borracha, 'le_borracha.pkl')\n",
    "\n",
    "print(\"Artefatos analíticos salvos localmente com sucesso!\")\n",
    "print(\"- 'modelo_chillywatts.pkl' (Random Forest Classifier)\")\n",
    "print(\"- 'le_estacao.pkl' (LabelEncoder Estações)\")\n",
    "print(\"- 'le_pico.pkl' (LabelEncoder Uso no Pico)\")\n",
    "print(\"- 'le_borracha.pkl' (LabelEncoder Estado Borracha)\")"
   ]
  }
 ],
 "metadata": {
  "kernelspec": {
   "display_name": "Python 3 (ipykernel)",
   "language": "python",
   "name": "python3"
  },
  "language_info": {
   "name": "python",
   "version": "3.13.0"
  }
 },
 "nbformat": 4,
 "nbformat_minor": 2
}

# Write out the notebook file
with open('notebook_chillywatts.ipynb', 'w', encoding='utf-8') as f:
    json.dump(notebook, f, indent=1, ensure_ascii=False)

print("Notebook notebook_chillywatts.ipynb construído com sucesso!")
