FROM python:3.8-slim
COPY requirements.txt requirements.txt
RUN pip install -r requirements.txt
COPY src/ myapp/
WORKDIR /myapp/
ENTRYPOINT ["python", "./main.py"]