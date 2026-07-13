from flask import Flask, request, jsonify
from flask_cors import CORS
import joblib
import pandas as pd
import numpy as np
import os

app = Flask(__name__)
CORS(app)

# Carrega modelo e encoders ao iniciar
base_dir = os.path.dirname(os.path.abspath(__file__))
modelo = joblib.load(os.path.join(base_dir, 'modelo_chillywatts.pkl'))
le_estacao = joblib.load(os.path.join(base_dir, 'le_estacao.pkl'))
le_pico = joblib.load(os.path.join(base_dir, 'le_pico.pkl'))
le_borracha = joblib.load(os.path.join(base_dir, 'le_borracha.pkl'))


ESTACAO_MAP = {'Verão': 'Verao', 'Verao': 'Verao'}

@app.route('/prever', methods=['POST'])
def prever():
    dados = request.get_json()

    try:
        estacao = ESTACAO_MAP.get(dados['estacao_ano'], dados['estacao_ano'])
        pico = dados['uso_horario_pico']
        qtd = int(dados['quantidade_equipamentos'])
        borracha = dados['estado_borracha']
        consumo_teorico = float(dados['consumo_teorico_estimado_kwh'])
        consumo_real = float(dados['consumo_real_kwh'])

        input_ia = pd.DataFrame([{
            'estacao_num': le_estacao.transform([estacao])[0],
            'pico_num': le_pico.transform([pico])[0],
            'quantidade_equipamentos': qtd,
            'borracha_num': le_borracha.transform([borracha])[0],
            'consumo_teorico_estimado_kwh': consumo_teorico,
            'consumo_real_kwh': consumo_real
        }])

        perfil = modelo.predict(input_ia)[0]
        probabilidade = round(float(np.max(modelo.predict_proba(input_ia))), 2)

        return jsonify({
            'categoria': perfil,
            'probabilidade': probabilidade
        })

    except Exception as e:
        return jsonify({'erro': str(e)}), 400


@app.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'ok'})


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)
