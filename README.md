# Laboratorio: eventos DML do Hibernate

Aplicacao Spring Boot para demonstrar a captura de `INSERT`, `UPDATE` e `DELETE` de `Produto` diretamente pelos listeners do Hibernate (`PostInsertEventListener`, `PostUpdateEventListener` e `PostDeleteEventListener`).

## Fluxo

1. A API salva, atualiza ou exclui um produto usando Spring Data JPA.
2. O listener Hibernate cria um snapshot do produto no flush da sessao.
3. Depois do commit da transacao, o evento e registrado em `eventos_persistencia` e publicado no exchange topic `lab.dml`, com routing key `dml.event`.
4. Um `@RabbitListener` consome a mensagem da fila `lab.dml.events` e grava uma copia em `mensagens_recebidas`.
5. A interface web mostra os produtos, eventos capturados e mensagens consumidas.

Publicar depois do commit evita emitir mensagens para alteracoes que sejam posteriormente revertidas. A publicacao e deliberadamente sincrona para que falhas de conexao com RabbitMQ aparecam no log da aplicacao, em vez de serem ignoradas.

## Executar tudo com Podman (Git Bash no Windows)

Requisitos: Podman Desktop instalado e a maquina Podman inicializada. Abra Git Bash na raiz do projeto e confirme que o comando esta disponivel:

```bash
podman --version
podman compose version
```

Se a maquina estiver parada, inicie-a com `podman machine start`. Na primeira utilizacao, se ainda nao houver uma maquina, crie-a com `podman machine init` e depois inicie-a. Se o Git Bash nao encontrar o executavel, mas o Podman Desktop estiver instalado no caminho padrao, adicione-o ao `PATH` da sessao:

```bash
export PATH="$PATH:/c/Program Files/RedHat/Podman"
```

O [compose.yaml](./compose.yaml) inicia MySQL 8.4, RabbitMQ 4 com interface de gerenciamento e a aplicacao Spring Boot. O [Dockerfile](./Dockerfile) compila o JAR com Java 21 e cria a imagem da aplicacao.

```bash
podman compose up --build -d
podman compose ps
podman compose logs -f app
```

O compose aguarda as verificacoes de saude do MySQL e RabbitMQ antes de iniciar a aplicacao. Quando ela estiver pronta, abra:

- Aplicacao e interface: `http://localhost:8080`
- RabbitMQ Management: `http://localhost:15672` (usuario `lab`, senha `lab_password`)
- MySQL: `localhost:3306` (banco `events_lab`, usuario `root`, senha `lab_root_password`)

Os valores padrao sao credenciais apenas para o laboratorio local. Para personaliza-los no Git Bash antes de iniciar os containers:

```bash
export MYSQL_ROOT_PASSWORD='outra-senha-local'
export MYSQL_DATABASE='events_lab'
export RABBITMQ_USER='lab'
export RABBITMQ_PASSWORD='outra-senha-local'
podman compose up --build -d
```

Essas variaveis precisam permanecer definidas tambem nos comandos `podman compose` seguintes. O MySQL e o RabbitMQ usam volumes nomeados (`mysql-data` e `rabbitmq-data`), entao os dados continuam disponiveis depois de parar e recriar os containers.

As credenciais de inicializacao do MySQL e RabbitMQ sao aplicadas quando os volumes sao criados pela primeira vez. Alterar as variaveis com volumes existentes nao troca automaticamente as senhas ja configuradas neles.

```bash
podman compose logs -f mysql rabbitmq
podman compose stop
podman compose start
podman compose down
```

`podman compose down` remove os containers e a rede, mas preserva os volumes. Para apagar tambem os dados persistidos e reiniciar o lab do zero, use `podman compose down -v` — isso exclui os dados do banco e do broker.

## Diagnostico

Se `http://localhost:8080` retornar `ERR_EMPTY_RESPONSE`, confira se a aplicacao terminou de iniciar e se o container nao esta reiniciando:

```bash
podman compose ps
podman compose logs --tail=200 app
```

Depois de alterar o codigo ou atualizar a imagem, recrie a aplicacao com `podman compose up --build -d app`. Aguarde a inicializacao e confira novamente os logs. A linha `Started LabApplication` indica que o servidor web iniciou; a mensagem `Tomcat started on port 8080` confirma a porta da aplicacao. O compose tambem precisa ter MySQL e RabbitMQ saudaveis antes de iniciar o servico.

## Executar a aplicacao fora do container

Como alternativa, e possivel iniciar apenas MySQL e RabbitMQ com o compose e executar a aplicacao localmente. Requer JDK 21 e Maven 3.8+:

```bash
podman compose up -d mysql rabbitmq
export MYSQL_HOST=localhost
export MYSQL_PORT=3306
export MYSQL_DATABASE=events_lab
export MYSQL_USER=root
export MYSQL_PASSWORD=lab_root_password
export RABBITMQ_HOST=localhost
export RABBITMQ_PORT=5672
export RABBITMQ_USER=lab
export RABBITMQ_PASSWORD=lab_password
mvn spring-boot:run
```

As tabelas da aplicacao sao criadas/atualizadas pelo Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

## API

| Metodo | Endpoint | Descricao |
| --- | --- | --- |
| `GET` | `/api/produtos` | Lista produtos |
| `POST` | `/api/produtos` | Cria produto (`{"nome":"Teclado","preco":199.90}`) |
| `PUT` | `/api/produtos/{id}` | Atualiza produto |
| `DELETE` | `/api/produtos/{id}` | Exclui produto |
| `GET` | `/api/eventos` | Lista ate 100 eventos Hibernate persistidos |
| `GET` | `/api/mensagens` | Lista ate 100 mensagens recebidas da fila |

## Como validar

1. Inicie o laboratorio com `podman compose up --build -d` e confira `podman compose ps`.
2. Abra `http://localhost:8080` e crie um produto na pagina.
3. Confirme um `INSERT` em eventos de persistencia e, em seguida, a mensagem correspondente na secao RabbitMQ.
4. Edite e exclua o produto e observe `UPDATE` e `DELETE` nas duas listas.
5. Para conferir a fila ou o exchange, use o RabbitMQ Management em `http://localhost:15672`.

`eventos_persistencia` e `mensagens_recebidas` sao tabelas de auditoria didatica. A segunda prova o consumo RabbitMQ; ela nao e o mecanismo de captura do Hibernate.
