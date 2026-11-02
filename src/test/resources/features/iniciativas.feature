# language: pt
Funcionalidade: API REST de iniciativas ESG
  Como consumidor da API de cidades ESG
  Quero gerenciar iniciativas ambientais, sociais e de governança
  Para acompanhar seus impactos de forma confiável

  Cenário: Cadastrar iniciativa ESG válida
    Dado que possuo informações válidas de uma iniciativa ambiental
    Quando realizo o cadastro pela API
    Então o sistema retorna HTTP 201
    E a resposta contém os dados cadastrados
    E o identificador é gerado automaticamente

  Cenário: Rejeitar iniciativa com pontuação inválida
    Dado que informo pontuação de impacto igual a 11
    Quando tento cadastrar a iniciativa
    Então o sistema retorna HTTP 400
    E apresenta uma mensagem de validação em JSON

  Cenário: Consultar iniciativa existente
    Dado que existe uma iniciativa ESG cadastrada
    Quando consulto a iniciativa pelo identificador
    Então a API retorna HTTP 200
    E apresenta os dados da iniciativa

  Cenário: Consultar iniciativa inexistente
    Dado que uma iniciativa não existe
    Quando realizo uma consulta pelo identificador
    Então a API retorna HTTP 404
    E apresenta um erro estruturado em JSON

  Cenário: Atualizar iniciativa ESG
    Dado que existe uma iniciativa ambiental cadastrada
    Quando atualizo sua categoria para SOCIAL e sua pontuação para 9
    Então a API retorna HTTP 200
    E a iniciativa apresenta os dados atualizados

  Cenário: Excluir iniciativa ESG
    Dado que existe uma iniciativa cadastrada
    Quando realizo sua exclusão
    Então a API retorna HTTP 204
    E uma consulta posterior retorna HTTP 404

  Cenário: Listar iniciativas ESG
    Dado que existem iniciativas cadastradas
    Quando consulto a listagem
    Então a API retorna HTTP 200
    E apresenta uma lista JSON com as iniciativas existentes

  Cenário: Atualizar iniciativa inexistente
    Dado que uma iniciativa não existe
    Quando tento atualizar a iniciativa inexistente
    Então a API retorna HTTP 404
    E apresenta um erro estruturado em JSON

  Cenário: Excluir iniciativa inexistente
    Dado que uma iniciativa não existe
    Quando tento excluir a iniciativa inexistente
    Então a API retorna HTTP 404
    E apresenta um erro estruturado em JSON
