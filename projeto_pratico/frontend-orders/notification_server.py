"""
Servidor FastAPI para receber webhooks de notificação
"""
from fastapi import FastAPI, HTTPException, Request
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import uvicorn
import time
from datetime import datetime
import threading
import json

app = FastAPI(title="Notification Webhook Server", version="1.0.0")

# Configurar CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Lista global para armazenar notificações (persiste entre requests)
notifications_storage = []
notifications_lock = threading.Lock()

class NotificationPayload(BaseModel):
    """Modelo para o payload de notificação"""
    title: str
    description: str
    
    class Config:
        """Configuração do modelo"""
        json_encoders = {
            datetime: lambda v: v.isoformat()
        }
        json_schema_extra = {
            "example": {
                "title": "Pedido Processado",
                "description": "Seu pedido foi processado com sucesso!"
            }
        }

class NotificationResponse(BaseModel):
    """Modelo para resposta de notificação"""
    message: str
    status: str
    timestamp: str
    notification_id: str

@app.post("/notification", response_model=NotificationResponse)
async def receive_notification(payload: NotificationPayload):
    """
    Endpoint para receber webhooks de notificação
    
    Payload esperado:
    {
        "title": "Título da notificação",
        "description": "Descrição da notificação"
    }
    """
    try:
        print(f"📥 Payload recebido: {payload}")
        print(f"📝 Título: {payload.title}")
        print(f"📄 Descrição: {payload.description}")
        
        # Criar objeto de notificação com timestamp
        notification = {
            "title": payload.title,
            "description": payload.description,
            "timestamp": datetime.now().isoformat(),
            "notification_id": f"notif_{int(time.time() * 1000)}"
        }
        
        # Adicionar à lista de notificações
        with notifications_lock:
            notifications_storage.append(notification)
        
        print("🔔 Nova notificação recebida:")
        print(f"   Título: {payload.title}")
        print(f"   Descrição: {payload.description}")
        print(f"   Timestamp: {notification['timestamp']}")
        
        return NotificationResponse(
            message="Notificação recebida com sucesso",
            status="success",
            timestamp=notification['timestamp'],
            notification_id=notification['notification_id']
        )
        
    except Exception as e:
        print(f"❌ Erro ao processar notificação: {e}")
        raise HTTPException(status_code=500, detail=f"Erro interno: {str(e)}")

# Endpoint alternativo para debug
@app.post("/notification-debug")
async def receive_notification_debug(request: Request):
    """Endpoint alternativo para debug de problemas de parsing"""
    try:
        # Ler o body raw
        body = await request.body()
        print(f"📦 Body raw: {body}")
        
        # Tentar fazer parse manual
        import json
        data = json.loads(body)
        print(f"📋 Data parsed: {data}")
        
        title = data.get("title", "")
        description = data.get("description", "")
        
        if not title:
            raise HTTPException(status_code=400, detail="Campo 'title' é obrigatório")
        
        # Criar notificação
        notification = {
            "title": title,
            "description": description,
            "timestamp": datetime.now().isoformat(),
            "notification_id": f"notif_{int(time.time() * 1000)}"
        }
        
        with notifications_lock:
            notifications_storage.append(notification)
        
        return {
            "message": "Notificação recebida com sucesso (debug)",
            "status": "success",
            "data": notification
        }
        
    except json.JSONDecodeError as e:
        print(f"❌ Erro de JSON: {e}")
        raise HTTPException(status_code=400, detail=f"JSON inválido: {str(e)}")
    except Exception as e:
        print(f"❌ Erro geral: {e}")
        raise HTTPException(status_code=500, detail=f"Erro interno: {str(e)}")

@app.get("/notifications/latest")
async def get_latest_notifications():
    """
    Endpoint para o Streamlit consultar as notificações mais recentes
    """
    with notifications_lock:
        # Retornar todas as notificações e limpar a lista
        notifications = notifications_storage.copy()
        notifications_storage.clear()
        
    print(f"📤 Enviando {len(notifications)} notificações para Streamlit")
    if notifications:
        print(f"📋 Primeira notificação: {notifications[0]}")
    
    return {
        "notifications": notifications,
        "count": len(notifications),
        "timestamp": datetime.now().isoformat()
    }

@app.get("/health")
async def health_check():
    """Endpoint de health check"""
    return {
        "status": "healthy",
        "service": "Notification Webhook Server",
        "timestamp": datetime.now().isoformat(),
        "notifications_count": len(notifications_storage)
    }

@app.get("/")
async def root():
    """Endpoint raiz com informações do serviço"""
    return {
        "message": "Notification Webhook Server",
        "version": "1.0.0",
        "endpoints": {
            "webhook": "/notification [POST]",
            "latest": "/notifications/latest [GET]",
            "health": "/health [GET]"
        },
        "webhook_format": {
            "title": "string",
            "description": "string"
        }
    }

if __name__ == "__main__":
    print("🚀 Iniciando servidor de notificações...")
    print("📍 Endpoint webhook: http://localhost:8000/notification")
    print("📋 Documentação: http://localhost:8000/docs")
    print("🏥 Health check: http://localhost:8000/health")
    
    uvicorn.run(
        "notification_server:app", 
        host="0.0.0.0", 
        port=8000,
        reload=True,
        log_level="info"
    )
