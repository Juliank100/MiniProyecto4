package mvc.model.estados;

/**
 * EstadoAlterado - enum que define los posibles estados aplicables a los personajes.
 *
 * - NORMAL: sin estado alterado.
 * - VENENO: daño por turno.
 * - PARALIZADO: puede fallar la acción con cierta probabilidad.
 * - DORMIDO: no actúa hasta despertarse.
 *
 * Si en el futuro quieres más estados (confusión, quemadura, etc.) agrégalos aquí y
 * gestiona sus efectos en Personaje.procesarEstadosAntesDeActuar().
 */
public enum EstadoAlterado {
    NORMAL,
    VENENO,
    PARALIZADO,
    DORMIDO
}