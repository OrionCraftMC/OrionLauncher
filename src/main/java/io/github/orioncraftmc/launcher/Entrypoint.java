package io.github.orioncraftmc.launcher;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.util.Constants;
import picocli.CommandLine;

@SuppressWarnings("FieldMayBeFinal")
@CommandLine.Command()
public class Entrypoint implements Callable<Integer> {

    @CommandLine.Option(names = {"-s", "--side"}, defaultValue = Constants.SIDE_CLIENT)
    private MixinEnvironment.Side side;

    @CommandLine.Option(names = {"-c", "--main-class"}, required = true)
    private String mainClass;

    @CommandLine.Option(names = {"-m", "--mappings"})
    private Path obfMapping;

    @CommandLine.Option(names = "--excluded-packages")
    private List<String> excludedPackages = List.of();

    @CommandLine.Parameters(split = " ")
    private List<String> params = List.of();

    @Override
    public Integer call() {
        OrionLauncher.getInstance().setMixinSide(getMixinSideAsConstant(side));

        try {
            if (obfMapping != null) OrionLauncher.getInstance().initDeobfMappings(obfMapping);

            OrionLauncher.getInstance().init(Entrypoint.class.getClassLoader(), mainClass, params, excludedPackages);
        } catch (Throwable e) {
            RuntimeException ex = new RuntimeException(e);
            ex.printStackTrace();
            throw ex;
        }

        return 0;
    }

    private String getMixinSideAsConstant(MixinEnvironment.Side side) {
        return switch (side) {
            case UNKNOWN -> Constants.SIDE_UNKNOWN;
            case CLIENT -> Constants.SIDE_CLIENT;
            case SERVER -> Constants.SIDE_SERVER;
        };
    }

    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new Entrypoint());
        commandLine.setCaseInsensitiveEnumValuesAllowed(true);
        commandLine.execute(args);
    }
}
