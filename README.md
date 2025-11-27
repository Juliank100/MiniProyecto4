<<<<<<< HEAD
<<<<<<< HEAD
# 🐉 DragonQuest – Mini Proyecto 3  
*Materia:* Programación Orientada a Eventos  
*Profesor:* Joshua Triana  
*Universidad del Valle*

---

## Integrantes  
- *Juan Esteban Aguirre Castañeda* – 202459676  
- *Kevin Julián López Moreno* – 202380379  

---

## Descripción del Proyecto  
*DragonQuest* es un *juego de combate por turnos* desarrollado en *Java*, en el que cuatro héroes se enfrentan a cuatro enemigos.  
Cada personaje cuenta con atributos como *HP, **MP, **ataque, **defensa* y *velocidad*.  
Durante el combate, los héroes pueden *atacar, defenderse, usar habilidades o ítems, mientras que los enemigos actúan mediante una **IA básica*.  

El juego se ejecuta en consola y busca aplicar los conceptos fundamentales de la *Programación Orientada a Eventos (POE)*, incluyendo:
- Herencia  
- Polimorfismo  
- Clases abstractas e interfaces  
- Encapsulamiento  
- Organización modular por paquetes
- Interfaz gráfica con *Swing*
- patrón Modelo–Vista–Controlador (MVC).
---

## Estructura del Proyecto

```plaintext
dragonquest/

mvc/
├── model/        → Lógica del combate, personajes, habilidades, items
│   ├── combate/
│   │   └── Batalla.java               → Control del flujo de combate y turnos
│   │
│   ├── estados/
│   │   └── EstadoAlterado.java        → Enum con estados (normal, paralizado, dormido, etc.)
│   │
│   ├── habilidades/
│   │   ├── Habilidad.java             → Clase base abstracta para las habilidades
│   │   ├── Curacion.java              → Habilidad de curar HP
│   │   ├── CuracionGrupal.java              → Habilidad de curar HP al grupo
│   │   ├── Dormir.java              → Habilidad de Dormir al enemigo
│   │   ├── GolpeCritico.java              → Habilidad para que un golpe fisico tenga chance de ser un golpe critico
│   │   ├── Paralisis.java              → Habilidad de paralizar al enemigo
│   │   ├── RemoverEstado.java              → Habilidad para remover cualquier estado negativo 
│   │   ├── Veneno.java              → Habilidad de curar HP
│   │   ├── Aturdimiento.java              → Habilidad de envenenar al enemigo
│   │   └── DanioMagico.java           → Habilidad ofensiva mágica
│   │
│   ├── items/
│   │   ├── Item.java                  → Clase base abstracta para ítems
│   │   ├── InventarioGrupo.java              → Clase auxiliar simple para pares
│   │   ├── PocionCuracion.java                → Restaura HP
│   │   ├── Antidoto.java              → Habilidad para eliminar el estado envenenado
│   │   └── PocionMagia.java                  → Restaura MP
│   │
│   ├── personajes/
│   │   ├── Personaje.java             → Clase base abstracta con atributos y métodos comunes
│   │   ├── Heroe.java                 → Subclase que representa a los héroes
│   │   └── Enemigo.java               → Subclase que representa a los enemigos
│   │
│   └── GameModel.java
│
│
│
├── view/         → Vistas (consola y GUI)
│   │
│   ├── gui/                               → Contiene toda la interfaz gráfica (Java Swing)
│   │   ├── VentanaPrincipal.java          → Ventana inicial del juego (menú principal con opciones)
│   │   ├── VentanaCombate.java            → Ventana principal de combate con paneles de héroes y enemigos
│   │   ├── PanelHeroe.java                → Muestra la información de cada héroe (nombre, HP, MP, estados)
│   │   ├── PanelEnemigo.java              → Muestra la información de cada enemigo
│   │   ├── VentanaHabilidades.java        → Permite seleccionar y usar habilidades disponibles
│   │   ├── VentanaInventario.java         → Permite seleccionar y usar ítems del inventario
│   │   ├── PanelMensajes.java             → Panel auxiliar que muestra eventos del combate (ataques, curaciones, etc.)
│   │
│   ├── imagenes/
│   │    └── fondo_azul.png
│   │
│   ├── sonidos/
│   │   ├── heal.wav               
│   │   ├── hit.wav
│   │   ├── musica_batalla.wav
│   │   ├── musica_menu.wav
│   │   └── status.wav
│   │
│   └── ConsoleView.java
│
└── controller/   → GameController (manejo de flujo y eventos)
│   │
│   └── GameController.java
│
│
├── MainMVC.java           → Clase principal (main)
│
│
│
└── config/                            → Manejo de configuración general del juego
│   ├── ConfiguracionJuego.java        → Lee y aplica valores desde config.properties
│   └── ConfiguracionJuegoManager.java → Control centralizado de opciones (volumen, dificultad, etc.)


```
---

## Instrucciones para Colaboradores
*Si otro desarrollador desea contribuir:*

*Clonar el repositorio:*

- git clone https://github.com/Juliank100/MiniProyecto3MVC.git cd MiniProyecto3MVC


*Crear una nueva rama:*

- git checkout -b nombre-rama


*Realizar los cambios y hacer commit:*

- git add .
- git commit -m "Descripción del cambio"


*Subir los cambios y crear un Pull Request:*

- git push origin nombre-rama

- Luego abrir el PR en GitHub para revisión.

---
=======
# MiniProyecto4
Miniproyecto 4 POE
>>>>>>> 6b8dd00ed4c912a9874ab6e0ebca21807a0ef638
