package unal.optimization;

import java.util.Arrays;
import java.util.stream.IntStream;

public class SupplyChain {
    private int suppliers;
    private int plants;
    private int dcs;
    private int retails;

    private int[][] supplierToPlantCost;
    private int[][] plantToDCCost;
    private int[][] dcToRetailCost;

    private int[] supplierCost;
    private int[] plantCost;
    private int[] dcCost;

    private int[] supplierOffer;
    private int[] plantOffer;
    private int[] dcOffer;

    private int[] plantDemand;
    private int[] dcDemand;
    private int[] retailDemand;

    private int[][] supplierToPlant;
    private int[][] plantToDC;
    private int[][] dcToRetail;

    private int[] supplierUnused;
    private int[] plantUnused;
    private int[] dcUnused;
    private int[] retailUncovered;

    public SupplyChain(int suppliers, int plants, int dcs, int retails) {
        this.suppliers = suppliers;
        this.plants = plants;
        this.dcs = dcs;
        this.retails = retails;

        this.plantDemand = new int[plants];
        this.dcDemand = new int[dcs];

        supplierToPlant = new int[suppliers][plants];
        plantToDC = new int[plants][dcs];
        dcToRetail = new int[dcs][retails];

        supplierUnused = new int[suppliers];
        plantUnused = new int[plants];
        dcUnused = new int[dcs];
        retailUncovered = new int[retails];
    }

    public SupplyChain setTransportCost(int[][] supplierToPlantCost, int[][] plantToDCCost, int[][] dcToRetailCost) {
        if (supplierToPlantCost.length != suppliers || supplierToPlantCost[0].length != plants)
            throw new IllegalArgumentException();
        this.supplierToPlantCost = supplierToPlantCost;

        if (plantToDCCost.length != plants || plantToDCCost[0].length != dcs)
            throw new IllegalArgumentException();
        this.plantToDCCost = plantToDCCost;

        if (dcToRetailCost.length != dcs || dcToRetailCost[0].length != retails)
            throw new IllegalArgumentException();
        this.dcToRetailCost = dcToRetailCost;

        return this;
    }

    public SupplyChain setUnitCost(int[] supplierCost, int[] plantCost, int[] dcCost) {
        if (supplierCost.length != suppliers)
            throw new IllegalArgumentException();
        this.supplierCost = supplierCost;

        if (plantCost.length != plants)
            throw new IllegalArgumentException();
        this.plantCost = plantCost;

        if (dcCost.length != dcs)
            throw new IllegalArgumentException();
        this.dcCost = dcCost;

        return this;
    }

    public SupplyChain setOffer(int[] supplierOffer, int[] plantOffer, int[] dcOffer) {
        if (supplierOffer.length != suppliers)
            throw new IllegalArgumentException();
        this.supplierOffer = supplierOffer;

        if (plantOffer.length != plants)
            throw new IllegalArgumentException();
        this.plantOffer = plantOffer;

        if (dcOffer.length != dcs)
            throw new IllegalArgumentException();
        this.dcOffer = dcOffer;

        return this;
    }

    public SupplyChain setDemand(int[] retailDemand) {
        if (retailDemand.length != retails)
            throw new IllegalArgumentException();
        this.retailDemand = retailDemand;

        return this;
    }

    public SupplyChain simulate(int[] stagePermutation1, int[] stagePermutation2, int[] stagePermutation3) {
        if (stagePermutation1.length != suppliers + plants)
            throw new IllegalArgumentException();
        if (stagePermutation2.length != plants + dcs)
            throw new IllegalArgumentException();
        if (stagePermutation3.length != retails)
            throw new IllegalArgumentException();

        System.arraycopy(supplierOffer, 0, supplierUnused, 0, suppliers);
        System.arraycopy(plantOffer, 0, plantUnused, 0, plants);
        System.arraycopy(dcOffer, 0, dcUnused, 0, dcs);
        System.arraycopy(retailDemand, 0, retailUncovered, 0, retails);

        for (int i = 0; i < suppliers; i++)
            for (int j = 0; j < plants; j++)
                supplierToPlant[i][j] = 0;
        for (int i = 0; i < plants; i++)
            for (int j = 0; j < dcs; j++)
                plantToDC[i][j] = 0;
        for (int i = 0; i < dcs; i++)
            for (int j = 0; j < retails; j++)
                dcToRetail[i][j] = 0;

        unwrapSingleRightPermutation(dcToRetail, stagePermutation3, dcToRetailCost, dcUnused, retailUncovered);
        //Set dcDemand
        for (int i = 0; i < dcs; i++)
            dcDemand[i] = dcOffer[i] - dcUnused[i];
        unwrapPermutation(plantToDC, stagePermutation2, plantToDCCost, plantUnused, dcDemand);
        //Set plantDemand
        for (int i = 0; i < plants; i++)
            plantDemand[i] = plantOffer[i] - plantUnused[i];
        unwrapPermutation(supplierToPlant, stagePermutation1, supplierToPlantCost, supplierUnused, plantDemand);

        //Set dcDemand
        for (int i = 0; i < dcs; i++)
            dcDemand[i] = dcOffer[i] - dcUnused[i];
        //Set plantDemand
        for (int i = 0; i < plants; i++)
            plantDemand[i] = plantOffer[i] - plantUnused[i];
        return this;
    }

