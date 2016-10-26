import org.jenetics.*;
import org.jenetics.engine.*;
import org.jenetics.stat.DoubleMomentStatistics;

import java.time.Duration;
import java.util.Random;

public class GeneticSupplyChain {

    private static final int SUPPLIERS = 3;
    private static final int PLANTS = 3;
    private static final int DCS = 3;
    private static final int RETAILS = 3;

    private static int[][] supplierToPlantCost = new int[SUPPLIERS][PLANTS];
    private static int[][] plantToDCCost = new int[PLANTS][DCS];
    private static int[][] dcToRetailCost = new int[DCS][RETAILS];

    private static int[] supplierCost = new int[SUPPLIERS];
    private static int[] plantCost = new int[PLANTS];
    private static int[] dcCost = new int[DCS];

    private static int[] supplierOffer = new int[SUPPLIERS];
    private static int[] plantOffer = new int[PLANTS];
    private static int[] dcOffer = new int[DCS];

    private static int[] retailDemand = new int[RETAILS];

    static {
        randomMatrix(supplierToPlantCost, 1000);
        randomMatrix(plantToDCCost, 1000);
        randomMatrix(dcToRetailCost, 1000);

        randomVector(supplierCost, 1000);
        randomVector(plantCost, 1000);
        randomVector(dcCost, 1000);

        randomVector(supplierOffer, 1000);
        randomVector(plantOffer, 1000);
        randomVector(dcOffer, 1000);

        randomVector(retailDemand, 300);
    }

    private static final Codec<SupplyChain, EnumGene<Integer>> CODEC = Codec.of(
            Genotype.of(
                    PermutationChromosome.ofInteger(SUPPLIERS + PLANTS),
                    PermutationChromosome.ofInteger(PLANTS + DCS),
                    PermutationChromosome.ofInteger(RETAILS)
            ),
            GeneticSupplyChain::createSimulation
    );

    private static int fitness(final SupplyChain supplyChain) {
        return supplyChain.evaluate();
    }

    public static void main(String[] args) {
        Engine<EnumGene<Integer>, Integer> engine = Engine
                .builder(GeneticSupplyChain::fitness, CODEC)
                .optimize(Optimize.MINIMUM)
                .populationSize(100)
                .alterers(
                        new SwapMutator<>(0.2),
                        new PartiallyMatchedCrossover<>(0.2))
                .build();

        EvolutionStatistics<Integer, DoubleMomentStatistics> statistics = EvolutionStatistics.ofNumber();

        Genotype<EnumGene<Integer>> best = engine.stream()
                .limit(limit.byExecutionTime(Duration.ofMinutes(1)))
//                .limit(limit.byFitnessThreshold(0))
//                .limit(100000)
                .peek(statistics)
                .collect(EvolutionResult.toBestGenotype());

        System.out.println(statistics);
        System.out.println(best);
        SupplyChain SC = createSimulation(best);
        System.out.println(SC.evaluate());
        System.out.println(SC);
    }

    private static SupplyChain createSimulation(Genotype<EnumGene<Integer>> best) {
        return getNewSupplyChain().simulate(
                best.getChromosome(0).toSeq().stream().mapToInt(EnumGene<Integer>::getAllele).toArray(),
                best.getChromosome(1).toSeq().stream().mapToInt(EnumGene<Integer>::getAllele).toArray(),
                best.getChromosome(2).toSeq().stream().mapToInt(EnumGene<Integer>::getAllele).toArray());
    }

    private static SupplyChain getNewSupplyChain() {
        return new SupplyChain(SUPPLIERS, PLANTS, DCS, RETAILS)
                .setTransportCost(supplierToPlantCost, plantToDCCost, dcToRetailCost)
                .setUnitCost(supplierCost, plantCost, dcCost)
                .setOffer(supplierOffer, plantOffer, dcOffer)
                .setDemand(retailDemand);
    }

    private static void randomMatrix(int[][] target, int range) {
        Random r = new Random();
        for (int i = 0; i < target.length; i++) {
            randomVector(target[i], range);
        }
    }

    private static void randomVector(int[] target, int range) {
        Random r = new Random();
        for (int i = 0; i < target.length; i++) {
            target[i] = r.nextInt(range);
        }
    }
}