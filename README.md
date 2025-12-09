# 🐉 DragonQuest – Proyecto 4  
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
Cada personaje cuenta con atributos como *HP*, *MP*, *ataque*, *defensa* y *velocidad*.  
Durante el combate, los héroes pueden *atacar, defenderse, usar habilidades o ítems*, mientras que los enemigos actúan mediante una *IA básica*.  

El juego se ejecuta en consola y GUI, y busca aplicar los conceptos fundamentales de la *Programación Orientada a Eventos (POE)*, incluyendo:
- Herencia  
- Polimorfismo  
- Clases abstractas e interfaces  
- Encapsulamiento  
- Organización modular por paquetes
- Interfaz gráfica con *Swing*
- Patrón Modelo–Vista–Controlador (MVC)
- Estructuras de datos avanzadas (Queue, Stack, PriorityQueue, TreeMap, LinkedHashMap)
- Sistema de excepciones personalizadas
- Persistencia y carga de partidas

---

## Estructura del Proyecto

```plaintext
dragonquest/

src/
├── config/
│   └── ConfiguracionJuego.java        → Configuración general (volumen, idioma, etc.)
│
├── mvc/
│   ├── model/
│   │   ├── combate/
│   │   │   ├── Batalla.java           → Control del flujo de combate y turnos
│   │   │   └── BatallaConSistemas.java → Batalla con inventarios, deshacer/rehacer, historial
│   │   │
│   │   ├── estados/
│   │   │   └── EstadoAlterado.java    → Enum con estados (normal, paralizado, dormido, envenenado)
│   │   │
│   │   ├── excepciones/
│   │   │   ├── GameException.java                    → Excepción base personalizada
│   │   │   ├── PersonajeMuertoException.java         → Lanzada al actuar sobre personaje muerto
│   │   │   ├── EstadoYaPresenteException.java        → Lanzada al aplicar estado duplicado
│   │   │   ├── EstadoNoEncontradoException.java      → Lanzada al quitar estado inexistente
│   │   │   ├── ExcepcionInventarioLleno.java         → Inventario sin espacio
│   │   │   ├── ExcepcionMPInsuficiente.java          → MP insuficiente para habilidad
│   │   │   ├── ExcepcionGuardadoPartida.java         → Error al guardar/cargar partida
│   │   │   ├── ExcepcionPersonajeMuerto.java         → Acción sobre personaje muerto
│   │   │   └── ExcepcionJuego.java                   → Excepción genérica del juego
│   │   │
│   │   ├── gremio/
│   │   │   ├── SistemaGremio.java                    → Gestión del gremio de aventureros
│   │   │   ├── ColaTurnosGremio.java                 → Cola de turnos (PriorityQueue)
│   │   │   └── SolicitudGremio.java                  → Solicitud de turno en gremio
│   │   │
│   │   ├── habilidades/
│   │   │   ├── Habilidad.java         → Clase base abstracta para las habilidades
│   │   │   ├── Curacion.java          → Habilidad de curar HP individual
│   │   │   ├── CuracionGrupal.java    → Habilidad de curar HP al grupo
│   │   │   ├── Dormir.java            → Habilidad para dormir al enemigo
│   │   │   ├── GolpeCritico.java      → Golpe físico con chance de crítico
│   │   │   ├── Paralisis.java         → Habilidad para paralizar
│   │   │   ├── RemoverEstado.java     → Habilidad para remover estados negativos
│   │   │   ├── Veneno.java            → Habilidad para envenenar
│   │   │   ├── Aturdimiento.java      → Habilidad para aturdir
│   │   │   └── DanioMagico.java       → Habilidad ofensiva mágica
│   │   │
│   │   ├── items/
│   │   │   ├── Item.java              → Clase base abstracta para ítems
│   │   │   ├── InventarioGrupo.java   → Inventario compartido del grupo
│   │   │   ├── InventarioPersonal.java → Inventario individual por héroe
│   │   │   ├── PocionCuracion.java    → Restaura HP
│   │   │   ├── PocionMagia.java       → Restaura MP
│   │   │   ├── Antidoto.java          → Elimina estado envenenado
│   │   │   ├── HierbaMedicinal.java   → Restaura HP moderado
│   │   │   ├── AguaBendita.java       → Elimina maldiciones
│   │   │   ├── PlumaMundo.java        → Revive personaje
│   │   │   ├── SemillaMagica.java     → Restaura MP
│   │   │   └── AlaQuimera.java        → Efecto especial
│   │   │
│   │   ├── personajes/
│   │   │   ├── Personaje.java         → Clase base abstracta con atributos y métodos comunes
│   │   │   ├── Heroe.java             → Subclase que representa a los héroes
│   │   │   ├── Enemigo.java           → Subclase que representa a los enemigos
│   │   │   └── MiniBoss.java          → Subclase para jefes especiales
│   │   │
│   │   ├── persistencia/
│   │   │   ├── GestorPersistencia.java → Guarda/carga partidas
│   │   │   └── EstadoBatalla.java     → Estructura de datos para guardar estado
│   │   │
│   │   ├── registro/
│   │   │   ├── RegistroAventureros.java → Registro ordenado (TreeMap) de héroes
│   │   │   ├── HistorialBatallas.java   → Historial de batallas completadas
│   │   │   ├── HistorialBatallas.EstadisticasJugador.java → Estadísticas internas
│   │   │   ├── RegistroBatalla.java     → Registro individual de batalla
│   │   │   ├── HistorialCombate.java    → Deshacer/rehacer acciones (Stack)
│   │   │   ├── AccionCombate.java       → Acción registrada en combate
│   │   │   └── AccionCombate.TipoAccion.java → Tipos de acción (ATAQUE, HABILIDAD, ITEM)
│   │   │
│   │   ├── historia/
│   │   │   └── HistoriaJuego.java     → Narrativa y diálogos del juego
│   │   │
│   │   ├── GameModel.java
│   │   └── Main.java
│   │
│   ├── view/
│   │   ├── gui/
│   │   │   ├── VentanaPrincipalCompleta.java  → Menú principal con todos los botones
│   │   │   ├── VentanaCombate.java            → Ventana de combate con paneles
│   │   │   ├── VentanaGestionPartidas.java    → Cargar/eliminar partidas guardadas
│   │   │   ├── VentanaGremio.java             → Sistema del gremio
│   │   │   ├── VentanaOpciones.java           → Configuración del juego
│   │   │   ├── VentanaPrincipal.java          → Ventana alternativa
│   │   │   ├── GUIAdapter.java                → Adaptador MVC para GUI
│   │   │   ├── ConsolaRedirect.java           → Redirige System.out a JTextArea
│   │   │   └── EfectosVisuales.java          → Efectos de animación
│   │   │
│   │   ├── imagenes/
│   │   │   └── fondo_azul.png
│   │   │
│   │   ├── sonidos/
│   │   │   ├── heal.wav
│   │   │   ├── hit.wav
│   │   │   ├── musica_batalla.wav
│   │   │   ├── musica_menu.wav
│   │   │   └── status.wav
│   │   │
│   │   └── ConsoleView.java
│   │
│   ├── controller/
│   │   └── GameController.java        → Orquesta acciones modelo-vista
│   │
│   └── MainMVC.java                   → Punto de entrada GUI
│
└── Main.java                          → Punto de entrada consola
```

