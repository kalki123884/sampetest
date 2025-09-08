from fastapi import FastAPI
import uvicorn
app = FastAPI()
@app.get("/")
def test1():
    return "testing"
if __name__ == "__main__":
    uvicorn.run("sample:app",host="0.0.0.0",port=8000,reload=True)
