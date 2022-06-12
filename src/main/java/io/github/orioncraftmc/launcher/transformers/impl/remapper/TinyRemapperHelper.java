package io.github.orioncraftmc.launcher.transformers.impl.remapper;

import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.tinyremapper.IMappingProvider;

public class TinyRemapperHelper {

    private static IMappingProvider.Member memberOf(String className, String memberName, String descriptor) {
        return new IMappingProvider.Member(className, memberName, descriptor);
    }

    public static IMappingProvider create(MappingTree mappings, String from, String to, boolean remapLocalVariables) {
        return (acceptor) -> {
            final int fromId = mappings.getNamespaceId(from);
            final int toId = mappings.getNamespaceId(to);

            for (MappingTree.ClassMapping classDef : mappings.getClasses()) {
                String className = classDef.getName(fromId);
                String dstName = classDef.getName(toId);

                if (dstName == null) {
                    // Unsure if this is correct, should be better than crashing tho.
                    dstName = className;
                }

                acceptor.acceptClass(className, dstName);

                for (MappingTree.FieldMapping field : classDef.getFields()) {
                    acceptor.acceptField(memberOf(className, field.getName(fromId), field.getDesc(fromId)), field.getName(toId));
                }

                for (MappingTree.MethodMapping method : classDef.getMethods()) {
                    IMappingProvider.Member methodIdentifier = memberOf(className, method.getName(fromId), method.getDesc(fromId));
                    acceptor.acceptMethod(methodIdentifier, method.getName(toId));

                    if (remapLocalVariables) {
                        for (MappingTree.MethodArgMapping parameter : method.getArgs()) {
                            String name = parameter.getName(toId);

                            if (name == null) {
                                continue;
                            }

                            acceptor.acceptMethodArg(methodIdentifier, parameter.getLvIndex(), name);
                        }

                        for (MappingTree.MethodVarMapping localVariable : method.getVars()) {
                            acceptor.acceptMethodVar(methodIdentifier, localVariable.getLvIndex(),
                                    localVariable.getStartOpIdx(), localVariable.getLvtRowIndex(),
                                    localVariable.getName(toId));
                        }
                    }
                }
            }
        };
    }
}
