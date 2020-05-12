FROM quoinedev/python3.6-pandas-alpine
COPY requirements.txt requirements.txt
RUN pip install -r requirements.txt
COPY src/ myapp/
WORKDIR /myapp/
ENTRYPOINT ["python", "./main.py"]