    public int evaluate() {
        int total = 0;
        //Supplier unit cost
        total += IntStream.range(0, suppliers).map(i -> Arrays.stream(supplierToPlant[i]).sum() * supplierCost[i]).sum();
        //Supplier transport cost
        total += IntStream.range(0, suppliers).map(i -> IntStream.range(0, plants).map(j -> supplierToPlant[i][j] * supplierToPlantCost[i][j]).sum()).sum();

        //Plant unit cost
        total += IntStream.range(0, plants).map(i -> Arrays.stream(plantToDC[i]).sum() * plantCost[i]).sum();
        //Plant transport cost
        total += IntStream.range(0, plants).map(i -> IntStream.range(0, dcs).map(j -> plantToDC[i][j] * plantToDCCost[i][j]).sum()).sum();

        //DC unit cost
        total += IntStream.range(0, dcs).map(i -> Arrays.stream(dcToRetail[i]).sum() * dcCost[i]).sum();
        //DC transport cost
        total += IntStream.range(0, dcs).map(i -> IntStream.range(0, retails).map(j -> dcToRetail[i][j] * dcToRetailCost[i][j]).sum()).sum();

        return total;
    }

    public static void unwrapPermutation(int[][] target, int[] permutation, int[][] cost, int[] offer, int[] demand) {
        int currentCost;
        int chosen;
        int amount;
        for (int i = 0; i < permutation.length; i++) {
            currentCost = Integer.MAX_VALUE;
            chosen = -1;

            if (permutation[i] < target.length) {//In left side
                for (int j = 0; j < target[0].length; j++) {
                    if (cost[permutation[i]][j] < currentCost && demand[j] > 0) {
                        chosen = j;
                        currentCost = cost[permutation[i]][j];
                    }
                }
                if (chosen == -1)
                    continue;
                amount = Math.min(offer[permutation[i]], demand[chosen]);
                offer[permutation[i]] -= amount;
                demand[chosen] -= amount;
                target[permutation[i]][chosen] += amount;
            } else {//In right side
                for (int j = 0; j < target.length; j++) {
                    if (cost[j][permutation[i] - target.length] < currentCost && offer[j] > 0) {
                        chosen = j;
                        currentCost = cost[j][permutation[i] - target.length];
                    }
                }
                if (chosen == -1)
                    continue;
                amount = Math.min(offer[chosen], demand[permutation[i] - target.length]);
                offer[chosen] -= amount;
                demand[permutation[i] - target.length] -= amount;
                target[chosen][permutation[i] - target.length] += amount;
            }
        }
    }

    public static void unwrapSingleRightPermutation(int[][] target, int[] permutation, int[][] cost, int[] offer, int[] demand) {
        int currentCost;
        int chosen;
        int amount;
        for (int i = 0; i < permutation.length; i++) {
            currentCost = Integer.MAX_VALUE;
            chosen = -1;
            amount = demand[permutation[i]];

            for (int j = 0; j < target.length; j++) {
                if (cost[j][permutation[i]] < currentCost && offer[j] >= amount) {
                    chosen = j;
                    currentCost = cost[j][permutation[i]];
                }
            }
            if (chosen == -1)
                continue;
            offer[chosen] -= amount;
            demand[permutation[i]] = 0;
            target[chosen][permutation[i]] = amount;
        }
    }

    @Override
    public String toString() {
        return "unal.optimization.SupplyChain{" +
                "\nCOST=" + evaluate() +
                "\nsuppliers=" + suppliers +
                ", plants=" + plants +
                ", dcs=" + dcs +
                ", retails=" + retails +
                ",\nsupplierToPlantCost=" + printMatrix(supplierToPlantCost) +
                ",\nplantToDCCost=" + printMatrix(plantToDCCost) +
                ",\ndcToRetailCost=" + printMatrix(dcToRetailCost) +
                ",\nsupplierCost=" + Arrays.toString(supplierCost) +
                ", plantCost=" + Arrays.toString(plantCost) +
                ", dcCost=" + Arrays.toString(dcCost) +
                ",\nsupplierOffer=" + Arrays.toString(supplierOffer) +
                ", plantOffer=" + Arrays.toString(plantOffer) +
                ", dcOffer=" + Arrays.toString(dcOffer) +
                ",\nplantDemand=" + Arrays.toString(plantDemand) +
                ", dcDemand=" + Arrays.toString(dcDemand) +
                ", retailDemand=" + Arrays.toString(retailDemand) +
                ",\nretailUncovered=" + Arrays.toString(retailUncovered) +
                ",\nsupplierUnused=" + Arrays.toString(supplierUnused) +
                ", plantUnused=" + Arrays.toString(plantUnused) +
                ", dcUnused=" + Arrays.toString(dcUnused) +
                ",\nsupplierToPlant=" + printMatrix(supplierToPlant) +
                ",\nplantToDC=" + printMatrix(plantToDC) +
                ",\ndcToRetail=" + printMatrix(dcToRetail) +
                "\n}";
    }

    private static String printMatrix(int[][] matrix) {
        String str = "";
        for (int i = 0; i < matrix.length; i++) {
            str += "\n";
            for (int j = 0; j < matrix[0].length; j++) {
                str += matrix[i][j] + (j == matrix[0].length - 1 ? "" : ",");
            }
        }
        return str;
    }
}