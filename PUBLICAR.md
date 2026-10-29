# Fluid Sweep — publicación

El proyecto mantiene compilaciones separadas para 17 versiones de Minecraft, desde 1.21 hasta 26.3.
El archivo `versions.json` fija las versiones de Minecraft, Java y dependencias de cada compilación.
Los artefactos se generan en `build/<minecraft>/libs/`.

GitHub: https://github.com/XMasi/FluidSweep
Modrinth: https://modrinth.com/mod/fluid-sweep

Las versiones 1.3.0 se distribuyen como beta. En Modrinth deben seleccionarse las versiones exactas
que acepta cada JAR, con Fabric API y MaLiLib como dependencias obligatorias. Mod Menu es opcional.

Al publicar una etiqueta `v1.3.0`, GitHub Actions compila los 17 destinos y crea una prerelease
con sus JAR y archivos de código fuente. No publica automáticamente en Modrinth.

