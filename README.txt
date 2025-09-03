# 📦 NSBots

**NSBots** é um plugin de movimentação avançada para servidores Minecraft. Ele oferece botas personalizadas com salto direcional, encantamentos configuráveis e entrega via comando. Ideal para servidores que buscam jogabilidade dinâmica, mobilidade estratégica e itens exclusivos.

---

## ✅ Requisitos

- **Minecraft Version:** 1.8.8+
- **Server Software:** Spigot, Paper ou forks compatíveis
- **Java Version:** 8+

---

## ⚙️ Instalação

1. Baixe a versão mais recente do plugin [aqui](https://github.com/dev-ns-plugins/NSBots)
2. Coloque o arquivo `.jar` na pasta `plugins/` do seu servidor
3. Inicie o servidor para gerar os arquivos de configuração
4. Edite o `config.yml` conforme suas preferências

---

## 💬 Comandos

| Comando     | Permissão             | Descrição                          |
|-------------|------------------------|------------------------------------|
| `/nsbots`   | `nsbots.boots.give`    | Dá as botas ao jogador que executa |

---

## 🔐 Permissões

| Permissão             | Padrão | Descrição                          |
|-----------------------|--------|------------------------------------|
| `nsbots.boots.give`   | OP     | Permite dar as botas personalizadas |

---

## 🛠️ Configuração

```yaml
boots:
  name: "&bBOTAS DO NSBOT"
  material: "LEATHER_BOOTS"
  lore:
    - "&7Botas mágicas que aumentam sua velocidade"
    - "&7Te dão super pulo e proteção contra quedas"
    - "&7Ao pular, você é arremessado pra frente!"
    - "&5Feitas por kN"
  enchantments:
    PROTECTION_FALL: 4
    DURABILITY: 3
  effects:
    SPEED: 1
    JUMP: 2
    DAMAGE_RESISTANCE: 0

jump:
  power: 1.2     # força horizontal
  height: 0.5    # força vertical
  cooldown: 3000 # tempo em milissegundos entre pulos com dash

