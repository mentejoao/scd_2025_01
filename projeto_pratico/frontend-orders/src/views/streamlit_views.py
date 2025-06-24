"""
Views - Responsável pela apresentação e interface com o usuário
Implementa o padrão MVC e segue o princípio SRP
"""
import streamlit as st
import time
from typing import List, Dict
from ..models.entities import Product, Notification


class StylesView:
    """View responsável pelos estilos CSS da aplicação"""
    
    @staticmethod
    def render_custom_styles():
        """Renderiza os estilos CSS customizados"""
        st.markdown("""
        <style>
            .product-card {
                border: none;
                border-radius: 8px;
                padding: 15px;
                margin-bottom: 15px;
                background-color: transparent;
                height: 420px;
                display: flex;
                flex-direction: column;
                justify-content: space-between;
            }
            .product-name {
                font-size: 18px;
                font-weight: bold;
                margin: 10px 0;
                height: 50px;
                display: flex;
                align-items: center;
                justify-content: center;
                text-align: center;
            }
            .product-desc {
                font-size: 14px;
                color: #666;
                margin-bottom: 10px;
                height: 60px;
                overflow: hidden;
                text-overflow: ellipsis;
                display: -webkit-box;
                -webkit-line-clamp: 3;
                -webkit-box-orient: vertical;
                text-align: center;
            }
            .quantity-selector {
                display: flex;
                align-items: center;
                margin-top: 10px;
            }
            .quantity-btn {
                width: 30px;
                height: 30px;
                border-radius: 50%;
                border: none;
                background-color: #f0f0f0;
                cursor: pointer;
                font-size: 18px;
                font-weight: bold;
            }
            .quantity-display {
                margin: 0 10px;
                font-size: 16px;
            }
            /* Estilos para botões de quantidade - centralizados */
            .stButton {
                display: flex !important;
                justify-content: center !important;
                align-items: center !important;
            }
            
            .stButton > button {
                background-color: white !important;
                border: 2px solid #ddd !important;
                border-radius: 50% !important;
                width: 35px !important;
                height: 35px !important;
                min-width: 35px !important;
                min-height: 35px !important;
                padding: 0 !important;
                margin: 0 auto !important;
                font-size: 18px !important;
                font-weight: bold !important;
                color: #333 !important;
                display: flex !important;
                align-items: center !important;
                justify-content: center !important;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1) !important;
                transition: all 0.2s ease !important;
            }
            
            .stButton > button:hover {
                background-color: #f8f9fa !important;
                border-color: #007bff !important;
                transform: translateY(-1px) !important;
                box-shadow: 0 4px 8px rgba(0,0,0,0.15) !important;
            }
            
            .stButton > button:active {
                transform: translateY(0) !important;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1) !important;
            }
            
            /* Centralizar todas as colunas */
            .stColumn {
                display: flex !important;
                flex-direction: column !important;
                align-items: center !important;
                justify-content: center !important;
            }
            .stock-info {
                font-size: 12px;
                color: #888;
                margin-top: 5px;
                text-align: center;
            }
            /* Estilos para imagens dos produtos - tamanho absolutamente fixo */
            .stImage img {
                width: 150px !important;
                height: 150px !important;
                min-width: 150px !important;
                min-height: 150px !important;
                max-width: 150px !important;
                max-height: 150px !important;
                object-fit: cover !important;
                object-position: center !important;
                border-radius: 8px !important;
                border: none !important;
                box-sizing: border-box !important;
            }
            
            /* Forçar container da imagem também ter tamanho fixo */
            .stImage {
                width: 150px !important;
                height: 150px !important;
                min-width: 150px !important;
                min-height: 150px !important;
                max-width: 150px !important;
                max-height: 150px !important;
                margin: 0 auto !important;
                display: flex !important;
                justify-content: center !important;
                align-items: center !important;
            }
            
            .stImage > div {
                width: 150px !important;
                height: 150px !important;
                min-width: 150px !important;
                min-height: 150px !important;
                max-width: 150px !important;
                max-height: 150px !important;
            }
            
            /* Override específico para data-testid */
            [data-testid="stImage"] {
                width: 150px !important;
                height: 150px !important;
                min-width: 150px !important;
                min-height: 150px !important;
                max-width: 150px !important;
                max-height: 150px !important;
            }
            
            [data-testid="stImageContainer"] {
                width: 150px !important;
                height: 150px !important;
                min-width: 150px !important;
                min-height: 150px !important;
                max-width: 150px !important;
                max-height: 150px !important;
            }
            
            /* Remover toolbar de fullscreen das imagens */
            .stElementToolbar {
                display: none !important;
            }
            
            /* Centralizar elementos na coluna */
            .stColumn > div {
                display: flex;
                flex-direction: column;
                align-items: center;
            }
            
            /* Estilos para botão de enviar pedido */
            .order-button {
                width: 100% !important;
                min-height: 50px !important;
                font-size: 16px !important;
                font-weight: bold !important;
                padding: 12px 24px !important;
                margin-top: 10px !important;
                border-radius: 8px !important;
                background: linear-gradient(135deg, #28a745, #20c997) !important;
                color: white !important;
                border: none !important;
                box-shadow: 0 4px 8px rgba(0,0,0,0.15) !important;
                transition: all 0.3s ease !important;
            }
            
            .order-button:hover {
                transform: translateY(-2px) !important;
                box-shadow: 0 6px 12px rgba(0,0,0,0.2) !important;
                background: linear-gradient(135deg, #218838, #1abc9c) !important;
            }
            
            .order-button:disabled {
                background: #6c757d !important;
                cursor: not-allowed !important;
                transform: none !important;
                box-shadow: none !important;
            }
            
            /* Forçar tamanho do botão de pedido */
            div[data-testid="stButton"] button[kind="primary"],
            div[data-testid="stButton"] button:contains("Enviar Pedido") {
                width: 100% !important;
                min-height: 50px !important;
                font-size: 16px !important;
                font-weight: bold !important;
                padding: 12px 24px !important;
                white-space: nowrap !important;
            }
            
            /* Estilos para botão de enviar pedido - similar ao st.info */
            button[data-testid="baseButton-primary"] {
                width: 100% !important;
                min-height: 50px !important;
                background-color: #10b981 !important;
                border: 1px solid #059669 !important;
                border-radius: 8px !important;
                color: white !important;
                font-size: 14px !important;
                font-weight: 500 !important;
                padding: 12px 16px !important;
                margin: 4px 0 !important;
                transition: all 0.2s ease !important;
            }
            
            button[data-testid="baseButton-primary"]:hover {
                background-color: #059669 !important;
                border-color: #047857 !important;
                transform: translateY(-1px) !important;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1) !important;
            }
            
            button[data-testid="baseButton-primary"]:active {
                transform: translateY(0) !important;
                box-shadow: none !important;
            }
        </style>
        """, unsafe_allow_html=True)


