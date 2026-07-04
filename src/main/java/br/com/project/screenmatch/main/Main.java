package br.com.project.screenmatch.main;

import br.com.project.screenmatch.model.DadosSerie;
import br.com.project.screenmatch.model.DadosTemporada;
import br.com.project.screenmatch.service.ConsumoAPI;
import br.com.project.screenmatch.service.ConverterDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public Scanner sc = new Scanner(System.in);
    //    String nomeSerie;
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

    }

}
