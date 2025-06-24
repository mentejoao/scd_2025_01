"""
Utilitários - Funções auxiliares e configurações
Seguindo o princípio DRY (Don't Repeat Yourself)
"""
import streamlit as st
from typing import Dict, List
from ..models.entities import Cart, Notification


class SessionStateManager:
    """Gerenciador do estado da sessão do Streamlit"""
    
    PRODUCT_QUANTITIES_KEY = "product_quantities"
    NOTIFICATIONS_KEY = "notifications"
    CART_KEY = "cart"
    
    @classmethod
    def initialize_session_state(cls):
        """Inicializa o estado da sessão se necessário"""
        cls._initialize_notifications()
        cls._initialize_cart()
        cls._initialize_product_quantities()
    
    @classmethod
    def _initialize_notifications(cls):
        """Inicializa as notificações na sessão"""
        if cls.NOTIFICATIONS_KEY not in st.session_state:
            st.session_state[cls.NOTIFICATIONS_KEY] = []
            # Adicionar notificação de boas-vindas
            welcome_notification = Notification(
                "Bem-vindo ao sistema de pedidos!", 
                "info"
            )
            st.session_state[cls.NOTIFICATIONS_KEY].append(welcome_notification)
    
    @classmethod
    def _initialize_cart(cls):
        """Inicializa o carrinho na sessão"""
        if cls.CART_KEY not in st.session_state:
            st.session_state[cls.CART_KEY] = Cart()
    
    @classmethod
    def _initialize_product_quantities(cls):
        """Inicializa as quantidades dos produtos na sessão"""
        if cls.PRODUCT_QUANTITIES_KEY not in st.session_state:
            st.session_state[cls.PRODUCT_QUANTITIES_KEY] = {}
    
    @classmethod
    def get_notifications(cls) -> List[Notification]:
        """Retorna a lista de notificações da sessão"""
        return st.session_state.get(cls.NOTIFICATIONS_KEY, [])
    
    @classmethod
    def get_cart(cls) -> Cart:
        """Retorna o carrinho da sessão"""
        return st.session_state.get(cls.CART_KEY, Cart())
    
    @classmethod
    def get_product_quantities(cls) -> Dict[str, int]:
        """Retorna as quantidades dos produtos da sessão"""
        return st.session_state.get(cls.PRODUCT_QUANTITIES_KEY, {})
    
    @classmethod
    def update_product_quantity(cls, product_name: str, quantity: int):
        """Atualiza a quantidade de um produto na sessão"""
        quantities = cls.get_product_quantities()
        quantities[product_name] = quantity
        st.session_state[cls.PRODUCT_QUANTITIES_KEY] = quantities
        
        # NÃO sincronizar automaticamente com o carrinho aqui
        # O carrinho deve ser gerenciado pelos controllers
    
    @classmethod
    def sync_product_quantities_with_products(cls, product_names: List[str]):
        """Sincroniza as quantidades dos produtos com a lista de produtos disponíveis"""
        quantities = cls.get_product_quantities()
        for product_name in product_names:
            if product_name not in quantities:
                quantities[product_name] = 0
        st.session_state[cls.PRODUCT_QUANTITIES_KEY] = quantities


class ConfigManager:
    """Gerenciador de configurações da aplicação"""
    
    DEFAULT_API_BASE_URL = "http://localhost:8080"
    DEFAULT_PLACEHOLDER_IMAGE = "https://via.placeholder.com/150?text=Produto"
    
    PRODUCTS_CSV_FILE = "mock_products.csv"
    
    MAX_NOTIFICATIONS_DISPLAY = 5
    
    @classmethod
    def get_api_base_url(cls) -> str:
        """Retorna a URL base da API"""
        return cls.DEFAULT_API_BASE_URL
    
    @classmethod
    def get_placeholder_image_url(cls) -> str:
        """Retorna a URL da imagem placeholder"""
        return cls.DEFAULT_PLACEHOLDER_IMAGE
    
    @classmethod
    def get_products_csv_path(cls) -> str:
        """Retorna o caminho do arquivo CSV de produtos"""
        return cls.PRODUCTS_CSV_FILE


class ValidationUtils:
    """Utilitários para validação"""
    
    @staticmethod
    def is_valid_quantity(quantity: int) -> bool:
        """Valida se a quantidade é válida"""
        return isinstance(quantity, int) and quantity >= 0
    
    @staticmethod
    def is_valid_product_name(name: str) -> bool:
        """Valida se o nome do produto é válido"""
        return isinstance(name, str) and name.strip() != ""
    
    @staticmethod
    def is_valid_notification_level(level: str) -> bool:
        """Valida se o nível da notificação é válido"""
        valid_levels = ["info", "success", "warning", "error"]
        return level in valid_levels


class DataUtils:
    """Utilitários para manipulação de dados"""
    
    @staticmethod
    def convert_cart_to_dict(cart: Cart) -> Dict[str, int]:
        """Converte um carrinho para dicionário"""
        return {
            item.product_name: item.quantity 
            for item in cart.get_valid_items()
        }
    
    @staticmethod
    def sync_quantities_with_cart(cart: Cart, quantities: Dict[str, int]):
        """Sincroniza as quantidades com o carrinho"""
        cart.clear()
        for product_name, quantity in quantities.items():
            if quantity > 0:
                cart.add_item(product_name, quantity)
    
    @staticmethod
    def sync_quantities_from_cart(cart: Cart, quantities: Dict[str, int]):
        """Sincroniza as quantidades a partir do carrinho (direção oposta)"""

        for product_name in quantities.keys():
            quantities[product_name] = 0
        
        for item in cart.get_valid_items():
            quantities[item.product_name] = item.quantity