class ProductCardView:
    """View responsável por renderizar um card de produto"""
    
    def __init__(self, product: Product, quantity: int, image_path: str):
        self.product = product
        self.quantity = quantity
        self.image_path = image_path
    
    def render(self, on_increment_callback, on_decrement_callback):
        """Renderiza o card do produto"""
        with st.container():
            col_img_left, col_img_center, col_img_right = st.columns([1, 2, 1])
            with col_img_center:
                st.image(self.image_path, width=150, use_container_width=False)
            
            st.markdown(f'<div class="product-name">{self.product.name}</div>', 
                       unsafe_allow_html=True)
            
            st.markdown(f'<div class="product-desc">{self.product.description}</div>', 
                       unsafe_allow_html=True)
            
            st.markdown(f'<div class="stock-info">Estoque: {self.product.stock_quantity} unidades</div>', 
                       unsafe_allow_html=True)
            
            col1, col2, col3 = st.columns([1, 1, 1])
            
            with col1:
                dec_key = f"dec_{self.product.name}"
                if st.button("−", key=dec_key, help="Diminuir quantidade"):
                    on_decrement_callback(self.product.name)
                    st.rerun()
            
            with col2:
                st.markdown(f'<div style="text-align: center; font-size: 18px; line-height: 35px;">{self.quantity}</div>', 
                           unsafe_allow_html=True)
            
            with col3:
                inc_key = f"inc_{self.product.name}"
                if st.button("＋", key=inc_key, help="Aumentar quantidade"):
                    on_increment_callback(self.product.name, self.product.stock_quantity)
                    st.rerun()


