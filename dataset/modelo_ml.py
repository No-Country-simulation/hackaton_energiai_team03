import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import LabelEncoder
import joblib

# Carregar dados gerados
df = pd.read_csv('dataset_energia_sorveteria.csv')

# Codificadores
le_estacao = LabelEncoder()
le_pico = LabelEncoder()
le_borracha = LabelEncoder()

df['estacao_num'] = le_estacao.fit_transform(df['estacao_ano'])
df['pico_num'] = le_pico.fit_transform(df['uso_horario_pico'])
df['borracha_num'] = le_borracha.fit_transform(df['estado_borracha'])

X = df[['estacao_num', 'pico_num', 'quantidade_equipamentos', 'borracha_num', 'consumo_teorico_estimado_kwh', 'consumo_real_kwh']]
y = df['categoria_eficiencia']

# Treinando a IA
modelo_ia = RandomForestClassifier(n_estimators=100, random_state=42)
modelo_ia.fit(X, y)

# Salvando o modelo e os encoders em arquivos locais (Simulando OCI Object Storage)
joblib.dump(modelo_ia, 'modelo_chillywatts.pkl')
joblib.dump(le_estacao, 'le_estacao.pkl')
joblib.dump(le_pico, 'le_pico.pkl')
joblib.dump(le_borracha, 'le_borracha.pkl')

print("Modelo de Data Science treinado e salvo com sucesso como 'modelo_chillywatts.pkl'!")