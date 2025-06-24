"""
Repositórios - Implementam o padrão Repository para acesso a dados
Seguindo o princípio de Inversão de Dependência (DIP)
"""
from abc import ABC, abstractmethod
from typing import List, Optional
import pandas as pd
import os
from ..models.entities import Product


class ProductRepositoryInterface(ABC):
    """Interface para repositório de produtos (DIP - Dependency Inversion Principle)"""
    
    @abstractmethod
    def get_all_products(self) -> List[Product]:
        """Retorna todos os produtos"""
        pass
    
    @abstractmethod
    def get_product_by_name(self, name: str) -> Optional[Product]:
        """Retorna um produto pelo nome"""
        pass


class CSVProductRepository(ProductRepositoryInterface):
    """Implementação concreta do repositório de produtos usando CSV"""
    
    def __init__(self, csv_path: str = "mock_products.csv"):
        self.csv_path = csv_path
    
    def get_all_products(self) -> List[Product]:
        """Carrega produtos do arquivo CSV"""
        try:
            if not os.path.exists(self.csv_path):
                return []
            
            df = pd.read_csv(self.csv_path)
            products = []
            
            for _, row in df.iterrows():
                product = Product(
                    name=row["nome"],
                    image=row["imagem"], 
                    description=row["descricao"],
                    stock_quantity=int(row["quantidade_stock"])
                )
                products.append(product)
            
            return products
        except Exception as e:
            print(f"Erro ao carregar produtos: {e}")
            return []
    
    def get_product_by_name(self, name: str) -> Optional[Product]:
        """Busca um produto específico pelo nome"""
        products = self.get_all_products()
        for product in products:
            if product.name == name:
                return product
        return None


class ImageRepositoryInterface(ABC):
    """Interface para repositório de imagens"""
    
    @abstractmethod
    def get_image_path(self, image_path: str) -> str:
        """Retorna o caminho da imagem ou um placeholder"""
        pass


class LocalImageRepository(ImageRepositoryInterface):
    """Implementação para imagens locais com fallback para placeholder"""
    
    def __init__(self, placeholder_url: str = "https://via.placeholder.com/150?text=Produto"):
        self.placeholder_url = placeholder_url
    
    def get_image_path(self, image_path: str) -> str:
        """Verifica se a imagem existe localmente, senão retorna placeholder"""
        if os.path.exists(image_path):
            return image_path
        return self.placeholder_url
