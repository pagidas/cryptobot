FROM python:slim
RUN pip install rethinkdb
WORKDIR /usr/local/src
COPY init_db.py .
# CMD ["tail", "-f", "/dev/null"]
CMD [ "python", "init_db.py" ]