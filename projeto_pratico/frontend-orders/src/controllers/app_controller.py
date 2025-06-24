"""
Controllers - Coordena as interações entre View e Services
Implementa o padrão MVC e segue o princípio SRP
"""
from typing import List, Dict, Tuple
from ..models.entities import Product, Cart, Notification
from ..services.business_services import (
    ProductServiceInterface, 
    CartServiceInterface, 
    NotificationServiceInterface,
    OrderServiceInterface
)


class ProductController:
    """Controller para operações relacionadas a produtos"""
    
    def __init__(self, product_service: ProductServiceInterface):
        self._product_service = product_service
    
    def get_all_products(self) -> List[Product]:
        """Retorna todos os produtos"""
        return self._product_service.get_all_products()
    
    def get_product_image_path(self, image_path: str) -> str:
        """Retorna o caminho da imagem do produto"""
        return self._product_service.get_product_image_path(image_path)


class CartController:
    """Controller para operações do carrinho"""
    
    def __init__(self, cart_service: CartServiceInterface, 
                 notification_service: NotificationServiceInterface):
        self._cart_service = cart_service
        self._notification_service = notification_service
    
    def increment_quantity(self, cart: Cart, product_name: str, 
                          notifications: List[Notification], max_quantity: int) -> bool:
        """Incrementa a quantidade de um produto no carrinho"""
        current_item = cart.find_item(product_name)
        current_quantity = current_item.quantity if current_item else 0
        
        if current_quantity < max_quantity:
            new_quantity = current_quantity + 1
            success = self._cart_service.add_item_to_cart(cart, product_name, new_quantity)
            
            if not success:
                self._notification_service.add_notification(
                    notifications, 
                    f"Não foi possível adicionar {product_name} ao carrinho", 
                    "warning"
                )
            return success
        else:
            self._notification_service.add_notification(
                notifications, 
                f"Estoque máximo atingido para {product_name}", 
                "warning"
            )
            return False
    
    def decrement_quantity(self, cart: Cart, product_name: str) -> bool:
        """Decrementa a quantidade de um produto no carrinho"""
        current_item = cart.find_item(product_name)
        if not current_item or current_item.quantity <= 0:
            return False
        
        new_quantity = current_item.quantity - 1
        if new_quantity == 0:
            self._cart_service.remove_item_from_cart(cart, product_name)
        else:
            self._cart_service.add_item_to_cart(cart, product_name, new_quantity)
        
        return True
    
    def get_cart_items_dict(self, cart: Cart) -> Dict[str, int]:
        """Retorna os itens do carrinho como dicionário para compatibilidade com a view"""
        return {
            item.product_name: item.quantity 
            for item in cart.get_valid_items()
        }
    
    def validate_cart_for_order(self, cart: Cart, notifications: List[Notification]) -> bool:
        """Valida o carrinho para criação de pedido"""
        is_valid, errors = self._cart_service.validate_cart(cart)
        
        if not is_valid:
            for error in errors:
                self._notification_service.add_notification(notifications, error, "error")
        
        return is_valid


class NotificationController:
    """Controller para operações de notificação"""
    
    def __init__(self, notification_service: NotificationServiceInterface):
        self._notification_service = notification_service
    
    def add_notification(self, notifications: List[Notification], 
                        message: str, level: str = "info") -> None:
        """Adiciona uma notificação"""
        self._notification_service.add_notification(notifications, message, level)
    
    def get_recent_notifications(self, notifications: List[Notification], 
                               limit: int = 5) -> List[Notification]:
        """Retorna as notificações mais recentes"""
        return self._notification_service.get_recent_notifications(notifications, limit)


class OrderController:
    """Controller para operações de pedidos"""
    
    def __init__(self, order_service: OrderServiceInterface,
                 cart_service: CartServiceInterface,
                 notification_service: NotificationServiceInterface):
        self._order_service = order_service
        self._cart_service = cart_service
        self._notification_service = notification_service
    
    def create_order(self, cart: Cart, notifications: List[Notification]) -> Tuple[bool, str]:
        """
        Cria um pedido
        Retorna: (sucesso, mensagem)
        """

        is_valid, errors = self._cart_service.validate_cart(cart)
        if not is_valid:
            error_msg = "Carrinho inválido: " + "; ".join(errors)
            self._notification_service.add_notification(notifications, error_msg, "error")
            return False, error_msg
        
        success, order_response, error_msg = self._order_service.create_order(cart)
        
        if success and order_response:
            success_msg = f"Pedido criado com sucesso! ID: {order_response.id}"
            self._notification_service.add_notification(notifications, success_msg, "success")
            cart.clear() 
            return True, success_msg
        else:
            error_msg = error_msg or "Erro desconhecido ao criar pedido"
            self._notification_service.add_notification(notifications, error_msg, "error")
            return False, error_msg


class ApplicationController:
    """Controller principal que coordena todos os outros controllers"""
    
    def __init__(self, product_controller: ProductController,
                 cart_controller: CartController,
                 notification_controller: NotificationController,
                 order_controller: OrderController):
        self.product_controller = product_controller
        self.cart_controller = cart_controller
        self.notification_controller = notification_controller
        self.order_controller = order_controller
