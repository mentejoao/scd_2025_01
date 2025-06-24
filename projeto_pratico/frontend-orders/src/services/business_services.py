"""
Serviços - Contém a lógica de negócio da aplicação
Seguindo os princípios SOLID, especialmente SRP (Single Responsibility Principle)
"""
from abc import ABC, abstractmethod
from typing import List, Optional
import requests
from ..models.entities import Product, Cart, Notification, OrderRequest, OrderResponse
from ..repositories.product_repository import ProductRepositoryInterface, ImageRepositoryInterface


class ProductServiceInterface(ABC):
    """Interface para serviços de produto (DIP)"""
    
    @abstractmethod
    def get_all_products(self) -> List[Product]:
        pass
    
    @abstractmethod
    def get_product_by_name(self, name: str) -> Optional[Product]:
        pass
    
    @abstractmethod
    def get_product_image_path(self, image_path: str) -> str:
        pass


class ProductService(ProductServiceInterface):
    """Serviço para operações relacionadas a produtos"""
    
    def __init__(self, product_repository: ProductRepositoryInterface, 
                 image_repository: ImageRepositoryInterface):
        self._product_repository = product_repository
        self._image_repository = image_repository
    
    def get_all_products(self) -> List[Product]:
        """Retorna todos os produtos disponíveis"""
        return self._product_repository.get_all_products()
    
    def get_product_by_name(self, name: str) -> Optional[Product]:
        """Busca um produto específico pelo nome"""
        return self._product_repository.get_product_by_name(name)
    
    def get_product_image_path(self, image_path: str) -> str:
        """Retorna o caminho da imagem do produto"""
        return self._image_repository.get_image_path(image_path)


class CartServiceInterface(ABC):
    """Interface para serviços de carrinho (DIP)"""
    
    @abstractmethod
    def add_item_to_cart(self, cart: Cart, product_name: str, quantity: int) -> bool:
        pass
    
    @abstractmethod
    def remove_item_from_cart(self, cart: Cart, product_name: str) -> bool:
        pass
    
    @abstractmethod
    def validate_cart(self, cart: Cart) -> tuple[bool, List[str]]:
        pass


class CartService(CartServiceInterface):
    """Serviço para operações do carrinho de compras"""
    
    def __init__(self, product_service: ProductServiceInterface):
        self._product_service = product_service
    
    def add_item_to_cart(self, cart: Cart, product_name: str, quantity: int) -> bool:
        """Adiciona um item ao carrinho com validação de estoque"""
        if quantity <= 0:
            return False
        
        product = self._product_service.get_product_by_name(product_name)
        if not product or not product.has_stock(quantity):
            return False
        
        cart.add_item(product_name, quantity)
        return True
    
    def remove_item_from_cart(self, cart: Cart, product_name: str) -> bool:
        """Remove um item do carrinho"""
        cart.remove_item(product_name)
        return True
    
    def validate_cart(self, cart: Cart) -> tuple[bool, List[str]]:
        """Valida o carrinho e retorna erros se houver"""
        errors = []
        
        if cart.is_empty():
            errors.append("Carrinho está vazio")
            return False, errors
        
        for item in cart.get_valid_items():
            product = self._product_service.get_product_by_name(item.product_name)
            if not product:
                errors.append(f"Produto '{item.product_name}' não encontrado")
            elif not product.has_stock(item.quantity):
                errors.append(f"Estoque insuficiente para '{item.product_name}'")
        
        return len(errors) == 0, errors


class NotificationServiceInterface(ABC):
    """Interface para serviços de notificação (DIP)"""
    
    @abstractmethod
    def add_notification(self, notifications: List[Notification], message: str, level: str) -> None:
        pass
    
    @abstractmethod
    def get_recent_notifications(self, notifications: List[Notification], limit: int) -> List[Notification]:
        pass


class NotificationService(NotificationServiceInterface):
    """Serviço para gerenciamento de notificações"""
    
    def add_notification(self, notifications: List[Notification], message: str, level: str = "info") -> None:
        """Adiciona uma nova notificação à lista"""
        notification = Notification(message, level)
        notifications.append(notification)
    
    def get_recent_notifications(self, notifications: List[Notification], limit: int = 5) -> List[Notification]:
        """Retorna as notificações mais recentes"""
        return notifications[-limit:] if notifications else []


class OrderServiceInterface(ABC):
    """Interface para serviços de pedido (DIP)"""
    
    @abstractmethod
    def create_order(self, cart: Cart) -> tuple[bool, Optional[OrderResponse], Optional[str]]:
        pass


class OrderService(OrderServiceInterface):
    """Serviço para operações de pedidos"""
    
    def __init__(self, api_base_url: str = "http://localhost:8080"):
        self._api_base_url = api_base_url
        self._orders_endpoint = f"{api_base_url}/orders"
    
    def create_order(self, cart: Cart) -> tuple[bool, Optional[OrderResponse], Optional[str]]:
        """
        Cria um pedido através da API
        Retorna: (sucesso, resposta, mensagem_erro)
        """
        try:
            order_request = OrderRequest(cart)
            payload = order_request.to_json_format()
            
            print(f"DEBUG - Enviando POST para {self._orders_endpoint}")
            print(f"DEBUG - Payload: {payload}")
            
            response = requests.post(
                self._orders_endpoint,
                json=payload,
                headers={"Content-Type": "application/json"},
                timeout=30
            )
            
            print(f"DEBUG - Status Code: {response.status_code}")
            print(f"DEBUG - Response: {response.text}")
            
            if response.status_code == 200:
                order_response = OrderResponse(response.json())
                return True, order_response, None
            else:
                error_msg = f"Erro na API: {response.status_code} - {response.text}"
                return False, None, error_msg
                
        except requests.exceptions.RequestException as e:
            error_msg = f"Erro de conexão: {str(e)}"
            print(f"DEBUG - Erro de conexão: {error_msg}")
            return False, None, error_msg
        except Exception as e:
            error_msg = f"Erro inesperado: {str(e)}"
            print(f"DEBUG - Erro inesperado: {error_msg}")
            return False, None, error_msg
