FROM python:3.8-slim
RUN pip install pipenv
ENV PROJECT_DIR /usr/local/src/myapp
WORKDIR ${PROJECT_DIR}
COPY Pipfile Pipfile.lock ${PROJECT_DIR}/
RUN pipenv install --system --deploy
COPY src/ ${PROJECT_DIR}/
EXPOSE 8050
# ENTRYPOINT ["python", "./cryptobot_api.py"]
ENTRYPOINT ["python", "./plotly_test.py"]