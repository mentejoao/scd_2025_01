"""
Models - Representa as entidades do domínio seguindo princípios SOLID
"""
from dataclasses import dataclass
from typing import List, Optional
import time


@dataclass
class Product:
    """Modelo representando um produto no catálogo"""
    name: str
    image: str
    description: str
    stock_quantity: int
    
    def has_stock(self, requested_quantity: int) -> bool:
        """Verifica se há estoque suficiente para a quantidade solicitada"""
        return self.stock_quantity >= requested_quantity
    
    def is_available(self) -> bool:
        """Verifica se o produto está disponível (tem estoque > 0)"""
        return self.stock_quantity > 0


@dataclass
class CartItem:
    """Modelo representando um item no carrinho"""
    product_name: str
    quantity: int
    
    def is_valid(self) -> bool:
        """Valida se o item do carrinho é válido"""
        return self.quantity > 0 and self.product_name.strip() != ""


@dataclass
class Cart:
    """Modelo representando o carrinho de compras"""
    items: List[CartItem]
    
    def __init__(self):
        self.items = []
    
    def add_item(self, product_name: str, quantity: int) -> None:
        """Adiciona ou atualiza um item no carrinho"""
        existing_item = self.find_item(product_name)
        if existing_item:
            existing_item.quantity = quantity
        else:
            self.items.append(CartItem(product_name, quantity))
    
    def remove_item(self, product_name: str) -> None:
        """Remove um item do carrinho"""
        self.items = [item for item in self.items if item.product_name != product_name]
    
    def find_item(self, product_name: str) -> Optional[CartItem]:
        """Encontra um item específico no carrinho"""
        for item in self.items:
            if item.product_name == product_name:
                return item
        return None
    
    def get_valid_items(self) -> List[CartItem]:
        """Retorna apenas os itens válidos do carrinho"""
        return [item for item in self.items if item.is_valid()]
    
    def is_empty(self) -> bool:
        """Verifica se o carrinho está vazio"""
        return len(self.get_valid_items()) == 0
    
    def clear(self) -> None:
        """Limpa o carrinho"""
        self.items = []


@dataclass
class Notification:
    """Modelo representando uma notificação"""
    message: str
    level: str  # info, success, warning, error
    timestamp: float
    
    def __init__(self, message: str, level: str = "info"):
        self.message = message
        self.level = level
        self.timestamp = time.time()
    
    def is_recent(self, seconds: int = 60) -> bool:
        """Verifica se a notificação é recente"""
        return (time.time() - self.timestamp) < seconds


@dataclass
class OrderRequest:
    """Modelo representando uma requisição de pedido"""
    items: List[dict]
    
    def __init__(self, cart: Cart):
        self.items = [
            {
                "itemName": item.product_name, 
                "quantity": item.quantity
            }
            for item in cart.get_valid_items()
        ]
    
    def to_dict(self) -> List[dict]:
        """Converte para lista de dicionários para envio na API"""
        return self.items
    
    def to_json_format(self) -> List[dict]:
        """Retorna no formato JSON exato para o POST: array de objetos"""
        return self.items


@dataclass
class OrderResponse:
    """Modelo representando a resposta de um pedido"""
    id: str
    status: str
    success: bool
    
    def __init__(self, response_data: dict):
        self.id = response_data.get("id", "")
        self.status = response_data.get("status", "")
        self.success = response_data.get("status") == "success"