class ProductCatalogView:
    """View responsável por renderizar o catálogo de produtos"""
    
    def __init__(self, products: List[Product], quantities: Dict[str, int], 
                 get_image_path_callback):
        self.products = products
        self.quantities = quantities
        self.get_image_path_callback = get_image_path_callback
    
    def render(self, on_increment_callback, on_decrement_callback):
        """Renderiza o catálogo completo de produtos em uma matriz 2x3"""
        
        col1_r1, col2_r1, col3_r1 = st.columns(3)
        
        if len(self.products) > 0:
            with col1_r1:
                product = self.products[0]
                quantity = self.quantities.get(product.name, 0)
                image_path = self.get_image_path_callback(product.image)
                product_card = ProductCardView(product, quantity, image_path)
                product_card.render(on_increment_callback, on_decrement_callback)
        
        if len(self.products) > 1:
            with col2_r1:
                product = self.products[1]
                quantity = self.quantities.get(product.name, 0)
                image_path = self.get_image_path_callback(product.image)
                product_card = ProductCardView(product, quantity, image_path)
                product_card.render(on_increment_callback, on_decrement_callback)
        
        if len(self.products) > 2:
            with col3_r1:
                product = self.products[2]
                quantity = self.quantities.get(product.name, 0)
                image_path = self.get_image_path_callback(product.image)
                product_card = ProductCardView(product, quantity, image_path)
                product_card.render(on_increment_callback, on_decrement_callback)
        
        st.markdown("<br>", unsafe_allow_html=True)
        
        col1_r2, col2_r2, col3_r2 = st.columns(3)
        
        if len(self.products) > 3:
            with col1_r2:
                product = self.products[3]
                quantity = self.quantities.get(product.name, 0)
                image_path = self.get_image_path_callback(product.image)
                product_card = ProductCardView(product, quantity, image_path)
                product_card.render(on_increment_callback, on_decrement_callback)
        
        if len(self.products) > 4:
            with col2_r2:
                product = self.products[4]
                quantity = self.quantities.get(product.name, 0)
                image_path = self.get_image_path_callback(product.image)
                product_card = ProductCardView(product, quantity, image_path)
                product_card.render(on_increment_callback, on_decrement_callback)
        
        if len(self.products) > 5:
            with col3_r2:
                product = self.products[5]
                quantity = self.quantities.get(product.name, 0)
                image_path = self.get_image_path_callback(product.image)
                product_card = ProductCardView(product, quantity, image_path)
                product_card.render(on_increment_callback, on_decrement_callback)


