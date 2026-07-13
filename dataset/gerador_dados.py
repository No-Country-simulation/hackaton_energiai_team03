import numpy as np
import pandas as pd

np.random.seed(42)
n_amostras = 1000

estacao = np.random.choice(
    ['Verao', 'Outono', 'Inverno', 'Primavera'], n_amostras
)
uso_horario_pico = np.random.choice(['Alto', 'Medio', 'Baixo'], n_amostras)
quantidade_equipamentos = np.random.randint(3, 11, n_amostras)

consumo_teorico_lista = []
estado_borracha_lista = []
tipo_predominante_lista = []
tecnologia_predominante_lista = []

for i in range(n_amostras):
  qtd = quantidade_equipamentos[i]
  tipos = np.random.choice(['Exibicao', 'Armazenamento'], qtd, p=[0.6, 0.4])
  tecnologias = np.random.choice(['Inverter', 'Convencional'], qtd, p=[0.4, 0.6])
  borrachas = np.random.choice(['Integra', 'Gasta'], qtd, p=[0.7, 0.3])

  perc_borracha_gasta = np.sum(borrachas == 'Gasta') / qtd
  estado_borracha_est = 'Gasta' if perc_borracha_gasta > 0.3 else 'Integra'
  estado_borracha_lista.append(estado_borracha_est)

  tipo_predominante_lista.append(
      'Exibicao' if np.sum(tipos == 'Exibicao') >= qtd / 2 else 'Armazenamento'
  )
  tecnologia_predominante_lista.append(
      'Convencional'
      if np.sum(tecnologias == 'Convencional') >= qtd / 2
      else 'Inverter'
  )

  consumo_total_aparelhos = 0
  for t, tec, bor in zip(tipos, tecnologias, borrachas):
    if t == 'Exibicao' and tec == 'Convencional':
      potencia = 0.25
    elif t == 'Exibicao' and tec == 'Inverter':
      potencia = 0.18
    elif t == 'Armazenamento' and tec == 'Convencional':
      potencia = 0.15
    else:
      potencia = 0.10

    fator_desgaste = 1.25 if bor == 'Gasta' else 1.0
    consumo_aparelho = potencia * 24 * 30 * fator_desgaste
    consumo_total_aparelhos += consumo_aparelho

  consumo_teorico_lista.append(consumo_total_aparelhos)

consumo_teorico = np.array(consumo_teorico_lista)

impacto_clima = np.where(
    estacao == 'Verao',
    1.25,
    np.where(estacao == 'Inverno', 0.85, 1.0),
)
impacto_comportamento = np.where(
    uso_horario_pico == 'Alto',
    1.20,
    np.where(uso_horario_pico == 'Baixo', 0.95, 1.05),
)
ruido = np.random.uniform(0.95, 1.15, n_amostras)
consumo_real = consumo_teorico * impacto_clima * impacto_comportamento * ruido

categorias = []
for i in range(n_amostras):
  condicoes_ineficiente = 0
  if uso_horario_pico[i] == 'Alto':
    condicoes_ineficiente += 1
  if estado_borracha_lista[i] == 'Gasta':
    condicoes_ineficiente += 1
  if consumo_real[i] > consumo_teorico[i] * 1.20:
    condicoes_ineficiente += 1

  if condicoes_ineficiente >= 2:
    categorias.append('Ineficiente')
  elif (
      estado_borracha_lista[i] == 'Integra'
      and uso_horario_pico[i] == 'Baixo'
      and consumo_real[i] <= consumo_teorico[i] * 1.10
  ):
    categorias.append('Eficiente')
  else:
    categorias.append('Moderado')

df_sorveteria = pd.DataFrame({
    'estacao_ano': estacao,
    'uso_horario_pico': uso_horario_pico,
    'quantidade_equipamentos': quantidade_equipamentos,
    'tipo_predominante': tipo_predominante_lista,
    'tecnologia_predominante': tecnologia_predominante_lista,
    'estado_borracha': estado_borracha_lista,
    'consumo_teorico_estimado_kwh': np.round(consumo_teorico, 2),
    'consumo_real_kwh': np.round(consumo_real, 2),
    'categoria_eficiencia': categorias,
})

df_sorveteria.to_csv('dataset_energia_sorveteria.csv', index=False)
print('Dataset gerado com sucesso!')
