# Diagrama de Clases - Sistema de Organizador de Scrims

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        JUEGOS Y ROLES                                    │
└─────────────────────────────────────────────────────────────────────────┘

                    ┌───────────────────┐
                    │   <<abstract>>    │
                    │      Juego        │
                    ├───────────────────┤
                    │ - instance        │
                    ├───────────────────┤
                    │ + getNombre()     │
                    │ + getRolesDisp()  │
                    │ + getFormatos()   │
                    │ + esRolValido()   │
                    │ + esFormatoValido()│
                    └─────────┬─────────┘
                              │
                              │ extends
                              │
                    ┌─────────▼─────────┐
                    │ LeagueOfLegends   │
                    │   (Singleton)     │
                    ├───────────────────┤
                    │ - instance        │
                    │ - rolesDisp       │
                    │ - formatosDisp    │
                    ├───────────────────┤
                    │ + getInstance()   │
                    │ + getNombre()     │
                    └───────────────────┘


     ┌──────────────────┐                      ┌──────────────────┐
     │  <<interface>>   │                      │  <<interface>>   │
     │    RolJuego      │                      │  ScrimFormat     │
     ├──────────────────┤                      ├──────────────────┤
     │ + getNombre()    │                      │ + getPlayersPerTeam()│
     │ + getDescripcion()│                     │ + getFormatName() │
     │ + getJuego()     │                      │ + isValidFormat() │
     │ + esCompatibleCon()│                    └────────▲─────────┘
     └────────▲─────────┘                               │
              │                                         │ implements
              │ implements                              │
     ┌────────┴─────────┐                      ┌────────┴──────────┐
     │   RolTopLoL      │                      │  Formato5v5LoL    │
     │   RolJungleLoL   │                      │  FormatoARAMLoL   │
     │   RolMidLoL      │                      └───────────────────┘
     │   RolADCLoL      │
     │   RolSupportLoL  │
     └──────────────────┘


┌─────────────────────────────────────────────────────────────────────────┐
│                        SCRIM Y PARTICIPANTES                             │
└─────────────────────────────────────────────────────────────────────────┘

         ┌──────────────────────┐         ┌─────────────────────────┐
         │       Usuario        │         │        Scrim            │
         ├──────────────────────┤         ├─────────────────────────┤
         │ - username           │         │ - id                    │
         │ - email              │         │ - juego: Juego          │
         │ - hashedPassword     │         │ - formato: ScrimFormat  │
         ├──────────────────────┤         │ - fechaHora             │
         │ + getUsername()      │         │ - plazas                │
         │ + getEmail()         │         │ - state: ScrimState     │
         │ + verifyPassword()   │         │ - listaPostulaciones    │
         └──────────┬───────────┘         │ - listaConfirmaciones   │
                    │                     ├─────────────────────────┤
                    │                     │ + postular()            │
                    │                     │ + confirmar()           │
                    │                     │ + iniciar()             │
                    │ has-a               │ + getJuego()            │
                    │                     │ + getEstado()           │
         ┌──────────▼───────────┐         └──────────▲──────────────┘
         │  ParticipanteScrim   │                    │
         ├──────────────────────┤                    │ has-a
         │ - usuario: Usuario   │◄───────────────────┘
         │ - rolAsignado: RolJuego │
         │ - confirmado         │
         ├──────────────────────┤
         │ + getUserId()        │
         │ + getRolAsignado()   │
         │ + setRolAsignado()   │
         │ + confirmar()        │
         └──────────────────────┘


┌─────────────────────────────────────────────────────────────────────────┐
│                    ACCIONES DEL ORGANIZADOR                              │
└─────────────────────────────────────────────────────────────────────────┘

                    ┌────────────────────────────┐
                    │     <<interface>>          │
                    │   AccionOrganizador        │
                    ├────────────────────────────┤
                    │ + ejecutar(organizador)    │
                    │ + deshacer(organizador)    │
                    │ + puedeEjecutarse()        │
                    │ + getDescripcion()         │
                    │ + getTipoAccion()          │
                    └──────────┬─────────────────┘
                               │
                               │ implements
           ┌───────────────────┼───────────────────┐
           │                   │                   │
  ┌────────▼────────┐  ┌───────▼────────┐  ┌──────▼──────────┐
  │ InvitarJugador  │  │  AsignarRol    │  │ SwapJugadores   │
  │    Accion       │  │    Accion      │  │    Accion       │
  ├─────────────────┤  ├────────────────┤  ├─────────────────┤
  │ - usuario       │  │ - userId       │  │ - userId1       │
  │ - rolAsignado   │  │ - nuevoRol     │  │ - userId2       │
  │                 │  │ - rolAnterior  │  │ - rolOriginal1  │
  ├─────────────────┤  ├────────────────┤  │ - rolOriginal2  │
  │ + ejecutar()    │  │ + ejecutar()   │  ├─────────────────┤
  │ + deshacer()    │  │ + deshacer()   │  │ + ejecutar()    │
  │ + puedeEjecutarse│  │+ puedeEjecutarse│ │ + deshacer()    │
  └─────────────────┘  └────────────────┘  │ + puedeEjecutarse│
                                            └─────────────────┘


