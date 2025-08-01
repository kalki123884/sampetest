FROM python:3.9.23
WORKDIR /app
RUN pip install fastapi && pip install uvicorn
COPY sample.py /app/.
ENTRYPOINT ["/bin/bash","-c","python /app/sample.py"]
