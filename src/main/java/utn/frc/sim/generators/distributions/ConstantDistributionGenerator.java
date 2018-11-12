package utn.frc.sim.generators.distributions;

import utn.frc.sim.generators.distributions.valuegenerator.DistributionValueGenerator;

public class ConstantDistributionGenerator extends BaseRandomGenerator {

    private final double constant;

    private ConstantDistributionGenerator(double constant) {
        this.constant = constant;
    }

    public static ConstantDistributionGenerator createOf(double constant){
        return new ConstantDistributionGenerator(constant);
    }

    @Override
    public DistributionValueGenerator getDistribution() {
        return new ConstantValueGenerator();
    }

    @Override
    public double random() {
        return constant;
    }

    private class ConstantValueGenerator implements DistributionValueGenerator{

        @Override
        public double getExpectedFrequency(double from, double to) {
            if (from < constant && constant < to){
                return 1;
            }
            return 0;
        }
    }
}
