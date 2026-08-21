# Plano: Conclusão e Consistência do Módulo `cargo`

## Contexto

O módulo `cargo` (`src/main/java/br/com/logicore/modules/cargo/`) é um CRUD administrativo
sob `/api/v1/cargos`, com status `Boolean ativo` e duas chaves de unicidade (`nome`,
`codigo`). Já possui testes em todas as camadas (controller, service, mapper, validator,
spec). O objetivo é corrigir o único defeito funcional real e alinhar o que for
necessário com as convenções atuais, **sem copiar cegamente o Department** e sem alterar
outros módulos.

## 1. Estado atual do Cargo

- **Controller** (`CargoController.java`): completo e correto; OpenAPI presente; paths
  versionados `/api/v1/cargos`; 201/200/204; PATCH activate/deactivate.
- **DTOs**: `CreateCargoRequest` validado (`@NotBlank` em `nome`/`codigo`). `UpdateCargoRequest`
  **sem `@NotBlank`** em `nome`/`codigo` (update parcial, por design).
- **Entity** (`Cargo.java`): `nome`/`codigo` únicos `NOT NULL`, `ativo` `Boolean` default
  `true`, timestamps. Consistente com a migration `V2`.
- **Mapper** (`CargoMapper.java`): `toEntity` + `toResponse` manual `@Component`. OK.
- **Repository** (`CargoRepository.java`): `@Repository` presente; métodos derivados.
- **Specifications** (`CargoSpecifications.java`): `withSearch(nome|codigo)` +
  `withStatus(Boolean)`; `null` = sem filtro. OK.
- **Validator** (`CargoValidator.java`): unicidade de `nome` e `codigo` (create e update).
- **Service** (`CargoService.java`): create/summary/findById/activate/deactivate OK.
  `update` sobrescreve `nome`/`descricao`/`codigo` **incondicionalmente** (defeito).
- **Migration** `V2__create_table_cargo.sql`: consistente com a entidade.
- **Testes**: 5 classes cobrindo todas as camadas. Cobertura funcional ampla.

## 2. Problemas encontrados

| # | Problema | Severidade | Confirmação |
|---|----------|-----------|-------------|
| P1 | `CargoService.update` faz `setNome/setDescricao/setCodigo` mesmo quando o request vem `null`/vazio → `DataIntegrityViolationException` (coluna `NOT NULL`) → **500** em PUT parcial | 🔴 Crítico (funcional) | Confirmado em `CargoService.java:92-102` + `UpdateCargoRequest` sem `@NotBlank` |
| P2 | Redundância/divergência de padrão: CargoValidator usa `findByNomeIgnoreCase`+`filter` para update, enquanto Department usa `existsByNomeIgnoreCaseAndIdNot` | 🟡 Melhoria | `CargoRepository` tem `findBy*` **e** `existsBy*`; ambos usados |
| P3 | Estilo Lombok do request (`@Getter/@Setter`) difere de Department (`@Data`) | 🟢 Normal | Cosmético |

Itens da auditoria anterior reavaliados e **descartados como não-defeito**:
- "Múltiplas estratégias de unicidade" → `cargo` tem nome **e** código únicos por
  requisito de domínio; é correto e intencional.
- "Diferenças de PUT (total em Department, parcial em Cargo)" → documentado em
  `AGENTS.md` §9 como divergência aceitável; Cargo é parcial (igual a Address/Employee).
  Não será forçado a virar total.
- "Risco de PUT com valores null" → é exatamente o P1; resolvido no service.

## 3. O que já está correto (preservar)

- Estrutura de packages e separação de responsabilidades.
- Controller + OpenAPI + `ResponseEntity` + paths `/api/v1`.
- `PageResponse<T>`, `@PageableDefault(size=20, sort="id")`.
- Specifications estáticas e combinação `where().and()`.
- `ativo` como `Boolean` (decisão de domínio, não é defeito).
- PATCH activate/deactivate.
- `CargoMapper` manual `@Component`.
- Migration `V2` coerente com a entidade.
- Suíte de testes existente (base sólida).

## 4. Arquivos que precisarão ser alterados

- `modules/cargo/service/CargoService.java` — corrigir `update` (P1). **Obrigatório.**
- `modules/cargo/service/CargoServiceTest.java` — adicionar/cobrir cenários de update
  parcial (P1). **Obrigatório.**
- `modules/cargo/repository/CargoRepository.java` — **apenas se** a decisão D2 (alinhar a
  Department) for aprovada.
- `modules/cargo/validator/CargoValidator.java` — **apenas se** D2 aprovada.
- `modules/cargo/validator/CargoValidatorTest.java` — **apenas se** D2 aprovada.

