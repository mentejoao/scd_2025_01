import streamlit as st
import requests
import pandas as pd
import os
import time

st.set_page_config(page_title="Catálogo de Produtos", layout="wide")

def get_image_path(image_path):
    if os.path.exists(image_path):
        return image_path
    else:
        return "https://via.placeholder.com/150?text=Produto"

def add_notification(message, level="info"):
    st.session_state.notifications.append({
        'message': message,
        'level': level,
        'time': time.time()
    })

if 'notifications' not in st.session_state:
    st.session_state.notifications = []
    
if len(st.session_state.notifications) == 0:
    st.session_state.notifications.append({
        'message': 'Bem-vindo ao sistema de pedidos!',
        'level': 'info',
        'time': time.time()
    })

st.title("🛒 Catálogo de Produtos")

with st.sidebar.expander("🧪 Teste de Notificações"):
    test_message = st.text_input("Mensagem", key="test_message", value="Pedido atualizado!")
    test_level = st.selectbox("Nível", ["info", "success", "warning", "error"], key="test_level")
    if st.button("Enviar notificação de teste"):
        add_notification(test_message, test_level)
        st.success("Notificação enviada!")

if 'product_quantities' not in st.session_state:
    st.session_state.product_quantities = {}

df = pd.read_csv("mock_products.csv")  # nome, imagem, descricao, quantidade_stock

produtos = df.to_dict(orient="records")

for produto in produtos:
    nome = produto["nome"]
    if nome not in st.session_state.product_quantities:
        st.session_state.product_quantities[nome] = 0

colunas = st.columns(3)
itens_carrinho = {}

st.markdown("""
<style>
    .product-card {
        border: 1px solid #ddd;
        border-radius: 8px;
        padding: 15px;
        margin-bottom: 15px;
        background-color: white;
    }
    .product-name {
        font-size: 18px;
        font-weight: bold;
        margin: 10px 0;
    }
    .product-desc {
        font-size: 14px;
        color: #666;
        margin-bottom: 10px;
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
    .stock-info {
        font-size: 12px;
        color: #888;
        margin-top: 5px;
    }
</style>
""", unsafe_allow_html=True)

def increment_quantity(nome, max_qty):
    if st.session_state.product_quantities[nome] < max_qty:
        st.session_state.product_quantities[nome] += 1

def decrement_quantity(nome):
    if st.session_state.product_quantities[nome] > 0:
        st.session_state.product_quantities[nome] -= 1

for i, produto in enumerate(produtos):
    with colunas[i % 3]:
        with st.container():
            st.markdown('<div class="product-card">', unsafe_allow_html=True)
            
            image_path = get_image_path(produto["imagem"])
            st.image(image_path, width=150)
            
            # Nome do produto
            st.markdown(f'<div class="product-name">{produto["nome"]}</div>', unsafe_allow_html=True)
            
            # Descrição do produto
            st.markdown(f'<div class="product-desc">{produto["descricao"]}</div>', unsafe_allow_html=True)
            
            # Estoque disponível
            st.markdown(f'<div class="stock-info">Estoque: {produto["quantidade_stock"]} unidades</div>', unsafe_allow_html=True)
            
            # Seletor de quantidade
            nome = produto["nome"]
            max_qty = produto["quantidade_stock"]
            
            col1, col2, col3 = st.columns([1, 2, 1])
            
            with col1:
                st.button("-", key=f"dec_{nome}", on_click=decrement_quantity, args=(nome,))
            
            with col2:
                st.markdown(f'<div style="text-align: center; font-size: 18px;">{st.session_state.product_quantities[nome]}</div>', unsafe_allow_html=True)
            
            with col3:
                st.button("+", key=f"inc_{nome}", on_click=increment_quantity, args=(nome, max_qty))
            
            st.markdown('</div>', unsafe_allow_html=True)

with st.sidebar:
    st.title("📬 Notificações")
    
    # Exibir notificações (as mais recentes primeiro)
    for notification in reversed(st.session_state.notifications[-5:]):  # Exibir apenas as últimas 5
        if notification['level'] == 'success':
            st.success(notification['message'])
        elif notification['level'] == 'warning':
            st.warning(notification['message'])
        elif notification['level'] == 'error':
            st.error(notification['message'])
        else:
            st.info(notification['message'])

for nome, quantidade in st.session_state.product_quantities.items():
    if quantidade > 0:
        itens_carrinho[nome] = quantidade

if itens_carrinho:
    st.markdown("### 🛒 Seu Carrinho")
    for nome, quantidade in itens_carrinho.items():
        st.write(f"**{nome}**: {quantidade} unidade(s)")

if st.button("Enviar Pedido"):
    if not itens_carrinho:
        st.warning("⚠️ Selecione ao menos um produto.")
    else:
        # Converter para formato esperado pela API (novo formato)
        itens = [{"name": nome, "quantity": quantidade} for nome, quantidade in itens_carrinho.items()]
        try:
            # Endpoint para o backend FastAPI local
            response = requests.post("http://localhost:8080/orders", json=itens)
            if response.status_code == 200:
                data = response.json()
                st.success(f"✅ Pedido enviado! ID: `{data['id']}`")
                
                # Adicionar à lista de notificações
                st.session_state.notifications.append({
                    'message': f"Pedido enviado com sucesso! ID: {data['id']}",
                    'level': 'success',
                    'time': time.time()
                })
                
                # Limpar o carrinho após envio bem-sucedido
                for nome in st.session_state.product_quantities:
                    st.session_state.product_quantities[nome] = 0
            else:
                st.error(f"❌ Erro ao enviar pedido: {response.text}")
                
                # Adicionar à lista de notificações
                st.session_state.notifications.append({
                    'message': f"Erro ao enviar pedido: {response.text}",
                    'level': 'error',
                    'time': time.time()
                })
        except Exception as e:
            st.error(f"❌ Erro ao conectar com o servidor: {e}")
            
            # Adicionar à lista de notificações
            st.session_state.notifications.append({
                'message': f"Erro de conexão: {e}",
                'level': 'error',
                'time': time.time()
            })
