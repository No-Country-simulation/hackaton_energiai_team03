import json
import pandas as pd
import numpy as np
import joblib

# Carregar o modelo e os codificadores salvos (Muito mais rápido e performático)
modelo = joblib.load('modelo_chillywatts.pkl')
le_estacao = joblib.load('le_estacao.pkl')
le_pico = joblib.load('le_pico.pkl')
le_borracha = joblib.load('le_borracha.pkl')

# Função analítica para processar um estabelecimento
def analisar_cliente(dados_cliente):
    # RN02.2 - Cálculo Teórico
    potencia = 0.25 if dados_cliente['tipo_predominante'] == 'Exibicao' and dados_cliente['tecnologia_predominante'] == 'Convencional' else 0.18
    fator = 1.25 if dados_cliente['estado_borracha'] == 'Gasta' else 1.0
    consumo_teorico = round(dados_cliente['quantidade_equipamentos'] * (potencia * 24 * 30 * fator), 2)
    
    # RF03 / RN01 - Predição por Machine Learning
    input_ia = pd.DataFrame([{
        'estacao_num': le_estacao.transform([dados_cliente['estacao_ano']])[0],
        'pico_num': le_pico.transform([dados_cliente['uso_horario_pico']])[0],
        'quantidade_equipamentos': dados_cliente['quantidade_equipamentos'],
        'borracha_num': le_borracha.transform([dados_cliente['estado_borracha']])[0],
        'consumo_teorico_estimado_kwh': consumo_teorico,
        'consumo_real_kwh': dados_cliente['consumo_real_kwh']
    }])
    
    perfil = modelo.predict(input_ia)[0]
    probabilidade = round(float(np.max(modelo.predict_proba(input_ia))), 2)
    
    # RN03 - Alertas
    alertas = []
    if dados_cliente['consumo_real_kwh'] > (consumo_teorico * 1.15):
        diff = round(((dados_cliente['consumo_real_kwh'] - consumo_teorico) / consumo_teorico) * 100)
        alertas.append({"tipo": "ANOMALIA", "mensagem": f"Consumo real {diff}% acima do esperado."})
    if dados_cliente['uso_horario_pico'] == 'Alto':
        alertas.append({"tipo": "HORARIO_PICO", "mensagem": "Uso crítico no horário de ponta (18h-21h)."})
        
    # RN04 - Custos e Projeção
    custo_atual = round(dados_cliente['consumo_real_kwh'] * 0.75, 2)
    energia_poupada = round(dados_cliente['consumo_real_kwh'] * 0.20, 2)
    economia_reais = round(energia_poupada * 0.75, 2)
    
    return {
        "estabelecimentoId": dados_cliente["estabelecimentoId"],
        "analise": {
            "perfilEnergetico": perfil,
            "probabilidade": probabilidade,
            "consumoRealKwh": dados_cliente["consumo_real_kwh"],
            "consumoTeoricoEstimadoKwh": consumo_teorico,
            "custoMensalAtual": custo_atual,
            "energiaEconomizadaPotencialKwh": energia_poupada
        },
        "economiaEstimadaPotencialReais": economia_reais,
        "alertas": alertas,
        "recomendacoes": ["Monitore o pico diário", "Faça manutenção das borrachas"]
    }

# ==========================================
# RNF04: OS 3 CENÁRIOS OBRIGATÓRIOS PARA OS JURADOS
# ==========================================
cenarios_teste = [
    {"estabelecimentoId": "loja_eficiente_01", "estacao_ano": "Inverno", "uso_horario_pico": "Baixo", "quantidade_equipamentos": 4, "tipo_predominante": "Armazenamento", "tecnologia_predominante": "Inverter", "estado_borracha": "Integra", "consumo_real_kwh": 310.0},
    {"estabelecimentoId": "loja_moderado_02", "estacao_ano": "Primavera", "uso_horario_pico": "Medio", "quantidade_equipamentos": 5, "tipo_predominante": "Exibicao", "tecnologia_predominante": "Inverter", "estado_borracha": "Integra", "consumo_real_kwh": 750.0},
    {"estabelecimentoId": "loja_ineficiente_03", "estacao_ano": "Verao", "uso_horario_pico": "Alto", "quantidade_equipamentos": 7, "tipo_predominante": "Exibicao", "tecnologia_predominante": "Convencional", "estado_borracha": "Gasta", "consumo_real_kwh": 1850.0}
]

print("--- EXIBINDO OS 3 CENÁRIOS DE TESTE EXIGIDOS (RNF04) ---")
for cenario in cenarios_teste:
    resultado = analisar_cliente(cenario)
    print(json.dumps(resultado, indent=4, ensure_ascii=False))
    print("-" * 50)