Não serão alterados: `CreateCargoRequest`, `UpdateCargoRequest` (mantém update parcial),
`CargoController`, `CargoMapper`, `CargoSpecifications`, `CargoControllerTest`, migration,
Department, frontend, `pom.xml`.

## 5. Plano de implementação (ordem)

1. **Corrigir `CargoService.update` (P1)** — adotar guarda de update parcial igual à usada
   em `AddressService` (`isPresent` = não-nulo e não-vazio):
   - `nome`: só `setNome` se `isPresent(request.getNome())`; validar duplicidade só quando
     realmente alterado.
   - `codigo`: só `setCodigo` se `isPresent(request.getCodigo())`; mesma lógica.
   - `descricao`: só `setDescricao` se `isPresent` (ou manter null-safe; coluna permite
     null).
   - `ativo`: manter lógica atual (`!= null`).
   - Reaproveitar helper `isPresent(String)` (já existe em Address; criar local em Cargo se
     preferir não acoplar).
   - Efeito: PUT parcial nunca nula colunas obrigatórias; brancos são ignorados.

2. **(Opcional — D2) Alinhar validator/repository a Department**:
   - `CargoRepository`: adicionar `existsByNomeIgnoreCaseAndIdNot`, `existsByCodigoIgnoreCaseAndIdNot`;
     remover `findByNomeIgnoreCase`/`findByCodigoIgnoreCase` (passam a ser órfãos).
   - `CargoValidator`: `validateUniqueNameForUpdate`/`validateUniqueCodeForUpdate` passam a
     usar `existsBy...AndIdNot`, como Department.
   - Mantém cobertura e remove redundância.

3. **Ajustar/criar testes (ver seção 6).**

4. **Validação**: rodar `mvn test` (ou apenas os pacotes `cargo`) e `mvn -q -DskipTests=false
   verify` para garantir build verde e cobertura.

## 6. Testes a criar/ajustar

Em `CargoServiceTest.java` (adicionar, não remover existentes):
- `shouldUpdateCargoPartiallyKeepingNomeAndCodigo`: PUT enviando só `descricao` → `nome`/`codigo`
  inalterados (cobre P1).
- `shouldUpdateCargoAtivoOnly`: PUT enviando só `ativo=false` → demais campos inalterados.
- `shouldIgnoreBlankNomeOnPartialUpdate`: PUT com `nome=""` → não sobrescreve (ou rejeita
  conforme D1).
- Manter os testes atuais de update (incluindo `shouldUpdateCargoWithoutValidatingWhenNameAndCodeAreTheSame`).

Em `CargoControllerTest.java` (opcional): um PUT parcial (`{"descricao":"x"}`) retornando 200
e preservando `nome`/`codigo` — valida o contrato de ponta a ponta.

Se D2 aprovada: ajustar `CargoValidatorTest` para mockar `existsBy...AndIdNot` em vez de
`findBy...IgnoreCase`.

## 7. Riscos / decisões que precisam de aprovação

- **D1 — Semântica do PUT (parcial vs total):** recomendo **preservar parcial** (consistência
  com Address/Employee e com `AGENTS.md` §9) e corrigir o bug no service (P1). A alternativa
  (adicionar `@NotBlank` em `UpdateCargoRequest`) tornaria o update *total*, quebrando a
  coerência com Address/Employee. **Requer sua aprovação.**
- **D2 — Redundância do Repository/Validator:** recomendo **manter o atual** (funciona e está
  testado) para minimizar risco; o alinhamento a Department é opcional. Se preferir
  consistência estrita com a referência primária, aprovo a mudança (passos 2 + ajustes de
  teste). **Requer sua aprovação.**
- **D3 — Brancos (`""`) em update parcial:** recomendo tratá-los como "não informado"
  (ignorar), igual a Address. Alternativa: rejeitar com 400. **Requer sua aprovação** (baixo
  impacto).

## Decisões já tomadas (sem ambiguidade)

- Não alterar `UpdateCargoRequest` para `@NotBlank` (mantém parcial) salvo aprovação de D1.
- Não mexer em Department, frontend, migration, `pom.xml`.
- Não introduzir nova dependência.
- Manter `ativo` como `Boolean` (não converter para enum).

## Validação final esperada

- `mvn test` verde para o módulo `cargo`.
- Novos testes de update parcial aprovados.
- Nenhuma regressão em `CargoControllerTest`/`CargoSpecificationsIntegrationTest`.
- Código de Cargo funcional, testado e aderente às convenções (`docs/conventions/backend.md`).
