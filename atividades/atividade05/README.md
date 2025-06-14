# Atividade 05

Resolva o exercício abaixo. Entregar apenas os arquivos .JAVA, compacte-os em um único arquivo .ZIP e submeta-o por este link.

Defina uma classe Mailbox que tem um atributo message do tipo String. A classe Mailbox deve ter dois métodos: storeMessage e retrieveMessage. O método storeMessage recebe um String e, se o mesmo tiver sido consumido (message == null), armazena no atributo message do Mailbox. Caso contrário, quem chamou o método storeMessage deve esperar até que alguém consuma a mensagem (chamando o método retrieveMessage). De forma similar, o método retrieveMessage retorna o valor da mensagem, caso ela tenha sido produzida/armazenada (message != null). Caso contrário quem chamou o método deve esperar até que alguém produza uma mensagem (chamando o método ​storeMessage​).


Passos:

a) Crie a classe ​Producer​, que é um thread e deve ter um atributo do tipo Mailbox e no seu método run deve ser definido um loop que executa o método ​storeMessage do Mailbox, armazenando mensagens no Mailbox.

b) Defina uma classe ​Consumer​, que também é um thread, e que deve consumir mensagens (chamando o método ​retrieveMessage)​ escritas em no seu atributo do tipo Mailbox.


c) Crie uma classe de teste com um método main que cria um Producer e um Consumer que compartilham o mesmo Mailbox e iniciam a execução.

d) Altere a classe de teste para criar mais de um produtor e/ou consumidor para o mesmo Mailbox


Dicas:

Utilize os métodos ​wait ​e ​notifyAll ​para implementar os métodos da classe Mailbox.
Na última parte do exercício, onde é pedido para alterar a classe de teste, você deve adicionar um atributo de identificação (String) nas classes Producer e Consumer, de modo a permitir que sejam identificados tanto quem está consumindo uma mensagem, quanto quem está produzindo a mesma. Para tal concatene o identificador dos objetos a mensagem a ser enviada (no caso do produtor) ou a mensagem a ser impressa (no caso do consumidor).
