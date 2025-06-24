"""
Factory - Implementa o padrão Factory e Dependency Injection
Seguindo o princípio de Inversão de Dependência (DIP)
"""
from ..repositories.product_repository import (
    CSVProductRepository, 
    LocalImageRepository
)
from ..services.business_services import (
    ProductService,
    CartService, 
    NotificationService,
    OrderService
)
from ..services.webhook_notification_service import (
    WebhookNotificationService,
    NotificationPollingService
)
from ..controllers.app_controller import (
    ProductController,
    CartController,
    NotificationController,
    OrderController,
    ApplicationController
)
from ..utils.helpers import ConfigManager


class DependencyFactory:
    """Factory para criação e injeção de dependências"""
    
    def __init__(self):
        self._repositories = {}
        self._services = {}
        self._controllers = {}
    
    def create_repositories(self) -> dict:
        """Cria e retorna os repositórios"""
        if not self._repositories:
            self._repositories = {
                'product_repository': CSVProductRepository(
                    ConfigManager.get_products_csv_path()
                ),
                'image_repository': LocalImageRepository(
                    ConfigManager.get_placeholder_image_url()
                )
            }
        return self._repositories
    
    def create_services(self) -> dict:
        """Cria e retorna os serviços"""
        if not self._services:
            repositories = self.create_repositories()
            
            product_service = ProductService(
                repositories['product_repository'],
                repositories['image_repository']
            )
            
            cart_service = CartService(product_service)
            notification_service = NotificationService()
            order_service = OrderService(ConfigManager.get_api_base_url())
            
            webhook_notification_service = WebhookNotificationService()
            polling_service = NotificationPollingService(webhook_notification_service)
            
            self._services = {
                'product_service': product_service,
                'cart_service': cart_service,
                'notification_service': notification_service,
                'order_service': order_service,
                'webhook_notification_service': webhook_notification_service,
                'polling_service': polling_service
            }
        
        return self._services
    
    def create_controllers(self) -> dict:
        """Cria e retorna os controllers"""
        if not self._controllers:
            services = self.create_services()
            
            product_controller = ProductController(
                services['product_service']
            )
            
            cart_controller = CartController(
                services['cart_service'],
                services['notification_service']
            )
            
            notification_controller = NotificationController(
                services['notification_service']
            )
            
            order_controller = OrderController(
                services['order_service'],
                services['cart_service'],
                services['notification_service']
            )
            
            self._controllers = {
                'product_controller': product_controller,
                'cart_controller': cart_controller,
                'notification_controller': notification_controller,
                'order_controller': order_controller
            }
        
        return self._controllers
    
    def create_application_controller(self) -> ApplicationController:
        """Cria e retorna o controller principal da aplicação"""
        controllers = self.create_controllers()
        
        return ApplicationController(
            controllers['product_controller'],
            controllers['cart_controller'],
            controllers['notification_controller'],
            controllers['order_controller']
        )


class ApplicationFactory:
    """Factory principal para criação da aplicação"""
    
    @staticmethod
    def create_application() -> ApplicationController:
        """Cria uma instância completa da aplicação com todas as dependências"""
        factory = DependencyFactory()
        return factory.create_application_controller()


_application_instance = None

def get_application() -> ApplicationController:
    """Retorna a instância singleton da aplicação"""
    global _application_instance
    if _application_instance is None:
        _application_instance = ApplicationFactory.create_application()
    return _application_instance
