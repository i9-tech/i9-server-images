package school.sptech.dto;

import java.io.Serializable;

public record EventoImagemProcessadaDto(
        String tipo,
        String identificador,
        String urlImagem
) implements Serializable {}
