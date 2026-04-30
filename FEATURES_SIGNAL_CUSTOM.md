# Signal Android Custom - Guía de Funcionalidades

Este documento detalla todas las capacidades de tu versión personalizada de Signal con integración de **Supabase**.

## 1. Núcleo de Comunicación (Ahora vía Supabase)
*   **Mensajería de Texto:** Envío y recepción de mensajes en tiempo real.
*   **Multimedia:** Soporte completo para:
    *   **Imágenes:** Envío de fotos desde galería o cámara.
    *   **Audio:** Notas de voz con visualización de onda.
    *   **Video:** Envío y reproducción integrada.
    *   **Archivos:** Documentos, PDFs, etc.
*   **Historias (Stories):** Comparte imágenes, texto o videos que desaparecen a las 24 horas.
*   **Reacciones:** Añade emojis a los mensajes recibidos.
*   **Edición de Mensajes:** Capacidad para corregir mensajes ya enviados.

## 2. Autenticación y Perfil (Sistema Supabase)
*   **Login por Email:** Registro e inicio de sesión con correo y contraseña.
*   **Perfiles Personalizados:**
    *   Nombre y apellidos.
    *   Avatar (foto de perfil) almacenada en Supabase Storage.
    *   Estado / "About me".

## 3. Grupos (V2)
*   **Grupos Modernos:** Gestión de miembros, administradores y enlaces de invitación.
*   **Menciones:** Etiquetado de usuarios con `@nombre` en chats grupales.
*   **Llamadas Grupales:** Capacidad para iniciar llamadas de voz y video en grupo (Requiere servidor SFU opcional).

## 4. Interfaz y Experiencia de Usuario (UI/UX)
*   **Diseño Material 3:** Interfaz moderna, limpia y con soporte para temas.
*   **Modo Oscuro/Claro:** Adaptación automática al sistema.
*   **Wallpaper Personalizado:** Cambia el fondo de pantalla de cada chat.
*   **Stickers:** Soporte para packs de stickers animados y estáticos.
*   **Giphy:** Integración para buscar y enviar GIFs.

## 5. Seguridad y Privacidad (Local)
*   **Bloqueo de Pantalla:** Protege la app con huella dactilar, cara o PIN del sistema.
*   **Incognito Keyboard:** Solicita al teclado que no aprenda lo que escribes.
*   **Seguridad de Pantalla:** Opción para bloquear capturas de pantalla dentro de la app.
*   **Base de Datos Cifrada:** Los mensajes se guardan localmente en un archivo SQLite cifrado con **SQLCipher**.

## 6. Funcionalidades Avanzadas
*   **Pagos (MobileCoin):** Sistema integrado para enviar y recibir pagos (si el servidor de pagos está activo).
*   **In-App Badges:** Sistema de insignias para usuarios (donantes, verificados, etc.).
*   **Buscador Global:** Encuentra mensajes, archivos o contactos rápidamente.
*   **Copia de Seguridad:** Creación de backups locales para migrar entre dispositivos.

---

## 🛠 Detalles Técnicos de tu Versión
*   **Backend:** Supabase (PostgreSQL + Auth + Storage + Realtime).
*   **CI/CD:** GitHub Actions configurado para firmar APKs automáticamente.
*   **Firma de App:** Alias `signal-android` en `my-release-key.jks`.