┌─────────────────────────────────────────────────────────────────────────┐
│                    ORGANIZADOR (FACADE)                                  │
└─────────────────────────────────────────────────────────────────────────┘

                    ┌────────────────────────────┐
                    │    ScrimOrganizador        │
                    │       (Facade)             │
                    ├────────────────────────────┤
                    │ - scrim: Scrim             │
                    │ - participantes: List      │
                    │ - historialAcciones: Stack │
                    │ - bloqueado: boolean       │
                    ├────────────────────────────┤
                    │ + ejecutarAccion(accion)   │
                    │ + deshacerUltimaAccion()   │
                    │ + confirmarScrim()         │
                    │ + buscarParticipante()     │
                    │ + esRolOcupado()           │
                    │ + getParticipantes()       │
                    │ + isBloqueado()            │
                    └────────┬─────────┬─────────┘
                             │         │
                    uses     │         │ manages
                             │         │
                ┌────────────▼─┐   ┌──▼───────────────┐
                │ AccionOrg.   │   │ ParticipanteScrim│
                └──────────────┘   └──────────────────┘


┌─────────────────────────────────────────────────────────────────────────┐
│                    BUILDER PATTERN                                       │
└─────────────────────────────────────────────────────────────────────────┘

                    ┌────────────────────────┐
                    │    ScrimBuilder        │
                    ├────────────────────────┤
                    │ - juego: Juego         │
                    │ - formato: ScrimFormat │
                    │ - fechaHora            │
                    │ - rangoMin, rangoMax   │
                    │ - plazas               │
                    ├────────────────────────┤
                    │ + withJuego(juego)     │
                    │ + withFormato(formato) │
                    │ + withFechaHora(fecha) │
                    │ + withRango(min, max)  │
                    │ + build(): Scrim       │
                    └────────┬───────────────┘
                             │
                             │ creates
                             ▼
                    ┌────────────────┐
                    │     Scrim      │
                    └────────────────┘


┌─────────────────────────────────────────────────────────────────────────┐
│                    FLUJO DE EJECUCIÓN                                    │
└─────────────────────────────────────────────────────────────────────────┘

1. Cliente crea Scrim con ScrimBuilder
        │
        ▼
2. Cliente crea ScrimOrganizador(scrim)
        │
        ▼
3. Cliente crea AccionOrganizador (InvitarJugador, AsignarRol, Swap)
        │
        ▼
4. Cliente ejecuta: organizador.ejecutarAccion(accion)
        │
        ├─► Acción se ejecuta sobre el scrim
        │
        └─► Acción se guarda en historial (Stack)
        │
        ▼
5. (Opcional) Cliente deshace: organizador.deshacerUltimaAccion()
        │
        ├─► Se extrae última acción del Stack
        │
        └─► Se invoca accion.deshacer(organizador)
        │
        ▼
6. Cliente confirma: organizador.confirmarScrim()
        │
        ├─► bloqueado = true
        │
        ├─► Participantes marcados como confirmados
        │
        └─► Historial se limpia (no más undo)
```

## Relaciones Clave

- **Usuario** ←→ **ParticipanteScrim**: Composición (un participante tiene un usuario)
- **ParticipanteScrim** ←→ **RolJuego**: Agregación (un participante tiene un rol)
- **Scrim** ←→ **Juego**: Composición (un scrim tiene un juego)
- **Scrim** ←→ **ScrimFormat**: Composición (un scrim tiene un formato)
- **ScrimOrganizador** ←→ **Scrim**: Asociación (gestiona un scrim)
- **ScrimOrganizador** ←→ **ParticipanteScrim**: Agregación (gestiona múltiples participantes)
- **ScrimOrganizador** ←→ **AccionOrganizador**: Dependencia (ejecuta acciones)
- **Juego** ←→ **RolJuego**: Dependencia (un juego conoce sus roles)
- **Juego** ←→ **ScrimFormat**: Dependencia (un juego conoce sus formatos)

## Cardinalidades

- **Scrim** 1 ←→ \* **ParticipanteScrim**
- **Usuario** 1 ←→ \* **ParticipanteScrim**
- **Juego** 1 ←→ \* **RolJuego**
- **Juego** 1 ←→ \* **ScrimFormat**
- **ScrimOrganizador** 1 ←→ 1 **Scrim**
- **ScrimOrganizador** 1 ←→ \* **AccionOrganizador** (historial)
