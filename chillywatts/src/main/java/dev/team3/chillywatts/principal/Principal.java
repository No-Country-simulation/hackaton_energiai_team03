package dev.team3.chillywatts.principal;

import dev.team3.chillywatts.enums.EstadoBorracha;
import dev.team3.chillywatts.enums.TecnologiaFreezer;
import dev.team3.chillywatts.enums.TipoFreezer;
import dev.team3.chillywatts.entity.Freezer;
import dev.team3.chillywatts.repository.FreezerRepository;

import java.util.List;
import java.util.Scanner;

public class Principal {

    private Scanner leitura = new Scanner(System.in);

    private FreezerRepository repositorio;

    private List<Freezer> freezers;

    public Principal(FreezerRepository repositorio){
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Adicionar Freezer
                    2 - Buscar Freezer por marca
                    0 - Sair                                 
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    addFreezer();
                    break;

                case 2:
                    buscaPorMarca();
                    break;

                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }



    private void addFreezer() {
        String marca = "";
        System.out.println("Insira a marca");
        marca = this.leitura.nextLine();

        String tipo = "";
        System.out.println("Insira o tipo");
        tipo = this.leitura.nextLine();

        String tecnologia = "";
        System.out.println("Insira a tecnologia");
        tecnologia = this.leitura.nextLine();

        String borracha = "";
        System.out.println("Insira o estado da borracha");
        borracha = this.leitura.nextLine();

        Integer quant = -1;
        System.out.println("Insira a quantidade");
        quant = this.leitura.nextInt();

        Freezer freezer = new Freezer(marca, TipoFreezer.fromString(tipo), TecnologiaFreezer.fromString(tecnologia), EstadoBorracha.fromString(borracha), quant);
        repositorio.save(freezer);
        System.out.println(freezer + "\n");

    }

    private void buscaPorMarca() {
        String marcaBusca = "";
        System.out.println("Insira a marca para buscar");
        marcaBusca = this.leitura.nextLine();

        freezers = repositorio.findByMarcaContainingIgnoreCase(marcaBusca);

        System.out.println(freezers);
    }

/*
        String epoca = "";
        System.out.println("Insira a epoca do ano");
        epoca = this.leitura.nextLine();
        System.out.println("Potencia do freezer é de: " + freezer.obterPotencia() + " kw \n");

        double consumo = freezer.calcularConsumoTeorico(epoca);
        System.out.println("Consumo teórico é de: " + consumo + "kw/h \n");

        */



}