class NotificationView:
    """View responsável por renderizar as notificações como popups"""
    
    @staticmethod
    def render_popup_notifications(notifications: List[Notification]):
        """Renderiza notificações como popups no canto superior direito"""
        if not notifications:
            return
            
        st.markdown("""
        <style>
            .notification-popup {
                position: fixed;
                top: 80px;
                right: 20px;
                z-index: 99999;
                max-width: 350px;
                min-width: 250px;
                padding: 15px 20px;
                border-radius: 8px;
                box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
                margin-bottom: 10px;
                animation: slideInBounce 0.5s ease-out;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                font-size: 14px;
                line-height: 1.4;
                transition: opacity 0.5s ease-out, transform 0.5s ease-out;
            }
            
            .notification-popup.fade-out {
                opacity: 0;
                transform: translateX(100%);
            }
            
            .notification-success {
                background-color: #d4edda;
                border: 1px solid #c3e6cb;
                color: #155724;
            }
            
            .notification-warning {
                background-color: #fff3cd;
                border: 1px solid #ffeaa7;
                color: #856404;
            }
            
            .notification-error {
                background-color: #f8d7da;
                border: 1px solid #f5c6cb;
                color: #721c24;
            }
            
            .notification-info {
                background-color: #d1ecf1;
                border: 1px solid #bee5eb;
                color: #0c5460;
            }
            
            @keyframes slideInBounce {
                0% {
                    transform: translateX(100%);
                    opacity: 0;
                }
                50% {
                    transform: translateX(-10px);
                    opacity: 0.8;
                }
                100% {
                    transform: translateX(0);
                    opacity: 1;
                }
            }
            
            .notification-close {
                position: absolute;
                top: 8px;
                right: 12px;
                font-size: 20px;
                font-weight: bold;
                cursor: pointer;
                color: inherit;
                background: none;
                border: none;
                padding: 0;
                width: 24px;
                height: 24px;
                display: flex;
                align-items: center;
                justify-content: center;
                border-radius: 50%;
                transition: background-color 0.2s ease;
            }
            
            .notification-close:hover {
                background-color: rgba(0, 0, 0, 0.1);
            }
        </style>
        
        <script>
            function closeNotification(element) {
                element.classList.add('fade-out');
                setTimeout(function() {
                    element.remove();
                }, 500);
            }
            
            function autoCloseNotification(element) {
                setTimeout(function() {
                    if (element && element.parentNode) {
                        closeNotification(element);
                    }
                }, 5000);
            }
        </script>
        """, unsafe_allow_html=True)
        
        latest_notification = notifications[-1]
        notification_class = f"notification-{latest_notification.level}"
        notification_id = f"notification-{int(time.time() * 1000)}"
        
        popup_html = f"""
        <div id="{notification_id}" class="notification-popup {notification_class}" onload="autoCloseNotification(this)">
            <button class="notification-close" onclick="closeNotification(this.parentElement)" title="Fechar notificação">&times;</button>
            <div style="margin-right: 30px;">
                <strong>{latest_notification.level.upper()}:</strong> {latest_notification.message}
            </div>
        </div>
        
        <script>
            // Auto-close após 5 segundos
            (function() {{
                const notification = document.getElementById('{notification_id}');
                if (notification) {{
                    setTimeout(function() {{
                        if (notification && notification.parentNode) {{
                            closeNotification(notification);
                        }}
                    }}, 5000);
                }}
            }})();
        </script>
        """
        
        st.markdown(popup_html, unsafe_allow_html=True)


class CartView:
    """View responsável por renderizar o carrinho"""
    
    @staticmethod
    def render_cart(cart_items: Dict[str, int]):
        """Renderiza o carrinho de compras"""
        if cart_items:
            st.markdown("### 🛒 Seu Carrinho")
            for product_name, quantity in cart_items.items():
                st.write(f"**{product_name}**: {quantity} unidade(s)")
        else:
            st.info("Carrinho vazio")
    
    @staticmethod
    def render_order_button(cart_items: Dict[str, int], on_order_callback):
        """Renderiza o botão de enviar pedido"""
        if len(cart_items) == 0:
            st.info("Selecione produtos para enviar pedido")
        else:
            button_clicked = st.button(
                "🛒 Enviar Pedido",
                type="primary",
                use_container_width=True,
                help="Clique para enviar seu pedido"
            )
            
            if button_clicked:
                success, message = on_order_callback()
                if success:
                    st.success(f"✅ {message}")
                else:
                    st.error(f"❌ {message}")


class MainView:
    """View principal que coordena todas as outras views"""
    
    def __init__(self):
        self._setup_page_config()
        self.styles_view = StylesView()
    
    def _setup_page_config(self):
        """Configura a página do Streamlit"""
        st.set_page_config(
            page_title="Catálogo de Produtos", 
            layout="wide",
            initial_sidebar_state="collapsed"
        )
    
    def render_header(self, notifications: List[Notification]):
        """Renderiza o cabeçalho da aplicação com notificações popup"""
        st.title("🛒 Catálogo de Produtos")
        self.styles_view.render_custom_styles()
        
        NotificationView.render_popup_notifications(notifications)
    
    def render_catalog(self, products: List[Product], quantities: Dict[str, int],
                      get_image_path_callback, on_increment_callback, on_decrement_callback):
        """Renderiza o catálogo de produtos"""
        catalog_view = ProductCatalogView(products, quantities, get_image_path_callback)
        catalog_view.render(on_increment_callback, on_decrement_callback)
    
    def render_cart_section(self, cart_items: Dict[str, int], on_order_callback):
        """Renderiza a seção do carrinho no conteúdo principal"""
        st.markdown("---")
        st.markdown("## 🛒 Carrinho de Compras")
        
        col1, col2 = st.columns([2, 1])
        
        with col1:
            CartView.render_cart(cart_items)
        
        with col2:
            CartView.render_order_button(cart_items, on_order_callback)
