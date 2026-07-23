# Usa uma imagem oficial e enxuta do Python para não torrar os créditos da OCI
FROM python:3.12-slim

# Define o diretório de trabalho isolado dentro do container
WORKDIR /app

# Copia apenas o arquivo de dependências primeiro para otimizar o cache estrutural do Docker
COPY requirements.txt .

# Instala as bibliotecas exigidas ignorando bloqueios de sistema host
RUN pip install --no-cache-dir -r requirements.txt

# Instala curl para permitir healthchecks no container
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copia o script Flask e os arquivos .pkl do modelo para dentro da imagem
COPY . .

# Expõe a porta que o Flask utiliza para escutar as requisições do Spring Boot
EXPOSE 5000

# Define o comando que mantém o container vivo e a inteligência de dados no ar
CMD ["python", "ml_service.py"]