---

## Características Principales

### 🎮 Sistema de Combate
- Turnos ordenados por velocidad
- 4 héroes vs 4 enemigos (1 mini jefe)
- Estados alterados (paralizado, dormido, envenenado)

### 🎒 Inventarios Individuales
- Cada héroe tiene su propio inventario (5 ítems máximo)
- Estructura: LinkedHashMap (O(1))

### 💾 Persistencia
- Guardar/cargar partidas en formato texto
- Sistema de excepciones personalizado

### 📊 Historial y Estadísticas
- Registro automático de batallas
- TreeMap para registro alfabético (O(log n))
- Queue para historial cronológico

### ↩️ Deshacer/Rehacer
- Stack (LIFO) para historial de acciones
- Hasta 10 acciones deshacibles

### 🏛️ Gremio de Aventureros
- PriorityQueue para cola de turnos
- Registro ordenado de aventureros
- Sistema de solicitudes de aventureros

---

## Instrucciones para Ejecutar

```bash
# Compilar
javac -d bin -sourcepath src $(find src -name "*.java")

# Ejecutar (GUI)
java -cp bin mvc.MainMVC

# Ejecutar (Consola)
java -cp bin Main
```

---

## Instrucciones para Colaboradores

*Clonar el repositorio:*
```bash
git clone https://github.com/Juliank100/MiniProyecto4MVC.git
cd MiniProyecto4MVC
```

*Crear una nueva rama:*
```bash
git checkout -b nombre-rama
```

*Realizar los cambios y hacer commit:*
```bash
git add .
git commit -m "Descripción del cambio"
```

*Subir los cambios y crear un Pull Request:*
```bash
git push origin nombre-rama
```

---
