# Atividade 6b
```
Mesma atividade anterior, porém precisa utilizar Executors (pacote concurrent).
 
 
Seguir os seguintes passos:
Criar a classe ContaBancaria com os atributos número e saldo. Também deverá ter os métodos sacar (subtrai ao saldo) e depositar (acrescenta ao saldo)
Criar a classe CaixaBancario: recebe a conta bancária no construtor. No run, irá chamar o método sacar com o valor de 500,00 e depositar 500,00. Entre um saque e um depósito, dê um intervalo de 1 segundos (sleep).
Imprimir o saldo (após todas as threads concluírem)
Criar o programa principal para rodar e verificar os resultados. Deve ser criada uma classe ContaBancaria e várias CaixaBancario (entre 10 a 30). Os resultados foram os esperados (saldo zero)?!
Corrija o programa utilizando Lock.
```
