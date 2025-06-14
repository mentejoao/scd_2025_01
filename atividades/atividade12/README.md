# Atvidade 12
Criar uma aplicação cliente servidor, na qual diversos clientes possam fazer conexão com o servidor para trocarem mensagens entre si. Utilizar comunicação de socket por TCP.

A troca de mensagens é realizada entre os usuários conectados no servidor. Portanto, o servidor precisa armazenar a lista de usuários conectados e fazer a mediação da comunicação entre os usuários.

Para a comunicação acontecer, algumas definições precisam ser compreendidas:

Todo usuário precisa ser identificado: ao conectar no servidor o usuário envia seu nome para ser registrado no servidor. 
Regras de comunicação (protocolo): para enviar uma mensagem é preciso identificar o destinatário (por exemplo: nome:mensagem, ou seja a mensagem será enviada para o nome).
Uma mensagem com o símbolo * no local do nome, deve ser encaminhada para todos os participantes do chat.
Qualquer mensagem que não siga o formato indicado, deve ser ignorada.
É necessário o uso de Sockets, Threads e classe de entrada e saída (Scanner e PrintStream).

MESMO EXERCÍCIO ANTERIO, PORÉM COM CAPACIDADE PARA ENVIAR ARQUIVOS:

Usuario:File:nome_arquivo

