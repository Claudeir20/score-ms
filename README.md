# score-ms

![Java](https://img.shields.io/badge/Java-26-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Message%20Broker-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Wrapper-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-Annotation%20Processor-BC4521?style=for-the-badge)

Microsservico de analise de credito criado com Spring Boot e RabbitMQ. O servico faz parte de uma arquitetura orientada a eventos, consumindo solicitacoes de credito, calculando um score simples e publicando a decisao de aprovacao ou reprovacao.

## Visao geral

O `score-ms` atua como consumidor e produtor dentro do fluxo de credito:

1. Consome eventos `credit.requested` publicados no exchange `credit.exchange`.
2. Calcula uma pontuacao com base em renda, valor solicitado e prazo.
3. Publica `credit.approved` quando o score e maior ou igual a `60`.
4. Publica `credit.rejected` quando o score e menor que `60`.
5. Encaminha mensagens com falha para uma Dead Letter Queue.

## Arquitetura

```mermaid
flowchart LR
    credito["credito-ms"] -->|"credit.requested"| exchange["RabbitMQ<br/>credit.exchange"]
    exchange -->|"credit.requested"| score["score-ms"]
    score -->|"credit.approved"| exchange
    score -->|"credit.rejected"| exchange
    exchange -->|"credit.approved / credit.rejected"| notificacao["notification-ms"]
    score -. falhas .-> dlq["score.dlq"]
```

## Tecnologias

| Tecnologia | Uso no projeto |
| --- | --- |
| ![Java](https://img.shields.io/badge/Java-26-ED8B00?style=flat-square&logo=openjdk&logoColor=white) | Linguagem principal |
| ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?style=flat-square&logo=springboot&logoColor=white) | Framework da aplicacao |
| ![Spring AMQP](https://img.shields.io/badge/Spring%20AMQP-Mensageria-6DB33F?style=flat-square&logo=spring&logoColor=white) | Integracao com RabbitMQ |
| ![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Broker-FF6600?style=flat-square&logo=rabbitmq&logoColor=white) | Exchange, filas, routing keys e DLQ |
| ![Maven](https://img.shields.io/badge/Maven-Wrapper-C71A36?style=flat-square&logo=apachemaven&logoColor=white) | Build, dependencias e testes |
| ![Lombok](https://img.shields.io/badge/Lombok-Boilerplate-BC4521?style=flat-square) | Reducao de codigo repetitivo |

## Estrutura do projeto

```text
score-ms/
  pom.xml
  src/main/java/dev/mota/score_ms/
    config/
      RabbitMQConfig.java
    event/
      CreditRequestedEvent.java
      CreditApprovedEvent.java
      CreditRejectedEvent.java
    listener/
      CreditRequestedListener.java
    publisher/
      CreditDecisionPublisher.java
    service/
      CreditScoreService.java
  src/main/resources/
    application.properties
```

## Configuracao do RabbitMQ

O servico usa as seguintes configuracoes em `application.properties`:

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=mota
spring.rabbitmq.password=1234
```

Recursos declarados pela aplicacao:

| Tipo | Nome |
| --- | --- |
| Topic exchange | `credit.exchange` |
| Fila de entrada | `score.credit.requested.queue` |
| Routing key de entrada | `credit.requested` |
| Routing key de aprovacao | `credit.approved` |
| Routing key de reprovacao | `credit.rejected` |
| Dead letter exchange | `score.dlx` |
| Dead letter queue | `score.dlq` |
| Dead letter routing key | `score.dlq` |

## Regra de score

A pontuacao maxima e `100` pontos:

| Criterio | Pontos |
| --- | ---: |
| Renda maior ou igual a `5000.00` | 40 |
| Valor solicitado menor ou igual a 4x a renda | 25 |
| Prazo menor ou igual a 36 meses | 20 |
| Valor solicitado menor ou igual a 8x a renda | 15 |

Resultado:

- Score `>= 60`: publica `CreditApprovedEvent`.
- Score `< 60`: publica `CreditRejectedEvent`.

## Contratos de eventos

### Evento consumido

Routing key: `credit.requested`

```json
{
  "eventId": "b632c8c8-5ce2-4f4f-8f98-385c7c8f5a6f",
  "requestId": "8150262d-97b0-40f6-8f73-4c45e1f9c1c8",
  "cpf": "12345678900",
  "name": "Jose Mota",
  "income": 6500.00,
  "valueRequest": 20000.00,
  "termMonths": 24,
  "correlationId": "c2cf0f79-71dd-4cc7-9e45-5d3bb4ea80f1",
  "occurredAt": "2026-07-05T20:30:00"
}
```

### Eventos publicados

Routing key: `credit.approved`

```json
{
  "eventId": "d57bc450-71b2-4655-a735-256d6429cc29",
  "requestId": "8150262d-97b0-40f6-8f73-4c45e1f9c1c8",
  "correlationId": "c2cf0f79-71dd-4cc7-9e45-5d3bb4ea80f1",
  "occurred": "2026-07-05T20:30:01"
}
```

Routing key: `credit.rejected`

```json
{
  "eventId": "14efc8dc-f138-49f0-b2b7-b065444387a7",
  "requestId": "8150262d-97b0-40f6-8f73-4c45e1f9c1c8",
  "correlationId": "c2cf0f79-71dd-4cc7-9e45-5d3bb4ea80f1",
  "occurred": "2026-07-05T20:30:01"
}
```

## Como executar

Entre na pasta da aplicacao:

```bash
cd score-ms
```

Execute os testes:

```bash
./mvnw test
```

No Windows:

```bash
mvnw.cmd test
```

Suba a aplicacao:

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

A aplicacao inicia na porta `8081`.

## Observacoes

- O servico depende de uma instancia do RabbitMQ rodando localmente com usuario `mota` e senha `1234`.
- O modo de acknowledgement esta configurado como `auto`.
- O retry do listener esta habilitado com ate 3 tentativas.
- Mensagens que falham apos as tentativas podem ser direcionadas para a DLQ `score.dlq`.
