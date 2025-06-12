# Projeto Prático – Mensageria em Java
```
Disciplina : Software Concorrente e Distribuído
Curso : Bacharelado em Engenharia de Software
Professor responsável : ELIAS BATISTA FERREIRA
```
# 1. Objetivo geral
Projetar, implementar e documentar um sistema distribuído em Java que use o Apache Kafka ou ActiveMQ como backbone de mensageria para processar eventos em tempo real, explorando tópicos, partições, etc.
# 2. Cenário proposto
Você fará parte da equipe de engenharia de uma plataforma de comércio eletrônico.
● Serviço “Order‑Service” (produtor) publica em um tópico orders todos os pedidos confirmados.
● Serviço “Inventory‑Service” (consumidor + produtor) consome orders, reserva estoque e publica resultado em inventory-events.
● Serviço “Notification‑Service” (consumidor) lê inventory-events e envia e‑mails/SMS simulados.
# 3. Requisitos funcionais

* RF‑1
```Criar tópicos/fila orders e inventory-events via kafka-topics.sh.```
* RF‑2
```Order‑Service expõe uma REST API (POST /orders) que gera um UUID, timestamp e lista de itens.```
* RF‑3
```Inventory‑Service processa mensagens em ordem e publica sucesso ou falha (sem estoque).```
* RF‑4
```Notification‑Service registra no console a notificação enviada.```
# 4. Requisitos não‑funcionais
1. Escalabilidade – Explique como você poderia conseguir escalabilidade com o Broker utilizado?.
2. Tolerância à falha – O que significa? Explique uma situação de falha que poderia ocorrer e como o Broker poderia tratá-la.
3. Idempotência - Explique esse conceito e como fazer para garanti-lo.
4. Documentação – README.md explicando os serviços e como fazer para executar a solução. O nome dos componentes do grupo e as respostas às questões anteriores também podem ser adicionadas a este arquivo.
# 5. Entregáveis
| Item          | Formato                              | Onde entregar                            |
|---------------|---------------------------------------|------------------------------------------|
| Código‑fonte  | Repositório GitHub público            | link: turing                             |
| Vídeo-demo (~5 min) | link YouTube não listado ou Google Drive | link: Moodle                             |
    
# 6. Critérios de avaliação (100 pts)
| Critério                                            | Pontos |
|-----------------------------------------------------|--------|
| Funcionamento end‑to‑end (RF‑1 → RF‑4)              | 50     |
| Qualidade de código e boas práticas OOP/DDD         | 15     |
| Tratamento de concorrência (commit e idempotência)  | 10     |
| Relatório/Documentação (clareza, métricas, diagramas)| 10     |
| Demonstração em vídeo                               | 15     |

# 7. Orientações gerais
* O(s) aluno(s) deve(m) aparecer no início da gravação e se apresentar, em seguida execute o projeto e explique-o.
* Pode ser feito em grupo de até três (3) pessoas..
# 8. Recursos de apoio
* Kafka: The Definitive Guide (O’Reilly).
* Documentação oficial Kafka 4.0.0.
* Spring for Apache Kafka Reference.
