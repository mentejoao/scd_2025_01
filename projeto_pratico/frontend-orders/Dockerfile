FROM python:3.10-slim-buster

WORKDIR /app

COPY requirements.txt .

COPY mock_products.csv .

RUN pip install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 8501

CMD ["bash", "-c", "python notification_server.py & streamlit run app_mvc.py --server.port=8501 --server.enableCORS=true --server.enableXsrfProtection=false"]
