package br.com.project.screenmatch.main;

import br.com.project.screenmatch.model.DadosEpisodio;
import br.com.project.screenmatch.model.DadosSerie;
import br.com.project.screenmatch.model.DadosTemporada;
import br.com.project.screenmatch.model.Episodio;
import br.com.project.screenmatch.service.ConsumoAPI;
import br.com.project.screenmatch.service.ConverterDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class Main {
        public Scanner sc = new Scanner(System.in);
    ConsumoAPI consumo = new ConsumoAPI();
    ConverterDados conversor = new ConverterDados();

    private final String API_KEY = "&apikey=ef4dc0c8";
    private final String ENDERECO = "https://www.omdbapi.com/?t=";

   public void exibeMenu() {
        System.out.println("Digite a série desejada:");
        var nomeSerie = sc.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);

        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

//    List<String> professores = Arrays.asList("Jacque", "Iasmin", "Nico", "Paulo");
//    professores.stream()
//            .sorted()
//            .filter(n -> n.startsWith("N"))
//            .map(n -> n.toUpperCase())
//            .forEach(System.out::println);
//}
    List<DadosEpisodio> dadosEpisodios = temporadas.stream()
            .flatMap(t -> t.episodios().stream())
            .collect(Collectors.toList());

       System.out.println("\nTop 5 episódios:");
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.temporada(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);

       System.out.println("A partir de que ano vc deseja ver os episodios?");
       var ano = sc.nextInt();
       sc.nextLine();

       LocalDate dataBusca = LocalDate.of(ano, 1, 1);

       DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

       episodios.stream()
               .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
               .forEach(e -> System.out.println(
                       "Temporada: " + e.getTemporada() +
                               " Episódio: " + e.getTitulo() +
                               " Data de lançamento: " + e.getDataLancamento().format(formatador)
               ));
}}
