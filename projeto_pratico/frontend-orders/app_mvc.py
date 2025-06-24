"""
Aplicação Principal - Arquivo principal modularizado seguindo padrão MVC e SOLID
"""
import sys
import os

sys.path.append(os.path.join(os.path.dirname(__file__), 'src'))

from src.utils.factory import get_application
from src.utils.helpers import SessionStateManager, DataUtils
from src.views.streamlit_views import MainView

from streamlit_autorefresh import st_autorefresh


def main():
    """Função principal da aplicação"""
    print("Iniciando aplicação Streamlit...")
    
    SessionStateManager.initialize_session_state()
    print("Session state inicializado")
    
    app_controller = get_application()
    
    notifications = SessionStateManager.get_notifications()
    cart = SessionStateManager.get_cart()
    quantities = SessionStateManager.get_product_quantities()
    
    products = app_controller.product_controller.get_all_products()
    
    product_names = [product.name for product in products]
    SessionStateManager.sync_product_quantities_with_products(product_names)
    
    DataUtils.sync_quantities_from_cart(cart, quantities)
    
    main_view = MainView()
    
    try:
        from src.services.webhook_notification_service import WebhookNotificationService, NotificationPollingService
        print("Carregando serviços de webhook...")
        
        webhook_service = WebhookNotificationService()
        polling_service = NotificationPollingService(webhook_service)
        
        # Verificar status do servidor webhook
        if webhook_service.check_server_health():
            print("Servidor webhook está ativo")
        else:
            print("Servidor webhook está offline")
        
        # Auto-refresh a cada 3 segundos para polling contínuo de notificações
        # Isso garante que notificações apareçam automaticamente sem interação manual
        refresh_count = st_autorefresh(interval=3000, key="notification_polling")
        
        # Verificar notificações webhook automaticamente
        print(f"Verificação de notificações webhook (refresh #{refresh_count})...")
        polling_service.poll_and_update_notifications(notifications)
        print(f"Total de notificações na sessão: {len(notifications)}")
            
    except Exception as e:
        print(f"Erro ao carregar serviços webhook: {e}")
        import traceback
        traceback.print_exc()
    
    recent_notifications = app_controller.notification_controller.get_recent_notifications(notifications)
    
    main_view.render_header(recent_notifications)
    
    def on_increment_quantity(product_name: str, max_quantity: int):
        """Callback para incrementar quantidade"""
        success = app_controller.cart_controller.increment_quantity(
            cart, product_name, notifications, max_quantity
        )
        if success:
            DataUtils.sync_quantities_from_cart(cart, quantities)
    
    def on_decrement_quantity(product_name: str):
        """Callback para decrementar quantidade"""
        success = app_controller.cart_controller.decrement_quantity(cart, product_name)
        if success:
            DataUtils.sync_quantities_from_cart(cart, quantities)
    
    def get_image_path(image_path: str) -> str:
        """Callback para obter caminho da imagem"""
        return app_controller.product_controller.get_product_image_path(image_path)
    
    def on_create_order():
        """Callback para criar pedido"""
        return app_controller.order_controller.create_order(cart, notifications)
    
    main_view.render_catalog(
        products=products,
        quantities=quantities,
        get_image_path_callback=get_image_path,
        on_increment_callback=on_increment_quantity,
        on_decrement_callback=on_decrement_quantity
    )
    
    cart_items = DataUtils.convert_cart_to_dict(cart)
    main_view.render_cart_section(cart_items, on_create_order)


if __name__ == "__main__":
    main()
