from fastapi import FastAPI, HTTPException, WebSocket, WebSocketDisconnect
from pydantic import BaseModel
from typing import List, Dict, Any
import uuid
from datetime import datetime
import json

app = FastAPI(title="Orders API", description="API para gerenciar pedidos de produtos")

class OrderItem(BaseModel):
    name: str
    quantity: int

orders_db = {}

# Gerenciador de conexões WebSocket
class ConnectionManager:
    def __init__(self):
        self.active_connections: List[WebSocket] = []

    async def connect(self, websocket: WebSocket):
        await websocket.accept()
        self.active_connections.append(websocket)

    def disconnect(self, websocket: WebSocket):
        self.active_connections.remove(websocket)

    async def broadcast(self, message: Dict[str, Any]):
        for connection in self.active_connections:
            await connection.send_json(message)

manager = ConnectionManager()

@app.post("/orders")
async def create_order(orders: List[OrderItem]):
    """
    Cria um novo pedido com os itens selecionados.
    
    Exemplo de payload:
    [
        {
            "name": "Teclado",
            "quantity": 2
        },
        {
            "name": "Mouse",
            "quantity": 3
        }
    ]
    """
    if not orders:
        raise HTTPException(status_code=400, detail="O pedido deve conter pelo menos um item")
    
    order_id = str(uuid.uuid4())
    
    order_data = {
        "id": order_id,
        "items": [{"name": item.name, "quantity": item.quantity} for item in orders],
        "date": datetime.now().isoformat(),
        "status": "PENDING"
    }
    
    orders_db[order_id] = order_data
    
    if manager.active_connections:
        await manager.broadcast({
            "type": "new_order",
            "message": f"Novo pedido recebido! ID: {order_id}",
            "order": order_data
        })
    
    return {"id": order_id, "status": "success"}

@app.websocket("/ws")
async def websocket_endpoint(websocket: WebSocket):
    await manager.connect(websocket)
    try:
        while True:
            data = await websocket.receive_text()
            # Processar mensagens recebidas (se necessário)
            try:
                message = json.loads(data)
                if "type" in message and message["type"] == "notification":
                    # Transmitir a notificação para todos os clientes
                    await manager.broadcast({
                        "type": "notification",
                        "message": message.get("message", "Nova notificação!"),
                        "level": message.get("level", "info")
                    })
            except json.JSONDecodeError:
                pass
    except WebSocketDisconnect:
        manager.disconnect(websocket)

# Para executar a API: uvicorn backend:app --reload --port 8080
