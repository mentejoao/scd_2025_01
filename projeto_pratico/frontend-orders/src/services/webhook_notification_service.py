"""
Serviço para integração de notificações webhook com Streamlit
"""
import requests
import streamlit as st
from typing import List, Dict, Any
from ..models.entities import Notification


class WebhookNotificationService:
    """Serviço para gerenciar notificações recebidas via webhook"""
    
    def __init__(self, webhook_server_url: str = "http://localhost:8000"):
        self.webhook_server_url = webhook_server_url
        self.notifications_endpoint = f"{webhook_server_url}/notifications/latest"
        self.health_endpoint = f"{webhook_server_url}/health"
    
    def check_server_health(self) -> bool:
        """Verifica se o servidor de webhook está disponível"""
        try:
            response = requests.get(self.health_endpoint, timeout=5)
            return response.status_code == 200
        except requests.exceptions.RequestException:
            return False
    
    def fetch_latest_notifications(self) -> List[Dict[str, Any]]:
        """Busca as notificações mais recentes do servidor webhook"""
        try:
            print(f"Buscando notificações em: {self.notifications_endpoint}")
            response = requests.get(self.notifications_endpoint, timeout=5)
            print(f"Status da resposta: {response.status_code}")
            
            if response.status_code == 200:
                data = response.json()
                notifications = data.get('notifications', [])
                print(f"Notificações encontradas: {len(notifications)}")
                if notifications:
                    print(f"Primeira notificação: {notifications[0]}")
                return notifications
            else:
                print(f"❌ Erro na resposta: {response.status_code}")
            return []
        except requests.exceptions.RequestException as e:
            print(f"❌ Erro de conexão ao buscar notificações: {e}")
            return []
    
    def convert_to_streamlit_notifications(self, webhook_notifications: List[Dict[str, Any]]) -> List[Notification]:
        """Converte notificações do webhook para objetos Notification do Streamlit"""
        streamlit_notifications = []
        
        for notif in webhook_notifications:
            title = notif.get('title', 'Notificação')
            description = notif.get('description', '')
            
            message = f"{title}: {description}" if description else title
            
            notification = Notification(message, "info")
            streamlit_notifications.append(notification)
        
        return streamlit_notifications
    
    def add_webhook_notifications_to_session(self, notifications_list: List[Notification]) -> int:
        """Adiciona notificações do webhook à lista de notificações da sessão"""
        print("🔄 Verificando notificações webhook...")
        webhook_notifications = self.fetch_latest_notifications()
        
        if webhook_notifications:
            print(f"Encontradas {len(webhook_notifications)} notificações webhook")
            new_notifications = self.convert_to_streamlit_notifications(webhook_notifications)
            notifications_list.extend(new_notifications)
            print(f"Adicionadas {len(new_notifications)} notificações à sessão")
            return len(new_notifications)
        else:
            print("ℹNenhuma notificação webhook encontrada")
        
        return 0
    
    def display_webhook_status(self):
        """Exibe o status do servidor webhook na interface"""
        if self.check_server_health():
            st.success("🟢 Servidor de notificações conectado")
        else:
            st.warning("🔴 Servidor de notificações offline")
            st.info("Execute: `python notification_server.py` para iniciar o servidor")
    
    def get_webhook_info(self) -> Dict[str, str]:
        """Retorna informações sobre o webhook para exibição"""
        return {
            "endpoint": f"{self.webhook_server_url}/notification",
            "method": "POST",
            "format": '{"title": "Título", "description": "Descrição"}',
            "test_url": f"{self.webhook_server_url}/docs"
        }


class NotificationPollingService:
    """Serviço para polling automático de notificações"""
    
    def __init__(self, webhook_service: WebhookNotificationService):
        self.webhook_service = webhook_service
        self.polling_interval = 3  # segundos
    
    def should_poll(self) -> bool:
        """Verifica se deve fazer polling baseado no intervalo"""
        if 'last_notification_poll' not in st.session_state:
            st.session_state.last_notification_poll = 0
        
        import time
        current_time = time.time()
        
        if current_time - st.session_state.last_notification_poll > self.polling_interval:
            st.session_state.last_notification_poll = current_time
            return True
        
        return False
    
    def poll_and_update_notifications(self, notifications_list: List[Notification]) -> bool:
        """Faz polling de notificações e atualiza a lista se necessário"""
        if self.should_poll():
            print("Executando polling de notificações...")
            new_count = self.webhook_service.add_webhook_notifications_to_session(notifications_list)
            if new_count > 0:
                print(f"Adicionadas {new_count} novas notificações (auto-refresh ativo)")
                return True
        return False
